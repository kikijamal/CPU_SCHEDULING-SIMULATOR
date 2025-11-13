# Shortest Remaining Time First (SRTF) Scheduling Guide

## Table of Contents
1. [What is SRTF Scheduling?](#what-is-srtf-scheduling)
2. [How SRTF Works](#how-srtf-works)
3. [Understanding the Code](#understanding-the-codes-technical-terms)
4. [Key Differences from Other Scheduling](#key-differences-from-other-scheduling)
5. [Advantages and Disadvantages](#advantages-and-disadvantages)

---

## What is SRTF Scheduling?

**Shortest Remaining Time First (SRTF)** is a preemptive version of Shortest Job First (SJF). The CPU always executes the process with the **shortest remaining burst time** in the ready queue.

### Two Types:
1. **Non-preemptive (SJF)**: Once a process starts, it runs until completion
2. **Preemptive (SRTF)**: A process with shorter remaining time can interrupt the currently running process

---

## How SRTF Works

### Core Concept
At **every time unit**, the scheduler checks:
- Which processes have arrived?
- Which has the shortest remaining burst time?
- Is it different from the current running process?

If a process with shorter remaining time arrives, it **immediately takes over** the CPU.

### Example Scenario

**Processes:**
```
P1: Arrival=0, Burst=5
P2: Arrival=2, Burst=3
```

**Timeline:**
```
t=0: P1 starts (only process available, remaining=5)
t=1: P1 continues (remaining=4)
t=2: P2 arrives with remaining=3 < P1's remaining=4
     → P2 PREEMPTS P1 (P1 pauses with 4 units remaining)
t=3: P2 continues (remaining=2)
t=4: P2 continues (remaining=1)
t=5: P2 finishes → P1 resumes (remaining=4)
t=6: P1 continues (remaining=3)
t=7: P1 continues (remaining=2)
t=8: P1 continues (remaining=1)
t=9: P1 finishes
```

**Key point:** P1 was interrupted mid-execution when a process with shorter remaining time (P2) arrived.

---

## Understanding the Code's Technical Terms

### 1. Process Class

```java
new Process("P1", 0, 3, 1)
```

Creates a process with:
- **`"P1"`** - Process ID (name)
- **`0`** - Priority (not used in SRTF, set to 0)
- **`3`** - Arrival time (when it enters the system)
- **`1`** - Burst time (CPU time needed)

### 2. Remaining Time Tracking

```java
processes.get(i).getRemainingBurst()
```

**What it does:** Returns how much CPU time the process still needs.

**Real-world analogy:** If you need 5 minutes to complete a task and have worked 2 minutes, your remaining time is 3 minutes.

### 3. Finding Shortest Remaining Process

```java
for (int i = 0; i < n; i++) {
    if (processes.get(i).getArrivalTime() <= time && 
        processes.get(i).getRemainingBurst() > 0) {
        if (processes.get(i).getRemainingBurst() < minRemaining) {
            minRemaining = processes.get(i).getRemainingBurst();
            shortest = i;
        }
    }
}
```

**What it does:** Loops through all processes to find the one with:
- **Arrived** (arrival time ≤ current time)
- **Not finished** (remaining burst > 0)
- **Shortest remaining time** among all ready processes

**Example:**
```
Before: [P1(remaining=4), P2(remaining=3), P3(remaining=6)]
Shortest: P2 (remaining=3)
```

### 4. Preemption Mechanism

```java
processes.get(shortest).runFor(1, time);
time++;
```

**Why 1 time unit?**
- After every unit, the scheduler **re-checks** all processes
- If a new process with shorter remaining time arrived, it gets selected next
- This is how preemption happens automatically

### 5. Idle CPU Handling

```java
if (shortest == -1) {
    time++;
    continue;
}
```

**What it does:** If no process is ready:
- **Increments time** by 1 unit (CPU sits idle)
- Continues to next time unit to check for arrivals

**Example:**
```
t=5: No ready processes
t=6: Check again (P5 might arrive)
```

### 6. Completion Detection

```java
if (processes.get(shortest).isFinished()) {
    complete++;
    completionTime[shortest] = time;
    turnaroundTime[shortest] = completionTime[shortest] - 
                               processes.get(shortest).getArrivalTime();
    waitingTime[shortest] = turnaroundTime[shortest] - 
                           processes.get(shortest).getOriginalBurst();
}
```

**What it does:** When a process finishes (remaining time = 0):
- Records **completion time** (current time)
- Calculates **turnaround time** (completion - arrival)
- Calculates **waiting time** (turnaround - original burst)

### 7. Metrics Calculation

```java
int turnaround = completionTime - arrivalTime;
int waiting = turnaround - originalBurst;
```

**Turnaround Time:** Total time from arrival to completion
- P1 arrives at t=0, finishes at t=9 → turnaround = 9

**Waiting Time:** Time spent NOT executing
- P1 needed 5 units, but took 9 total → waited 4 units

---

## How the Scheduler Works Step-by-Step

### Step 1: Initialization

```java
processes.sort(Comparator.comparingInt(Process::getArrivalTime));
int time = 0;
int complete = 0;
```

- Sorts processes by arrival time
- Starts simulation at `time = 0`
- Tracks number of completed processes

### Step 2: Main Loop

```java
while (complete != n) {
```

Runs until all processes complete.

### Step 3: Find Shortest Remaining Process

Searches for the process with:
- Arrived at or before current time
- Not yet finished
- Minimum remaining burst time

### Step 4: Handle Idle CPU

If no process is ready (`shortest == -1`), increment time and continue.

### Step 5: Execute Process

- Runs selected process for **1 time unit**
- Updates remaining burst time
- Increments time

### Step 6: Check Completion

If process finishes, calculate and store metrics.

### Step 7: Display Results

After all processes complete, prints table with averages.

---

## Key Differences from Other Scheduling

| Algorithm | Selection Criteria | Preemptive? |
|-----------|-------------------|-------------|
| **FCFS** | First arrival | No |
| **SJF** | Shortest burst (original) | No |
| **SRTF** | **Shortest remaining time** | **Yes** |
| **Round Robin** | Time quantum rotation | Yes |
| **Priority** | Highest priority | Yes (preemptive variant) |

**SRTF's Unique Feature:** Selection changes **dynamically** based on how much time processes have left, not just their original burst.

---

## Advantages and Disadvantages

### Advantages
✓ **Optimal average waiting time** - minimizes overall waiting
✓ **Quick jobs get priority** - short processes don't wait behind long ones
✓ **Dynamic adaptation** - adjusts as processes progress

### Disadvantages
✗ **Starvation risk** - long processes may be repeatedly preempted by shorter ones
✗ **High context switching** - frequent preemptions increase overhead
✗ **Requires burst time knowledge** - must know/estimate remaining time
✗ **Complexity** - more difficult to implement than FCFS

---

## Example Execution

With processes from the code:

```java
P1: arrival=3, burst=1
P2: arrival=1, burst=4
P3: arrival=4, burst=2
P4: arrival=0, burst=6
P5: arrival=2, burst=3
```

**After sorting by arrival:**
```
P4: arrival=0, burst=6
P2: arrival=1, burst=4
P5: arrival=2, burst=3
P1: arrival=3, burst=1
P3: arrival=4, burst=2
```

**Expected Timeline:**

1. **t=0:** P4 starts (remaining=6, only process)
2. **t=1:** P2 arrives (remaining=4), P4 has remaining=5 → P2 preempts (4 < 5)
3. **t=2:** P5 arrives (remaining=3) → P5 preempts P2 (3 < 4)
4. **t=3:** P1 arrives (remaining=1) → P1 preempts P5 (1 < 3)
5. **t=4:** P1 finishes, P3 arrives (remaining=2), choose between P3(2), P5(3), P2(4), P4(5) → P3 runs
6. **t=6:** P3 finishes → P5 runs (remaining=3)
7. **t=9:** P5 finishes → P2 runs (remaining=4)
8. **t=13:** P2 finishes → P4 runs (remaining=5)
9. **t=18:** P4 finishes, all done

This demonstrates **multiple preemptions** as shorter processes continually arrive and take over.

---

## Key Formulas

```
Completion Time = time when process finishes
Turnaround Time = Completion Time - Arrival Time
Waiting Time = Turnaround Time - Burst Time
Average Waiting Time = (Sum of all Waiting Times) / Number of Processes
Average Turnaround Time = (Sum of all Turnaround Times) / Number of Processes
```

---

## Key Takeaway

SRTF is **optimal for average waiting time** but requires constant monitoring of remaining burst times. The scheduler evaluates which process has the shortest remaining time at **every single time unit**, enabling immediate preemption when a shorter job becomes available. This makes it ideal for environments where minimizing average waiting time is critical, but can cause longer processes to starve if short jobs keep arriving.
