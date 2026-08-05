package dev.youth; import java.time.*; import java.util.*;
public class Main { public static void main(String[] args){var now=Instant.parse("2030-01-01T09:00:00Z");var p=new Scheduler().schedule(List.of(new Scheduler.Request("mentor",45,5,now.plusSeconds(7200),Set.of("math"))),List.of(new Scheduler.Slot("volunteer-a",now,now.plusSeconds(3600),Set.of("math"))));System.out.println(p);}}
