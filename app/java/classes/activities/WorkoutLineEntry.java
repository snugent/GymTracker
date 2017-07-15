package com.example.admin1.gymtracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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
    private final String TAG = "WorkoutLineEntry";
    private final int INVALID_POSITION = -1;

    private String stWorkoutId;
    private String stExerciseId;

    //Database variables
    private FirebaseDatabase dbRef;
    private DatabaseReference tblWorkoutHead;

    // Workout Head

    // Exercise Tables variables
    private HashMap<String, Exercise> exercises;
    private ArrayAdapter<String> stExerciseAdapter;
    private List<Exercise> exerciseList;
    private List<String> exerciseKeysList;
    private DatabaseReference tblExerciseRef;
    private ValueEventListener elExercises;

    //  Objective Tables variables
    private HashMap<String, Objective> objectives;
    private DatabaseReference tblObjectiveRef;
    private ValueEventListener elObjective;

    //  ExerciseObjective Tables variables
    private DatabaseReference tblExObjRef;
    private ValueEventListener elExObj;

    // Currently visible Workout Line Tables variables
    private HashMap<String, WorkoutLine> workoutLines;
    private List<WorkoutLine> workoutLinesList;
    private List<String> workoutLineKeysList;
    private DatabaseReference tblLineRef;
    private ValueEventListener elLine;

    //  All Workout lines table variables
    private HashMap<String, WorkoutLine> allWorkoutLines;
    private List<WorkoutLine> allWorkoutLinesList;
    private List<String> allWorkoutLineKeysList;
    private DatabaseReference tblAllLines;
    private ValueEventListener elAllLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_line_entry);

        initialiseScreen();

        spnExercise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                createLineEventListener(exerciseKeysList.get(position));
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
                // Return workout Id to parent
                Intent itParent = new Intent();
                itParent.putExtra("workoutId", stWorkoutId);
                setResult(RESULT_OK, itParent);
                finish();
            }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return workout Id to parent
                Intent itParent = new Intent();
                itParent.putExtra("workoutId", stWorkoutId);
                setResult(RESULT_OK, itParent);
                finish();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        // Populate the database tables
        createAllEventListeners();
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
        dbRef = FirebaseDatabase.getInstance();
        Bundle extras = getIntent().getExtras();
        stWorkoutId = extras.getString("workoutId") ;
        stExerciseId    = extras.getString("exerciseId");
        tblWorkoutHead = dbRef.getReference().child("Workout").child(stWorkoutId);

        spnExercise = (Spinner) findViewById(R.id.spnExercise);
        btnSave     = (Button)  findViewById(R.id.btnSave);
        btnCancel   = (Button)  findViewById(R.id.btnCancel);

        //Setup Recycle View
        rvList=(RecyclerView)findViewById(R.id.rvList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);

        // Populates Exercise GUI spinner
        initialiseExerciseAdapter();
        // Populate the database tables
        createAllEventListeners();
    }

    // This method will validate the user data entered.
    private boolean isValidRecord(){
        Boolean blValid = true;
        // If new line check if the data was entered before
        if (stExerciseId == null || stExerciseId.equals("")){
            for(int iCnt =0; iCnt < workoutLinesList.size(); iCnt++){
                WorkoutLine currentRecord = workoutLinesList.get(iCnt);
                if (alreadyExists(currentRecord.getExerciseId())){
                    blValid = false;
                    Toast.makeText(this, getString(R.string.err_workout_line_1), Toast.LENGTH_LONG).show();

                }
                else{
                    blValid = true;
                }
            } //for
        }// if stExercise Id = null ...
        return blValid;
    } // isValidRecord method

    //Saves Record Details to the database
    private void saveData(){
        // If new
        if (stExerciseId == null || stExerciseId.equals("")){
            for(int iCnt =0; iCnt < workoutLinesList.size(); iCnt++) {
                WorkoutLine currentRecord = workoutLinesList.get(iCnt);
                tblWorkoutHead.child("WorkoutLine").push().setValue(currentRecord);
            }
        }
        //If update
        else{
            for(WorkoutLine child: workoutLinesList){
                String stKey  = workoutLineKeysList.get(workoutLinesList.indexOf(child));
                tblWorkoutHead.child("WorkoutLine").child(stKey).setValue(child);
            }
        }



    }// End Save Profile

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

    //Connect to adapter for recycle view
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
        boolean blExists = false;

        for (int iCnt = 0; iCnt < allWorkoutLinesList.size(); iCnt++){
            if (allWorkoutLinesList.get(iCnt).getExerciseId().equals(ipStExerciseId)){
                iCnt = allWorkoutLinesList.size();
                blExists = true;
            }
        }
        return blExists;

    }

    private void createAllEventListeners(){
        int iSpnPos;
        createAllLinesEventListener();
        createExerciseEventListener();
        createObjectiveEventListener();
        iSpnPos = spnExercise.getSelectedItemPosition();
        if (iSpnPos > INVALID_POSITION) {
            createLineEventListener(exerciseKeysList.get(iSpnPos));
        }
    }


    private void deleteAllEventListeners(){
        deleteAllLinesEventListener();
        deleteExerciseEventListener();
        deleteObjectiveEventListener();
        deleteLineEventListener();
    }

    // fetches all created lines for the workout, not just lines for the selected exercise
    private void createAllLinesEventListener(){
        tblAllLines = tblWorkoutHead.child("WorkoutLine");

        if(elAllLines == null) {
            elAllLines = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    allWorkoutLines = new HashMap<>();
                    allWorkoutLineKeysList = new ArrayList<>();
                    allWorkoutLinesList = new ArrayList<>();
                    int iCnt= 0;
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        WorkoutLine mLine = child.getValue(WorkoutLine.class);
                        allWorkoutLines.put(child.getKey(), mLine);
                        allWorkoutLineKeysList.add(iCnt, child.getKey());
                        allWorkoutLinesList.add(iCnt, child.getValue(WorkoutLine.class));
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

    // Fetches all Exercise Objectives for the selected Exercise
    private void createLineEventListener(final String ipstExerciseId){
        if (stExerciseId == null || stExerciseId.equals("")) {
            tblExObjRef = tblExerciseRef.getRef().child(ipstExerciseId).child("ExerciseObjective");
            elExObj = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int iCnt = 0;
                    workoutLines = new HashMap<>();
                    workoutLineKeysList = new ArrayList<>();
                    workoutLinesList = new ArrayList<>();

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        ExerciseObjective mExerciseObjective = child.getValue(ExerciseObjective.class);
                        WorkoutLine mWorkoutLine = new WorkoutLine();
                        mWorkoutLine.setExerciseId(ipstExerciseId);
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
            };// Event Listener
            //Only retrieve the data once for simplicity
            tblExObjRef.addListenerForSingleValueEvent(elExObj);

        } // if
        else{
            //Load Existing Workout Lines
            tblLineRef = tblWorkoutHead.getRef().child("WorkoutLine");
            elLine = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    workoutLines = new HashMap<>();
                    workoutLineKeysList = new ArrayList<>();
                    workoutLinesList    = new ArrayList<>();
                    int iCnt = 0;
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        WorkoutLine mWorkoutLine = child.getValue(WorkoutLine.class);
                        if (mWorkoutLine.getExerciseId().equals(ipstExerciseId)) {
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
            }; //addValueEventListener
            tblLineRef.addValueEventListener(elLine);

        } //else
    } // method


    // Detaches the event listener when activity goes into background
    private void deleteLineEventListener(){
        if(elExObj  != null){
            tblExObjRef.removeEventListener(elExObj);
        }
        if(elLine  != null){
            tblLineRef.removeEventListener(elLine);
        }
    }

    //Gets a list of Objectives
    private void createObjectiveEventListener(){
        dbRef = FirebaseDatabase.getInstance();
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
}
