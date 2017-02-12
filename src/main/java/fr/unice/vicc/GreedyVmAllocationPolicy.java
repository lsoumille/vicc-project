package fr.unice.vicc;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

import java.util.*;

/**
 * @author Lucas Martinez
 * @version 30/01/17.
 *
 * Role :
 * Overall Design and technical choices :
 * Complexity :
 */
public class GreedyVmAllocationPolicy extends VmAllocationPolicy{
    /** The map to track the server that host each running VM. */
    private Map<Vm,Host> hoster;

    public GreedyVmAllocationPolicy(List<? extends Host> list) {
        super(list);
        hoster = new HashMap<>();
    }

    @Override
    protected void setHostList(List<? extends Host> hostList) {
        super.setHostList(hostList);
        hoster = new HashMap<>();
    }

    public List<Host> getHostList() {
        //Sort the list according to the available mips
        List<Host> allHosts = super.getHostList();
        Collections.sort(allHosts, new Comparator<Host>() {
            @Override
            public int compare(Host host, Host t1) {
                return host.getTotalMips() < t1.getTotalMips() ? 1 : host.getTotalMips() > t1.getTotalMips() ? -1 : 0;
            }
        });
        return allHosts;
    }

    @Override
    public boolean allocateHostForVm(Vm vm) {
        for(Host h : getHostList()) {
            if (h.vmCreate(vm)) {
                    hoster.put(vm, h);
                    return true;
            }
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
}
