package com.example.admin1.gymtracker.browsers;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.activities.BaseClass;
import com.example.admin1.gymtracker.adapters.ExerciseObjectiveRVAdapter;
import com.example.admin1.gymtracker.layout.SimpleDividerItemDecoration;
import com.example.admin1.gymtracker.models.ExerciseObjective;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ExerciseObjectiveBrowse extends BaseClass {
    private RecyclerView rvList;
    private final String TAG = "ExerciseObjectiveBrowse";

    // Database queries
    private DatabaseReference tableRef;
    private HashMap<String, ExerciseObjective> exerciseObjectives;
    private ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_objective_browse);
        
        initialiseDatabase();
        initialiseScreen();
        createEventListener();
        initialiseAdapter();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
        tableRef = dbRef.getReference().child("ExerciseObjective");
    }


    //Connect to adapter for List Items
    private void initialiseAdapter() {
        ExerciseObjectiveRVAdapter adapter;
        if (exerciseObjectives != null){
            adapter = new ExerciseObjectiveRVAdapter(exerciseObjectives, tableRef);
            rvList.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
            adapter.setOnItemClickListener(onItemClickListener);
            rvList.setAdapter(adapter);
        }
    }


    // Creates an event listener for when we change data
    private void createEventListener(){
        if (eventListener == null) {
            ValueEventListener mEventListener = new ValueEventListener() {
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

    ExerciseObjectiveRVAdapter.OnItemClickListener onItemClickListener = new ExerciseObjectiveRVAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, String id) {
            // To do Exercise objective entry
        }
    };


}
