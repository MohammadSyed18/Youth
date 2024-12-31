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
