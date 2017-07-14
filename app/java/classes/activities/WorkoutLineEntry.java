package com.example.admin1.gymtracker.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.admin1.gymtracker.models.WorkoutLine;
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

    String stWorkoutId;
    String stExerciseId;

    // Exercise Tables
    private HashMap<String, Exercise> exercises;
    private ArrayAdapter<String> stExerciseAdapter;
    private List<Exercise> exerciseList;
    private List<String> exerciseKeysList;

    FirebaseDatabase dbRef;
    DatabaseReference tblAllLines;
    private ValueEventListener elAllLines;
    DatabaseReference tblExerciseRef;
    private ValueEventListener elExercises;
    DatabaseReference tblLineRef;
    private ValueEventListener elAllWorkoutLines;



    //  Objective Tables
    private HashMap<String, Objective> objectives;

    //  ExerciseObjective Tables
    private HashMap<String, ExerciseObjective> exerciseObjectives;

    //  Line Tables
    private HashMap<String, WorkoutLine> workoutLines;
    private List<WorkoutLine> workoutLinesList;
    private List<String> workoutLineKeysList;

    //  All workout lines
    private HashMap<String, WorkoutLine> allWorkoutLines;
    private List<WorkoutLine> allWorkoutLinesList;
    private List<String> allWorkoutLineKeysList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_line_entry);

        initialiseScreen();

        spnExercise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                createLineEventListener(exerciseKeysList.get(position));
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
                saveData();
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
        dbRef = FirebaseDatabase.getInstance();
        Bundle extras = getIntent().getExtras();
        stWorkoutId = extras.getString("workoutId") ;
        stExerciseId    = extras.getString("exerciseId");

        spnExercise = (Spinner) findViewById(R.id.spnExercise);
        btnSave     = (Button)  findViewById(R.id.btnSave);
        btnCancel   = (Button)  findViewById(R.id.btnCancel);

        //Setup Recycle View
        rvList=(RecyclerView)findViewById(R.id.rvList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);

        // Populate the database tables
        createExerciseListener();
        createObjectiveEventListener();
    }

    // This method will validate the user data entered.
    private boolean isValidRecord(){
        //To do Validate User Input
        return true;
    }
    //Saves Record Details to the database
    private void saveData(){
        DatabaseReference tblLines;
        Boolean blExists;



        tblLines = dbRef.getReference().child("Workout").child(stWorkoutId);

        if (stExerciseId == null || stExerciseId.equals("")){
            blExists = false;

            for(int iCnt =0; iCnt < workoutLinesList.size(); iCnt++){
                WorkoutLine curentRecord = workoutLinesList.get(iCnt);
                if (alreadyExists(curentRecord.getExerciseId())){

                }
                else{
                    tblLines.child("WorkoutLine").push().setValue(child);
                }



            }
        }
        else{
            for(WorkoutLine child: workoutLinesList){
                String stKey  = workoutLineKeysList.get(workoutLinesList.indexOf(child));
                tblLines.child("WorkoutLine").child(stKey).setValue(child);
            }

        }

    }// End Save Profile

    //Connect to adapter for List Items
    private void initialiseExerciseAdapter() {
        String stExercises[];
        int iCnt;
        int iStartPos = -1;
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
        if(iStartPos >= 0){
            spnExercise.setSelection(iStartPos);
            spnExercise.setEnabled(false);
        }
    }

    //Connect to adapter for List Items
    private void initialiseExerciseObjectivesAdapter() {
        WorkoutLineEntryRVAdapter adapter;
        if (objectives != null && workoutLinesList != null){
            adapter = new WorkoutLineEntryRVAdapter(stWorkoutId,  workoutLines, objectives);
            rvList.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
            rvList.setAdapter(adapter);
        }
    }

    // Don't create a new workoutline if the Exercise has been used by this workout before
    private boolean alreadyExists(String ipStExerciseId){
        for ()



    }

    // Creates an event listener for when we change data
    private void createAllLinesEventListener(){
        tblAllLines; = dbRef.getReference().child("Workout").child(stWorkoutId).child("WorkoutLine");


        if(elAllLines == null) {
            elAllLines = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    allWorkoutLines = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        WorkoutLine mLine = child.getValue(WorkoutLine.class);
                        allWorkoutLines.put(child.getKey(), mLine);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            tblAllLines.addValueEventListener(elAllLines);

        } // End if eventListener == null

    }

    // Detaches the event listener when activity goes into background
    private void deleteAllLinesEventListener(){
        if(elAllLines  != null){
            tblAllLines.removeEventListener(elAllLines);
        }
    }

    private void createExerciseListener() {
        tblExerciseRef = dbRef.getReference().child("Exercise");

        if (elExercises == null) {

            final ValueEventListener el = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    exercises = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Exercise mExercise = child.getValue(Exercise.class);
                        exercises.put(child.getKey(), mExercise);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            tblExerciseRef.addValueEventListener(el);
            elExercises = el;
        }


    }
    // Detaches the event listener when activity goes into background
    private void deleteExerciseListener(){
        if(elAllLines  != null){
            tblExerciseRef.removeEventListener(elAllLines);
        }
    }

    private void createLineEventListener(final String stExerciseId){
        if (this.stExerciseId == null || this.stExerciseId.equals("")){
            tableRef = dbRef.getReference().child("Exercise").child(stExerciseId).child("ExerciseObjective");
            tableRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int iCnt = 0;
                    workoutLines = new HashMap<>();
                    workoutLineKeysList = new ArrayList<String>();
                    workoutLinesList    = new ArrayList<WorkoutLine>();

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        ExerciseObjective mExerciseObjective = child.getValue(ExerciseObjective.class);
                        WorkoutLine mWorkoutLine = new WorkoutLine();
                        mWorkoutLine.setExerciseId(stExerciseId);
                        mWorkoutLine.setObjectiveId(mExerciseObjective.getObjectiveId());
                        mWorkoutLine.setEntryValue(0);

                        workoutLineKeysList.add(iCnt, ("" + iCnt));
                        workoutLinesList.add(iCnt, mWorkoutLine);
                        workoutLines.put(child.getKey(), mWorkoutLine);

                        iCnt = iCnt + 1;
                    }
                    initialiseExerciseObjectivesAdapter();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        //Load Existing Workout Lines
        else{
            tableRef = dbRef.getReference().child("Workout").child(stWorkoutId).child("WorkoutLine").getRef();
            tableRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    workoutLines = new HashMap<>();
                    workoutLineKeysList = new ArrayList<String>();
                    workoutLinesList    = new ArrayList<WorkoutLine>();
                    int iCnt = 0;
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        WorkoutLine mWorkoutLine = child.getValue(WorkoutLine.class);
                        if (mWorkoutLine.getExerciseId().equals(stExerciseId)) {
                            workoutLines.put(child.getKey(), mWorkoutLine);
                            workoutLineKeysList.add(iCnt, child.getKey());
                            workoutLinesList.add(iCnt, mWorkoutLine);
                            iCnt = iCnt + 1;
                        }
                    }
                    initialiseExerciseObjectivesAdapter();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void createObjectiveEventListener(){
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


}
