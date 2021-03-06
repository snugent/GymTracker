package com.example.admin1.gymtracker.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.models.Objective;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;
import java.util.HashMap;

public class ObjectiveEntry extends BaseClass {

    private ArrayAdapter<String> stAdapter;
    private Button btnSave;
    private Button btnCancel;
    private EditText etObjectiveName;
    private EditText etLabel;
    private Spinner spnType;
    private String stObjectiveId;
    final String TAG = "ObjectiveEntry";

    // Database queries
    private DatabaseReference tableExRef;
    private HashMap<String, Objective> objectives;
    private ValueEventListener eventListener;
    private EventListener elObjective;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objective_entry);
        initialiseScreen();

        etLabel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if (etLabel.getText().toString().equals("")){
                        etLabel.setText(etObjectiveName.getText().toString());
                    }// if (et Label
                } // if (hasFocus
            } // public voic onFcused Changed
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidRecord()) {
                    saveRecord(stObjectiveId);
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

    // Sets up the initial values for the screen
    private  void initialiseScreen(){
        FirebaseDatabase dbRef;
        //Adds Titlebar
        getSupportActionBar().setTitle(R.string.title_objective_entry);
        initialiseDatabase();
        // Array and array adapter for Objective Sex Dropdown
        String stType[] = {getString(R.string.number),getString(R.string.time)};
        Bundle extras = getIntent().getExtras();
        stObjectiveId = extras.getString("objectiveId");
        int iPos = 0;

        // Setup GUI Elements
        stAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stType);
        stAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etObjectiveName = (EditText) findViewById(R.id.etObjectiveName);
        etLabel         = (EditText) findViewById(R.id.etLabel);
        spnType         = (Spinner)  findViewById(R.id.spnType);
        btnSave         = (Button) findViewById(R.id.btnSave);
        btnCancel       = (Button) findViewById(R.id.btnCancel);
        spnType.setAdapter(stAdapter);


        dbRef = getmFirebaseDatabase();
        tableExRef = dbRef.getReference().child("Objective");

        //Populate Data
        getCurrentRecord(stObjectiveId);
    }

    // If doing a modify task this method gets the Current Record and poulates the GUI fields
    private void getCurrentRecord(String ipstId){
        int iPos;
        Objective currentObjective;
        Log.d(TAG, "getCurrentId " + ipstId + (ipstId == ""));
        if (!ipstId.equalsIgnoreCase("")){
            if (objectives != null) {
                currentObjective = objectives.get(ipstId);
                etObjectiveName.setText(currentObjective.getName());
                etLabel.setText(currentObjective.getLabel());
                iPos = stAdapter.getPosition(currentObjective.getViewType());
                if (iPos >= 0) {
                    spnType.setSelection(iPos);
                }
            }// objectives != null
        }// if(ipstId != "" ....
    } // End getProfile Method


    //Saves Record Details to the database
    private void saveRecord(String ipstObjectiveId){
        boolean blFound = false;
        final Objective savingData ;
        DatabaseReference dbNewRef;

        savingData= new Objective(
                etObjectiveName.getText().toString(),
                etLabel.getText().toString(),
                false,
                spnType.getSelectedItem().toString());

        // Save the Record
        if (ipstObjectiveId.equals("") || ipstObjectiveId == null){
            //New Record
            dbNewRef = tableExRef.push();
            stObjectiveId = dbNewRef.getKey();
            dbNewRef.setValue(savingData);
        }
        else{
            //Existing Record
            tableExRef.child(ipstObjectiveId).setValue(savingData);
        }

    }// End Save Profile

    // This method will validate the user data entered.
    private boolean isValidRecord(){
        boolean blValid = true;
        if (etObjectiveName.getText().toString().equals("") || etObjectiveName.getText().toString() == null){
            etObjectiveName.setError(getString(R.string.error_not_blank));
            blValid = false;
        }
        if(etLabel.getText().toString().equals("") || etLabel.getText().toString() == null){
            etLabel.setError(getString(R.string.error_not_blank));
            blValid = false;
        }
        return blValid;
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
        if(eventListener == null) {
            ValueEventListener elObjective = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    objectives = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Objective mObjective = child.getValue(Objective.class);
                        objectives.put(child.getKey(), mObjective);
                    }
                    getCurrentRecord(stObjectiveId);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            tableExRef.addValueEventListener(elObjective);
            eventListener = elObjective;
        } // End if eventListener == null

    }

    // Detaches the event listener when activity goes into background
    private void deleteEventListener(){
        if(eventListener  != null){
            tableExRef.removeEventListener(eventListener);
            eventListener = null;
        }
    }
}
