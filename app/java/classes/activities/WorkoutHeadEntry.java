package com.example.admin1.gymtracker.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.adapters.WorkoutEntryRVAdapter;
import com.example.admin1.gymtracker.layout.SimpleDividerItemDecoration;
import com.example.admin1.gymtracker.models.Workout;
import com.example.admin1.gymtracker.models.WorkoutLine;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Random;

public class WorkoutHeadEntry extends BaseClass {
    private RecyclerView rvList;
    Button btnSave;
    Button btnCancel;
    EditText etWorkoutDate;
    EditText etWorkoutComment;

    String stWorkoutId;
    String stNewWorkoutId;
    final String TAG = "WorkoutHeadEntry";

    // Database queries
    private DatabaseReference tableWkHeadRef;
    private HashMap<String, Workout> workouts;
    private ValueEventListener eventListener;

    //Variables for Workout Entry screen
    private DatabaseReference tableEntryRef;
    private HashMap<String, WorkoutLine> lines;
    private ValueEventListener elWorkoutLine;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_head_entry);
        initialiseDatabase();
        initialiseScreen();
        initialiseAdapter();


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

         // Floating Action Bar
         FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
         fab.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent itExerciseEntry = new Intent(getApplicationContext(), WorkoutLineEntry.class);
                 startActivity(itExerciseEntry);
             }
         });
    }

    // Sets up the initial values for the screen
    private  void initialiseScreen(){
        FirebaseDatabase dbRef;
        Bundle extras = getIntent().getExtras();
        stWorkoutId = extras.getString("workoutId");

        // If not an update generate a temporary workout Id to enable the createion
        // of workout lines
        if ( (stWorkoutId == null) || (stWorkoutId.equals(""))){
            int iMaxValue = Integer.MAX_VALUE;
            Random iRandom = new Random();
            //
            stNewWorkoutId = getCurrentUserId() + iRandom.nextInt(iMaxValue);
        }
        //Setup Recycle View
        rvList=(RecyclerView)findViewById(R.id.rvList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);

        // Setup GUI Elements
        etWorkoutDate    = (EditText) findViewById(R.id.etWorkoutDate);
        etWorkoutComment = (EditText) findViewById(R.id.etWorkoutComment);
        btnSave         = (Button) findViewById(R.id.btnSave);
        btnCancel       = (Button) findViewById(R.id.btnCancel);
        dbRef = FirebaseDatabase.getInstance();
        tableWkHeadRef = dbRef.getReference().child("Workout");
        tableEntryRef  = dbRef.getReference().child("WorkoutLine");

        //Populate Data
        getCurrentRecord(stWorkoutId);
    }

    //Connect to adapter for List Items
    private void initialiseAdapter() {
        WorkoutEntryRVAdapter adapter;
        if (lines != null){
            adapter = new WorkoutEntryRVAdapter(lines, tableEntryRef);
            rvList.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
            adapter.setOnItemClickListener(onItemClickListener);
            rvList.setAdapter(adapter);
        }
    }

    // If doing a modify task this method gets the Current Record and poulates the GUI fields
    private void getCurrentRecord(String ipstId){
        Workout currentWorkout;
        Log.d(TAG, "getCurrentId " + ipstId + (ipstId.equals("")));
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
            dbNewRef = tableWkHeadRef.push();
            stWorkoutId = dbNewRef.getKey();
            dbNewRef.setValue(savingData);
        }
        else{
            //Existing Record
            tableWkHeadRef.child(ipstWorkoutId).setValue(savingData);
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
        createLineEventListener();
    }

    @Override
    protected void onPause(){
        super.onPause();
        deleteEventListener();
        deleteLineEventListener();
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
            tableWkHeadRef.addValueEventListener(elWorkout);
            eventListener = elWorkout;
        } // End if eventListener == null

    }

    // Detaches the event listener when activity goes into background
    private void deleteEventListener(){
        if(eventListener  != null){
            tableWkHeadRef.removeEventListener(eventListener);
        }
    }

    WorkoutEntryRVAdapter.OnItemClickListener onItemClickListener = new WorkoutEntryRVAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, String id, int lineNo) {
            //Go the to Workout Entry Screen pass in data to be modified.
            Intent iWorkoutLine = new Intent(getApplicationContext(), WorkoutLineEntry.class);
            iWorkoutLine.putExtra("workoutId", id);
            iWorkoutLine.putExtra("lineId", lineNo);
            startActivity(iWorkoutLine);
        }
    };

    // Creates an event listener for when we change data
    private void createLineEventListener(){
        if(elWorkoutLine == null) {
            final ValueEventListener elLine = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    lines = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        WorkoutLine mLine = child.getValue(WorkoutLine.class);
                        if ((!stWorkoutId.equals("")) && (mLine.getWorkoutId().equals(stWorkoutId))){
                            lines.put(child.getKey(), mLine);
                        }
                        else if (child.getKey().equals(stNewWorkoutId)){
                            lines.put(child.getKey(), mLine);
                        }
                    }
                    initialiseAdapter();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            tableEntryRef.addValueEventListener(elLine);
            elWorkoutLine = elLine;
        } // End if eventListener == null

    }

    // Detaches the event listener when activity goes into background
    private void deleteLineEventListener(){
        if(elWorkoutLine  != null){
            tableEntryRef.removeEventListener(elWorkoutLine);
        }
    }
}
