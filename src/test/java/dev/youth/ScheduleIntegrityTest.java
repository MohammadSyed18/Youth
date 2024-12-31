package dev.youth;
import static org.junit.jupiter.api.Assertions.*;
import java.time.*;import java.util.*;import org.junit.jupiter.api.Test;
class ScheduleIntegrityTest {
 @Test void overlappingAvailabilityCannotDoubleBook(){
  Instant start=Instant.parse("2024-12-01T09:00:00Z");
  var slots=List.of(new Scheduler.Slot("v",start,start.plusSeconds(3600),Set.of("math")),new Scheduler.Slot("v",start,start.plusSeconds(3600),Set.of("math","science")));
  var requests=List.of(new Scheduler.Request("a",45,5,start.plusSeconds(7200),Set.of("math")),new Scheduler.Request("b",45,4,start.plusSeconds(7200),Set.of("math")));
  var plan=new Scheduler().schedule(requests,slots);
  assertEquals(1,plan.assignments().size());assertTrue(new ScheduleValidator().conflicts(plan).isEmpty());
 }
 @Test void duplicateIdsRejected(){Instant now=Instant.now();var request=new Scheduler.Request("a",30,3,now.plusSeconds(3600),Set.of());assertThrows(IllegalArgumentException.class,()->new Scheduler().schedule(List.of(request,request),List.of()));}
}
