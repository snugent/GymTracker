package com.example.admin1.gymtracker.models;

/**
 * Created by admin1 on 24/06/2017.
 */

public class ExerciseObjective {
    private String exerciseId;
    private String objectiveId;


    public ExerciseObjective(String exerciseId, String objectiveId) {
        this.exerciseId = exerciseId;
        this.objectiveId = objectiveId;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getObjectiveId() {
        return objectiveId;
    }

    public void setObjectiveId(String objectiveId) {
        this.objectiveId = objectiveId;
    }
}
