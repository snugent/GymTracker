package com.example.admin1.gymtracker.models;

import java.util.Map;

/**
 * Created by admin1 on 24/06/2017.
 */

public class Exercise {
    private String name;
    private String type;
    private Map<String, ExerciseObjective> exerciseObjective;

    public Exercise(){}
    public Exercise(String name, String type){
        this.name = name;
        this.type = type;
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

    public Map<String, ExerciseObjective> getExerciseObjective() {
        return exerciseObjective;
    }

    public void setExerciseObjective(Map<String, ExerciseObjective> exerciseObjective) {
        this.exerciseObjective = exerciseObjective;
    }
}

