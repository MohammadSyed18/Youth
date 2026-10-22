# Youth

A Java scheduler for matching requests to volunteer time slots. Checks skills, deadlines, duration, and priority, then reports what was assigned and what couldn't fit.

```sh
mvn test
mvn -q package
java -cp target/classes dev.youth.Main
```

The example is synthetic. The greedy scheduling strategy is deterministic, but doesn't guarantee the best possible assignment.
