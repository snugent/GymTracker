package com.example.admin1.gymtracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.models.ExerciseObjective;
import com.example.admin1.gymtracker.models.Objective;
import com.example.admin1.gymtracker.models.WorkoutLine;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin1 on 10/07/2017.
 */

public class WorkoutLineEntryRVAdapter extends RecyclerView.Adapter<WorkoutLineEntryRVAdapter.ItemViewActivity>{
    //ExerciseObjective variables
    private HashMap<String, ExerciseObjective> exerciseObjectives;
    private List<ExerciseObjective> exerciseObjectiveList;
    private List<String> exerciseKeysList;

    // Objecteives variables
    private HashMap<String, Objective> objectives;
    private List<Objective> objectiveList;
    private List<String> objectiveKeysList;

    // Workout Entry (Line) variables
    private HashMap<String, WorkoutLine> WorkoutLineHashMap;
    private List<WorkoutLine> WorkoutLineList;
    private List<String> WorkoutLineKeysList;

    private final String TAG = "WorkoutLineEntryRVAdapter";

    private FirebaseDatabase dbRef = FirebaseDatabase.getInstance();

    public WorkoutLineEntryRVAdapter(HashMap<String, ExerciseObjective> exerciseObjectives,
                                HashMap<String, Objective> objectives){
        // Set Exercise tables data
        this.exerciseObjectives = exerciseObjectives;
        exerciseKeysList = new ArrayList<>(exerciseObjectives.keySet());
        exerciseObjectiveList = new ArrayList<>(exerciseObjectives.values());

        //Set Objective table data
        this.objectives = objectives;
        objectiveKeysList = new ArrayList<>(objectives.keySet());
        objectiveList = new ArrayList<>(objectives.values());

    }


    @Override
    public int getItemCount() {
        return exerciseObjectives.size();
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
    public void onBindViewHolder(ItemViewActivity objectiveViewHolder, int pos) {
        ExerciseObjective mExerciseObjective;
        Objective mObjective;
        DatabaseReference objectiveRef = dbRef.getReference().child("Objective").child(exerciseKeysList.get(pos));
        String stObjectiveName = "";
        String stObjectiveType = "";

        //Get the Name of the objective
        mExerciseObjective = exerciseObjectiveList.get(pos);
        mObjective = objectives.get(mExerciseObjective.getObjectiveId());

        if (mObjective != null){
            stObjectiveName = mObjective.getLabel();
            stObjectiveType = mObjective.getViewType();
        }


        // Set GUI elements
        objectiveViewHolder.tvLabel.setText(stObjectiveName);
        if (stObjectiveType.equalsIgnoreCase("Time") ){
            objectiveViewHolder.etValue.setHint(R.string.time_entry_hint);
            objectiveViewHolder.etValue.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME);
        }
        else{
            objectiveViewHolder.etValue.setHint(R.string.value_entry_hint);
            objectiveViewHolder.etValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL );
        }

    }

    private boolean saveData(){
         return true;
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
