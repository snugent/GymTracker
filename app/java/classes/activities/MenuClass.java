package com.example.admin1.gymtracker.activities;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.browsers.ExerciseBrowse;
import com.example.admin1.gymtracker.browsers.MemberBrowse;
import com.example.admin1.gymtracker.browsers.ObjectiveBrowse;
import com.example.admin1.gymtracker.browsers.WorkoutBrowse;

/**
 * Created by admin1 on 09/07/2017.
 * Class that holds Menu Options
 */

public class MenuClass extends BaseClass{
    private Menu menu;
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isAdmin = isAdminUser(getCurrentUserId());
        MenuItem item;
        item = menu.findItem(R.id.exercise);
        item.setVisible(isAdmin);
        item = menu.findItem(R.id.objective);
        item.setVisible(isAdmin);
        item = menu.findItem(R.id.workout_setup);
        item.setVisible(isAdmin);
        item = menu.findItem(R.id.edit_member);
        item.setVisible(isAdmin);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean blResult = false;
        int id = item.getItemId();

        switch(id){
            case R.id.workout_entry:
                launchWorkoutActivity();
                blResult = true;
                break;
            case R.id.profile:
                launchProfileActivity();
                blResult = true;
                break;
            case R.id.exercise:
                launchExerciseActivity();
                blResult = true;
                break;
            case R.id.objective:
                launchObjectiveActivity();
                blResult = true;
                break;
            case R.id.edit_member:
                launchMemberActivity();
                blResult = true;
                break;
            case R.id.sign_out:
                signOut();
                launchWorkoutActivity();
                blResult = true;
                break;
        }
        return blResult;
    }
    // Calls the User Workout Entry Browse Screen
    private void launchWorkoutActivity(){
        Intent iWorkout = new Intent(getApplicationContext(), WorkoutBrowse.class);
        startActivity(iWorkout);
    }
    // Calls the User Profile Maintenance Screen
    private void launchProfileActivity(){
        Intent iProfile = new Intent(getApplicationContext(), MemberEntry.class);
        iProfile.putExtra("memberId", getCurrentUserId());
        iProfile.putExtra("isAdmin", isAdminUser(getCurrentUserId()));
        startActivity(iProfile);
    }

    // Calls the Exercise Maintenance Screen
    private void launchExerciseActivity(){
        Intent itBrowse = new Intent(getApplicationContext(), ExerciseBrowse.class);
        startActivity(itBrowse);
    }

    // Calls the Objective Maintenance Screen
    private void launchObjectiveActivity() {
        Intent itBrowse = new Intent(getApplicationContext(), ObjectiveBrowse.class);
        startActivity(itBrowse);
    }

    //Calls the Member screen to allow trainers (admin members) delete other users
    // Promote and delete Member functionality
    private void launchMemberActivity() {
        Intent itBrowse = new Intent(getApplicationContext(), MemberBrowse.class);
        startActivity(itBrowse);
    }
}
