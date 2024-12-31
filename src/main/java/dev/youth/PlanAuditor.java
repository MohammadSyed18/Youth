package dev.youth;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/** Independent validation of generated or externally supplied plans. */
public final class PlanAuditor {
    public record Issue(String request,String code,String detail) {}
    public List<Issue> audit(Scheduler.Plan plan,List<Scheduler.Request> requests,List<Scheduler.Slot> availability) {
        var definitions = new HashMap<String,Scheduler.Request>();
        for (var request : requests) {
            if (definitions.putIfAbsent(request.id(),request) != null) throw new IllegalArgumentException("duplicate request definition");
        }
        var issues = new ArrayList<Issue>();
        var assigned = new HashSet<String>();
        for (var item : plan.assignments()) {
            var request = definitions.get(item.request());
            if (request == null) { issues.add(new Issue(item.request(),"unknown","missing request definition")); continue; }
            if (!assigned.add(item.request())) issues.add(new Issue(item.request(),"duplicate","request assigned more than once"));
            if (!item.end().equals(item.start().plus(Duration.ofMinutes(request.minutes())))) issues.add(new Issue(item.request(),"duration","duration does not match request"));
            if (item.end().isAfter(request.deadline())) issues.add(new Issue(item.request(),"deadline","ends after deadline"));
            boolean covered = availability.stream().anyMatch(slot -> slot.volunteer().equals(item.volunteer())
                && !slot.start().isAfter(item.start()) && !slot.end().isBefore(item.end()) && slot.skills().containsAll(request.skills()));
            if (!covered) issues.add(new Issue(item.request(),"availability","no matching skilled availability"));
        }
        for (var request : requests) {
            if (!assigned.contains(request.id()) && !plan.unassigned().containsKey(request.id())) issues.add(new Issue(request.id(),"missing","neither assigned nor reported unassigned"));
            if (assigned.contains(request.id()) && plan.unassigned().containsKey(request.id())) issues.add(new Issue(request.id(),"contradiction","both assigned and unassigned"));
        }
        for (var key : plan.unassigned().keySet()) if (!definitions.containsKey(key)) issues.add(new Issue(key,"unknown","unassigned request is undefined"));
        for (var conflict : new ScheduleValidator().conflicts(plan)) issues.add(new Issue("", "overlap",conflict));
        return List.copyOf(issues);
    }
}
