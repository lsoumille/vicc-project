# Notes about the project

## The team

- Lucas Martinez: lucas.martinez1@etu.unice.fr
- Cindy Najjar: cindy.najjar@etu.unice.fr
- Lucas Soumille: lucas.soumille@etu.unice.fr

## Comments

## Naive ##

##### Principle

The scheduler places each VM to the first suitable host.

##### Output

Incomes:    12398,59€
Penalties:  402,16€
Energy:     2645,63€
Revenue:    9350,80€

## Fault-tolerance for replicated  applications ##

##### Principle

Scheduler that supports the fault tolerance for replicated applications. Before placing the VM, we check if any other VM in the same interval is already hosted on this node.

##### Output

Incomes:    12398,59€
Penalties:  200,95€
Energy:     2688,44€
Revenue:    9509,21€

- What is the impact of such an algorithm (antiAffinity) over the cluster hosting capacity ? Why ?

This algorithm implies that there will be more hosts turned on because of the need of fault-tolerance, also we can assume that there will be less capacity because a lot of the cluster will be used for replicas.


## Preparing for disaster recovery

##### Principle

Scheduler that supports the fault tolerance of switches. We assume that the host with 3720 mips are on a different network than the host with 5320 mips. To achieve this objective, we put a VM on a host in the first network, the next one in the second network and so on. 

##### Output

Incomes:    12398,59€
Penalties:  2223,24€
Energy:     2649,07€
Revenue:    7526,28€

## Fault-tolerance for standalone VMs ##

##### Principle

Scheduler that supports the fault tolerance without replica. When a VM is assigned on a node, another space is booked on another node in order to restart the VM in case of failure. We need to figure out for each new VM if a host is suitable for it that's why we store the booked resources in a map.    


##### Output

Incomes:    12398,59€
Penalties:  161,72€
Energy:     2911,59€
Revenue:    9325,28€

- How can we report the infrastructure load in that particular context ?

In a normal state, the load is calculated according to the started VMs. In our case, we need to consider also the reserved resources for the fault tolerant VMs. So the infrastructure load must be reported like the sum of resources of the started VMs and the reserved resources.


## Load balancing ##

- Develop a scheduler that performs load balancing using a next fit algorithm (flag nextFit). You should observe fewer penalties with regards to the naive scheduler.

For the next fit algorithm, the idea is to store the last host ID where the VM was allocated. In our implementation, the search for the next allocation starts at host ID+1. If we arrive at the last host and the allocation is not possible, we start again at the beginning of the hosts list (at host ID=0).

With the naive method, the output for all days is the following:

Incomes:    12398,59€
Penalties:  402,16€
Energy:     2645,63€
Revenue:    9350,80€

With the scheduler performing load balancing and using a next fit algorithm, the output for all days is:

Incomes:    12398,59€
Penalties:  208,26€
Energy:     3288,96€
Revenue:    8901,37€

We observe indeed that we have fewer penalties with the second scheduler (~200€ fewer).

- Develop another algorithm based on a worst fit algorithm (worstFit flag) that balances with regards to both RAM and mips. Justify the method you choosed to consider the two dimensions and an evaluation metric.

To consider both RAM and mips, we used a Map <HostID,[AvailableMIPS,AvailableRAM]>. This map is filled with all hosts in the hosts list.
What we do in our algorithm is that we check for the host with highest mips or RAM. This allows less penalties and therefore more revenues than if we had to pick the host with highest mips and RAM. The "extraction" of the identifier of the host with biggest available mips and RAM is done through the getBiggestHost() method.

- Which algorithm performs the best in terms of reducing the SLA violation? Why ?

It is the worst fit algorithm that performs the best in terms of reducing the SLA violation, because this algorithm assigns each virtual machine to a physical machine with the greatest free capacity (mips and RAM).
It gives a better load balanced allocation than with the next fit algorithm.

##### Outputs

Next Fit :
Incomes:    12398,59€
Penalties:  208,26€
Energy:     3288,96€
Revenue:    8901,37€

Worst Fit :
Incomes:    12398,59€
Penalties:  6,06€
Energy:     3264,77€
Revenue:    9127,77€

## Performance satisfaction ##

The idea here is to ensure there can be no SLA violations. For the implementation of the algorithm, we used the isSuitableForVm() function which returns true if a host is suitable for a vm, regarding its parameters like processing elements capacity and available mips. If the host is suitable for the vm, we do the allocation. We obtained the following output :

##### Output

Incomes:    12398,59€
Penalties:  0,00€
Energy:     2868,74€
Revenue:    9529,85€

## Energy-efficient scheduler ##

##### Principle

Scheduler that reduces the overall energy consumption. We pack all the VMs in the minimum number of nodes. We try to put the VM on the node with the least total mips because they consume less energy. 

##### Output

Incomes:    12398,59€
Penalties:  1413,50€
Energy:     2604,30€
Revenue:    8380,79€

## Greedy scheduler ##

##### Principle

Scheduler that maximizes revenues. The first objective is energy savings so we pack the VM on the minimum number of nodes like the energy efficient schedulers. We've seen that the energy-efficient scheduler has a lot of penalties because when the VM client asked for more resources the host (with small amount of mips) was already overloaded. The second objective here is to avoid the overload in order to avoid the penalties. The solution is to put the VM on the node with the max total mips amount instead of the minimum number.   

##### Output

Incomes:    12398,59€
Penalties:  31,57€
Energy:     2686,64€
Revenue:    9680,38€

##### Complexity

The VM placement method is like the naive one so the complexity is O(n) with n the number of hosts. But the previous step was to sort the host list to obtain a descending order on the mips value. The Java method sort guarantees a complexity of O(nlogn) with n the number of hosts.
The total complexity for this algorithm is O(n + nlogn) ≈ O(nlogn)

## Feedback on the course ##

The lectures and the project were interesting but we were not fond of the paper explanation and oral exam especially when it isn't really valued in the final grade, doing a full lecture only for these presentations was really exhausting and we couldn't be focused all the way through. Too bad we were not able to do the Amazon lab because that would have been really interesting.