package com.example.admin1.gymtracker.browsers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.activities.MenuClass;
import com.example.admin1.gymtracker.activities.WorkoutHeadEntry;
import com.example.admin1.gymtracker.adapters.WorkoutRVAdapter;
import com.example.admin1.gymtracker.layout.SimpleDividerItemDecoration;
import com.example.admin1.gymtracker.models.Workout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class WorkoutBrowse extends MenuClass {

    private RecyclerView rvList;
    private final String TAG = "WorkoutBrowse";

    // Database queries
    private DatabaseReference tableRef;
    private HashMap<String, Workout> workouts;
    private ValueEventListener eventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_browse);

        //Sets up the database in Base class for later use
        initialiseDatabase();
        initialiseScreen();
        createEventListeners();
        initialiseAdapter();


        // Floating Action Bar
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent itWorkoutEntry = new Intent(getApplicationContext(), WorkoutHeadEntry.class);
            itWorkoutEntry.putExtra("workoutId", "");
            startActivity(itWorkoutEntry);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        setAuthStateListener();
        createEventListeners();
    }

    @Override
    protected void onPause(){
        super.onPause();
        removeAuthStateListener();
        deleteEventListeners();
    }

    // Sets up the initial values for the screen
    private  void initialiseScreen(){
        FirebaseDatabase dbRef = getmFirebaseDatabase();

        //Setup Recycle View
        rvList=(RecyclerView)findViewById(R.id.rvList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);
        tableRef = dbRef.getReference().child("Workout");
    }


    //Connect to adapter for List Items
    private void initialiseAdapter() {
        WorkoutRVAdapter adapter;
        if (workouts != null){
            adapter = new WorkoutRVAdapter(workouts, tableRef, this);
            rvList.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
            adapter.setOnItemClickListener(onItemClickListener);
            rvList.setAdapter(adapter);
        }
    }

    // Creates all the event listeners for fetching data
    private void createEventListeners(){
        launchBaseEventListener();
        createEventListener();
    }

    // Delete all the event listeners
    private void deleteEventListeners(){
        destroyBaseEventListener();
        deleteEventListener();
    }


    // Creates an event listener for when we change data
    private void createEventListener(){
        if (eventListener == null) {
            ValueEventListener mEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    workouts = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Workout mWorkout = child.getValue(Workout.class);
                        workouts.put(child.getKey(), mWorkout);
                    }
                    initialiseAdapter();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            tableRef.addValueEventListener(mEventListener);
            eventListener = mEventListener;
        }
    }

    // Detaches the event listener when activity goes into background
    private void deleteEventListener(){
        if(eventListener  != null){
            tableRef.removeEventListener(eventListener);
        }
    }

    WorkoutRVAdapter.OnItemClickListener onItemClickListener = new WorkoutRVAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, String id) {
            //Go the to Workout Entry Screen pass in data to be modified.
            Intent iWorkoutEntry = new Intent(getApplicationContext(), WorkoutHeadEntry.class);
            iWorkoutEntry.putExtra("workoutId", id);
            startActivity(iWorkoutEntry);
        }
    };

}
