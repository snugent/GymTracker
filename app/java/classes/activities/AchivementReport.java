package com.example.admin1.gymtracker.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.admin1.gymtracker.Manifest;
import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.adapters.WorkoutLineEntryRVAdapter;
import com.example.admin1.gymtracker.models.Exercise;
import com.example.admin1.gymtracker.models.Objective;
import com.example.admin1.gymtracker.models.Workout;
import com.example.admin1.gymtracker.models.WorkoutLine;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AchivementReport extends BaseClass {
    private final static int RP_WRITE_PERMISSION = 400;
    private final static String stWritePermission = "android.permission.WRITE_EXTERNAL_STORAGE";
    private boolean blHasPermissions;
    //Database variables
    private FirebaseDatabase dbRef;

    private String stObjectiveId;
    private String stExerciseId;

    // Exercise Tables variables
    private HashMap<String, Exercise> exercises;
    private ArrayAdapter<String> stExerciseAdapter;
    private List<Exercise> exerciseList;
    private List<String> exerciseKeysList;
    private DatabaseReference tblExerciseRef;
    private ValueEventListener elExercises;

    //  Objective Tables variables
    private HashMap<String, Objective> objectives;
    private ArrayAdapter<String> stObjectiveAdapter;
    private List<Objective> objectiveList;
    private List<String> objectiveKeysList;
    private DatabaseReference tblObjectiveRef;
    private ValueEventListener elObjective;

    //Adapter for Report Type spinner
    private ArrayAdapter<String> stTypeAdapter;

    // Workout Head Database queries
    private DatabaseReference tblHeadRef;
    private HashMap<String, Workout> workouts;
    private List<Workout> workoutList;
    private List<String> workoutKeysList;

    private ValueEventListener elHead;

    private WorkoutLineEntryRVAdapter exObjAdapter;
    private Button btnRun;
    private Button btnCancel;
    private Button btnExport;
    private Spinner spnExercise;
    private Spinner spnObjective;
    private Spinner spnType;
    private TextView tvResDate;
    private TextView tvResExercise;
    private TextView tvResObjective;
    private TextView tvResValue;
    private ImageView ivProgress;

    private final String TAG = "AchievementReport";
    private final int INVALID_POSITION = -1;

    //Result Variables
    private String stWorkoutDate = "";
    private String stExerciseName = "";
    private String stObjectiveName = "";
    private double entryValue = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achivement_report);
        initialiseScreen();
        btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spnExercise.getSelectedItem() != null && spnObjective.getSelectedItem() != null){
                    if(spnType.getSelectedItem().toString().equals(getString(R.string.highest)) ){
                        generateHighestReport();
                    }
                    else{
                        generateLowestReport();
                    }
                }

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return workout Id to parent
                finish();
            }
        });
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWritePermission();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        // Populate the database tables
        createAllEventListeners();
    }

    @Override
    public void onPause(){
        super.onPause();
        // Clean up all Event Liseners
        deleteAllEventListeners();
    }

    // Sets up the initial values for the screen
    private  void initialiseScreen(){
        initialiseDatabase();
        dbRef = getmFirebaseDatabase();
        //Adds Titlebar
        getSupportActionBar().setTitle(R.string.title_achievement_report);

        spnExercise = (Spinner) findViewById(R.id.spnExercise);
        spnObjective = (Spinner) findViewById(R.id.spnObjective);
        spnType = (Spinner) findViewById(R.id.spnType);
        btnRun      = (Button)  findViewById(R.id.btnRun);
        btnCancel   = (Button)  findViewById(R.id.btnCancel);
        btnExport   = (Button)  findViewById(R.id.btnExport);

        //Initialise
        tvResDate       = (TextView)  findViewById(R.id.tvResDate);
        tvResExercise   = (TextView)  findViewById(R.id.tvResExercise);
        tvResObjective  = (TextView)  findViewById(R.id.tvResObjective);
        tvResValue      = (TextView)  findViewById(R.id.tvResValue);
        ivProgress      = (ImageView) findViewById(R.id.ivProgress);


        tblExerciseRef = dbRef.getReference().child("Exercise");
        tblObjectiveRef = dbRef.getReference().child("Objective");
        tblHeadRef = dbRef.getReference().child("Workout");
        stWorkoutDate = getString(R.string.no_results);


        // populated the report type GUI spinner
        initialiseTypeAdapter();
        // Populate the database tables
        createAllEventListeners();
    }

    //Populates the exercise spinner at top o screen
    private void initialiseExerciseAdapter() {
        String stExercises[];
        int iCnt;
        int iStartPos = INVALID_POSITION;
        if (exercises != null && exercises.size() > 0) {
            stExercises = new String[exercises.size()];
            exerciseKeysList = new ArrayList<>(exercises.keySet());
            exerciseList = new ArrayList<>(exercises.values());
            iCnt = 0;
            for (Exercise child: exerciseList) {
                stExercises[iCnt] = child.getName();
                if (stExerciseId != null && stExerciseId.equals(exerciseKeysList.get(iCnt))){
                    iStartPos = iCnt;
                }
                iCnt++;
            }

            stExerciseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stExercises);
            stExerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnExercise.setAdapter(stExerciseAdapter);
        }
        if(iStartPos > INVALID_POSITION){
            spnExercise.setSelection(iStartPos);
            spnExercise.setEnabled(false);
        }
    }

    //Populates the objective spinner at top o screen
    private void initialiseObjectiveAdapter() {
        String stObjectives[];
        int iCnt;
        int iStartPos = INVALID_POSITION;
        if (objectives != null && objectives.size() > 0) {
            stObjectives = new String[objectives.size()];
            objectiveKeysList = new ArrayList<>(objectives.keySet());
            objectiveList = new ArrayList<>(objectives.values());
            iCnt = 0;
            for (Objective child: objectiveList) {
                stObjectives[iCnt] = child.getName();
                if (stObjectiveId != null && stObjectiveId.equals(objectiveKeysList.get(iCnt))){
                    iStartPos = iCnt;
                }
                iCnt++;
            }

            stObjectiveAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stObjectives);
            stObjectiveAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnObjective.setAdapter(stObjectiveAdapter);
        }
        if(iStartPos > INVALID_POSITION){
            spnObjective.setSelection(iStartPos);
            spnObjective.setEnabled(false);
        }
    }

    //Populates the exercise spinner at top o screen
    private void initialiseTypeAdapter() {
        String stType[] = {getString(R.string.highest), getString(R.string.lowest)};
        int iStartPos = 0;

        stTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stType);
        stTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnType.setAdapter(stTypeAdapter);
        spnType.setSelection(iStartPos);
    }

    // Returns the highest values for an objective done by a memeber
    // E.g. Heaviest Weight
    private void generateHighestReport(){
        DatabaseReference tblLine;
        final Animation anRotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);

        workoutList     = new ArrayList<>(workouts.values());
        workoutKeysList  = new ArrayList<>(workouts.keySet());
        Query qryWorkoutLine[];
        qryWorkoutLine = new Query[workoutKeysList.size()];

        // Set Initial values
        tvResDate.setText(getString(R.string.no_results));
        tvResExercise.setText("");
        tvResObjective.setText("");
        tvResValue.setText("");
        stWorkoutDate = "";
        stExerciseId = "";
        stObjectiveId = "";
        entryValue = -1;  // Chose a large value to get a lower value

        for (int iCnt = 0; iCnt < workoutKeysList.size(); iCnt++){

            qryWorkoutLine[iCnt] = tblHeadRef.getRef().orderByChild("memberId").equalTo(getCurrentUserId());

            ValueEventListener mEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ivProgress.setVisibility(View.VISIBLE);
                    ivProgress.startAnimation(anRotate);
                    String stExId  = exerciseKeysList.get(spnExercise.getSelectedItemPosition());
                    String stObjId = objectiveKeysList.get(spnObjective.getSelectedItemPosition());
                    String stEntryValue = "";

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Workout mWorkout = child.getValue(Workout.class);
                        for (DataSnapshot lineChild : child.child("WorkoutLine").getChildren()){
                            WorkoutLine mLine = lineChild.getValue(WorkoutLine.class);

                            if (mLine.getExerciseId().equals(stExId) &&
                                mLine.getObjectiveId().equals(stObjId) &&
                                mLine.getEntryValue() >= entryValue ){

                                stWorkoutDate = mWorkout.getWorkoutDate();
                                stExerciseName = exercises.get(mLine.getExerciseId()).getName();
                                stObjectiveName = objectives.get(mLine.getObjectiveId()).getName();
                                if (objectives.get(mLine.getObjectiveId()).getViewType()
                                        .equalsIgnoreCase("Time")){
                                    stEntryValue = showAsTime(mLine.getEntryValue());

                                }
                                else{
                                    stEntryValue = "" + mLine.getEntryValue();
                                }
                                entryValue = mLine.getEntryValue();

                                tvResDate.setText(stWorkoutDate);
                                tvResExercise.setText(stExerciseName);
                                tvResObjective.setText(stObjectiveName);
                                tvResValue.setText("" + stEntryValue);
                            } //if

                        } // inner for
                    } //outer for

                    ivProgress.clearAnimation();
                    ivProgress.setVisibility(View.GONE);

                } //onDataChanged


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            qryWorkoutLine[iCnt].addListenerForSingleValueEvent(mEventListener);

        } // for (Workout

    }// End of method

    // Returns the lowest values for an objective done by a memeber
    // E.g. Fastest time
    private void generateLowestReport(){
        DatabaseReference tblLine;

        workoutList     = new ArrayList<>(workouts.values());
        workoutKeysList  = new ArrayList<>(workouts.keySet());
        Query qryWorkoutLine[];
        qryWorkoutLine = new Query[workoutKeysList.size()];
        final Animation anRotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);



        // Set Initial values
        tvResDate.setText(getString(R.string.no_results));
        tvResExercise.setText("");
        tvResObjective.setText("");
        tvResValue.setText("");
        stWorkoutDate = "";
        stExerciseId = "";
        stObjectiveId = "";
        entryValue = 999999999;  // Chose a large value to ensure a lower value is returned



        for (int iCnt = 0; iCnt < workoutKeysList.size(); iCnt++) {
            qryWorkoutLine[iCnt] = tblHeadRef.getRef().orderByChild("memberId").equalTo(getCurrentUserId());
            ValueEventListener mEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ivProgress.setVisibility(View.VISIBLE);
                    ivProgress.startAnimation(anRotate);
                    String stExId  = exerciseKeysList.get(spnExercise.getSelectedItemPosition());
                    String stObjId = objectiveKeysList.get(spnObjective.getSelectedItemPosition());
                    String stEntryValue = "";

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Workout mWorkout = child.getValue(Workout.class);
                        for (DataSnapshot lineChild : child.child("WorkoutLine").getChildren()) {

                            WorkoutLine mLine = lineChild.getValue(WorkoutLine.class);
                            if (mLine.getExerciseId().equals(stExId) &&
                                mLine.getObjectiveId().equals(stObjId) &&
                                mLine.getEntryValue() <= entryValue) {

                                stWorkoutDate = mWorkout.getWorkoutDate();
                                stExerciseName = exercises.get(mLine.getExerciseId()).getName();
                                stObjectiveName = objectives.get(mLine.getObjectiveId()).getName();
                                if (objectives.get(mLine.getObjectiveId()).getViewType()
                                              .equalsIgnoreCase("Time")){
                                    stEntryValue = showAsTime(mLine.getEntryValue());

                                }
                                else{
                                    stEntryValue = "" + mLine.getEntryValue();
                                }
                                entryValue = mLine.getEntryValue();

                                tvResDate.setText(stWorkoutDate);
                                tvResExercise.setText(stExerciseName);
                                tvResObjective.setText(stObjectiveName);
                                tvResValue.setText("" + stEntryValue);
                            } //if

                        } // inner for
                    } //outer for

                    ivProgress.clearAnimation();
                    ivProgress.setVisibility(View.GONE);

                } //onDataChanged

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            qryWorkoutLine[iCnt].addListenerForSingleValueEvent(mEventListener);
        }

    }

    private void exportFile(){
        String stFileName = "export.csv";
        String stPath     = null;
        String stFullPath = null;

        if (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) !=null) {
            stPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            stFullPath = stPath + File.separator + stFileName;
        }

        try {

            if (stFullPath != null && isExternalStorageWritable()) {
                File mFile = new File(stFullPath );
                mFile.setReadable(true, false);
                FileOutputStream fsOut = new FileOutputStream (mFile, false);
                String stLine = tvResDate.getText().toString() +
                        "," +
                        tvResExercise.getText().toString() +
                        "," +
                        tvResObjective.getText().toString() +
                        "," +
                        tvResValue.getText().toString();

                fsOut.write(stLine.getBytes());
                fsOut.flush();
                fsOut.close();
                showWarningMessageDialog(getString(R.string.export_file_msg));
            }
        }
        catch (IOException e){
            Log.d(TAG, e.getMessage());
        }

    }

    private void getWritePermission(){
        // API 23 Manually request permissions.
        // Check if permission to write to photos directory on the phone
            /* Manual Permission Check API 23 only*/
        if (ActivityCompat.checkSelfPermission(this, stWritePermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this ,
                    new String[]{stWritePermission},
                    RP_WRITE_PERMISSION);

        }
        else {
            blHasPermissions = true;
            //Export Report to a file
            exportFile();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        blHasPermissions = true;
        switch (requestCode) {
            case RP_WRITE_PERMISSION: {
                blHasPermissions = (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED);

                break;
            }
        } // Switch

        if (blHasPermissions){
            //Export Report to a file
            exportFile();

        }
        else{
            showWarningMessageDialog(getString(R.string.write_permission_denied));
        }
    }// onRequestPermission Result

    /*  Checks if external storage is available for read and write
        This method was copied from the android developers guide
        https://developer.android.com/guide/topics/data/data-storage.html#filesExternal
    */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    //Shows the value in Time format
    private String showAsTime(double entryValue){
        String timeValue = "";
        // variables for time fields
        final int HOURS_MILI = 3600000;
        final int MINS_MILI =    60000;
        final int SECS_MILI =     1000;

        // variables for time fields
        Double dblEntryVal = entryValue;
        int    iEntryVal;
        int    iHours;
        int    iMins;
        int    iSecs;
        int    iMilis;


        iEntryVal = dblEntryVal.intValue();
        iHours = iEntryVal / HOURS_MILI;
        iMins = (iEntryVal - (iHours * HOURS_MILI)) / MINS_MILI;
        iSecs = (iEntryVal - (iHours * HOURS_MILI) - (iMins * MINS_MILI)) / SECS_MILI;
        iMilis = (iEntryVal - (iHours * HOURS_MILI) - (iMins * MINS_MILI) - (iSecs * SECS_MILI));

        timeValue = String.format("%02d", iHours)
                    + ":" + String.format("%02d", iMins)
                    + ":" + String.format("%02d", iSecs)
                    + ":" + String.format("%03d", iMilis);
        return timeValue;
    }

    private void createAllEventListeners(){
        launchBaseEventListener();
        createExerciseEventListener();
        createObjectiveEventListener();
        createHeadEventListener();
    }


    private void deleteAllEventListeners(){
        destroyBaseEventListener();
        deleteExerciseEventListener();
        deleteObjectiveEventListener();
        deleteHeadEventListener();
    }

    //Retrieves all Exercise records
    private void createExerciseEventListener() {
        if (elExercises == null) {

            elExercises = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    exercises = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Exercise mExercise = child.getValue(Exercise.class);
                        exercises.put(child.getKey(), mExercise);
                    }
                    initialiseExerciseAdapter();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            tblExerciseRef.addValueEventListener(elExercises);
        }
    }

    // Detaches the event listener when activity goes into background
    private void deleteExerciseEventListener(){
        if(elExercises  != null){
            tblExerciseRef.removeEventListener(elExercises);
            elExercises = null;
        }
    }

    //Gets a list of Objectives
    private void createObjectiveEventListener(){
        if (elObjective == null){
            elObjective = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    objectives = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Objective mObjective = child.getValue(Objective.class);
                        objectives.put(child.getKey(), mObjective);
                    }
                    initialiseObjectiveAdapter();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            tblObjectiveRef.addValueEventListener(elObjective);
        }
    }

    // Detaches the event listener when activity goes into background
    private void deleteObjectiveEventListener(){
        if(elObjective  != null){
            tblObjectiveRef.removeEventListener(elObjective);
            elObjective = null;
        }
    }

    // Creates an event listener to retrieve Workout Head information
    private void createHeadEventListener(){
        Query qryWorkout = tblHeadRef.getRef().orderByChild("memberId").equalTo(getCurrentUserId());
        if (elHead == null) {
            ValueEventListener mEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    workouts = new HashMap<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Workout mWorkout = child.getValue(Workout.class);
                        workouts.put(child.getKey(), mWorkout);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            qryWorkout.addListenerForSingleValueEvent(mEventListener);
            elHead = mEventListener;
        }
    }

    // Detaches the event listener when activity goes into background
    private void deleteHeadEventListener(){
        if(elHead  != null){
            tblHeadRef.removeEventListener(elHead);
            elHead = null;
        }
    }
} //end of method
