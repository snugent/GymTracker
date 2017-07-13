package com.example.admin1.gymtracker.models;

import android.support.v7.widget.RecyclerView;



import java.util.Date;
import java.util.Map;

/**
 * Created by admin1 on 24/06/2017.
 */

public class Workout {
    private String memberId;
    private String workoutDate;
    private String comment;
    private Map <String, WorkoutLine> line;

    private Workout(){

    }

    public Workout(String memberId, String workoutDate, String comment) {
        this.memberId = memberId;
        this.workoutDate = workoutDate;
        this.comment = comment;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
