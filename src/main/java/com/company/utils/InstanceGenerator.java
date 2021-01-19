package com.company.utils;

import com.company.core.Task;

import java.util.*;

import static com.company.utils.FileHandler.TASK_DELIMITER;
import static java.util.concurrent.ThreadLocalRandom.current;

public class InstanceGenerator {
    public static final Integer MIN_TIME = 1;
    public static final Integer MAX_TIME = 100;
    public static final Integer MIN_PREDECESSORS = 0;
    public static final Integer MAX_PREDECESSORS = 100;

    public static Task createTask(String[] metadata) {
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

    public static List<Task> generateProblemInstance(int problemSize) {
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

    public static List<String> generatePredecessors(int index) {
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
