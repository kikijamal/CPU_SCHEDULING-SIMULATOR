package util;

import fcfs.GanttEntry;
import fcfs.ScheduleResult;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class HtmlGanttExporter {
    public static void export(ScheduleResult r, String path) throws IOException {
        List<GanttEntry> gantt = r.gantt;
        int makespan = r.makespan;
        int width = Math.min(1200, Math.max(600, makespan * 5));
        int height = 100 + gantt.size() * 20;

        StringBuilder svg = new StringBuilder();
        svg.append(String.format("<svg xmlns='http://www.w3.org/2000/svg' width='%d' height='%d'>", width, height));
        int y = 20;
        for (GanttEntry e : gantt) {
            int x = (int) ((double) e.start / makespan * (width - 100));
            int w = Math.max(2, (int) ((double) (e.end - e.start) / makespan * (width - 100)));
            svg.append(String.format("<rect x='%d' y='%d' width='%d' height='16' fill='%s' />", x + 50, y, w,
                    colorFor(e.id)));
            svg.append(String.format("<text x='%d' y='%d' font-size='10'>%s</text>", x + 52, y + 12, e.id));
            y += 20;
        }
        svg.append("</svg>");

        String html = "<html><body>" + svg.toString() + "</body></html>";
        try (FileWriter fw = new FileWriter(path)) {
            fw.write(html);
        }
    }

    private static String colorFor(String id) {
        int h = Math.abs(id.hashCode()) % 360;
        return "hsl(" + h + ",70%,60%)";
    }
}
