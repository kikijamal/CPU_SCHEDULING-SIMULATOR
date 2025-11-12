package util;

import java.util.List;

public class TablePrinter {
    public static void printTable(List<String[]> rows, String[] headers) {
        int cols = headers.length;
        int[] widths = new int[cols];
        for (int i = 0; i < cols; i++)
            widths[i] = headers[i].length();
        for (String[] row : rows)
            for (int i = 0; i < cols; i++)
                widths[i] = Math.max(widths[i], row[i].length());
        // build format and separator
        StringBuilder fmt = new StringBuilder();
        fmt.append("|");
        StringBuilder sep = new StringBuilder();
        sep.append("+");
        for (int w : widths) {
            fmt.append(" %-").append(w).append("s |");
            sep.append(repeat('-', w + 2)).append('+');
        }

        // header
        System.out.println(sep.toString());
        System.out.printf(fmt.toString() + "%n", (Object[]) headers);
        System.out.println(sep.toString());

        if (rows.isEmpty()) {
            System.out.println("(no rows)");
            System.out.println(sep.toString());
            return;
        }

        for (String[] row : rows)
            System.out.printf(fmt.toString() + "%n", (Object[]) row);
        System.out.println(sep.toString());
    }

    private static String repeat(char c, int times) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < times; i++)
            b.append(c);
        return b.toString();
    }
}
