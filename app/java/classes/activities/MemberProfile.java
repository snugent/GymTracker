package com.example.admin1.gymtracker.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.adapters.ProfileAdapter;
import com.example.admin1.gymtracker.models.Member;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    private FirebaseDatabase  dbRef;
    private DatabaseReference tableMemRef;
    private ValueEventListener eventListener;
    private final String TAG = "MemberProfile";




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
                if (isValidProfile()) {
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
                Log.d("MemberProfile", "onResume");

                members = new ArrayList<>();
                Log.d("MemberProfile", "onResume2");
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    Member mMember = child.getValue(Member.class);
                    members.add(mMember);
                }
                Log.d("MemberProfile", "onResume3");
                if (members.size() > 0) {
                    Log.d("MemberProfile", "onResume4" + members.get(0).getName());
                    Toast.makeText(getApplicationContext(), "in here" + members.get(0).getMemberId() + members.get(0).getName(), Toast.LENGTH_LONG).show();

                }
                Log.d("MemberProfile", "onResume5");
                getCurrentMember();
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
    }

    // This method gets the Current Member and poulates the data
    private void getCurrentMember(){
        Log.d("Member", stUid );
        if (members.size() > 0) {
            for (Member currentMember : members) {
                Log.d("Member", stUid + " - " + currentMember.getMemberId());
                if (currentMember.getMemberId().equals(stUid)) {
                    currentMember.getMemberId();
                    etName.setText(currentMember.getName());
                    etDob.setText(currentMember.getDob());
                    etWeight.setText(Double.toString(currentMember.getWeight()));
                    etHeight.setText(Double.toString(currentMember.getHeight()));
                    chkAdmin.setChecked(currentMember.getIsAdmin());
                    chkDeleted.setChecked(currentMember.getIsDeleted());

                    int iPos = 0;

                    iPos = stAdapter.getPosition("Male");
                    if (iPos >= 0){
                        spnSex.setSelection(iPos);
                    }

                }
            }
        }
    } // End getProfile Method

    //Saves Profile Details to the database
    private void saveProfile(){
        boolean blFound = false;
        final Member savingData ;

        Log.d("MemberProfile", "saveProfile" + stUid + " " + etName.getText().toString() );

        savingData= new Member( stUid,
                                etName.getText().toString(),
                                etDob.getText().toString(),
                                spnSex.getSelectedItem().toString(),
                                Double.parseDouble(etHeight.getText().toString()),
                                Double.parseDouble(etWeight.getText().toString()),
                                chkAdmin.isChecked(),
                                chkDeleted.isChecked());

        int iIndex = -1;
        if (members.size() > 0 ) {
            for (final Member currentMember : members) {
                if(!blFound){
                    iIndex++;
                }

                if (currentMember.getMemberId().equals(stUid)) {
                    blFound = true;
                    members.set(iIndex, savingData);

                    Query userQuery = tableMemRef.orderByChild("memberId").equalTo(stUid);

                    userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot curentRec: dataSnapshot.getChildren()) {
                                curentRec.getRef().setValue(savingData);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled", databaseError.toException());
                        }
                    });
                }
            }
        }
        // New Record
        if (!blFound){
            tableMemRef.push().setValue(savingData);
        }
    }// End Save Profile



    // This method will validate the user data entered.
    private boolean isValidProfile(){
        return true;
    }


}
