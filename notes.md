# Notes about the project

## The team

- Lucas Martinez: lucas.martinez1@etu.unice.fr
- Cindy Najjar: cindy.najjar@etu.unice.fr
- Lucas Soumille: lucas.soumille@etu.unice.fr

## Comments

## Fault-tolerance for replicated  applications ##

- What is the impact of such an algorithm (antiAffinity) over the cluster hosting capacity ? Why ?

This algorithm implies that there will be more hosts turned on because of the need of fault-tolerance, also we can assume that there will be less capacity because a lot of the cluster will be used for replicas.

## Load balancing ##

- Develop a scheduler that performs load balancing using a next fit algorithm (flag nextFit). You should observe fewer penalties with regards to the naive scheduler.

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