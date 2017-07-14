package com.example.admin1.gymtracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.models.Exercise;
import com.example.admin1.gymtracker.models.ExerciseObjective;
import com.example.admin1.gymtracker.models.Objective;

import com.example.admin1.gymtracker.models.WorkoutLine;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin1 on 10/07/2017.
 */

public class WorkoutLineEntryRVAdapter extends RecyclerView.Adapter<WorkoutLineEntryRVAdapter.ItemViewActivity>{
    String workoutId;

    // Objecteives variables
    private HashMap<String, Objective> objectives;
    private List<Objective> objectiveList;
    private List<String> objectiveKeysList;

    // Workout Line variables
    private HashMap<String, WorkoutLine> workoutLineHashMap;
    private List<WorkoutLine> workoutLineList;
    private List<String> workoutLineKeysList;

    private final String TAG = "WorkoutLineEntryRVAdapter";

    private FirebaseDatabase dbRef = FirebaseDatabase.getInstance();

    public WorkoutLineEntryRVAdapter(String workoutId, HashMap<String, WorkoutLine> workoutLineHashMap,
                                HashMap<String, Objective> objectives){

        this.workoutId = workoutId;
        // Set Workoutlines tables data
        this.workoutLineHashMap = workoutLineHashMap;
        this.workoutLineList = new ArrayList<>(workoutLineHashMap.values());
        this.workoutLineKeysList = new ArrayList<>((workoutLineHashMap.keySet()));

        //Set Objective table data
        this.objectives = objectives;

        objectiveKeysList = new ArrayList<>(objectives.keySet());
        objectiveList = new ArrayList<>(objectives.values());
    }


    @Override
    public int getItemCount() {
        return workoutLineList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ItemViewActivity onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_workout_entry, viewGroup, false);
        return new ItemViewActivity(v);
    }

    @Override
    public void onBindViewHolder(final ItemViewActivity objectiveViewHolder, final int pos) {
        WorkoutLine mWorkoutLine;
        Objective mObjective;
        String stObjectiveName = "";
        String stObjectiveType = "";
        String stEntryValue    = "";

        //Get the Name of the objective
        mWorkoutLine = workoutLineList.get(pos);
        mObjective = objectives.get(mWorkoutLine.getObjectiveId());

        if (mObjective != null){
            stObjectiveName = mObjective.getLabel();
            stObjectiveType = mObjective.getViewType();
            if (workoutLineHashMap != null ){
                for (WorkoutLine current: workoutLineList) {
                    if (current != null && current.getObjectiveId().equals(mWorkoutLine.getObjectiveId()) ){
                        stEntryValue = "" + current.getEntryValue();
                    }
                } // for (WorkoutLine
            }// if workoutLineHashMap != null

        } // if mObjective != null


        // Set GUI elements
        objectiveViewHolder.tvLabel.setText(stObjectiveName);
        if (stObjectiveType.equalsIgnoreCase("Time") ){
            objectiveViewHolder.etValue.setHint(R.string.time_entry_hint);
            objectiveViewHolder.etValue.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME);
            objectiveViewHolder.etValue.setText(stEntryValue);
        }
        else{
            objectiveViewHolder.etValue.setHint(R.string.value_entry_hint);
            objectiveViewHolder.etValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL );
            objectiveViewHolder.etValue.setText(stEntryValue);
        }

        objectiveViewHolder.etValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateRow(pos, s.toString());
            }
        });
    }

    private void updateRow(int pos, String newValue){
        try {
            workoutLineList.get(pos).setEntryValue(Double.parseDouble(newValue));
        }
        catch(Exception e){

        }
    }

    class ItemViewActivity extends RecyclerView.ViewHolder {
        private LinearLayout placeHolder;
        private TextView tvLabel;
        private EditText etValue;


        ItemViewActivity(View v ){
            super(v);
            placeHolder = (LinearLayout) v.findViewById(R.id.mainHolder);
            tvLabel = (TextView) v.findViewById(R.id.tvLabel);
            etValue = (EditText) v.findViewById(R.id.etValue);
        }
    }// End ItemViewAcitivty Class



}
