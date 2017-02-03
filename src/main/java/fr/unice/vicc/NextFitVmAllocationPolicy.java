package fr.unice.vicc;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lucas Martinez
 * @version 30/01/17.
 *
 * Role : Perform load balancing using a next fit algorithm.
 * Overall Design and technical choices : Store the last host id where the VM was allocated.
 * The search for the next allocation starts at host id+1.
 * We start again at id = 0 if we arrive at the last host and the allocation is not possible.
 * Complexity : O(n) with n the number of hosts.
 */
public class NextFitVmAllocationPolicy extends VmAllocationPolicy {
    /** The map to track the server that host each running VM. */
    private Map<Vm,Host> hoster;

    public NextFitVmAllocationPolicy(List<? extends Host> list) {
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

    private static int counter = 0;

    @Override
    public boolean allocateHostForVm(Vm vm) {
        List<Host> lHosts = super.getHostList();
        int cur = counter;
        /* boolean fit = false;
        Host curHost;

        //Next fit algo
        do {
            curHost = lHosts.get(cur);
            if (curHost.vmCreate(vm) == true) {
                hoster.put(vm,curHost);
                fit = true;

                if (cur == lHosts.size() - 1) {
                    cur = 0;
                    counter = cur+1;
                } else {
                    counter= cur+1;
                } break;
            } cur++;

            if (cur > lHosts.size()-1) {
                cur = 0;
            }
        } while (fit == false || cur < lHosts.size());

        return fit; */

        for (; cur < lHosts.size(); ++cur){
            Host host = lHosts.get(cur);
            if (host.vmCreate(vm)){
                hoster.put(vm, host);
                if (cur == lHosts.size() - 1){
                    counter = 1;
                } else {
                    counter = cur + 1;
                }
                //if the vm is created return true
                return true;
            }
        }

        return false;
    }

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