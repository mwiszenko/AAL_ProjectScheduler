package com.company.core;

import org.apache.commons.cli.*;

import static com.company.core.Solver.*;
import static com.company.utils.OptionBuilder.getOptions;

public class Main {
    public static void main(String[] args) {
        Options options = getOptions();

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("java -jar <NAME> -m <arg> [-option <arg>]", options);
            System.exit(1);
        }

        switch (cmd.getOptionValue("m")) {
            case "1":
                singleRunMode(cmd);
                break;
            case "2":
                generateInstanceMode(cmd);
                break;
            case "3":
                benchmarkMode(cmd);
                break;
            default:
                System.out.println("Wrong mode value");
                System.exit(1);
                break;
        }
    }
}