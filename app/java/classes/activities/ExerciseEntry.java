package com.example.admin1.gymtracker.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.models.Exercise;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExerciseEntry extends AppCompatActivity {
    private ArrayAdapter<String> stAdapter;
    Button btnSave;
    Button btnCancel;
    EditText etExerciseName;
    Spinner spnType;
    String stExerciseId;
    final String TAG = "ExerciseEntry";

    // Database queries
    private FirebaseDatabase dbRef;
    private DatabaseReference tableMemRef;
    private List<Exercise> exercises;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_entry);
        initialiseScreen();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExercise();
            }
        });
    }

    // Sets up the initial values for the screen
    private  void initialiseScreen(){
        // Array and array adapter for Exercise Sex Dropdown
        String stType[] = {getString(R.string.strength), getString(R.string.cardio)};
        Bundle extras = getIntent().getExtras();
        stExerciseId = extras.getString("exerciseId");
        stAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stType);
        stAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etExerciseName = (EditText) findViewById(R.id.etExerciseName);
        spnType        = (Spinner)  findViewById(R.id.spnType);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        spnType.setAdapter(stAdapter);

        dbRef = FirebaseDatabase.getInstance();
        tableMemRef = dbRef.getReference().child("Exercise");
        exercises = new ArrayList<>();
    }

    //Saves Profile Details to the database
    private void saveExercise(){
        boolean blFound = false;
        final Exercise savingData ;

        savingData= new Exercise(
                stExerciseId,
                etExerciseName.getText().toString(),
                spnType.getSelectedItem().toString());

        int iIndex = -1;
        if (exercises.size() > 0 ) {
            for (final Exercise currentExercise : exercises) {
                if(!blFound){
                    iIndex++;
                }

                if (currentExercise.getExerciseId().equals(stExerciseId)) {
                    blFound = true;
                    exercises.set(iIndex, savingData);

                    Query userQuery = tableMemRef.orderByChild("exerciseId").equalTo(stExerciseId);

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
}
