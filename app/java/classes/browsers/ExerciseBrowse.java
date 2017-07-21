package com.example.admin1.gymtracker.browsers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;


import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.activities.BaseClass;
import com.example.admin1.gymtracker.activities.ExerciseEntry;
import com.example.admin1.gymtracker.activities.MenuClass;
import com.example.admin1.gymtracker.adapters.ExerciseRVAdapter;
import com.example.admin1.gymtracker.layout.SimpleDividerItemDecoration;
import com.example.admin1.gymtracker.models.Exercise;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

public class ExerciseBrowse extends MenuClass {
    private RecyclerView rvList;
    private final String TAG = "ExerciseBrowse";

    // Database queries
    private DatabaseReference tableRef;
    private HashMap<String, Exercise> exercises;
    private ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_browse);
        initialiseDatabase();
        initialiseScreen();
        createEventListeners();
        initialiseAdapter();

        // Floating Action Bar
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itExerciseEntry = new Intent(getApplicationContext(), ExerciseEntry.class);
                itExerciseEntry.putExtra("exerciseId", "");
                startActivity(itExerciseEntry);
            }
        });
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
        FirebaseDatabase dbRef;

        //Adds Titlebar
        getSupportActionBar().setTitle(R.string.title_exercises);

        //Setup Recycle View
        rvList=(RecyclerView)findViewById(R.id.rvList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);

        dbRef = FirebaseDatabase.getInstance();
        tableRef = dbRef.getReference().child("Exercise");
    }


    //Connect to adapter for List Items
    private void initialiseAdapter() {
        ExerciseRVAdapter adapter;
        if (exercises != null){
            adapter = new ExerciseRVAdapter(exercises, tableRef, this, getmFirebaseDatabase(), getFragmentManager());
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

                    exercises = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Exercise mExercise = child.getValue(Exercise.class);
                        exercises.put(child.getKey(), mExercise);
                        Log.d(TAG, "New" + mExercise.getName());
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

    ExerciseRVAdapter.OnItemClickListener onItemClickListener = new ExerciseRVAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, String id) {
            //Go the to Exercise Entry Screen pass in data to be modified.
            Intent iExerciseEntry = new Intent(getApplicationContext(), ExerciseEntry.class);
            iExerciseEntry.putExtra("exerciseId", id);
            startActivity(iExerciseEntry);
        }
    };

}
