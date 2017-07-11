package com.example.admin1.gymtracker.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.adapters.WorkoutLineEntryRVAdapter;
import com.example.admin1.gymtracker.layout.SimpleDividerItemDecoration;
import com.example.admin1.gymtracker.models.Exercise;
import com.example.admin1.gymtracker.models.ExerciseObjective;
import com.example.admin1.gymtracker.models.Objective;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkoutLineEntry extends BaseClass {
    private RecyclerView rvList;
    private Button btnSave;
    private Button btnCancel;
    private Spinner spnExercise;
    final String TAG = "WorkoutLineEntry";

    // Exercise Tables
    private HashMap<String, Exercise> exercises;
    private String stExercises[];
    private ArrayAdapter<String> stExerciseAdapter;
    List<Exercise> exerciseList;
    List<String> exerciseKeysList;

    //  Objective Tables
    private HashMap<String, Objective> objectives;
    List<Exercise> objectiveList;
    List<String> objectiveKeysList;

    //  ExerciseObjective Tables
    private HashMap<String, ExerciseObjective> exerciseObjectives;
    List<Exercise> exerciseObjectivesList;
    List<String> exerciseObjectiveKeysList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_line_entry);

        initialiseScreen();

        spnExercise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getExerciseObjectives(exerciseKeysList.get(position));
                initialiseExerciseObjectivesAdapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (isValidRecord()) {

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

    @Override
    public void onStart(){
        super.onStart();
        spnExercise.setSelection(0);
        initialiseExerciseObjectivesAdapter();
    }

    // Sets up the initial values for the screen
    private  void initialiseScreen(){
        spnExercise = (Spinner) findViewById(R.id.spnExercise);
        btnSave     = (Button)  findViewById(R.id.btnSave);
        btnCancel   = (Button)  findViewById(R.id.btnCancel);

        //Setup Recycle View
        rvList=(RecyclerView)findViewById(R.id.rvList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);

        // Populate the database tables
        getExercises();
        getObjectives();
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

    private void getObjectives(){
        FirebaseDatabase dbRef;
        DatabaseReference tableRef;

        dbRef = FirebaseDatabase.getInstance();
        tableRef = dbRef.getReference().child("Objective");
        tableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                objectives = new HashMap<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Objective mObjective = child.getValue(Objective.class);
                    objectives.put(child.getKey(), mObjective);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getExerciseObjectives(final String stExerciseId){
        FirebaseDatabase dbRef;
        DatabaseReference tableRef;

        dbRef = FirebaseDatabase.getInstance();
        tableRef = dbRef.getReference().child("ExerciseObjective");
        tableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                exerciseObjectives = new HashMap<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ExerciseObjective mExerciseObjective = child.getValue(ExerciseObjective.class);
                    if (mExerciseObjective.getExerciseId().equals(stExerciseId) ) {
                        exerciseObjectives.put(child.getKey(), mExerciseObjective);
                    }
                }
                initialiseExerciseObjectivesAdapter();
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
            exerciseKeysList = new ArrayList<>(exercises.keySet());
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

    //Connect to adapter for List Items
    private void initialiseExerciseObjectivesAdapter() {
        WorkoutLineEntryRVAdapter adapter;
        if (objectives != null && exerciseObjectives != null){
            adapter = new WorkoutLineEntryRVAdapter(exerciseObjectives, objectives);
            rvList.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
            rvList.setAdapter(adapter);
        }
    }

}
