package rr;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import fcfs.GanttEntry;
import fcfs.ScheduleResult;
import model.Process;

/**
 * Round Robin scheduler implementation. Returns a ScheduleResult (reusing the
 * fcfs.ScheduleResult/GanttEntry types) so output formatting/tests can be
 * shared.
 */
public class RoundRobinScheduler {
    private final int quantum;

    public RoundRobinScheduler(int quantum) {
        if (quantum <= 0)
            throw new IllegalArgumentException("quantum > 0");
        this.quantum = quantum;
    }

    public ScheduleResult run(List<Process> processes) {
        List<Process> all = new ArrayList<>(processes);
        // Sort by arrival so we can walk arrivals efficiently
        all.sort((a, b) -> Integer.compare(a.getArrivalTime(), b.getArrivalTime()));

        LinkedList<Process> ready = new LinkedList<>();
        List<GanttEntry> gantt = new ArrayList<>();

        int n = all.size();
        int finished = 0;
        int time = 0;
        long totalBusy = 0;

        int nextArrIdx = 0;
        // add initial arrivals at time 0
        while (nextArrIdx < n && all.get(nextArrIdx).getArrivalTime() <= time) {
            ready.add(all.get(nextArrIdx++));
        }

        while (finished < n) {
            if (ready.isEmpty()) {
                // jump to next arrival if any
                if (nextArrIdx < n) {
                    time = Math.max(time, all.get(nextArrIdx).getArrivalTime());
                    while (nextArrIdx < n && all.get(nextArrIdx).getArrivalTime() <= time) {
                        ready.add(all.get(nextArrIdx++));
                    }
                    continue;
                } else {
                    break; // nothing left
                }
            }

            Process cur = ready.removeFirst();
            int start = time;
            int run = Math.min(quantum, cur.getRemainingBurst());
            cur.runFor(run, start);
            int end = (cur.isFinished() ? cur.getCompletionTime() : start + run);

            gantt.add(new GanttEntry(cur.getId(), start, end));
            totalBusy += (end - start);
            time = end;

            // add newly arrived processes that arrived during this slice
            while (nextArrIdx < n && all.get(nextArrIdx).getArrivalTime() <= time) {
                ready.add(all.get(nextArrIdx++));
            }

            if (cur.isFinished()) {
                finished++;
            } else {
                // not finished -> re-enqueue
                ready.addLast(cur);
            }
        }

        double avgTurnaround = all.stream()
                .mapToInt(p -> p.getCompletionTime() - p.getArrivalTime())
                .average().orElse(0.0);
        // preemptive -> waiting = turnaround - original burst
        double avgWaiting = all.stream()
                .mapToInt(p -> (p.getCompletionTime() - p.getArrivalTime()) - p.getOriginalBurst())
                .average().orElse(0.0);

        double cpuUtil = time == 0 ? 0.0 : (100.0 * totalBusy / time);
        return new ScheduleResult(all, gantt, avgTurnaround, avgWaiting, cpuUtil, time);
    }
}
