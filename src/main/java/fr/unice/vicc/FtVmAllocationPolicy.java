package fr.unice.vicc;

import org.apache.commons.math3.util.Pair;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lucas Martinez
 * @version 30/01/17.
 *
 * Role : Ensure that if a node crash, another has the resources to restart it
 * Overall Design and technical choices : Save the CPU and the RAM used in a map et used this map to determine which host is suitable
 * Complexity : O(n) in most cases with n the number of host
 */
public class FtVmAllocationPolicy extends VmAllocationPolicy{
    /** The map to track the server that host each running VM. */
    private Map<Vm,Host> hoster;

    /** Save the resources used and booked for the host */
    private Map<Host, Pair<Integer,Double>> hostWithReservedRes;


    public FtVmAllocationPolicy(List<? extends Host> list) {
        super(list);
        hoster = new HashMap<>();
        hostWithReservedRes = new HashMap<>();
    }

    @Override
    protected void setHostList(List<? extends Host> hostList) {
        super.setHostList(hostList);
        hoster = new HashMap<>();
    }

    @Override
    public <T extends Host> List<T> getHostList() {
        return super.getHostList();
    }

    @Override
    public boolean allocateHostForVm(Vm vm) {
        Host h = getSuitableHost(vm, null);
        int id = vm.getId();
        if (h != null && h.vmCreate(vm)) {
            hoster.put(vm, h);
            updateResourcesMap(h, vm);
            //If the vm is a multiple of 10
            if ((id % 10) == 0) {
                Host hForReplica = getSuitableHost(vm, h);
                //impossible to save resources for the fault tolerant
                //Delete the VM
                if (hForReplica == null) {
                    h.vmDestroy(vm);
                    hoster.remove(vm);
                    deleteResources(h, vm);
                    return false;
                }
                updateResourcesMap(hForReplica, vm);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        return false;
    }

    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> list) {
        return null;
    }

    @Override
    public void deallocateHostForVm(Vm vm) {
        //Remove the VM on each host in the hoster
        for(Host h : hoster.values()) {
            h.vmDestroy(vm);
        }
    }

    @Override
    public Host getHost(Vm vm) {
        return vm.getHost();
    }

    @Override
    public Host getHost(int vmId, int userId) {
        //Iterate through the map and check ids
        for(Map.Entry<Vm, Host> e : hoster.entrySet()) {
            if (e.getKey().getId() == vmId && e.getKey().getUserId() == userId)
                return e.getValue();

        }
        //Default
        return null;
    }

    private Host getSuitableHost(Vm vm, Host alreadyUsed) {
        int ramWanted = vm.getRam();
        double mipsWanted = vm.getMips();
        for(Host h : getHostList()) {
            if(h.equals(alreadyUsed)) continue;
            Pair<Integer, Double> hRes = hostWithReservedRes.get(h);
            //Check if the vm could be hosted
            if(hRes == null || ((h.getTotalMips() - hRes.getValue()) > mipsWanted
                            && ((h.getRam() - hRes.getKey()) > ramWanted))) {
                return h;
            }
        }
        //Default
        return null;
    }

    private void updateResourcesMap(Host h, Vm vm) {
        //If the host doesn't contain the host, create it
        if (!hostWithReservedRes.containsKey(h)) {
            hostWithReservedRes.put(h, new Pair<Integer, Double>(vm.getRam(), vm.getMips()));
        } else {
            //update the values for the host
            Pair<Integer, Double> e = hostWithReservedRes.get(h);
            hostWithReservedRes.put(h, new Pair<Integer, Double>(e.getKey() + vm.getRam(), e.getValue() + vm.getMips()));
        }
    }

    private void deleteResources(Host h, Vm vm) {
        if(!hostWithReservedRes.containsKey(h)) {
            return;
        }
        Pair<Integer, Double> e = hostWithReservedRes.get(h);
        hostWithReservedRes.put(h, new Pair<Integer, Double>(e.getKey() - vm.getRam(), e.getValue() - vm.getMips()));
    }
}
