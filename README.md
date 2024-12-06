# youth

a Java scheduler for matching requests to volunteer time slots. checks skills, deadlines, duration, and priority, then reports what was assigned and what couldn't fit.

```sh
mvn test
mvn -q package
java -cp target/classes dev.youth.Main
```

the example is synthetic. the greedy scheduling strategy is deterministic, but doesn't guarantee the best possible assignment.
