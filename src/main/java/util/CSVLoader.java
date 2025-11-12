package util;

import model.Process;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVLoader {
    // CSV format: id,priority,arrival,burst
    public static List<Process> load(String path) throws IOException {
        List<Process> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;
                String[] parts = line.split(",");
                if (parts.length < 4)
                    continue;
                String id = parts[0].trim();
                int pr = Integer.parseInt(parts[1].trim());
                int at = Integer.parseInt(parts[2].trim());
                int b = Integer.parseInt(parts[3].trim());
                out.add(new Process(id, pr, at, b));
            }
        }
        return out;
    }
}
