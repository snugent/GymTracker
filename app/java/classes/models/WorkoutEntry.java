package com.example.admin1.gymtracker.models;

/**
 * Created by admin1 on 24/06/2017.
 */

public class WorkoutEntry {
    private String workoutId;
    private int    entryId;
    private String exerciseId;
    private String objectiveId;
    private double entryValue;
    private String note;

    public WorkoutEntry(String workoutId, int entryId, String exerciseId, String objectiveId, double entryValue, String note) {
        this.workoutId = workoutId;
        this.entryId = entryId;
        this.exerciseId = exerciseId;
        this.objectiveId = objectiveId;
        this.entryValue = entryValue;
        this.note = note;
    }

    public String getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(String workoutId) {
        this.workoutId = workoutId;
    }

    public int getEntryId() {
        return entryId;
    }

    public String getExerciseId() {
        return exerciseId;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
