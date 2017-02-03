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