package dev.youth;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/** Preserve confirmed assignments while rebuilding the remaining volunteer plan. */
public final class Rescheduler {
    public Scheduler.Plan rebuild(List<Scheduler.Request> requests, List<Scheduler.Slot> slots,
                                  List<Scheduler.Assignment> confirmed) {
        var byId = new HashMap<String,Scheduler.Request>();
        for (var request : requests) {
            if (byId.putIfAbsent(request.id(),request) != null) throw new IllegalArgumentException("duplicate request");
        }
        var reserved = new ArrayList<Scheduler.Assignment>();
        var seen = new HashSet<String>();
        for (var assignment : confirmed) {
            var request = byId.get(assignment.request());
            if (request == null || !seen.add(assignment.request())) throw new IllegalArgumentException("unknown or duplicate confirmed request");
            if (!assignment.end().equals(assignment.start().plus(Duration.ofMinutes(request.minutes())))
                    || assignment.end().isAfter(request.deadline())) throw new IllegalArgumentException("confirmed assignment violates duration or deadline");
            boolean available = slots.stream().anyMatch(slot -> slot.volunteer().equals(assignment.volunteer())
                && !slot.start().isAfter(assignment.start()) && !slot.end().isBefore(assignment.end())
                && slot.skills().containsAll(request.skills()));
            if (!available) throw new IllegalArgumentException("confirmed assignment lacks eligible availability");
            reserved.add(assignment);
        }
        if (!new ScheduleValidator().conflicts(new Scheduler.Plan(reserved,Map.of())).isEmpty()) {
            throw new IllegalArgumentException("confirmed assignments overlap");
        }
        var remainingSlots = new ArrayList<>(slots);
        for (var assignment : reserved) remainingSlots = subtract(remainingSlots,assignment);
        var remainingRequests = requests.stream().filter(request -> !seen.contains(request.id())).toList();
        var planned = new Scheduler().schedule(remainingRequests,remainingSlots);
        reserved.addAll(planned.assignments());
        reserved.sort(Comparator.comparing(Scheduler.Assignment::start).thenComparing(Scheduler.Assignment::request));
        return new Scheduler.Plan(List.copyOf(reserved),planned.unassigned());
    }
    private ArrayList<Scheduler.Slot> subtract(List<Scheduler.Slot> slots,Scheduler.Assignment assignment) {
        var result = new ArrayList<Scheduler.Slot>();
        for (var slot : slots) {
            if (!slot.volunteer().equals(assignment.volunteer()) || !slot.start().isBefore(assignment.end())
                    || !assignment.start().isBefore(slot.end())) { result.add(slot); continue; }
            if (slot.start().isBefore(assignment.start())) result.add(new Scheduler.Slot(slot.volunteer(),slot.start(),assignment.start(),slot.skills()));
            if (assignment.end().isBefore(slot.end())) result.add(new Scheduler.Slot(slot.volunteer(),assignment.end(),slot.end(),slot.skills()));
        }
        return result;
    }
}
