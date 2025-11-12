package fcfs;

import model.Process;
import java.util.List;

public interface Scheduler {
    ScheduleResult run(List<Process> processes);
}
