package dev.youth;

public final class PlanReport {
    private String safe(String value) { return value.replace("|","\\|").replace("\n"," ").replace("<","&lt;").replace(">","&gt;"); }
    public String markdown(Scheduler.Plan plan) {
        var output = new StringBuilder("# Volunteer plan\n\n| Request | Volunteer | Start (UTC) | End (UTC) |\n| --- | --- | --- | --- |\n");
        for (var item : plan.assignments()) output.append("| ").append(safe(item.request())).append(" | ")
            .append(safe(item.volunteer())).append(" | ").append(item.start()).append(" | ").append(item.end()).append(" |\n");
        output.append("\n## Unassigned\n");
        new java.util.TreeMap<>(plan.unassigned()).forEach((id,reason) -> output.append("- ").append(safe(id)).append(": ").append(safe(reason)).append('\n'));
        var metrics = PlanMetrics.from(plan);
        return output.append("\nScheduled minutes: ").append(metrics.scheduledMinutes()).append('\n').toString();
    }
}
