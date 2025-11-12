package fcfs;

import model.Process;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * First-Come First-Serve scheduler (non-preemptive) with configurable context
 * switch overhead
 * and extra reporting (Gantt timeline, averages, CPU utilization).
 */
public class FCFSScheduler implements Scheduler {
    private final int contextSwitchTime;

    public FCFSScheduler() {
        this(0);
    }

    public FCFSScheduler(int contextSwitchTime) {
        if (contextSwitchTime < 0)
            throw new IllegalArgumentException("contextSwitchTime >= 0");
        this.contextSwitchTime = contextSwitchTime;
    }

    public ScheduleResult run(List<Process> processes) {
        // Defensive copy and sort by arrival
        List<Process> procs = new ArrayList<>(processes);
        procs.sort(Comparator.comparingInt(Process::getArrivalTime));

        int time = 0;
        long totalBusy = 0;
        List<GanttEntry> gantt = new ArrayList<>();

        for (int i = 0; i < procs.size(); i++) {
            Process p = procs.get(i);

            if (p.getArrivalTime() > time) {
                // idle
                gantt.add(new GanttEntry("idle", time, p.getArrivalTime()));
                time = p.getArrivalTime();
            }

            // context switch before starting this process (if it's not immediate
            // continuation)
            if (!gantt.isEmpty() && contextSwitchTime > 0) {
                int csStart = time;
                time += contextSwitchTime;
                gantt.add(new GanttEntry("CS", csStart, time));
            }

            int start = time;
            int run = p.getRemainingBurst();
            p.runFor(run, start);
            int end = p.getCompletionTime();

            gantt.add(new GanttEntry(p.getId(), start, end));
            totalBusy += (end - start);
            time = end;
        }

        // Calculate metrics
        double avgTurnaround = procs.stream()
                .mapToInt(pr -> pr.getCompletionTime() - pr.getArrivalTime())
                .average().orElse(0.0);
        double avgWaiting = procs.stream()
                .mapToInt(pr -> pr.getStartTime() - pr.getArrivalTime())
                .average().orElse(0.0);
        double cpuUtil = time == 0 ? 0.0 : (100.0 * totalBusy / time);

        return new ScheduleResult(procs, gantt, avgTurnaround, avgWaiting, cpuUtil, time);
    }
}
