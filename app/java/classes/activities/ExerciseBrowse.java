package com.example.admin1.gymtracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;


import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.adapters.ExerciseRVAdapter;
import com.example.admin1.gymtracker.layout.SimpleDividerItemDecoration;
import com.example.admin1.gymtracker.models.Exercise;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;
import java.util.HashMap;

public class ExerciseBrowse extends AppCompatActivity {
    private RecyclerView rvList;

    // Database queries
    private DatabaseReference tableExRef;
    private HashMap<String, Exercise> exercises;
    private ValueEventListener eventListener;
    private EventListener elExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_browse);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialiseScreen();
        initialiseAdapter();

        // Floating Action Bar
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        createEventListener();
    }

    @Override
    protected void onPause(){
        super.onPause();

        deleteEventListener();
    }

    // Sets up the initial values for the screen
    private  void initialiseScreen(){
        FirebaseDatabase dbRef;

        //Setup Recycle View
        rvList=(RecyclerView)findViewById(R.id.rvList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);

        dbRef = FirebaseDatabase.getInstance();
        tableExRef = dbRef.getReference().child("Exercise");
    }


    //Connect to adapter for List Items
    private void initialiseAdapter() {
        ExerciseRVAdapter adapter;
        adapter = new ExerciseRVAdapter(exercises);
        rvList.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        adapter.setOnItemClickListener(onItemClickListener);
        rvList.setAdapter(adapter);
    }

    // Creates an event listener for when we change data
    private void createEventListener(){
        if(eventListener == null) {
            ValueEventListener elExercise = new ValueEventListener() {
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
            tableExRef.addValueEventListener(elExercise);
            eventListener = elExercise;
        } // End if eventListener == null

    }

    // Detaches the event listener when activity goes into background
    private void deleteEventListener(){
        if(eventListener  != null){
            tableExRef.removeEventListener(eventListener);
        }
    }

    ExerciseRVAdapter.OnItemClickListener onItemClickListener = new ExerciseRVAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            //To do go to the next screen

        }
    };


}
