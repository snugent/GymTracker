package com.example.admin1.gymtracker.models;

/**
 * Created by admin1 on 24/06/2017.
 */

public class Exercise {
    private String exerciseId;
    private String name;
    private String type;

    public Exercise(String exerciseId, String name, String type){

        this.exerciseId = exerciseId;
        this.name = name;
        this.type = type;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

