package fr.unice.vicc;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lucas on 30/01/17.
 *
 * Role : fault-tolerant to node failures
 * Overall Design and technical choices : when we create a VM we check that
 * there's no other VM with the same affinity on the same host
 * Complexity : O(n.m) with n number of hosts and m number of VMs
 */
public class AntiAffinityVmAllocationPolicy extends VmAllocationPolicy {

    /** The map to track the server that host each running VM. */
    private Map<Vm,Host> hoster;

    public AntiAffinityVmAllocationPolicy(List<? extends Host> list) {
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
        //Get the VM id
        int id = vm.getId();
        //Get the interval value that is equivalent to affinity
        int interval = id / 100;
        for(Host h : getHostList()) {
            boolean isSuitable = true;
            for(Vm v : h.getVmList()) {
                if ((v.getId() / 100) == interval) {
                    isSuitable = false;
                    break;
                }
            }
            //if the host has not a VM in the same interval then create it
            if(isSuitable && h.vmCreate(vm)) {
                hoster.put(vm, h);
                return true;
            }
        }
        //Default
        return false;
    }

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        //Forced the VM to the host
        //return true if the vm is created
        if (host.vmCreate(vm)) {
            hoster.put(vm, host);
            return true;
        }
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
