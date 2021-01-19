package com.company.utils;

import com.company.core.Task;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.company.utils.InstanceGenerator.createTask;

public class FileHandler {
    public static final String COMMA_DELIMITER = ",";
    public static final String TASK_DELIMITER = " ";

    public static void writeTasksToCsv(List<Task> taskList, String filename) {
        try (FileWriter csvWriter = new FileWriter(filename)) {
            csvWriter.append("id");
            csvWriter.append(COMMA_DELIMITER);
            csvWriter.append("time");
            csvWriter.append(COMMA_DELIMITER);
            csvWriter.append("dependencies");
            csvWriter.append("\n");
            for (Task task : taskList) {
                csvWriter.append(task.getName());
                csvWriter.append(COMMA_DELIMITER);
                csvWriter.append(String.valueOf(task.getTime()));
                csvWriter.append(COMMA_DELIMITER);
                csvWriter.append(String.join(TASK_DELIMITER, task.getPredecessors()));
                csvWriter.append("\n");
            }
            csvWriter.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static List<Task> readTasksFromCsv(String fileName) {
        List<Task> tasks = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {
            // remove header
            br.readLine();
            String line = br.readLine();
            while (line != null) {
                String[] values = line.split(COMMA_DELIMITER);
                Task task = createTask(values);
                tasks.add(task);
                line = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return tasks;
    }
}
