package util;

import fcfs.GanttEntry;

import java.util.List;

public class GanttRenderer {
    public static void render(List<GanttEntry> gantt) {
        if (gantt.isEmpty()) {
            System.out.println("<empty gantt>");
            return;
        }
        int makespan = gantt.get(gantt.size() - 1).end;

        // Build a simple timeline with one char per unit time (suitable for small
        // makespans)
        int scale = Math.max(1, makespan / 120); // cap width ~120
        StringBuilder line = new StringBuilder();
        for (GanttEntry e : gantt) {
            int len = Math.max(1, (e.end - e.start) / scale);
            String bar = repeat(getLabel(e.id), len);
            line.append(bar);
        }
        System.out.println(line.toString());
    }

    private static String getLabel(String id) {
        return id.length() > 1 ? id.substring(0, 2) : id;
    }

    private static String repeat(String s, int times) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < times; i++)
            b.append(s);
        return b.toString();
    }
}
