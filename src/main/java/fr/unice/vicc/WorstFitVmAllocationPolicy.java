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
 * Role :
 * Overall Design and technical choices :
 * Complexity :
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
    public int biggestHost(Map<Integer,ArrayList> map) {
        int max0 = 0;
        int max1 = 0;
        int biggerHost = 0;

        //get the host with biggest values for mips and ram
        for (Map.Entry<Integer,ArrayList> m : map.entrySet()) {
            int key = m.getKey();
            ArrayList<Integer> value = m.getValue();
            if (value.get(0) > max0 || value.get(1) > max1) {
                max0 = value.get(0);
                max1 = value.get(1);
                biggerHost = key;
            }
        }
        return biggerHost;
    }

    @Override
    public boolean allocateHostForVm(Vm vm) {
        Map<Integer,ArrayList> map = new HashMap<>();
        List<Host> lHosts = super.getHostList();
        boolean alloc = false;

        //create a map <HostID , [MIPS,RAM]>
        for (Host curH : lHosts) {
            ArrayList<Integer> l = new ArrayList<>();
            l.add((int) curH.getAvailableMips());
            l.add(curH.getRamProvisioner().getAvailableRam());
            map.put(curH.getId(),l);
        }

        //we get the host with biggest mips and ram
        int curHost = biggestHost(map);

        do {
            Host host = lHosts.get(curHost);
            if (host.vmCreate(vm)) {
                hoster.put(vm, host);
                alloc = true;
            } else {
                //we get the next host in the map with biggest mips and ram
                map.remove(curHost);
                curHost = biggestHost(map);
                host = lHosts.get(curHost);
            }
        } while(alloc==false);

        return alloc;
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
