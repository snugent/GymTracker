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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.admin1.gymtracker.R;
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
    private String workoutId;

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
        Double dblEntryVal = null;
        int    iEntryVal;
        int    iHours;
        int    iMins;
        int    iSecs;
        int    iMilis;
        final int HOURS_MILI = 360000;
        final int MINS_MILI = 6000;
        final int SECS_MILI = 100;



        //Get the Name of the objective
        mWorkoutLine = workoutLineList.get(pos);
        mObjective = objectives.get(mWorkoutLine.getObjectiveId());

        if (mObjective != null){
            stObjectiveName = mObjective.getLabel();
            stObjectiveType = mObjective.getViewType();
            if (workoutLineHashMap != null ){
                for (WorkoutLine current: workoutLineList) {
                    if (current != null
                        && current.getObjectiveId().equals(mWorkoutLine.getObjectiveId())
                        && current.getEntryValue() >= 0){
                        stEntryValue = "" + current.getEntryValue();
                        dblEntryVal  = current.getEntryValue();
                    }
                } // for (WorkoutLine
            }// if workoutLineHashMap != null

        } // if mObjective != null
        else{
            stEntryValue = null;
        }


        // Set GUI elements
        objectiveViewHolder.tvLabel.setText(stObjectiveName);
        if (stObjectiveType.equalsIgnoreCase("Time") ){
            //Disable default
            objectiveViewHolder.etValue.setVisibility(View.GONE);
            // Enable time entry element
            objectiveViewHolder.etHours.setVisibility(View.VISIBLE);
            objectiveViewHolder.tvHours.setVisibility(View.VISIBLE);
            objectiveViewHolder.etMins.setVisibility(View.VISIBLE);
            objectiveViewHolder.tvMins.setVisibility(View.VISIBLE);
            objectiveViewHolder.etSecs.setVisibility(View.VISIBLE);
            objectiveViewHolder.tvSecs.setVisibility(View.VISIBLE);
            objectiveViewHolder.etMili.setVisibility(View.VISIBLE);


            // Set fields to be numbers
            objectiveViewHolder.etHours.setInputType(InputType.TYPE_CLASS_NUMBER);
            objectiveViewHolder.etMins.setInputType(InputType.TYPE_CLASS_NUMBER);
            objectiveViewHolder.etSecs.setInputType(InputType.TYPE_CLASS_NUMBER);
            objectiveViewHolder.etMili.setInputType(InputType.TYPE_CLASS_NUMBER);

            if (dblEntryVal != null){
                iEntryVal = dblEntryVal.intValue();
                iHours = iEntryVal / HOURS_MILI;
                iMins = (iEntryVal - (iHours * HOURS_MILI)) / MINS_MILI;
                iSecs = (iEntryVal - (iHours * HOURS_MILI) - (iMins * MINS_MILI)) / 100;
                iMilis = (iEntryVal - (iHours * HOURS_MILI) - (iMins * MINS_MILI) - (iSecs * SECS_MILI));


                stEntryValue = "" + String.format("%02d", iHours);
                objectiveViewHolder.etHours.setText(stEntryValue);
                stEntryValue = "" + String.format("%02d", iMins);
                objectiveViewHolder.etMins.setText(stEntryValue);
                stEntryValue = "" + String.format("%02d", iSecs);
                objectiveViewHolder.etSecs.setText(stEntryValue);
                stEntryValue = "" + String.format("%02d", iMilis);
                objectiveViewHolder.etMili.setText(stEntryValue);
            }


            // objectiveViewHolder.etValue.setText(stEntryValue);
        }
        else{
            //Disable default
            objectiveViewHolder.etValue.setVisibility(View.VISIBLE);
            // Enable time entry element
            objectiveViewHolder.etHours.setVisibility(View.GONE);
            objectiveViewHolder.tvHours.setVisibility(View.GONE);
            objectiveViewHolder.etMins.setVisibility(View.GONE);
            objectiveViewHolder.tvMins.setVisibility(View.GONE);
            objectiveViewHolder.etSecs.setVisibility(View.GONE);
            objectiveViewHolder.tvSecs.setVisibility(View.GONE);
            objectiveViewHolder.etMili.setVisibility(View.GONE);

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
        private EditText etHours;
        private TextView tvHours;
        private EditText etMins;
        private TextView tvMins;
        private EditText etSecs;
        private TextView tvSecs;
        private EditText etMili;

        ItemViewActivity(View v ){
            super(v);
            placeHolder = (LinearLayout) v.findViewById(R.id.mainHolder);
            tvLabel = (TextView) v.findViewById(R.id.tvLabel);
            etValue = (EditText) v.findViewById(R.id.etValue);
            etHours = (EditText) v.findViewById(R.id.etHours);
            tvHours = (TextView) v.findViewById(R.id.tvHours);
            etMins = (EditText) v.findViewById(R.id.etMins);
            tvMins = (TextView) v.findViewById(R.id.tvMins);
            etSecs = (EditText) v.findViewById(R.id.etSecs);
            tvSecs = (TextView) v.findViewById(R.id.tvSecs);
            etMili = (EditText) v.findViewById(R.id.etMili);
        }
    }// End ItemViewAcitivty Class



}
