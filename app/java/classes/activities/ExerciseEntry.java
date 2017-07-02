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
import com.example.admin1.gymtracker.models.Exercise;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;
import java.util.HashMap;

public class ExerciseEntry extends AppCompatActivity {
    private ArrayAdapter<String> stAdapter;
    Button btnSave;
    Button btnCancel;
    EditText etExerciseName;
    Spinner spnType;
    String stExerciseId;
    final String TAG = "ExerciseEntry";

    // Database queries
    private DatabaseReference tableExRef;
    private HashMap<String, Exercise> exercises;
    private ValueEventListener eventListener;
    private EventListener elExercise;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_entry);
        initialiseScreen();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidRecord()) {
                    saveRecord(stExerciseId);
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
        // Array and array adapter for Exercise Sex Dropdown
        String stType[] = {getString(R.string.strength), getString(R.string.cardio)};
        Bundle extras = getIntent().getExtras();
        stExerciseId = extras.getString("exerciseId");
        int iPos = 0;

        // Setup GUI Elements
        stAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stType);
        stAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etExerciseName = (EditText) findViewById(R.id.etExerciseName);
        spnType        = (Spinner)  findViewById(R.id.spnType);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        spnType.setAdapter(stAdapter);


        dbRef = FirebaseDatabase.getInstance();
        tableExRef = dbRef.getReference().child("Exercise");

        //Populate Data
        getCurrentRecord(stExerciseId);
    }

    // If doing a modify task this method gets the Current Record and poulates the GUI fields
    private void getCurrentRecord(String ipstId){
        int iPos;
        Exercise currentExercise;
        Log.d(TAG, "getCurrentId " + ipstId + (ipstId == ""));
        if (!ipstId.equalsIgnoreCase("")){
            if (exercises != null) {
                currentExercise = exercises.get(ipstId);
                etExerciseName.setText(currentExercise.getName());
                iPos = stAdapter.getPosition(currentExercise.getType());
                if (iPos >= 0) {
                    spnType.setSelection(iPos);
                }
            }// exercises != null
        }// if(ipstId != "" ....
    } // End getProfile Method


    //Saves Record Details to the database
    private void saveRecord(String ipstExerciseId){
        boolean blFound = false;
        final Exercise savingData ;
        DatabaseReference dbNewRef;

        savingData= new Exercise(
                etExerciseName.getText().toString(),
                spnType.getSelectedItem().toString());

        // Save the Record
        if (ipstExerciseId.equals("") || ipstExerciseId == null){
            //New Record
            dbNewRef = tableExRef.push();
            stExerciseId = dbNewRef.getKey();
            dbNewRef.setValue(savingData);
        }
        else{
            //Existing Record
            tableExRef.child(ipstExerciseId).setValue(savingData);
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
            ValueEventListener elExercise = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    exercises = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Exercise mExercise = child.getValue(Exercise.class);
                        exercises.put(child.getKey(), mExercise);
                    }
                    getCurrentRecord(stExerciseId);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            tableExRef.addValueEventListener(elExercise);
            eventListener = elExercise;
        } // End if eventListener == null

    }

    // Detaches the event listener when activity goes into background
    private void deleteEventListener(){
        if(eventListener  != null){
            tableExRef.removeEventListener(eventListener);
        }
    }

}