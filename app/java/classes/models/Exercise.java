package com.example.admin1.gymtracker.models;

/**
 * Created by admin1 on 24/06/2017.
 */

public class Exercise {
    private String name;
    private String type;
    private ExerciseObjective exerciseObjective;

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
}

