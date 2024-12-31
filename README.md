# youth

a Java scheduler for matching requests to volunteer time slots. checks skills, deadlines, duration, and priority, then reports what was assigned and what couldn't fit.

```sh
mvn test
mvn -q package
java -cp target/classes dev.youth.Main
```

the example is synthetic. the greedy scheduling strategy is deterministic, but doesn't guarantee the best possible assignment.

## Implementation details

Assignments subtract reserved intervals from every overlapping availability entry for the selected volunteer. Eligible slots are balanced by assigned minutes, and duplicate request IDs are rejected.

## Preserving bookings and auditing plans

[Rescheduler](src/main/java/dev/youth/Rescheduler.java) preserves confirmed assignments while reserving their intervals across every overlapping availability entry. It rejects missing requests, duplicate commitments, conflicts, incorrect durations, missed deadlines, and unavailable or unskilled commitments before scheduling remaining work.

[PlanAuditor](src/main/java/dev/youth/PlanAuditor.java) independently checks generated or external plans for overlap, skill coverage, duration, deadlines, missing requests, and contradictory assigned/unassigned status. [PlanReport](src/main/java/dev/youth/PlanReport.java) renders an escaped Markdown plan and workload totals.

The greedy scheduler still does not guarantee a global optimum. Replanning balances newly assigned minutes; confirmed historical workload is reserved as time rather than included in its initial fairness score. Tests cover preserved bookings, remaining capacity, independent audit, and skill rejection.
