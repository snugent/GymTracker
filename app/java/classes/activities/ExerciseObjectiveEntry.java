package com.example.admin1.gymtracker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.adapters.ExerciseObjectiveRVAdapter;
import com.example.admin1.gymtracker.layout.SimpleDividerItemDecoration;
import com.example.admin1.gymtracker.models.Exercise;
import com.example.admin1.gymtracker.models.ExerciseObjective;
import com.example.admin1.gymtracker.models.Objective;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

public class ExerciseObjectiveEntry extends BaseClass {
    private TextView tvExerciseName;
    private Button btnSave;
    private Button btnCancel;
    private String stExerciseId;
    private RecyclerView rvList;
    private final String TAG = "ExerciseObjectiveEntry";

    private FirebaseDatabase dbRef;
    // Database queries - Exercise Objective
    private DatabaseReference tblExObjRef;
    private HashMap<String, ExerciseObjective> exerciseObjectives;
    private ValueEventListener elExObj;

    // Database queries - Objective
    private DatabaseReference tblObjectiveRef;
    private HashMap<String, Objective> objectives;
    private ValueEventListener elObjective;

    // Database queries - Exercise
    private DatabaseReference tblExerciseRef;
    private HashMap<String, Exercise> exercises;
    private ValueEventListener elExercise;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_objective_entry);
        initialiseScreen();


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                // Return workout Id to parent
                Intent itParent = new Intent();
                itParent.putExtra("exerciseId", stExerciseId);
                setResult(RESULT_OK, itParent);
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return workout Id to parent
                Intent itParent = new Intent();
                itParent.putExtra("exerciseId", stExerciseId);
                setResult(RESULT_OK, itParent);
                finish();
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        createEventListeners();
    }


    @Override
    protected void onResume(){
        super.onResume();
        createEventListeners();
    }

    @Override
    protected void onPause(){
        super.onPause();
        deleteEventListeners();
    }

    // Sets up the initial values for the screen
    private  void initialiseScreen(){
        tvExerciseName  = (TextView) findViewById(R.id.tvExerciseName);
        btnSave         = (Button) findViewById(R.id.btnSave);
        btnCancel       = (Button) findViewById(R.id.btnCancel);
        dbRef           = getmFirebaseDatabase();
        Bundle extras = getIntent().getExtras();
        stExerciseId = extras.getString("exerciseId") ;

        //Setup Recycle View
        rvList=(RecyclerView)findViewById(R.id.rvList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);


        tblExerciseRef  = dbRef.getReference().child("Exercise");
        tblObjectiveRef = dbRef.getReference().child("Objective");
        tblExObjRef = tblExerciseRef.child(stExerciseId).child("ExerciseObjective");
        createEventListeners();

        initialiseAdapter();
    }

    //Connect to adapter for List Items
    private void initialiseAdapter() {
        ExerciseObjectiveRVAdapter adapter;
        if (objectives != null){
            adapter = new ExerciseObjectiveRVAdapter(exerciseObjectives,
                                                     objectives, tblExObjRef);
            rvList.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
            rvList.setAdapter(adapter);
        }
    }

    //Saves Record Details to the database
    private void saveData(){
        tblExObjRef.setValue(exerciseObjectives);


    }// End Save Data

    // Creates all the event listeners for fetching data
    private void createEventListeners(){
        createExerciseEventListener();
        createObjectiveEventListener();
    }

    // Delete all the event listeners
    private void deleteEventListeners(){
        deleteExerciseEventListener();
        deleteObjectiveEventListener();
        deleteExObjEventListener();
    }

    // Creates an event listener for Exercise Objective Data Fetching
    private void createExObjEventListener(){
        if (elExObj == null) {
            elExObj= new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    exerciseObjectives = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        ExerciseObjective mExerciseObjective = child.getValue(ExerciseObjective.class);
                        exerciseObjectives.put(child.getKey(), mExerciseObjective);
                    }
                    initialiseAdapter();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            tblExObjRef.addValueEventListener(elExObj);
        }
    }

    // Detaches the event listener when activity goes into background
    private void deleteExObjEventListener(){
        if(elExObj  != null){
            tblExObjRef.removeEventListener(elExObj);
        }
    }

    // Creates an event listener for  Objective Data Fetching
    private void createObjectiveEventListener(){
        if (elObjective == null) {
            elObjective = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    objectives = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Objective mObjective = child.getValue(Objective.class);
                        objectives.put(child.getKey(), mObjective);
                    }
                    createExObjEventListener();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            tblObjectiveRef.addValueEventListener(elObjective);
        }
    }

    // Detaches the event listener when activity goes into background
    private void deleteObjectiveEventListener(){
        if(elObjective  != null){
            tblObjectiveRef.removeEventListener(elObjective);
        }
    }

    // Creates an event listener for Exercise Data Fetching
    private void createExerciseEventListener(){
        if (elExercise == null) {
            elExercise = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    exercises = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getKey().equals(stExerciseId)){
                            Exercise mExercise = child.getValue(Exercise.class);
                            exercises.put(child.getKey(), mExercise);
                            tvExerciseName.setText(mExercise.getName());

                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            tblExerciseRef.addValueEventListener(elExercise);
        }
    }

    // Detaches the event listener when activity goes into background
    private void deleteExerciseEventListener(){
        if(elExercise  != null){
            tblExerciseRef.removeEventListener(elExercise);
        }
    }

}
