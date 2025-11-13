package util;

import java.util.ArrayList;
import java.util.List;

import fcfs.ScheduleResult;
import model.Process;
import rr.RoundRobinScheduler;

public class MainRoundRobin {
    public static void main(String[] args) {
        List<Process> procs = new ArrayList<>();
        procs.add(new Process("P1", 1, 0, 5));
        procs.add(new Process("P2", 1, 1, 3));
        procs.add(new Process("P3", 1, 2, 1));
        procs.add(new Process("P4", 1, 3, 2));
        procs.add(new Process("P5", 1, 4, 3));

        RoundRobinScheduler s = new RoundRobinScheduler(2);
        ScheduleResult r = s.run(procs);

        // Print per-process table: id, arrival, burst, start, completion, turnaround, waiting
        List<String[]> rows = new ArrayList<>();
        String[] headers = new String[] { "ID", "Arr", "Burst", "Start", "Completion", "Turnaround", "Waiting" };
        for (Process p : r.processes) {
            int turnaround = p.getCompletionTime() - p.getArrivalTime();
            int waiting = turnaround - p.getOriginalBurst();
            rows.add(new String[] {
                    p.getId(),
                    Integer.toString(p.getArrivalTime()),
                    Integer.toString(p.getOriginalBurst()),
                    Integer.toString(p.getStartTime()),
                    Integer.toString(p.getCompletionTime()),
                    Integer.toString(turnaround),
                    Integer.toString(waiting)
            });
        }

        TablePrinter.printTable(rows, headers);

        System.out.printf("Average Turnaround: %.2f\n", r.avgTurnaround);
        System.out.printf("Average Waiting:    %.2f\n", r.avgWaiting);
        System.out.printf("CPU Utilization:    %.2f%%\n", r.cpuUtilizationPercent);
        System.out.println("Gantt:");
        GanttRenderer.render(r.gantt);
    }
}
