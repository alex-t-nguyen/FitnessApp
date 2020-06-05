package com.example.fitnessapp.workoutCategory;

public class workoutGroup {
    private String workoutName;

    public workoutGroup() {

    }

    public workoutGroup(String s) {
        workoutName = s;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public String getWorkoutName() {
        return workoutName;
    }
}
