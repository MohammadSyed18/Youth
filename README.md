# Youth

A Java scheduling engine for matching time-bounded requests to eligible volunteer slots. It respects skills, deadlines, duration, and priority, reuses leftover availability, merges compatible slots, validates conflicts, reports workload metrics, and explains unassigned requests.

Run tests with `mvn test` and the synthetic example with `mvn -q package && java -cp target/classes dev.youth.Main`. The current greedy strategy is deterministic but does not guarantee a globally optimal assignment.
