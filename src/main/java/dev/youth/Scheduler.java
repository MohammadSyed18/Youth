package dev.youth;
import java.time.*; import java.util.*;
public final class Scheduler {
 public record Request(String id,int minutes,int priority,Instant deadline,Set<String> skills){public Request{if(id==null||id.isBlank()||minutes<=0||priority<1||priority>5)throw new IllegalArgumentException("invalid request");skills=Set.copyOf(skills);}}
 public record Slot(String volunteer,Instant start,Instant end,Set<String> skills){public Slot{if(!end.isAfter(start))throw new IllegalArgumentException("invalid slot");skills=Set.copyOf(skills);}}
 public record Assignment(String request,String volunteer,Instant start,Instant end){}
 public record Plan(List<Assignment> assignments,Map<String,String> unassigned){}
 public Plan schedule(List<Request> requests,List<Slot> slots){
  var ordered=requests.stream().sorted(Comparator.comparingInt(Request::priority).reversed().thenComparing(Request::deadline)).toList();var free=new ArrayList<>(slots);var out=new ArrayList<Assignment>();var missed=new LinkedHashMap<String,String>();
  for(var r:ordered){var chosen=free.stream().filter(s->s.skills().containsAll(r.skills())&&!s.start().plus(Duration.ofMinutes(r.minutes())).isAfter(s.end())&&!s.start().plus(Duration.ofMinutes(r.minutes())).isAfter(r.deadline())).min(Comparator.comparing(Slot::start)).orElse(null);if(chosen==null){missed.put(r.id(),"no eligible slot before deadline");continue;}var end=chosen.start().plus(Duration.ofMinutes(r.minutes()));out.add(new Assignment(r.id(),chosen.volunteer(),chosen.start(),end));free.remove(chosen);if(end.isBefore(chosen.end()))free.add(new Slot(chosen.volunteer(),end,chosen.end(),chosen.skills()));}
  return new Plan(List.copyOf(out),Map.copyOf(missed));
 }
}
