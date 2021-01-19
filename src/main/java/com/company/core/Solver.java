package com.company.core;

import org.apache.commons.cli.CommandLine;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.company.utils.FileHandler.readTasksFromCsv;
import static com.company.utils.FileHandler.writeTasksToCsv;
import static com.company.utils.InstanceGenerator.generateProblemInstance;
import static com.company.utils.OptionBuilder.*;

public class Solver {
    private static Map<String, Task> tasks = new HashMap<>();
    private static Map<String, Integer> completionTime = new HashMap<>();

    protected static void singleRunMode(CommandLine cmd) {
        List<Task> taskList;
        String fileName = cmd.hasOption("i") ? cmd.getOptionValue("i") : DEFAULT_INPUT_FILE;
        taskList = readTasksFromCsv(fileName);
        solveProblem(taskList);
    }

    protected static void generateInstanceMode(CommandLine cmd) {
        List<Task> taskList;
        int numberOfTasks = cmd.hasOption("n") ? Integer.parseInt(cmd.getOptionValue("n")) : DEFAULT_TASKS;
        String fileName = cmd.hasOption("o") ? cmd.getOptionValue("o") : DEFAULT_OUTPUT_FILE;
        taskList = generateProblemInstance(numberOfTasks);
        writeTasksToCsv(taskList, fileName);
        solveProblem(taskList);
    }

    protected static void benchmarkMode(CommandLine cmd) {
        List<Task> taskList;
        int initialNumberOfTasks = cmd.hasOption("n") ? Integer.parseInt(cmd.getOptionValue("n")) : DEFAULT_TASKS;
        int iterations = cmd.hasOption("t") ? Integer.parseInt(cmd.getOptionValue("t")) : DEFAULT_ITERATIONS;
        int step = cmd.hasOption("s") ? Integer.parseInt(cmd.getOptionValue("s")) : DEFAULT_STEP;
        int replays = cmd.hasOption("r") ? Integer.parseInt(cmd.getOptionValue("r")) : DEFAULT_REPETITIONS;
        String fileName = cmd.hasOption("o") ? cmd.getOptionValue("o") : DEFAULT_OUTPUT_FILE;
        try (FileWriter csvWriter = new FileWriter(fileName)) {
            for(int i = 0; i < iterations; i++) {
                int numberOfTasks = initialNumberOfTasks + step * i;
                csvWriter.append(String.valueOf(numberOfTasks));
                for(int j = 0; j < replays; j++) {
                    taskList = generateProblemInstance(numberOfTasks);
                    tasks = new HashMap<>();
                    completionTime = new HashMap<>();
                    long startTime = System.nanoTime();
                    solveProblem(taskList);
                    long endTime = System.nanoTime();
                    csvWriter.append(" ").append(String.valueOf((endTime - startTime) / 1000000));
                }
                csvWriter.append("\n");
            }
            csvWriter.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static void solveProblem(List<Task> taskList) {
        for (Task task : taskList) {
            tasks.put(task.name, task);
        }

        for (Task task : tasks.values()) {
            if (completionTime.get(task.name) == null) {
                try {
                    calculatePredecessors(task);
                } catch (StackOverflowError e) {
                    System.out.println("Instance of problem impossible to complete");
                    System.exit(0);
                }
            }
        }
    }

    private static void calculatePredecessors(Task parentTask) {
        // check if already calculated
        if (completionTime.get(parentTask.name) == null) {
            // trivial case
            if (parentTask.predecessors.size() == 0) {
                completionTime.put(parentTask.name, parentTask.time);
            } else {
                // normal case
                for (String taskName : parentTask.predecessors) {
                    calculatePredecessors(tasks.get(taskName));
                }
                completionTime.put(parentTask.name, calculateMaxTime(parentTask.predecessors) + parentTask.time);
            }
        }
    }

    private static int calculateMaxTime(List<String> taskList) {
        int tmpMax = -1;
        for (String name : taskList) {
            tmpMax = Math.max(tmpMax, completionTime.get(name));
        }
        return tmpMax;
    }

    protected static int calculateCompletionTime() {
        int tmpMax = -1;
        for (Integer time : completionTime.values()) {
            tmpMax = Math.max(tmpMax, time);
        }
        return tmpMax;
    }
}
