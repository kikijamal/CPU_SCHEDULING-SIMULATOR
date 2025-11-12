package fcfs;

import model.Process;

import util.TablePrinter;
import util.GanttRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Small runner that demonstrates FCFSScheduler with normal and extreme
 * workloads.
 * Supports interactive input and command-line modes.
 */
public class FCFSSimulator {
    public static void main(String[] args) {
        if (args.length == 0) {
            interactiveMode();
            return;
        }

        // Simple CLI parsing: --demo | --extreme | --from-csv <path> [--context-switch
        // N]
        int contextSwitch = 0;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--demo":
                    runDemo();
                    return;
                case "--extreme":
                    runExtreme();
                    return;
                case "--from-csv":
                    if (i + 1 >= args.length) {
                        System.out.println("--from-csv requires a path");
                        return;
                    }
                    String path = args[++i];
                    try {
                        java.util.List<Process> procs = util.CSVLoader.load(path);
                        // look for context-switch override
                        for (int j = 0; j < args.length; j++) {
                            if ("--context-switch".equals(args[j]) && j + 1 < args.length) {
                                contextSwitch = Integer.parseInt(args[j + 1]);
                            }
                            if ("--export-html".equals(args[j]) && j + 1 < args.length) {
                                // handle after schedule
                            }
                        }
                        FCFSScheduler scheduler = new FCFSScheduler(contextSwitch);
                        ScheduleResult r = scheduler.run(procs);
                        printResult(r);
                        // handle export-html if present
                        for (int j = 0; j < args.length; j++) {
                            if ("--export-html".equals(args[j]) && j + 1 < args.length) {
                                String out = args[j + 1];
                                try {
                                    util.HtmlGanttExporter.export(r, out);
                                    System.out.println("Exported HTML to " + out);
                                } catch (Exception ex) {
                                    System.out.println("Export failed: " + ex.getMessage());
                                }
                            }
                        }
                    } catch (Exception ex) {
                        System.out.println("Failed to load CSV: " + ex.getMessage());
                    }
                    return;
                case "--context-switch":
                    if (i + 1 < args.length) {
                        contextSwitch = Integer.parseInt(args[++i]);
                    }
                    break;
                default:
                    System.out.println("Unknown arg: " + args[i]);
                    return;
            }
        }
    }

    private static void interactiveMode() {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println(
                    "FCFS Simulator (interactive). Enter processes one per line in format: id priority arrival burst\nEmpty line to run.");
            List<Process> list = new ArrayList<>();
            while (true) {
                System.out.print("proc> ");
                String line = sc.nextLine().trim();
                if (line.isEmpty())
                    break;
                String[] parts = line.split("\\s+");
                if (parts.length < 4) {
                    System.out.println("need 4 fields: id priority arrival burst");
                    continue;
                }
                try {
                    String id = parts[0];
                    int pr = Integer.parseInt(parts[1]);
                    int at = Integer.parseInt(parts[2]);
                    int b = Integer.parseInt(parts[3]);
                    list.add(new Process(id, pr, at, b));
                } catch (NumberFormatException ex) {
                    System.out.println("bad numbers");
                }
            }

            if (list.isEmpty()) {
                System.out.println("No processes provided, running demo instead.");
                runDemo();
                return;
            }
            FCFSScheduler scheduler = new FCFSScheduler(1);
            ScheduleResult r = scheduler.run(list);
            printResult(r);
        }
    }

    private static void runDemo() {
        List<Process> processes = List.of(
                new Process("P1", 1, 0, 5),
                new Process("P2", 2, 2, 3),
                new Process("P3", 1, 4, 2),
                new Process("P4", 3, 5, 4));

        FCFSScheduler scheduler = new FCFSScheduler(1); // add 1 unit context switch
        ScheduleResult r = scheduler.run(processes);
        printResult(r);
    }

    private static void runExtreme() {
        List<Process> processes = generateExtremeWorkload(50, 0, 5, 100, 3);
        FCFSScheduler scheduler = new FCFSScheduler(2);
        ScheduleResult r = scheduler.run(processes);
        printResult(r);
    }

    private static List<Process> generateExtremeWorkload(int count, int arrivalJitter, int minBurst, int maxBurst,
            int burstSkewFactor) {
        // Extreme: mixture of tiny and huge bursts, out-of-order arrivals, same
        // timestamps
        List<Process> out = new ArrayList<>();
        Random rnd = new Random(12345);
        for (int i = 0; i < count; i++) {
            String id = "X" + (i + 1);
            int arrival = (i < count / 3) ? (i / 5) : (rnd.nextInt(Math.max(1, count / 4)));
            // skewed distribution: many very small, some huge
            int burst = (rnd.nextDouble() < 0.15) ? (maxBurst * burstSkewFactor)
                    : (minBurst + rnd.nextInt(maxBurst - minBurst + 1));
            int priority = 1 + rnd.nextInt(5);
            out.add(new Process(id, priority, arrival, burst));
        }
        return out;
    }

    private static void printResult(ScheduleResult r) {
        System.out.println("Gantt:");
        GanttRenderer.render(r.gantt);

        System.out.printf("Makespan: %d, Avg Turnaround: %.2f, Avg Waiting: %.2f, CPU%%: %.2f\n",
                r.makespan, r.avgTurnaround, r.avgWaiting, r.cpuUtilizationPercent);

        System.out.println("Per-process:");
        List<String[]> rows = new ArrayList<>();
        for (Process p : r.processes) {
            int turnaround = p.getCompletionTime() - p.getArrivalTime();
            int waiting = p.getStartTime() - p.getArrivalTime();
            rows.add(new String[] { p.getId(), String.valueOf(p.getArrivalTime()), String.valueOf(p.getStartTime()),
                    String.valueOf(p.getCompletionTime()), String.valueOf(p.getOriginalBurst()),
                    String.valueOf(turnaround), String.valueOf(waiting) });
        }
        TablePrinter.printTable(rows,
                new String[] { "ID", "Arrival", "Start", "Completion", "Burst", "Turnaround", "Waiting" });

        System.out.printf("Averages -> Turnaround: %.3f, Waiting: %.3f\n", r.avgTurnaround, r.avgWaiting);
    }
}
