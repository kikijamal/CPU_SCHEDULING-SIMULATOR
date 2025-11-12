package model;

public class Process {
    private final String id;
    private final int priority;
    private final int arrivalTime;
    private final int originalBurst;
    private int remainingBurst;
    private int startTime = -1;
    private int completionTime = -1;

    public Process(String id, int priority, int arrivalTime, int burst) {
        this.id = id;
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.originalBurst = burst;
        this.remainingBurst = burst;
    }

    public String getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getRemainingBurst() {
        return remainingBurst;
    }

    public int getOriginalBurst() {
        return originalBurst;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public boolean isFinished() {
        return remainingBurst == 0;
    }

    public void runFor(int quantum, int currentTime) {
        if (startTime == -1)
            startTime = currentTime;
        int run = Math.min(quantum, remainingBurst);
        remainingBurst -= run;
        if (remainingBurst == 0)
            completionTime = currentTime + run;
    }

    @Override
    public String toString() {
        return id + "(prio=" + priority + ", arr=" + arrivalTime + ", rem=" + remainingBurst + ")";
    }
}
