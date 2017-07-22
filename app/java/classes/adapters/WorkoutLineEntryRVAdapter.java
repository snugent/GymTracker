package com.example.admin1.gymtracker.adapters;

import android.content.Context;
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

public class WorkoutLineEntryRVAdapter extends RecyclerView.Adapter<WorkoutLineEntryRVAdapter.ItemViewActivity> {
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

    // variables for time fields
    private static final int HOURS_MILI = 3600000;
    private static final int MINS_MILI =    60000;
    private static final int SECS_MILI =     1000;


    /* methods*/
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
    public void onBindViewHolder(final ItemViewActivity mView, final int pos) {
        // Database variables
        WorkoutLine mWorkoutLine;
        Objective mObjective;
        String stObjectiveName = "";
        String stObjectiveType = "";
        String stEntryValue    = "";
        Double dblEntryVal = null;

        // variables for time fields
        int    iEntryVal;
        int    iHours;
        int    iMins;
        int    iSecs;
        int    iMilis;

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
        mView.tvLabel.setText(stObjectiveName);
        if (stObjectiveType.equalsIgnoreCase("Time") ){
            //Disable default
            mView.etValue.setVisibility(View.GONE);
            // Enable time entry element
            mView.etHours.setVisibility(View.VISIBLE);
            mView.tvHours.setVisibility(View.VISIBLE);
            mView.etMins.setVisibility(View.VISIBLE);
            mView.tvMins.setVisibility(View.VISIBLE);
            mView.etSecs.setVisibility(View.VISIBLE);
            mView.tvSecs.setVisibility(View.VISIBLE);
            mView.etMili.setVisibility(View.VISIBLE);


            // Set fields to be numbers
            mView.etHours.setInputType(InputType.TYPE_CLASS_NUMBER);
            mView.etMins.setInputType(InputType.TYPE_CLASS_NUMBER);
            mView.etSecs.setInputType(InputType.TYPE_CLASS_NUMBER);
            mView.etMili.setInputType(InputType.TYPE_CLASS_NUMBER);

            if (dblEntryVal != null){
                iEntryVal = dblEntryVal.intValue();
                iHours = iEntryVal / HOURS_MILI;
                iMins = (iEntryVal - (iHours * HOURS_MILI)) / MINS_MILI;
                iSecs = (iEntryVal - (iHours * HOURS_MILI) - (iMins * MINS_MILI)) / SECS_MILI;
                iMilis = (iEntryVal - (iHours * HOURS_MILI) - (iMins * MINS_MILI) - (iSecs * SECS_MILI));

                // Show the valus inthe fields
                stEntryValue = "" + String.format("%02d", iHours);
                mView.etHours.setText(stEntryValue);
                stEntryValue = "" + String.format("%02d", iMins);
                mView.etMins.setText(stEntryValue);
                stEntryValue = "" + String.format("%02d", iSecs);
                mView.etSecs.setText(stEntryValue);
                stEntryValue = "" + String.format("%03d", iMilis);
                mView.etMili.setText(stEntryValue);
            }

            //watch for changes to the fields
            mView.etHours.addTextChangedListener(getTextChanged(mView, pos));
            mView.etMins.addTextChangedListener(getTextChanged(mView, pos));
            mView.etSecs.addTextChangedListener(getTextChanged(mView, pos));
            mView.etMili.addTextChangedListener(getTextChanged(mView, pos));
        }
        else{
            //Disable default
            mView.etValue.setVisibility(View.VISIBLE);
            // Enable time entry element
            mView.etHours.setVisibility(View.GONE);
            mView.tvHours.setVisibility(View.GONE);
            mView.etMins.setVisibility(View.GONE);
            mView.tvMins.setVisibility(View.GONE);
            mView.etSecs.setVisibility(View.GONE);
            mView.tvSecs.setVisibility(View.GONE);
            mView.etMili.setVisibility(View.GONE);

            mView.etValue.setHint(R.string.value_entry_hint);
            mView.etValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL );
            mView.etValue.setText(stEntryValue);

            // Watch for changes to the field
            mView.etValue.addTextChangedListener(getTextChanged(mView, pos));
        }

    }

    private void updateRow(int pos, String newValue, ItemViewActivity v){
        WorkoutLine currentLine = workoutLineHashMap.get(workoutLineKeysList.get(pos));
        Objective   currentObjecitve = objectives.get(currentLine.getObjectiveId());
        // variables for time fields
        double dblEntryVal = -1;
        int    iHours;
        int    iMins;
        int    iSecs;
        int    iMilis;

        if (currentObjecitve.getViewType().equalsIgnoreCase("Time")){
            if (!v.etHours.getText().toString().equals("") ){
                iHours = Integer.parseInt(v.etHours.getText().toString());
                dblEntryVal = (iHours * HOURS_MILI);
            }
            else {
                iHours = 0;
                dblEntryVal = (iHours * HOURS_MILI);
            }
            if (!v.etMins.getText().toString().equals("")){
                iMins = Integer.parseInt(v.etMins.getText().toString());
                dblEntryVal = dblEntryVal + (iMins * MINS_MILI);
            }
            else{
                iMins = 0;
                dblEntryVal = dblEntryVal + (iMins * MINS_MILI);

            }

            if (!v.etSecs.getText().toString().equals("")){
                iSecs = Integer.parseInt(v.etSecs.getText().toString());
                dblEntryVal = dblEntryVal + (iSecs * SECS_MILI);
            }
            else{
                iSecs = 0;
                dblEntryVal = dblEntryVal + (iSecs * SECS_MILI);
            }

            if (!v.etMili.getText().toString().equals("")){
                iMilis = Integer.parseInt(v.etMili.getText().toString());
                dblEntryVal = dblEntryVal + iMilis;
            }
            else{
                iMilis = 0;
                dblEntryVal = dblEntryVal + iMilis;
            }

            //if No time entered set dblentry value to -1 (equals null)
            if (dblEntryVal == 0){
                dblEntryVal = -1;
            }


            workoutLineList.get(pos).setEntryValue(dblEntryVal);
        }
        else{
            if (!v.etValue.getText().toString().equals("")){
                dblEntryVal = Double.parseDouble(v.etValue.getText().toString());
            }
            workoutLineList.get(pos).setEntryValue(dblEntryVal);

        }//else
    }// update row

    // If the user updates a field add the changes to the hashmap for saving
    private TextWatcher getTextChanged(final ItemViewActivity mView, final int pos){
        TextWatcher tw =     new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateRow(pos, s.toString(), mView);
            }
        };
        return tw;
    } // getTextChagned

    public void setErrorMsg(String stMessage, int pos, RecyclerView.ViewHolder vh){
        WorkoutLineEntryRVAdapter.ItemViewActivity v = (ItemViewActivity) vh;


        if (objectiveList != null){
            v.setErrorMsg(stMessage, objectiveList.get(pos).getViewType());
        }
    }

    class ItemViewActivity extends RecyclerView.ViewHolder{
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
        public void setErrorMsg(String stMessage, String stType){

            if (stType.equals("Time")){
                etHours.setError(stMessage);
            }
            else{
                etValue.setError(stMessage);
            }
        }
    }// End ItemViewActivity Class




}
