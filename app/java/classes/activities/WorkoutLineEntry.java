package com.example.admin1.gymtracker.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.models.Exercise;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkoutLineEntry extends BaseClass {
    private Button btnSave;
    private Button btnCancel;
    private Spinner spnExercise;
    private HashMap<String, Exercise> exercises;
    private String stExercises[];
    private ArrayAdapter<String> stExerciseAdapter;
    List<Exercise> exerciseList;
    List<String> keysList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_line_entry);

        initialiseScreen();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (isValidRecord()) {
                saveRecord();
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
        spnExercise = (Spinner) findViewById(R.id.spnExercise);
        btnSave     = (Button)  findViewById(R.id.btnSave);
        btnCancel   = (Button)  findViewById(R.id.btnCancel);
        getExercises();

    }

    // This method will validate the user data entered.
    private boolean isValidRecord(){
        //To do Validate User Input
        return true;
    }
    //Saves Record Details to the database
    private void saveRecord(){
        //To do Save Input

    }// End Save Profile

    private void getExercises(){
        FirebaseDatabase dbRef;
        DatabaseReference tableRef;

        dbRef = FirebaseDatabase.getInstance();
        tableRef = dbRef.getReference().child("Exercise");
        tableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                exercises = new HashMap<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Exercise mExercise = child.getValue(Exercise.class);
                    exercises.put(child.getKey(), mExercise);
                }
                initialiseExerciseAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Connect to adapter for List Items
    private void initialiseExerciseAdapter() {
        int iCnt;
        if (exercises != null && exercises.size() > 0) {
            stExercises = new String[exercises.size()];
            keysList     = new ArrayList<>(exercises.keySet());
            exerciseList = new ArrayList<>(exercises.values());
            iCnt = 0;
            for (Exercise child: exerciseList) {
                stExercises[iCnt] = child.getName();
                iCnt++;
            }

            stExerciseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stExercises);
            stExerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnExercise.setAdapter(stExerciseAdapter);

        }
    }
}
