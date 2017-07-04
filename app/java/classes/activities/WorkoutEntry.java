package com.example.admin1.gymtracker.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.models.Workout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;
import java.util.HashMap;

public class WorkoutEntry extends BaseClass {
    Button btnSave;
    Button btnCancel;
    EditText etWorkoutDate;
    EditText etWorkoutComment;

    String stWorkoutId;
    final String TAG = "WorkoutEntry";

    // Database queries
    private DatabaseReference tableExRef;
    private HashMap<String, Workout> workouts;
    private ValueEventListener eventListener;
    private EventListener elWorkout;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_entry);
        initialiseScreen();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidRecord()) {
                    saveRecord(stWorkoutId);
                    finish();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Sets up the initial values for the screen
    private  void initialiseScreen(){
        FirebaseDatabase dbRef;
        // Array and array adapter for Workout Sex Dropdown
        String stType[] = {getString(R.string.number),getString(R.string.time)};
        Bundle extras = getIntent().getExtras();
        stWorkoutId = extras.getString("workoutId");
        int iPos = 0;

        // Setup GUI Elements
        etWorkoutDate    = (EditText) findViewById(R.id.etWorkoutDate);
        etWorkoutComment = (EditText) findViewById(R.id.etWorkoutComment);
        btnSave         = (Button) findViewById(R.id.btnSave);
        btnCancel       = (Button) findViewById(R.id.btnCancel);
        dbRef = FirebaseDatabase.getInstance();
        tableExRef = dbRef.getReference().child("Workout");

        //Populate Data
        getCurrentRecord(stWorkoutId);
    }

    // If doing a modify task this method gets the Current Record and poulates the GUI fields
    private void getCurrentRecord(String ipstId){
        int iPos;
        Workout currentWorkout;
        Log.d(TAG, "getCurrentId " + ipstId + (ipstId == ""));
        if (!ipstId.equalsIgnoreCase("")){
            if (workouts != null) {
                currentWorkout = workouts.get(ipstId);
                etWorkoutDate.setText(currentWorkout.getWorkoutDate());
                etWorkoutComment.setText(currentWorkout.getComment());
            }// workouts != null
        }// if(ipstId != "" ....
    } // End getProfile Method


    //Saves Record Details to the database
    private void saveRecord(String ipstWorkoutId){
        boolean blFound = false;
        final Workout savingData ;
        DatabaseReference dbNewRef;

        savingData= new Workout(
                getCurrentUserId(),
                etWorkoutDate.getText().toString(),
                etWorkoutComment.getText().toString()
        );

        // Save the Record
        if (ipstWorkoutId.equals("") || ipstWorkoutId == null){
            //New Record
            dbNewRef = tableExRef.push();
            stWorkoutId = dbNewRef.getKey();
            dbNewRef.setValue(savingData);
        }
        else{
            //Existing Record
            tableExRef.child(ipstWorkoutId).setValue(savingData);
        }

    }// End Save Profile

    // This method will validate the user data entered.
    private boolean isValidRecord(){
        //To do Validate User Input
        return true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        createEventListener();
    }

    @Override
    protected void onPause(){
        super.onPause();

        deleteEventListener();
    }

    // Creates an event listener for when we change data
    private void createEventListener(){
        if(eventListener == null) {
            ValueEventListener elWorkout = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    workouts = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Workout mWorkout = child.getValue(Workout.class);
                        workouts.put(child.getKey(), mWorkout);
                    }
                    getCurrentRecord(stWorkoutId);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            tableExRef.addValueEventListener(elWorkout);
            eventListener = elWorkout;
        } // End if eventListener == null

    }

    // Detaches the event listener when activity goes into background
    private void deleteEventListener(){
        if(eventListener  != null){
            tableExRef.removeEventListener(eventListener);
        }
    }
}
