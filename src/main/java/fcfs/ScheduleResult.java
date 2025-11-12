package fcfs;

import model.Process;
import java.util.List;

public class ScheduleResult {
    public final List<Process> processes; // in start order
    public final List<GanttEntry> gantt;
    public final double avgTurnaround;
    public final double avgWaiting;
    public final double cpuUtilizationPercent;
    public final int makespan;

    public ScheduleResult(List<Process> processes, List<GanttEntry> gantt, double avgTurnaround,
            double avgWaiting, double cpuUtilizationPercent, int makespan) {
        this.processes = processes;
        this.gantt = gantt;
        this.avgTurnaround = avgTurnaround;
        this.avgWaiting = avgWaiting;
        this.cpuUtilizationPercent = cpuUtilizationPercent;
        this.makespan = makespan;
    }
}
