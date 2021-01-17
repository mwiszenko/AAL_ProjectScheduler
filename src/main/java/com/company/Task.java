package com.company;

import java.util.List;

public class Task {
    String name;
    Integer time;
    List<String> predecessors;

    public Task(String name, Integer time, List<String> predecessors) {
        this.name = name;
        this.time = time;
        this.predecessors = predecessors;
//        System.out.println(this);
    }

    public String toString() {
        return "Name: " + name + ", Time: " + time + ", Predecessors: " + predecessors;
    }
}
