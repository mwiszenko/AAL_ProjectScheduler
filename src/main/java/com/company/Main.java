package com.company;

import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.util.concurrent.ThreadLocalRandom.current;

public class Main {
    private static final String COMMA_DELIMITER = ",";
    private static final String TASK_DELIMITER = " ";
    private static final Integer MIN_TIME = 1;
    private static final Integer MAX_TIME = 100;
    private static final Integer MIN_PREDECESSORS = 0;
    private static final Integer MAX_PREDECESSORS = 100;
    private static final String DEFAULT_INPUT_FILE = "input.csv";
    private static final String DEFAULT_OUTPUT_FILE = "output.csv";
    private static final Integer DEFAULT_TASKS = 1000;

    private static final Map<String, Task> tasks = new HashMap<>();
    private static final Map<String, Integer> completionTime = new HashMap<>();

    public static void main(String[] args) {
        Options options = new Options()
                .addOption(Option.builder("m")
                        .hasArg(true)
                        .required(true)
                        .longOpt("mode")
                        .desc("mode:\n" + "1 - Solve problem from file\n" + "2 - Generate random instance of a problem")
                        .build())
                .addOption(Option.builder("i")
                        .hasArg(true)
                        .longOpt("input")
                        .desc("input file, default: " + DEFAULT_INPUT_FILE)
                        .build())
                .addOption(Option.builder("o")
                        .hasArg(true)
                        .longOpt("output")
                        .desc("output file, default: " + DEFAULT_OUTPUT_FILE)
                        .build())
                .addOption(Option.builder("n")
                        .hasArg(true)
                        .longOpt("tasks")
                        .desc("number of tasks, default: " + DEFAULT_TASKS)
                        .build())
                .addOption(Option.builder("h")
                        .hasArg(false)
                        .longOpt("help")
                        .desc("show help")
                        .build());

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("java -jar <NAME> -m <arg> [-i <arg>] [-o <arg>] [-n <arg>]", options);
            System.exit(1);
        }

        List<Task> taskList = new ArrayList<>();
        if (cmd.getOptionValue("m").equals("1")) {
            String fileName = cmd.hasOption("i") ? cmd.getOptionValue("i") : DEFAULT_INPUT_FILE;
            taskList = readTasksFromCsv(fileName);
        } else if (cmd.getOptionValue("m").equals("2")) {
            int numberOfTasks = cmd.hasOption("n") ? Integer.parseInt(cmd.getOptionValue("n")) : DEFAULT_TASKS;
            String fileName = cmd.hasOption("o") ? cmd.getOptionValue("o") : DEFAULT_OUTPUT_FILE;
            taskList = generateProblemInstance(numberOfTasks);
            writeTasksToCsv(taskList, fileName);
        } else {
            System.out.println("Wrong mode value");
            System.exit(1);
        }

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
        System.out.println("Project completion time: " + calculateCompletionTime());
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

    private static int calculateCompletionTime() {
        int tmpMax = -1;
        for (Integer time : completionTime.values()) {
            tmpMax = Math.max(tmpMax, time);
        }
        return tmpMax;
    }

    private static void writeTasksToCsv(List<Task> taskList, String filename) {
        try (FileWriter csvWriter = new FileWriter(filename)) {
            csvWriter.append("id");
            csvWriter.append(COMMA_DELIMITER);
            csvWriter.append("time");
            csvWriter.append(COMMA_DELIMITER);
            csvWriter.append("dependencies");
            csvWriter.append("\n");
            for (Task task : taskList) {
                csvWriter.append(task.name);
                csvWriter.append(COMMA_DELIMITER);
                csvWriter.append(String.valueOf(task.time));
                csvWriter.append(COMMA_DELIMITER);
                csvWriter.append(String.join(TASK_DELIMITER, task.predecessors));
                csvWriter.append("\n");
            }
            csvWriter.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static List<Task> readTasksFromCsv(String fileName) {
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

    private static Task createTask(String[] metadata) {
        String name = metadata[0];
        Integer time = Integer.parseInt(metadata[1]);
        if (metadata.length < 3) {
            List<String> predecessors = new ArrayList<>();
            return new Task(name, time, predecessors);
        }
        String[] array = metadata[2].split(TASK_DELIMITER);
        List<String> predecessors = Arrays.asList(array);
        return new Task(name, time, predecessors);
    }

    private static List<Task> generateProblemInstance(int problemSize) {
        List<Task> taskList = new ArrayList<>();
        List<String> predecessors = new ArrayList<>();
        for (int i = 0; i < problemSize; i++) {
            if (i != 0) {
                predecessors = generatePredecessors(i);
            }
            taskList.add(new Task(String.valueOf(i), current().nextInt(MIN_TIME, MAX_TIME + 1), predecessors));
        }
        return taskList;
    }

    private static List<String> generatePredecessors(int index) {
        int maxPredecessors = Math.min(index, MAX_PREDECESSORS);
        Set<String> taskSet = new HashSet<>();
        if (maxPredecessors > 0) {
            for (int i = 0; i < current().nextInt(MIN_PREDECESSORS, maxPredecessors + 1); i++) {
                taskSet.add(String.valueOf(current().nextInt(0, index)));
            }
        }
        return new ArrayList<>(taskSet);
    }
}