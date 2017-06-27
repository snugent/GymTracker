package com.example.admin1.gymtracker.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.admin1.gymtracker.R;

public class MemberProfile extends AppCompatActivity {
    String stUid;
    boolean blIsAdmin;

    Button btnSave;
    Button btnCancel;
    EditText etName;
    EditText etDob;
    Spinner  spnSex;
    EditText etWeight;
    EditText etHeight;
    CheckBox chkAdmin;
    CheckBox chkDeleted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_profile);
        // Get Initial Variables.
        Bundle extras = getIntent().getExtras();
        stUid = extras.getString("uId");
        blIsAdmin = extras.getBoolean("isAdmin");

        // Set up Initial Screen objects
        initialiseScreen();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    } // End on Create Method.

    // Gets the member profile from the database
    private boolean loadData(){
        boolean isSuccessful = false;

        return isSuccessful;
    } // End loadData;

    // Saves the member profile to  the database
    private boolean saveData(){
        boolean isSuccessful = false;

        return isSuccessful;
    }// End saveData

    // Sets up the initial values for the screen
    private  void initialiseScreen(){
        // Array and array adapter for Member Sex Dropdown
        String stSex[] = {"Male", "Female"};
        ArrayAdapter<String> stAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stSex);
        stAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        etName     = (EditText) findViewById(R.id.etName);
        etDob      = (EditText) findViewById(R.id.etDob);
        spnSex     = (Spinner)  findViewById(R.id.spnSex);;
        etWeight   = (EditText) findViewById(R.id.etWeight);
        etHeight   = (EditText) findViewById(R.id.etHeight);
        chkAdmin   = (CheckBox) findViewById(R.id.chkAdmin);
        chkDeleted = (CheckBox) findViewById(R.id.chkDeleted);
        etName.setText("No Name");
        spnSex.setAdapter(stAdapter);
    }

}
