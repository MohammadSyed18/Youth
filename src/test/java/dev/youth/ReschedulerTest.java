package dev.youth;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReschedulerTest {
    @Test void preservesBookingsAndFillsOnlyUnreservedTime() {
        var start=Instant.parse("2026-07-01T09:00:00Z");
        var requests=List.of(new Scheduler.Request("a",30,3,start.plusSeconds(7200),Set.of("math")),new Scheduler.Request("b",30,5,start.plusSeconds(7200),Set.of("math")));
        var slots=List.of(new Scheduler.Slot("v",start,start.plusSeconds(7200),Set.of("math")));
        var confirmed=List.of(new Scheduler.Assignment("a","v",start.plusSeconds(1800),start.plusSeconds(3600)));
        var plan=new Rescheduler().rebuild(requests,slots,confirmed);
        assertTrue(plan.assignments().contains(confirmed.getFirst()));
        assertEquals(2,plan.assignments().size());
        assertTrue(new PlanAuditor().audit(plan,requests,slots).isEmpty());
        assertTrue(new PlanReport().markdown(plan).contains("Scheduled minutes: 60"));
    }
    @Test void rejectsUnskilledConfirmedAssignments() {
        var start=Instant.parse("2026-07-01T09:00:00Z");
        var requests=List.of(new Scheduler.Request("a",30,3,start.plusSeconds(7200),Set.of("math")));
        var slots=List.of(new Scheduler.Slot("v",start,start.plusSeconds(7200),Set.of()));
        var confirmed=List.of(new Scheduler.Assignment("a","v",start,start.plusSeconds(1800)));
        assertThrows(IllegalArgumentException.class,()->new Rescheduler().rebuild(requests,slots,confirmed));
    }
}
