package com.company.core;

import java.util.List;

public class Task {
    protected String name;
    protected Integer time;
    protected List<String> predecessors;

    public Task(String name, Integer time, List<String> predecessors) {
        this.name = name;
        this.time = time;
        this.predecessors = predecessors;
    }

    public String getName() {
        return this.name;
    }

    public Integer getTime() {
        return this.time;
    }

    public List<String> getPredecessors() {
        return this.predecessors;
    }

    public String toString() {
        return "Name: " + name + ", Time: " + time + ", Predecessors: " + predecessors;
    }
}
