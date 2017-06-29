package com.example.admin1.gymtracker.models;

import java.util.Date;

/**
 * Created by admin1 on 24/06/2017.
 */

public class Workout {
    private String workoutId;
    private String memberId;
    private String workoutDate;

    public Workout(String workoutId, String memberId, String workoutDate) {
        this.workoutId = workoutId;
        this.memberId = memberId;
        this.workoutDate = workoutDate;
    }

    public String getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(String workoutId) {
        this.workoutId = workoutId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getWorkoutDate() {
        return workoutDate;
    }

    public void setWorkoutDate(String workoutDate) {
        this.workoutDate= workoutDate;
    }
}
