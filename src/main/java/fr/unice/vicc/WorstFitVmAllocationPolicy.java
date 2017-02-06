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
 * Role : Perform load balancing using a worst fit algorithm.
 * Overall Design and technical choices : We create a map with <HostID, [MIPS, RAM]> containing all the hosts
 * Then we pick the host with the most resources available thanks to the getBiggestHost function and we put a VM
 * We iterate on the hosts with the getBiggestHost method in a infinite loop
 * Complexity : O(n) with n the number of hosts
 */
public class WorstFitVmAllocationPolicy extends VmAllocationPolicy{
    /** The map to track the server that host each running VM. */
    private Map<Vm,Host> hoster;

    public WorstFitVmAllocationPolicy(List<? extends Host> list) {
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

    //return host ID with biggest values for mips and ram
    private int getBiggestHost(Map<Integer,ArrayList> map) {
        int maxMIPS = 0;
        int maxRAM = 0;
        int biggerHost = 0;

        //get the host with biggest values for mips and ram
        for (Map.Entry<Integer,ArrayList> m : map.entrySet()) {
            int currHost = m.getKey();
            ArrayList<Integer> values = m.getValue();
            if (values.get(0) > maxMIPS || values.get(1) > maxRAM) {
                maxMIPS = values.get(0);
                maxRAM = values.get(1);
                biggerHost = currHost;
            }
        }
        return biggerHost;
    }

    @Override
    public boolean allocateHostForVm(Vm vm) {
        Map<Integer,ArrayList> map = new HashMap<>();
        List<Host> lHosts = super.getHostList();

        //create a map <HostID , [MIPS,RAM]>
        for (Host curH : lHosts) {
            ArrayList<Integer> l = new ArrayList<>();
            l.add((int) curH.getAvailableMips());
            l.add(curH.getRamProvisioner().getAvailableRam());
            map.put(curH.getId(),l);
        }

        //we get the host with biggest mips and ram
        int curHost = getBiggestHost(map);
        Host host = lHosts.get(curHost);

        while (true){
            if (host.vmCreate(vm)) {
                hoster.put(vm, host);
                return true;
            } else {
                //we get the next host in the map with biggest mips and ram
                map.remove(curHost);
                curHost = getBiggestHost(map);
                host = lHosts.get(curHost);
            }
        }
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
