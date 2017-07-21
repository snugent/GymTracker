package com.example.admin1.gymtracker.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.models.Exercise;
import com.example.admin1.gymtracker.models.WorkoutLine;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin1 on 04/07/2017.
 */

public class WorkoutEntryRVAdapter extends RecyclerView.Adapter<WorkoutEntryRVAdapter.ItemViewActivity>{
    private FirebaseDatabase mFirebaseDatabase;
    private WorkoutEntryRVAdapter.OnItemClickListener mItemClickListener;
    private HashMap<String, WorkoutLine> workoutLines;
    private List<WorkoutLine> workoutLinesList;
    private List<String> keysList;
    private String stWorkoutId;
    private Context mContext;

    private HashMap<String, Exercise> exercises;
    private HashMap<String, String> chosenExercises;
    private List<String> chosenExercisesList;

    private final String TAG = "WorkoutEntryRVAdapter";
    private DatabaseReference tblLineRef;

    public WorkoutEntryRVAdapter(HashMap<String, WorkoutLine> workoutLines,
                                 HashMap<String, Exercise> exercises,
                                 DatabaseReference tblLineRef,
                                 String stWorkoutId,
                                 Context mContext,
                                 FirebaseDatabase mFirebaseDatabase){
        this.workoutLines = workoutLines;
        this.tblLineRef = tblLineRef;
        keysList = new ArrayList<>(workoutLines.keySet());
        workoutLinesList = new ArrayList<>(workoutLines.values());

        this.exercises = exercises;
        this.stWorkoutId = stWorkoutId;
        this.mContext    = mContext;
        chosenExercises = new HashMap<>();
        chosenExercisesList = new ArrayList<>();
        this.mFirebaseDatabase = mFirebaseDatabase;

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
        Exercise curentEx       = exercises.get(chosenExercisesList.get(pos));

        workoutEntryViewHolder.tvHeading.setText(curentEx.getName());
        workoutEntryViewHolder.tvDetail.setText("");
        workoutEntryViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeleteConfirmation("", pos);
            }
        });
    }

    private void deleteRow(int index ){
        String stKey = chosenExercisesList.get(index);
        DatabaseReference tblIdxExWrk;
        DatabaseReference tblIdxObjWrk;

        int iCnt;
        try{
            iCnt = 0;
            for (WorkoutLine item : workoutLinesList) {
                if (stKey.equals(item.getExerciseId())){
                    // Remove workout Id Item from Exercise Workout Index Table
                    tblIdxExWrk =  mFirebaseDatabase.getReference().child("IdxExWrk").child(item.getExerciseId());

                    if (tblIdxExWrk != null){
                        tblIdxExWrk.getRef().child(stWorkoutId).removeValue();
                    }

                    // Remove workout Id Item from Objective Workout Index Table
                    tblIdxObjWrk = mFirebaseDatabase.getReference().child("IdxObjWrk").child(item.getObjectiveId());

                    if (tblIdxObjWrk != null){
                        tblIdxObjWrk.getRef().child(stWorkoutId).removeValue();
                    }

                    tblLineRef.getRef().child(keysList.get(iCnt)).removeValue();
                    workoutLines.remove(keysList.get(iCnt));
                }
                iCnt++;
            }
            keysList = new ArrayList<>(workoutLines.keySet());
            workoutLinesList = new ArrayList<>(workoutLines.values());
            keysList.remove(index);
            notifyItemRemoved(index);
        }
        catch (Exception e){
            Log.d(TAG, mContext.getString(R.string.delete_exception));
        }

    }

    //Get confirmation of delete
    private void getDeleteConfirmation(String stConfirmMsg, final int iPos){
        String stMessage;

        AlertDialog mAdConfirm;
        AlertDialog.Builder mAdbConfirm;

        mAdbConfirm = new AlertDialog.Builder(mContext);
        if (stConfirmMsg.equals("")){
            stMessage = mContext.getResources().getString(R.string.confirm_general);
        }
        else{
            stMessage = stConfirmMsg;
        }


        mAdbConfirm.setMessage(stMessage)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteRow(iPos);
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setCancelable(false) ;


        mAdConfirm = mAdbConfirm.create();
        mAdConfirm.show();
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
