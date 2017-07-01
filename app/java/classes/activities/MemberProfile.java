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
import com.example.admin1.gymtracker.models.Member;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

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
    private HashMap<String, Member> members;

    //Firebase Database query fields
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
                    saveProfile(stUid);
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    } // End on Create Method.

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
        FirebaseDatabase  dbRef;
        // Array and array adapter for Member Sex Dropdown
        String stSex[] = {getString(R.string.male), getString(R.string.female)};
        stAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stSex);
        stAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        etName     = (EditText) findViewById(R.id.etName);
        etDob      = (EditText) findViewById(R.id.etDob);
        spnSex     = (Spinner)  findViewById(R.id.spnSex);
        etWeight   = (EditText) findViewById(R.id.etWeight);
        etHeight   = (EditText) findViewById(R.id.etHeight);
        chkAdmin   = (CheckBox) findViewById(R.id.chkAdmin);
        chkDeleted = (CheckBox) findViewById(R.id.chkDeleted);
        spnSex.setAdapter(stAdapter);

        dbRef = FirebaseDatabase.getInstance();
        tableMemRef = dbRef.getReference().child("Member");
        createEventListener();
        getCurrentMember(stUid);

    }

    // This method gets the Current Member and poulates the data
    private void getCurrentMember(String ipstUid){
        int iPos;
        Member currentMember ;
        if (members!= null){
            currentMember = members.get(ipstUid);
            etName.setText(currentMember.getName());
            etDob.setText(currentMember.getDob());
            etWeight.setText(Double.toString(currentMember.getWeight()));
            etHeight.setText(Double.toString(currentMember.getHeight()));
            chkAdmin.setChecked(currentMember.getIsAdmin());
            chkDeleted.setChecked(currentMember.getIsDeleted());


            iPos = stAdapter.getPosition(currentMember.getSex());
            if (iPos >= 0){
                spnSex.setSelection(iPos);
            }
        }

    } // End getProfile Method

    //Saves Profile Details to the database
    private void saveProfile(String ipStUid){
        final Member savingData ;

        Log.d(TAG, "saveProfile" + ipStUid + " " + etName.getText().toString() );

        savingData= new Member( etName.getText().toString(),
                                etDob.getText().toString(),
                                spnSex.getSelectedItem().toString(),
                                Double.parseDouble(etHeight.getText().toString()),
                                Double.parseDouble(etWeight.getText().toString()),
                                chkAdmin.isChecked(),
                                chkDeleted.isChecked());

        // Saver the Record
        tableMemRef.child(ipStUid).setValue(savingData);

    }// End Save Profile



    // This method will validate the user data entered.
    private boolean isValidProfile(){
        return true;
    }

    // Creates an event listener for when we change data
    private void createEventListener(){
        if(eventListener == null) {
            ValueEventListener elPost = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onResume");

                    members = new HashMap<>();
                    Log.d(TAG, "onResume2");
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Member mMember = child.getValue(Member.class);
                        members.put(child.getKey(), mMember);
                    }
                    Log.d(TAG, "onResume3");
                    if (members.size() > 0) {
                        Log.d(TAG, "onResume4" + members.get(stUid).getName());
                        Toast.makeText(getApplicationContext(), "in here" + members.get(stUid).getName(), Toast.LENGTH_LONG).show();

                    }
                    Log.d(TAG, "onResume5");
                    getCurrentMember(stUid);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            tableMemRef.addValueEventListener(elPost);
            eventListener = elPost;
        } // End if eventListener == null

    }

    // Detaches the event listener when activity goes into background
    private void deleteEventListener(){
        if(eventListener != null){
            tableMemRef.removeEventListener(eventListener);
        }
    }

}
