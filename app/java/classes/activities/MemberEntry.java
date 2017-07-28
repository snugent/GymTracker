package com.example.admin1.gymtracker.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.fragments.DatePicker;
import com.example.admin1.gymtracker.models.Member;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;

public class MemberEntry extends BaseClass implements DatePicker.setDateText{
    String stUid;
    boolean blIsAdmin;

    private Button btnSave;
    private Button btnCancel;
    private EditText etName;
    private EditText etDate;
    private Spinner  spnSex;
    private EditText etWeight;
    private EditText etHeight;
    private CheckBox chkAdmin;
    private CheckBox chkDeleted;
    private ArrayAdapter<String> stAdapter;
    private HashMap<String, Member> members;
    private ImageView ivDate;

    //Firebase Database query fields
    private DatabaseReference tableRef;
    private ValueEventListener eventListener;
    private final String TAG = "MemberEntry";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_entry);
        // Get Initial Variables.
        Bundle extras = getIntent().getExtras();
        stUid = extras.getString("memberId");
        blIsAdmin = isAdminUser(stUid);

        // Set up Initial Screen objects
        initialiseScreen();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (isValidRecord()) {
                saveRecord(stUid);
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

    } // End on Create Method.

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
        FirebaseDatabase  dbRef;
        //Adds Titlebar
        getSupportActionBar().setTitle(R.string.title_members_entry);

        initialiseDatabase();
        // Array and array adapter for Member Sex Dropdown
        String stSex[] = {getString(R.string.male), getString(R.string.female)};
        stAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stSex);
        stAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        ivDate    = (ImageView) findViewById(R.id.ivDate);

        etName     = (EditText) findViewById(R.id.etName);
        etDate     = (EditText) findViewById(R.id.etDate);
        etDate.setKeyListener(null);
        spnSex     = (Spinner)  findViewById(R.id.spnSex);
        etWeight   = (EditText) findViewById(R.id.etWeight);
        etHeight   = (EditText) findViewById(R.id.etHeight);
        chkAdmin   = (CheckBox) findViewById(R.id.chkAdmin);
        chkDeleted = (CheckBox) findViewById(R.id.chkDeleted);
        if (!hasProfile(stUid)){
            btnCancel.setVisibility(View.GONE);
        }
        spnSex.setAdapter(stAdapter);

        dbRef = getmFirebaseDatabase();
        tableRef = dbRef.getReference().child("Member");
        createEventListener();
        getCurrentMember(stUid);

        ivDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePicker();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

    }

    private void disableAdminFields() {
        if (!isAdminUser(getCurrentUserId())){
            chkAdmin.setVisibility(View.GONE);
            chkDeleted.setVisibility(View.GONE);
        }
    }

    // If doing a modify task this method gets the Current Record and poulates the GUI fields
    private void getCurrentMember(String ipstUid){
        int iPos;
        Member currentMember ;
        if (members!= null){
            currentMember = members.get(ipstUid);
            if (currentMember != null) {
                etName.setText(currentMember.getName());
                etDate.setText(currentMember.getDob());
                etWeight.setText(Double.toString(currentMember.getWeight()));
                etHeight.setText(Double.toString(currentMember.getHeight()));
                chkAdmin.setChecked(currentMember.getIsAdmin());
                chkDeleted.setChecked(currentMember.getIsDeleted());

                iPos = stAdapter.getPosition(currentMember.getSex());
                if (iPos >= 0){
                    spnSex.setSelection(iPos);
                }
            }

        }

    } // End getProfile Method

    //Saves Record Details to the database
    private void saveRecord(String ipStUid){
        final Member savingData ;

        savingData= new Member( etName.getText().toString(),
                                etDate.getText().toString(),
                                spnSex.getSelectedItem().toString(),
                                Double.parseDouble(etHeight.getText().toString()),
                                Double.parseDouble(etWeight.getText().toString()),
                                chkAdmin.isChecked(),
                                chkDeleted.isChecked());

        // Saver the Record
        tableRef.child(ipStUid).setValue(savingData);

    }// End Save Profile

    // This method will validate the user data entered.
    private boolean isValidRecord(){
        boolean blValid = true;
        DateTimeFormatter dtFmt = DateTimeFormat.forPattern("dd/MM/yyyy");
        DateTime dtToday;
        DateTime dtDob;
        Period pDiff ;
        int iAge = 0;



        if (etName.getText().toString().equals("") || etName.getText().toString() == null){
            etName.setError(getString(R.string.error_not_blank));
            blValid = false;
        }
        if(etDate.getText().toString().equals("") || etDate.getText().toString() == null){
            etDate.setError(getString(R.string.error_not_blank));
            blValid = false;
        }
        // Sanity check on age 16 - 80
        else {
            dtToday = new DateTime();
            dtDob   = new DateTime(dtFmt.parseDateTime(etDate.getText().toString()));
            //Period is a joda library that gets the differences between two date time
            // Period (start, end)
            pDiff = new Period(dtDob,dtToday);
            iAge = pDiff.getYears();
            if(iAge < 16 || iAge > 80){
                etDate.setError(getString(R.string.error_dob_check));
                blValid = false;
            }
        }
        if(etWeight.getText().toString().equals("") || etWeight.getText().toString() == null){
            etWeight.setError(getString(R.string.error_not_blank));
            blValid = false;
        }
        // Sanity check on Weight 0 - 600 KG
        else if (Double.parseDouble(etWeight.getText().toString()) <= 0 ||
                Double.parseDouble(etWeight.getText().toString()) > 600){
            etWeight.setError(getString(R.string.error_weight_check));
            blValid = false;
        }

        if (etHeight.getText().toString().equals("") || etHeight.getText().toString() == null){
            etHeight.setError(getString(R.string.error_not_blank));
            blValid = false;
        }
        // Sanity Check on Height 0.5 - 3 metres in height
        else if (Double.parseDouble(etHeight.getText().toString()) < 0.5 ||
                    Double.parseDouble(etHeight.getText().toString()) > 3){
                etHeight.setError(getString(R.string.error_height_check));
                blValid = false;
         }


        return blValid;
    }

    //Sets date text sent from date picker
    public void setDateText(String stMessage){
        etDate.setText(stMessage);
    }

    //Launches all event Listeners
    private void createEventListeners(){
        launchBaseEventListener();
        createEventListener();
    }

    // deletes all event listeners
    private void deleteEventListeners(){
        destroyBaseEventListener();
        deleteEventListener();
    }

    // Creates an event listener for when we change data
    private void createEventListener(){
        if(eventListener == null) {
            ValueEventListener mEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    members = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Member mMember = child.getValue(Member.class);
                        members.put(child.getKey(), mMember);
                    }
                    getCurrentMember(stUid);
                    disableAdminFields();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            tableRef.addValueEventListener(mEventListener);
            eventListener = mEventListener;
        } // End if eventListener == null

    }

    // Detaches the event listener when activity goes into background
    private void deleteEventListener(){
        if(eventListener != null){
            tableRef.removeEventListener(eventListener);
            eventListener = null;
        }
    }
}
