package rr;

import fcfs.ScheduleResult;
import model.Process;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RoundRobinSchedulerTest {

    @Test
    public void sampleCaseMatchesExpectedAverages() {
        List<Process> procs = List.of(
                new Process("P1", 1, 0, 5),
                new Process("P2", 1, 1, 3),
                new Process("P3", 1, 2, 1),
                new Process("P4", 1, 3, 2),
                new Process("P5", 1, 4, 3)
        );

        RoundRobinScheduler s = new RoundRobinScheduler(2);
        ScheduleResult r = s.run(procs);

        // Expected values computed by hand in analysis:
        // avg waiting = 5.8, avg turnaround = 8.6
        assertEquals(5.8, r.avgWaiting, 1e-6);
        assertEquals(8.6, r.avgTurnaround, 1e-6);

        // Check a couple of per-process completion times
        Process p1 = r.processes.stream().filter(p -> "P1".equals(p.getId())).findFirst().orElse(null);
        assertNotNull(p1);
        assertEquals(13, p1.getCompletionTime());

        Process p5 = r.processes.stream().filter(p -> "P5".equals(p.getId())).findFirst().orElse(null);
        assertNotNull(p5);
        assertEquals(14, p5.getCompletionTime());
    }
}
