package com.example.admin1.gymtracker.models;

/**
 * Created by admin1 on 24/06/2017.
 */

public class ExerciseObjective {
    private String objectiveId;

    public ExerciseObjective(){

    }

    public ExerciseObjective(String objectiveId) {
        this.objectiveId = objectiveId;
    }

    public String getObjectiveId() {
        return objectiveId;
    }

    public void setObjectiveId(String objectiveId) {
        this.objectiveId = objectiveId;
    }

}
