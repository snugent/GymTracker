package com.example.admin1.gymtracker.models;

/**
 * Created by admin1 on 24/06/2017.
 */

public class WorkoutLine {
    private String exerciseId;
    private String objectiveId;
    private double entryValue;

    public WorkoutLine(){

    }

    public WorkoutLine(String exerciseId, String objectiveId, double entryValue) {
        this.exerciseId = exerciseId;
        this.objectiveId = objectiveId;
        this.entryValue = entryValue;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId){
        this.exerciseId = exerciseId;
    }

    public String getObjectiveId() {
        return objectiveId;
    }

    public void setObjectiveId(String objectiveId) {
        this.objectiveId = objectiveId;
    }

    public double getEntryValue() {
        return entryValue;
    }

    public void setEntryValue(double entryValue) {
        this.entryValue = entryValue;
    }
}
