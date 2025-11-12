package fcfs;

public class GanttEntry {
    public final String id;
    public final int start;
    public final int end;

    public GanttEntry(String id, int start, int end) {
        this.id = id;
        this.start = start;
        this.end = end;
    }
}
