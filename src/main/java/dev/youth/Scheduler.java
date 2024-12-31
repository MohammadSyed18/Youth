package dev.youth;
import java.time.*; import java.util.*;
public final class Scheduler {
 public record Request(String id,int minutes,int priority,Instant deadline,Set<String> skills){public Request{if(id==null||id.isBlank()||minutes<=0||priority<1||priority>5)throw new IllegalArgumentException("invalid request");skills=Set.copyOf(skills);}}
 public record Slot(String volunteer,Instant start,Instant end,Set<String> skills){public Slot{if(!end.isAfter(start))throw new IllegalArgumentException("invalid slot");skills=Set.copyOf(skills);}}
 public record Assignment(String request,String volunteer,Instant start,Instant end){}
 public record Plan(List<Assignment> assignments,Map<String,String> unassigned){}
 public Plan schedule(List<Request> requests,List<Slot> slots){
  if(requests.stream().map(Request::id).distinct().count()!=requests.size())throw new IllegalArgumentException("duplicate request id");
  var ordered=requests.stream().sorted(Comparator.comparingInt(Request::priority).reversed().thenComparing(Request::deadline).thenComparing(Request::id)).toList();var free=new ArrayList<>(slots);var out=new ArrayList<Assignment>();var missed=new LinkedHashMap<String,String>();var workload=new HashMap<String,Integer>();
  for(var r:ordered){var chosen=free.stream().filter(s->s.skills().containsAll(r.skills())&&!s.start().plus(Duration.ofMinutes(r.minutes())).isAfter(s.end())&&!s.start().plus(Duration.ofMinutes(r.minutes())).isAfter(r.deadline())).min(Comparator.<Slot>comparingInt(s->workload.getOrDefault(s.volunteer(),0)).thenComparing(Slot::start).thenComparing(Slot::volunteer)).orElse(null);if(chosen==null){missed.put(r.id(),"no eligible slot before deadline");continue;}var end=chosen.start().plus(Duration.ofMinutes(r.minutes()));out.add(new Assignment(r.id(),chosen.volunteer(),chosen.start(),end));workload.merge(chosen.volunteer(),r.minutes(),Integer::sum);free=subtract(free,chosen.volunteer(),chosen.start(),end);}
  return new Plan(List.copyOf(out),Map.copyOf(missed));
 }
 private ArrayList<Slot> subtract(List<Slot> slots,String volunteer,Instant start,Instant end){
  var remaining=new ArrayList<Slot>();
  for(var slot:slots){
   if(!slot.volunteer().equals(volunteer)||!slot.start().isBefore(end)||!start.isBefore(slot.end())){remaining.add(slot);continue;}
   if(slot.start().isBefore(start))remaining.add(new Slot(volunteer,slot.start(),start,slot.skills()));
   if(end.isBefore(slot.end()))remaining.add(new Slot(volunteer,end,slot.end(),slot.skills()));
  }
  return remaining;
 }
}
