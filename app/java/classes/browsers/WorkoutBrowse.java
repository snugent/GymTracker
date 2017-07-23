package com.example.admin1.gymtracker.browsers;


import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.activities.MemberEntry;
import com.example.admin1.gymtracker.activities.MenuClass;
import com.example.admin1.gymtracker.activities.WorkoutHeadEntry;
import com.example.admin1.gymtracker.adapters.WorkoutRVAdapter;
import com.example.admin1.gymtracker.fragments.DatePicker;
import com.example.admin1.gymtracker.layout.SimpleDividerItemDecoration;

import com.example.admin1.gymtracker.models.Workout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkoutBrowse extends MenuClass {

    private RecyclerView rvList;
    private final String TAG = "WorkoutBrowse";

    FirebaseDatabase dbRef;
    // Database queries
    private DatabaseReference tableRef;
    private HashMap<String, Workout> workouts;
    private ValueEventListener eventListener;
    private HashMap<String, Workout> chosenWorkouts;

    // Screen Control Elements
    private TextView tvDate ;
    private ImageView ivForward ;
    private ImageView ivBack ;
    private ImageView ivDate;
    private DateTime dtFilterDateStart;
    private DateTime dtFilterDateEnd;

    private static final int DAYS_IN_WEEK = 7;
    private static final String DATE_TIME_FORMAT = "dd/MM/yyyy";
    private static final DateTimeFormatter dtFmt = DateTimeFormat.forPattern(DATE_TIME_FORMAT);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_browse);

        //Sets up the database in Base class for later use
        initialiseDatabase();
        initialiseScreen();
        createEventListeners();
        initialiseAdapter();

        //Forward 1 week button
        ivForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNextWeek();
            }
        });

        //Back 1 week button
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPrevWeek();
            }
        });

        //Select Date Button
        ivDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePicker("");
                newFragment.show(getSupportFragmentManager(), "datePicker");

            }
        });

        ivDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePicker("");
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        tvDate.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dtFilterDateStart = dtFmt.parseDateTime((String) s);
                dtFilterDateStart = getWeekStart(dtFilterDateStart);
                dtFilterDateStart = getWeekStart(dtFilterDateStart);
            }
        });

        // Floating Action Bar
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itWorkoutEntry = new Intent(getApplicationContext(), WorkoutHeadEntry.class);
                itWorkoutEntry.putExtra("workoutId", "");
                startActivity(itWorkoutEntry);
            }
        });
    }



    @Override
    protected void onResume(){
        super.onResume();
        setAuthStateListener();
        createEventListeners();
    }

    @Override
    protected void onPause(){
        super.onPause();
        removeAuthStateListener();
        deleteEventListeners();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Reload the current screen if successful login.
        if (resultCode == RESULT_OK && requestCode == RP_SIGN_IN_ID) {

        }
    }

    private void login(){
        // No Account
        if (getCurrentUserId().equals("")){
            setAuthStateListener();
            createEventListeners();

        }
        // Has an account but no Profile Setup (first login)
        else if (!hasProfile(getCurrentUserId())) {
            Intent itMember = new Intent(getApplicationContext(), MemberEntry.class);
            itMember.putExtra("memberId", getCurrentUserId());
            startActivity(itMember);
        }
        // Otherwise log in Log in
        else if(hasProfile(getCurrentUserId())){
            setAuthStateListener();
            createEventListeners();
        }


    }

    // Sets up the initial values for the screen
    private  void initialiseScreen(){
        //Adds Titlebar
        getSupportActionBar().setTitle(R.string.title_workout_browse);
        tvDate = (TextView) findViewById(R.id.tvDate);
        ivForward = (ImageView) findViewById(R.id.ivForward);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivDate = (ImageView) findViewById(R.id.ivDate);

        //Set Initial value for Date
        dtFilterDateStart = new DateTime();
        dtFilterDateEnd = new DateTime();
        dtFilterDateStart = getWeekStart(dtFilterDateStart);
        dtFilterDateEnd = dtFilterDateStart.plusDays(DAYS_IN_WEEK);
        tvDate.setText(getResources().getString(R.string.week_commencing) + ": " +
                                           dtFmt.print(dtFilterDateStart));
        dbRef = getmFirebaseDatabase();

        //Setup Recycle View
        rvList=(RecyclerView)findViewById(R.id.rvList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);
        tableRef = dbRef.getReference().child("Workout");
    }


    //Connect to adapter for List Items
    private void initialiseAdapter() {
        WorkoutRVAdapter adapter;
        if (workouts != null){
            adapter = new WorkoutRVAdapter(chosenWorkouts, tableRef, this, getmFirebaseDatabase());
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
        Query qryWorkout = tableRef.getRef().orderByChild("memberId").equalTo(getCurrentUserId());
        if (eventListener == null) {
            ValueEventListener mEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    workouts = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Workout mWorkout = child.getValue(Workout.class);
                        workouts.put(child.getKey(), mWorkout);
                    }
                    getWorkoutTimePeriod();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            qryWorkout.addValueEventListener(mEventListener);
            eventListener = mEventListener;
        }
    }

    // Detaches the event listener when activity goes into background
    private void deleteEventListener(){
        if(eventListener  != null){
            tableRef.removeEventListener(eventListener);
        }
    }

    //Gets the start of the week given a date
    private DateTime getWeekStart(DateTime dtDate){
        return dtDate.withDayOfWeek(DateTimeConstants.MONDAY);
    }

    //Move filter Date forward 1 Week
    private void getNextWeek(){
        dtFilterDateStart = dtFilterDateStart.plusDays(DAYS_IN_WEEK);
        dtFilterDateEnd = dtFilterDateStart.plusDays(DAYS_IN_WEEK);
        tvDate.setText(getResources().getString(R.string.week_commencing) + ": " +
                        dtFmt.print(dtFilterDateStart));
        getWorkoutTimePeriod();
        initialiseAdapter();
    }

    //Move filter Date backward 1 Week
    private void getPrevWeek(){
        dtFilterDateStart = dtFilterDateStart.minusDays(DAYS_IN_WEEK);
        dtFilterDateEnd = dtFilterDateStart.plusDays(DAYS_IN_WEEK);
        tvDate.setText(getResources().getString(R.string.week_commencing) + ": " +
                dtFmt.print(dtFilterDateStart));
        getWorkoutTimePeriod();
        initialiseAdapter();
    }

    //getWorkoutTimePeriod
    private void getWorkoutTimePeriod(){
        DateTime dtStart = dtFilterDateStart.minusDays(1);
        DateTime dtEnd   = dtFilterDateEnd.plusDays(1);
        Workout currentItem;
        List<String> keysList;
        List<Workout> workoutList;
        if (workouts != null) {
            keysList = new ArrayList<>(workouts.keySet());
            workoutList = new ArrayList<>(workouts.values());
            chosenWorkouts = new HashMap<>();
            for (int iCnt = 0; iCnt < workoutList.size(); iCnt++) {
                currentItem = workoutList.get(iCnt);
                if ((dtFmt.parseDateTime(currentItem.getWorkoutDate()).isAfter(dtStart)) &&
                        (dtFmt.parseDateTime(currentItem.getWorkoutDate()).isBefore(dtEnd))) {
                    chosenWorkouts.put(keysList.get(iCnt), workoutList.get(iCnt));
                }
            }
            initialiseAdapter();
        }
    } //End of

    WorkoutRVAdapter.OnItemClickListener onItemClickListener = new WorkoutRVAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, String id) {
            //Go the to Workout Entry Screen pass in data to be modified.
            Intent iWorkoutEntry = new Intent(getApplicationContext(), WorkoutHeadEntry.class);
            iWorkoutEntry.putExtra("workoutId", id);
            startActivity(iWorkoutEntry);
        }
    };

}
