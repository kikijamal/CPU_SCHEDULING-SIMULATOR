# CPU Scheduling Simulator — FCFS (First Come First Serve)

This repository contains a small CPU scheduling simulator focused on FCFS (First-Come First-Serve) with advanced features and tools to explore scheduling behavior.

Features
- FCFS scheduler (non-preemptive) with configurable context-switch overhead
- Handles idle CPU gaps correctly
- Interactive user input (enter processes at prompt)
- CLI modes: `--demo`, `--extreme`, `--from-csv <path>`
- ASCII Gantt chart in terminal and HTML/SVG exporter (`--export-html <path>`)
- Pretty ASCII tables with per-process metrics and averages
- Object-oriented design (Scheduler interface, `ScheduleResult`, `GanttEntry`) for easy extension
- Unit tests (JUnit 5) covering basic behaviors

Requirements
- Java 17 (or compatible)
- Maven 3.x

Build

Open a PowerShell terminal in the repository root and run:

```powershell
mvn -DskipTests package
```

Run tests

```powershell
mvn test
```

Run the simulator

Interactive mode (enter processes one per line: `id priority arrival burst`, blank line to run):

```powershell
java -cp target/cpu-scheduling-1.0.jar MainFCFS
```

Demo mode:

```powershell
java -cp target/cpu-scheduling-1.0.jar MainFCFS --demo
```

Extreme workload demo:

```powershell
java -cp target/cpu-scheduling-1.0.jar MainFCFS --extreme
```

Run from CSV (example CSV at `examples/sample_workload.csv`):

```powershell
java -cp target/cpu-scheduling-1.0.jar MainFCFS --from-csv examples/sample_workload.csv --context-switch 1
```

Export HTML Gantt

```powershell
java -cp target/cpu-scheduling-1.0.jar MainFCFS --from-csv examples/sample_workload.csv --export-html output/gantt.html
```

CSV format
- Each non-empty line: `id,priority,arrival,burst`
- Lines starting with `#` are ignored

Project layout
- `src/main/java/model/Process.java` — process model
- `src/main/java/fcfs/FCFSScheduler.java` — FCFS implementation
- `src/main/java/fcfs/FCFSSimulator.java` — CLI and interactive runner
- `src/main/java/util` — helpers (CSV loader, table printer, Gantt renderers, HTML exporter)
- `src/test/java` — JUnit tests

Next steps / Ideas
- Add other schedulers (SRTF, Round Robin, Priority) implementing the `Scheduler` interface
- Improve HTML/SVG export with timeline axis and per-process rows
- Add JSON input/output and richer CLI options
- Improve terminal output (colors, paging) for large schedules

If you want, I can implement any of the next steps above (HTML polishing, JSON input, more tests). Which one should I do next?
# CPU Scheduling Simulator
Maven project scaffold for a Java CPU scheduling simulator. Open in IntelliJ (File → Open) and import the Maven project.
Quick start:
```bash
mvn -q -DskipTests package
java -cp target/classes Main
```
