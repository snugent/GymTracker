package com.example.admin1.gymtracker.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.adapters.WorkoutEntryRVAdapter;
import com.example.admin1.gymtracker.fragments.DatePicker;
import com.example.admin1.gymtracker.layout.SimpleDividerItemDecoration;
import com.example.admin1.gymtracker.models.Exercise;
import com.example.admin1.gymtracker.models.Objective;
import com.example.admin1.gymtracker.models.Workout;
import com.example.admin1.gymtracker.models.WorkoutLine;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Random;

public class WorkoutHeadEntry extends BaseClass {
    private RecyclerView rvList;
    private Button btnSave;
    private Button btnCancel;
    private TextView tvDate;
    private EditText etWorkoutComment;
    private ImageView ivDate;

    private String stWorkoutId;
    private String stNewWorkoutId;
    private final String TAG = "WorkoutHeadEntry";
    private static final int RP_CREATE_LINE = 10;

    // Database queries
    private FirebaseDatabase dbRef;
    private DatabaseReference tableWkHeadRef;
    private HashMap<String, Workout> workouts;
    private ValueEventListener eventListener;

    //Variables for Workout Entry screen
    private DatabaseReference tableLinesRef;
    private HashMap<String, WorkoutLine> lines;
    private HashMap<String, WorkoutLine> allLines;
    private ValueEventListener elWorkoutLine;

    private DatabaseReference tableExRef;
    private HashMap<String, Exercise> exercises;
    private ValueEventListener elExercise;

    private DatabaseReference tableObjRef;
    private HashMap<String, Objective> objectives;
    private ValueEventListener elObjective;

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
                 saveRecord(stWorkoutId);
                 Intent itExerciseEntry = new Intent(getApplicationContext(), WorkoutLineEntry.class);
                 itExerciseEntry.putExtra("workoutId", stWorkoutId);
                 itExerciseEntry.putExtra("lineId", "");
                 startActivityForResult(itExerciseEntry,RP_CREATE_LINE);
             }
         });

         ivDate.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 DialogFragment newFragment = new DatePicker("");
                 newFragment.show(getSupportFragmentManager(), "datePicker");
             }
         });
    }

    @Override
    protected void onResume(){
        super.onResume();
        createEventListeners();
    }


    @Override
    protected void onStart(){
        super.onStart();
        createEventListeners();
    }

    @Override
    protected void onPause(){
        super.onPause();
        deleteEventListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RP_CREATE_LINE) {
            stWorkoutId = data.getStringExtra("workoutId");
        }
    }

    // Sets up the initial values for the screen
    private  void initialiseScreen(){
        Bundle extras = getIntent().getExtras();
        stWorkoutId = extras.getString("workoutId");
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
        DateTime dtDob = new DateTime();


        // If not an update generate a temporary workout Id to enable the creation
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
        tvDate = (TextView) findViewById(R.id.tvDate);
        etWorkoutComment = (EditText) findViewById(R.id.etWorkoutComment);
        btnSave         = (Button) findViewById(R.id.btnSave);
        btnCancel       = (Button) findViewById(R.id.btnCancel);
        ivDate          = (ImageView) findViewById(R.id.ivDate);
        tvDate.setText(fmt.print(dtDob));

        dbRef = getmFirebaseDatabase();
        tableWkHeadRef = dbRef.getReference().child("Workout");
        if (stWorkoutId != null && !stWorkoutId.equals("")) {
            tableLinesRef = tableWkHeadRef.child(stWorkoutId).child("WorkoutLine");
        }

        //Populate Data
        getCurrentRecord(stWorkoutId);
    }

    //Connect to adapter for List Items
    private void initialiseAdapter() {
        WorkoutEntryRVAdapter adapter;
        if (lines != null && exercises != null && objectives != null){
            adapter = new WorkoutEntryRVAdapter(lines, exercises, tableLinesRef, stWorkoutId, this );
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
                tvDate.setText(currentWorkout.getWorkoutDate());
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
            tvDate.getText().toString(),
            etWorkoutComment.getText().toString()
        );

        // Save the Record
        if (ipstWorkoutId.equals("")){
            //New Record
            dbNewRef = tableWkHeadRef.push();
            stWorkoutId = dbNewRef.getKey();
            dbNewRef.setValue(savingData);
        }
        else{
            tableWkHeadRef.child(ipstWorkoutId).child("memberId").setValue(savingData.getMemberId());
            tableWkHeadRef.child(ipstWorkoutId).child("workoutDate").setValue(savingData.getWorkoutDate());
            tableWkHeadRef.child(ipstWorkoutId).child("comment").setValue(savingData.getComment());
        }

    }// End Save Profile

    // This method will validate the user data entered.
    private boolean isValidRecord(){
        //To do Validate User Input
        return true;
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
        public void onItemClick(View v, String workoutId, String exerciseId) {
            //Go the to Workout Entry Screen pass in data to be modified.
            Intent iWorkoutLine = new Intent(getApplicationContext(), WorkoutLineEntry.class);
            iWorkoutLine.putExtra("workoutId", workoutId);
            iWorkoutLine.putExtra("exerciseId", exerciseId);
            startActivityForResult(iWorkoutLine,RP_CREATE_LINE);
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
                        lines.put(child.getKey(), mLine);
                    }
                    initialiseAdapter();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            tableLinesRef = tableWkHeadRef.child(stWorkoutId).child("WorkoutLine");

            if (tableLinesRef != null) {
                tableLinesRef.addValueEventListener(elLine);
                elWorkoutLine = elLine;
            }
        } // End if eventListener == null

    }

    // Detaches the event listener when activity goes into background
    private void deleteLineEventListener(){
        if(elWorkoutLine  != null){
            tableLinesRef.removeEventListener(elWorkoutLine);
        }
    }

    private void createExerciseEventListener(){
        tableExRef = dbRef.getReference().child("Exercise");
        if(elExercise == null) {
            final ValueEventListener mEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    exercises = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Exercise current = child.getValue(Exercise.class);
                        exercises.put(child.getKey(), current);
                    }
                    initialiseAdapter();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            tableExRef.addValueEventListener(mEventListener);
            elExercise = mEventListener;
        } // End if eventListener == null

    }
    private void deleteExerciseEventListener(){
        if(elExercise  != null){
            tableExRef.removeEventListener(elExercise);
        }
    }
    private void createObjectiveEventListener(){
        tableObjRef = dbRef.getReference().child("Objective");
        if(elObjective == null) {
            final ValueEventListener mEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    objectives = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Objective current = child.getValue(Objective.class);
                        objectives.put(child.getKey(), current);
                    }
                    initialiseAdapter();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            tableObjRef.addValueEventListener(mEventListener);
            elObjective = mEventListener;
        } // End if eventListener == null
    }
    private void deleteObjectiveEventListener(){
        if(elObjective  != null){
            tableExRef.removeEventListener(elObjective);
        }
    }

    private void createEventListeners(){
        createEventListener();
        createLineEventListener();
        createExerciseEventListener();
        createObjectiveEventListener();
    }

    private void deleteEventListeners(){
        deleteEventListener();
        deleteLineEventListener();
        deleteExerciseEventListener();
        deleteObjectiveEventListener();
    }
}
