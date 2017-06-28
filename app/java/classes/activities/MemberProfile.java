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
import com.example.admin1.gymtracker.adapters.ProfileAdapter;
import com.example.admin1.gymtracker.models.Member;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MemberProfile extends AppCompatActivity {
    String stUid;
    boolean blIsAdmin;

    private Button btnSave;
    private Button btnCancel;
    private EditText etName;
    private EditText etDob;
    private Spinner  spnSex;
    private EditText etWeight;
    private EditText etHeight;
    private CheckBox chkAdmin;
    private CheckBox chkDeleted;
    private ArrayAdapter<String> stAdapter;
    private List<Member> members;

    //Firebase Database query fields
    private ProfileAdapter memberAdapter;
    private FirebaseDatabase  dbRef;
    private DatabaseReference tableMemRef;
    private ValueEventListener eventListener;




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
                if (isValidProfile) {
                    saveProfile();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    } // End on Create Method.

    @Override
    protected void onResume(){
        super.onResume();
        ValueEventListener elPost = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                members = new ArrayList<>();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    Member mMember = child.getValue(Member.class);
                    members.add(mMember);
                }
                memberAdapter.updateList(members);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        tableMemRef.addValueEventListener(elPost);
        eventListener = elPost;

    }

    @Override
    protected void onPause(){
        super.onPause();

        if(eventListener != null){
            tableMemRef.removeEventListener(eventListener);
        }
    }

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
        stAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stSex);
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
        spnSex.setAdapter(stAdapter);

        members = new ArrayList<>();
        dbRef = FirebaseDatabase.getInstance();
        tableMemRef = dbRef.getReference().child("Member");
        getCurrentMember();


    }

    // This method gets the Current Member and poulates the data
    private void getCurrentMember(){
        if (members != null) {
            for (Member currentMember : members) {
                if (currentMember.getMemberId().equals(stUid)) {
                    currentMember.getMemberId();
                    etName.setText(currentMember.getName());
                    etDob.setText(currentMember.getDob().toString());
                    etWeight.setText((int) currentMember.getWeight());
                    etHeight.setText((int) currentMember.getHeight());
                    chkAdmin.setChecked(currentMember.getIsAdmin());
                    chkDeleted.setChecked(currentMember.getIsDeleted());

                    // Variables to populate sex spinner
                    char sex = currentMember.getSex();
                    int iPos = 0;

                    // Set the value on the Sex spinner to male or female depending on what was
                    // selected.
                    if (sex == 'm') {
                        iPos = stAdapter.getPosition("Male");
                        spnSex.setSelection(iPos);
                    } else {
                        iPos = stAdapter.getPosition("Female");
                        spnSex.setSelection(iPos);
                    }

                }
            }
        }
    } // End getProfile Method


    private void saveProfile(){
        boolean blFound = false;
        Member savingData ;
        int iIndex = -1;
        if (members != null) {
            for (Member currentMember : members) {
                if(!blFound){
                    iIndex++;
                }
                if (currentMember.getMemberId().equals(stUid)) {
                    blFound = true;
                    members.set(iIndex, savingData);
                }
            }
        }
        // New Record
        if (!blFound){
            tableMemRef.push().setValue(members);
        }
    }

    // This method will validate the user data entered.
    private boolean isValidProfile(){
        return true;
    }

}
