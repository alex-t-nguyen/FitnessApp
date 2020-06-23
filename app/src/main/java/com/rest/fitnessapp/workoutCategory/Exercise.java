package com.rest.fitnessapp.workoutCategory;

public class Exercise {
    private String name;
    private String reps;
    private String weight;
    private String rest;

    public Exercise() {
        this.name = "";
        this.reps = "";
        this.weight = "";
        this.rest = "";
    }
    public Exercise(String name, String reps, String weight, String rest) {
        this.name = name;
        this.reps = reps;
        this.weight = weight;
        this.rest = rest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String sets) {
        this.weight = sets;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public String getRest() {return rest; }

    public void setRest(String rest) { this.rest = rest; }
}
