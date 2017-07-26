package com.example.admin1.gymtracker.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.adapters.WorkoutLineEntryRVAdapter;
import com.example.admin1.gymtracker.models.Exercise;
import com.example.admin1.gymtracker.models.Objective;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AchivementReport extends BaseClass {
    //Database variables
    private FirebaseDatabase dbRef;

    private String stObjectiveId;
    private String stExerciseId;

    // Exercise Tables variables
    private HashMap<String, Exercise> exercises;
    private ArrayAdapter<String> stExerciseAdapter;
    private List<Exercise> exerciseList;
    private List<String> exerciseKeysList;
    private DatabaseReference tblExerciseRef;
    private ValueEventListener elExercises;

    //  Objective Tables variables
    private HashMap<String, Objective> objectives;
    private ArrayAdapter<String> stObjectiveAdapter;
    private List<Objective> objectiveList;
    private List<String> objectiveKeysList;
    private DatabaseReference tblObjectiveRef;
    private ValueEventListener elObjective;

    private WorkoutLineEntryRVAdapter exObjAdapter;
    private Button btnRun;
    private Button btnCancel;
    private Spinner spnExercise;
    private Spinner spnObjective;
    private final String TAG = "AchievementReport";
    private final int INVALID_POSITION = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achivement_report);
        initialiseScreen();
        btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spnExercise.)

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return workout Id to parent
                finish();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        // Populate the database tables
        createAllEventListeners();
    }

    @Override
    public void onPause(){
        super.onPause();
        // Clean up all Event Liseners
        deleteAllEventListeners();
    }

    // Sets up the initial values for the screen
    private  void initialiseScreen(){
        initialiseDatabase();
        dbRef = getmFirebaseDatabase();
        //Adds Titlebar
        getSupportActionBar().setTitle(R.string.title_achievement_report);

        spnExercise = (Spinner) findViewById(R.id.spnExercise);
        spnObjective = (Spinner) findViewById(R.id.spnObjective);
        btnRun      = (Button)  findViewById(R.id.btnRun);
        btnCancel   = (Button)  findViewById(R.id.btnCancel);


        // Populates Exercise GUI spinner
        initialiseExerciseAdapter();
        // Populate the database tables
        createAllEventListeners();
    }

    //Populates the exercise spinner at top o screen
    private void initialiseExerciseAdapter() {
        String stExercises[];
        int iCnt;
        int iStartPos = INVALID_POSITION;
        if (exercises != null && exercises.size() > 0) {
            stExercises = new String[exercises.size()];
            exerciseKeysList = new ArrayList<>(exercises.keySet());
            exerciseList = new ArrayList<>(exercises.values());
            iCnt = 0;
            for (Exercise child: exerciseList) {
                stExercises[iCnt] = child.getName();
                if (stExerciseId != null && stExerciseId.equals(exerciseKeysList.get(iCnt))){
                    iStartPos = iCnt;
                }
                iCnt++;
            }

            stExerciseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stExercises);
            stExerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnExercise.setAdapter(stExerciseAdapter);
        }
        if(iStartPos > INVALID_POSITION){
            spnExercise.setSelection(iStartPos);
            spnExercise.setEnabled(false);
        }
    }

    //Populates the objective spinner at top o screen
    private void initialiseObjectiveAdapter() {
        String stObjectives[];
        int iCnt;
        int iStartPos = INVALID_POSITION;
        if (objectives != null && objectives.size() > 0) {
            stObjectives = new String[objectives.size()];
            objectiveKeysList = new ArrayList<>(objectives.keySet());
            objectiveList = new ArrayList<>(objectives.values());
            iCnt = 0;
            for (Objective child: objectiveList) {
                stObjectives[iCnt] = child.getName();
                if (stObjectiveId != null && stObjectiveId.equals(objectiveKeysList.get(iCnt))){
                    iStartPos = iCnt;
                }
                iCnt++;
            }

            stObjectiveAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stObjectives);
            stObjectiveAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnObjective.setAdapter(stObjectiveAdapter);
        }
        if(iStartPos > INVALID_POSITION){
            spnObjective.setSelection(iStartPos);
            spnObjective.setEnabled(false);
        }
    }

    private void createAllEventListeners(){
        launchBaseEventListener();
        createExerciseEventListener();
        createObjectiveEventListener();
    }


    private void deleteAllEventListeners(){
        destroyBaseEventListener();
        deleteExerciseEventListener();
        deleteObjectiveEventListener();
    }

    //Retrieves all Exercise records
    private void createExerciseEventListener() {
        tblExerciseRef = dbRef.getReference().child("Exercise");

        if (elExercises == null) {

            elExercises = new ValueEventListener() {
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
            };
            tblExerciseRef.addValueEventListener(elExercises);
        }


    }
    // Detaches the event listener when activity goes into background
    private void deleteExerciseEventListener(){
        if(elExercises  != null){
            tblExerciseRef.removeEventListener(elExercises);
        }
    }

    //Gets a list of Objectives
    private void createObjectiveEventListener(){
        dbRef = getmFirebaseDatabase();
        tblObjectiveRef = dbRef.getReference().child("Objective");
        elObjective = new ValueEventListener() {
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
        };
        tblObjectiveRef.addValueEventListener(elObjective);
    }

    // Detaches the event listener when activity goes into background
    private void deleteObjectiveEventListener(){
        if(elObjective  != null){
            tblObjectiveRef.removeEventListener(elObjective);
        }
    }


    private void higherReport(){

    }
    private void lowerReport(){

    }
}
