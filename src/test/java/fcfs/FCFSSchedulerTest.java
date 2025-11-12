package fcfs;

import model.Process;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FCFSSchedulerTest {

    @Test
    public void basicOrderAndMetrics() {
        List<Process> procs = List.of(
                new Process("A", 1, 0, 3),
                new Process("B", 1, 1, 2),
                new Process("C", 1, 2, 1));

        FCFSScheduler s = new FCFSScheduler(0);
        ScheduleResult r = s.run(procs);

        assertEquals(6, r.makespan);
        assertEquals(11.0 / 3.0, r.avgTurnaround, 1e-6);
        assertEquals(1.6666666, r.avgWaiting, 1e-5);
        // order start times
        assertEquals(0, r.processes.get(0).getStartTime());
        assertEquals(3, r.processes.get(1).getStartTime());
        assertEquals(5, r.processes.get(2).getStartTime());
    }

    @Test
    public void handlesIdleGaps() {
        List<Process> procs = List.of(
                new Process("A", 1, 0, 2),
                new Process("B", 1, 5, 3));
        FCFSScheduler s = new FCFSScheduler(0);
        ScheduleResult r = s.run(procs);
        assertEquals(8, r.makespan);
        assertEquals((2 + (8 - 5)) / 2.0, r.avgTurnaround, 1e-6);
        assertEquals(0.0 + (5 - 5), r.avgWaiting, 1e-6);
    }

    @Test
    public void simultaneousArrivals() {
        List<Process> procs = List.of(
                new Process("A", 1, 0, 4),
                new Process("B", 1, 0, 1),
                new Process("C", 1, 0, 2));
        FCFSScheduler s = new FCFSScheduler(0);
        ScheduleResult r = s.run(procs);
        // FCFS should keep input order for same arrival time
        assertEquals("A", r.processes.get(0).getId());
        assertEquals("B", r.processes.get(1).getId());
        assertEquals("C", r.processes.get(2).getId());
    }
}
