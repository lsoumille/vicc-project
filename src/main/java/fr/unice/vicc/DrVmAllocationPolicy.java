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
 * Role : Ensures fault tolerance to a single switch failure
 * Overall Design and technical choices : Create a VM on one host that is in G4 (MIPS 3720) and the next one on another host
 * that is in G5 (MIPS 5320)
 * Complexity : O(n) with n the number of hosts
 */
public class DrVmAllocationPolicy extends VmAllocationPolicy {

    /** The map to track the server that host each running VM. */
    private Map<Vm,Host> hoster;
    /** Boolean to know what is the last swith used*/
    private boolean previousG4 = false;

    private final double MIPSG4 = 3720.0;
    private final double MIPSG5 = 5320.0;

    public DrVmAllocationPolicy(List<? extends Host> list) {
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
        for(Host h : getHostList()) {
            double mips = h.getTotalMips();
            if ((previousG4 && mips == MIPSG5 && h.vmCreate(vm)) || (!previousG4 && mips == MIPSG4 && h.vmCreate(vm))) {
                //VM created
                previousG4 = !previousG4;
                hoster.put(vm, h);
                return true;
            }
        }
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
