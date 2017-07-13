package com.example.admin1.gymtracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.models.Exercise;
import com.example.admin1.gymtracker.models.Objective;
import com.example.admin1.gymtracker.models.WorkoutLine;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin1 on 04/07/2017.
 */

public class WorkoutEntryRVAdapter extends RecyclerView.Adapter<WorkoutEntryRVAdapter.ItemViewActivity>{
    private WorkoutEntryRVAdapter.OnItemClickListener mItemClickListener;
    private HashMap<String, WorkoutLine> workoutLines;
    private List<WorkoutLine> workoutLinesList;
    private List<String> keysList;
    private String stWorkoutId;

    private HashMap<String, Exercise> exercises;
    private HashMap<String, Objective> objectives;
    private HashMap<String, String> chosenExercises;
    private List<String> chosenExercisesList;

    private final String TAG = "WorkoutEntryRVAdapter";
    private DatabaseReference tblHeadRef;
    private DatabaseReference tblLineRef;

    public WorkoutEntryRVAdapter(HashMap<String, WorkoutLine> workoutLines,
                                 HashMap<String, Exercise> exercises, HashMap<String, Objective> objectives,
                                 DatabaseReference tblHeadRef, DatabaseReference tblLineRef,
                                 String stWorkoutId){
        this.workoutLines = workoutLines;
        this.tblLineRef = tblLineRef;
        this.tblHeadRef = tblHeadRef;
        keysList = new ArrayList<>(workoutLines.keySet());
        workoutLinesList = new ArrayList<>(workoutLines.values());
        this.exercises = exercises;
        this.objectives = objectives;
        this.stWorkoutId = stWorkoutId;
        chosenExercises = new HashMap<>();
        chosenExercisesList = new ArrayList<>();

        for (WorkoutLine item : workoutLinesList) {
            if (chosenExercises == null || chosenExercises.get(item.getExerciseId()) == null){
                chosenExercises.put(item.getExerciseId(),item.getExerciseId());
                chosenExercisesList.add(item.getExerciseId());
            }
        }

    }

    public interface OnItemClickListener {
        void onItemClick(View view, String workoutId, String exerciseId);
    }

    public void setOnItemClickListener(final WorkoutEntryRVAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    @Override
    public int getItemCount() {
        return chosenExercisesList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public WorkoutEntryRVAdapter.ItemViewActivity onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        return new WorkoutEntryRVAdapter.ItemViewActivity(v);
    }

    @Override
    public void onBindViewHolder(WorkoutEntryRVAdapter.ItemViewActivity workoutEntryViewHolder, final int pos) {
        //WorkoutLine mWorkoutEntry;
        //mWorkoutEntry           = workoutLinesList.get(pos);
        ;
        //String stLineNo         = Integer.toString(pos);
        FirebaseDatabase dbRef  =  FirebaseDatabase.getInstance();
        Exercise curentEx       = exercises.get(chosenExercisesList.get(pos));
        //Objective curentObj     = objectives.get(mWorkoutEntry.getObjectiveId());



        workoutEntryViewHolder.tvHeading.setText(curentEx.getName());
        workoutEntryViewHolder.tvDetail.setText("");
        workoutEntryViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRow(pos);
            }
        });
    }

    private void deleteRow(int index ){
        String stKey = keysList.get(index);
        try{
            workoutLinesList.remove(index);
            keysList.remove(index);
            workoutLines.remove(stKey);
            notifyItemRemoved(index);
            tblLineRef.getRef().child(stKey).removeValue();
        }
        catch (Exception e){
            Log.d(TAG, "Delete Exception");
        }

    }


    class ItemViewActivity extends RecyclerView.ViewHolder implements View.OnClickListener{
        private LinearLayout placeHolder;
        private TextView tvHeading;
        private TextView tvDetail;
        private ImageButton btnDelete;


        ItemViewActivity(View v ){
            super(v);
            placeHolder = (LinearLayout) v.findViewById(R.id.mainHolder);
            placeHolder.setOnClickListener(this);
            tvHeading = (TextView) v.findViewById(R.id.tvHeading);
            tvDetail  = (TextView) v.findViewById((R.id.tvDetail));
            btnDelete = (ImageButton) v.findViewById(R.id.btnDelete);

        }
        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, stWorkoutId, chosenExercisesList.get(getAdapterPosition()) );
            }
        }
    }// End ItemViewAcitivty Class

}
