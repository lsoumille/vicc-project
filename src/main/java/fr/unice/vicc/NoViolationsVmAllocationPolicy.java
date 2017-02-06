package fr.unice.vicc;

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
 * Role :
 * Overall Design and technical choices :
 * Complexity :
 */
public class NoViolationsVmAllocationPolicy extends VmAllocationPolicy{
    /** The map to track the server that host each running VM. */
    private Map<Vm,Host> hoster;

    public NoViolationsVmAllocationPolicy(List<? extends Host> list) {
        super(list);
        hoster = new HashMap<>();
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
        List<Host> lHosts = super.getHostList();
        int cur = 0;

        for (; cur < lHosts.size(); ++cur){
            Host host = lHosts.get(cur);
            if (host.isSuitableForVm(vm)) { //look commented code of isSuitableForVm below
                if (host.vmCreate(vm)) {
                    hoster.put(vm, host);
                    return true;
                }
            }
        }

        return false;
    }

     /**
     * Checks if is suitable for vm.
     *
     * @param vm the vm
     * @return true, if is suitable for vm

    public boolean isSuitableForVm(Vm vm) {
        return (getVmScheduler().getPeCapacity() >= vm.getCurrentRequestedMaxMips()
                && getVmScheduler().getAvailableMips() >= vm.getCurrentRequestedTotalMips()
                && getRamProvisioner().isSuitableForVm(vm, vm.getCurrentRequestedRam()) && getBwProvisioner()
                .isSuitableForVm(vm, vm.getCurrentRequestedBw()));
    } */

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        if (host.vmCreate(vm)) {
            hoster.put(vm, host);
            return true;
        } return false;
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
        for (Vm v : hoster.keySet()) {
            if (v == vm) {
                return hoster.get(vm);
            }
        } return null;
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
}