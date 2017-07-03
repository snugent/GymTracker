package com.example.admin1.gymtracker.browsers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.activities.BaseClass;
import com.example.admin1.gymtracker.activities.ObjectiveEntry;
import com.example.admin1.gymtracker.adapters.ObjectiveRVAdapter;
import com.example.admin1.gymtracker.layout.SimpleDividerItemDecoration;
import com.example.admin1.gymtracker.models.Objective;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ObjectiveBrowse extends BaseClass {

    private RecyclerView rvList;
    private final String TAG = "ObjectiveBrowse";

    // Database queries
    private DatabaseReference tableRef;
    private HashMap<String, Objective> objectives;
    private ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objective_browse);
        initialiseDatabase();
        initialiseScreen();
        createEventListener();
        initialiseAdapter();

        // Floating Action Bar
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itObjectiveEntry = new Intent(getApplicationContext(), ObjectiveEntry.class);
                itObjectiveEntry.putExtra("objectiveId", "");
                startActivity(itObjectiveEntry);
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
        tableRef = dbRef.getReference().child("Objective");
    }


    //Connect to adapter for List Items
    private void initialiseAdapter() {
        ObjectiveRVAdapter adapter;
        if (objectives != null){
            adapter = new ObjectiveRVAdapter(objectives, tableRef);
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

                    objectives = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Objective mObjective = child.getValue(Objective.class);
                        objectives.put(child.getKey(), mObjective);
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

    ObjectiveRVAdapter.OnItemClickListener onItemClickListener = new ObjectiveRVAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, String id) {
            //Go the to Objective Entry Screen pass in data to be modified.
            Intent iObjectiveEntry = new Intent(getApplicationContext(), ObjectiveEntry.class);
            iObjectiveEntry.putExtra("objectiveId", id);
            startActivity(iObjectiveEntry);
        }
    };

}
