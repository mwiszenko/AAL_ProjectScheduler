package com.company.utils;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class OptionBuilder {
    public static final String DEFAULT_INPUT_FILE = "input.csv";
    public static final String DEFAULT_OUTPUT_FILE = "output.csv";
    public static final Integer DEFAULT_TASKS = 1000;
    public static final Integer DEFAULT_ITERATIONS = 90;
    public static final Integer DEFAULT_STEP = 1000;
    public static final Integer DEFAULT_REPETITIONS = 1;

    public static Options getOptions() {
        return new Options()
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
                .addOption(Option.builder("t")
                        .hasArg(true)
                        .longOpt("iterations")
                        .desc("number of iterations, default: " + DEFAULT_ITERATIONS)
                        .build())
                .addOption(Option.builder("s")
                        .hasArg(true)
                        .longOpt("step")
                        .desc("number of tasks, default: " + DEFAULT_STEP)
                        .build())
                .addOption(Option.builder("r")
                        .hasArg(true)
                        .longOpt("repetitions")
                        .desc("number of repetitions, default: " + DEFAULT_TASKS)
                        .build())
                .addOption(Option.builder("h")
                        .hasArg(false)
                        .longOpt("help")
                        .desc("show help")
                        .build());
    }
}
