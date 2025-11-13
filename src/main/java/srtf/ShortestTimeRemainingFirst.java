package srtf;

import model.Process;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ShortestTimeRemainingFirst {
    
    public static void main(String[] args) {
        // Shortest Remaining Time First (SRTF) Scheduling Algorithm
        
        // Process Data: [Process ID, Arrival Time, Burst Time]
        // Using Process class: Process(id, priority, arrivalTime, burst)
        List<Process> processes = new ArrayList<>();
        processes.add(new Process("P1", 0, 3, 1));
        processes.add(new Process("P2", 0, 1, 4));
        processes.add(new Process("P3", 0, 4, 2));
        processes.add(new Process("P4", 0, 0, 6));
        processes.add(new Process("P5", 0, 2, 3));
        
        // Sort by arrival time
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        
        int n = processes.size();
        int[] completionTime = new int[n];
        int[] waitingTime = new int[n];
        int[] turnaroundTime = new int[n];
        
        int time = 0;
        int complete = 0;
        int shortest = -1;
        int minRemaining = Integer.MAX_VALUE;
        
        while (complete != n) {
            // Find process with smallest remaining time at current time
            shortest = -1;
            minRemaining = Integer.MAX_VALUE;
            
            for (int i = 0; i < n; i++) {
                if (processes.get(i).getArrivalTime() <= time && 
                    processes.get(i).getRemainingBurst() > 0) {
                    if (processes.get(i).getRemainingBurst() < minRemaining) {
                        minRemaining = processes.get(i).getRemainingBurst();
                        shortest = i;
                    }
                }
            }
            
            if (shortest == -1) {
                time++;
                continue;
            }
            
            // Execute one unit of the shortest process
            processes.get(shortest).runFor(1, time);
            time++;
            
            // If the process is completed
            if (processes.get(shortest).isFinished()) {
                complete++;
                completionTime[shortest] = time;
                turnaroundTime[shortest] = completionTime[shortest] - 
                                           processes.get(shortest).getArrivalTime();
                waitingTime[shortest] = turnaroundTime[shortest] - 
                                       processes.get(shortest).getOriginalBurst();
            }
        }
        
        // Display results
        System.out.printf("%-8s%-10s%-8s%-12s%-12s%-10s%n", 
                         "Process", "Arrival", "Burst", "Completion", "Turnaround", "Waiting");
        
        double totalWt = 0;
        double totalTat = 0;
        
        for (int i = 0; i < n; i++) {
            totalWt += waitingTime[i];
            totalTat += turnaroundTime[i];
            System.out.printf("%-8s%-10d%-8d%-12d%-12d%-10d%n",
                            processes.get(i).getId(),
                            processes.get(i).getArrivalTime(),
                            processes.get(i).getOriginalBurst(),
                            completionTime[i],
                            turnaroundTime[i],
                            waitingTime[i]);
        }
        
        System.out.printf("%nAverage Waiting Time: %.2f%n", totalWt / n);
        System.out.printf("Average Turnaround Time: %.2f%n", totalTat / n);
    }
}
