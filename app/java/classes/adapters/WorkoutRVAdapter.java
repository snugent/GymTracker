package com.example.admin1.gymtracker.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.activities.BaseClass;
import com.example.admin1.gymtracker.fragments.ConfirmDialog;
import com.example.admin1.gymtracker.models.Member;
import com.example.admin1.gymtracker.models.Workout;
import com.example.admin1.gymtracker.models.WorkoutLine;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin1 on 04/07/2017.
 */

public class WorkoutRVAdapter extends RecyclerView.Adapter<WorkoutRVAdapter.ItemViewActivity>{

    private FirebaseDatabase mFirebaseDatabase;
    private WorkoutRVAdapter.OnItemClickListener mItemClickListener;
    private HashMap<String, Workout> workouts;
    private List<Workout> workoutList;
    private List<String> keysList;
    private Context mContext;
    private HashMap<String, WorkoutLine> workoutLines;
    private List<WorkoutLine> workoutLineList;
    private List<String> keysLineList;

    private final String TAG = "WorkoutRVAdapter";
    private DatabaseReference tblRecord;
    boolean blConfirm;

    public WorkoutRVAdapter(HashMap<String, Workout> workouts, DatabaseReference tblRecord,
                            Context mContext, FirebaseDatabase mFirebaseDatabase){
        this.workouts = workouts;
        this.tblRecord = tblRecord;
        keysList = new ArrayList<>(workouts.keySet());
        workoutList = new ArrayList<>(workouts.values());
        this.mContext    = mContext;
        this.mFirebaseDatabase = mFirebaseDatabase;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, String id);
    }

    public void setOnItemClickListener(final WorkoutRVAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    @Override
    public int getItemCount() {
        return workouts.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public WorkoutRVAdapter.ItemViewActivity onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        return new WorkoutRVAdapter.ItemViewActivity(v);
    }

    @Override
    public void onBindViewHolder(WorkoutRVAdapter.ItemViewActivity workoutViewHolder, final int pos) {
        Workout mWorkout;
        mWorkout= workoutList.get(pos);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
        DateTime dtWorkout = fmt.parseDateTime(mWorkout.getWorkoutDate());
        String stDayName = dtWorkout.dayOfWeek().getAsText();
        workoutViewHolder.tvHeading.setText(stDayName);
        workoutViewHolder.tvDetail.setText(mWorkout.getWorkoutDate());


        workoutViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeleteConfirmation("", pos);
            }
        });

        workoutViewHolder.btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCopyConfirmation(mContext.getString(R.string.confirm_copy_workout), pos, v);
            }
        });
    }

    //Copy an existing workout
    private void copyWorkout(int index, final View v ){
        final String fromKey = keysList.get(index);


        //Get the current workout id
        Query qryCopy = tblRecord.getRef().child(fromKey);


        qryCopy.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String toKey = null;
                DatabaseReference dbRef;

                if (dataSnapshot.exists()){
                    int iLocation = -1;
                    Workout newWorkout = dataSnapshot.getValue(Workout.class);
                    dbRef = tblRecord.push();
                    dbRef.setValue(dataSnapshot.getValue());
                    toKey = dbRef.getKey();
                    keysList.add(toKey);
                    workoutList.add(newWorkout);
                    for (int iCnt = 0; iCnt < workoutList.size(); iCnt++){
                        if (keysList.get(iCnt).equals(toKey)){
                            iLocation = iCnt;
                        }
                    }
                    if (iLocation >= 0){
                        mItemClickListener.onItemClick(v,keysList.get(iLocation));
                    } // if (iLocation >= 0
                }// if datasnapshot.exists
            }//onDataChanged

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Data Write Error");

            }
        });
    }


    // Delete chosen row
    private void deleteRow(int index ){
        String stKey = keysList.get(index);
        DatabaseReference tblIdxExWrk;
        DatabaseReference tblIdxObjWrk;

        try{
            // Remove workout Id Item from Exercise Workout Index Table
            tblIdxExWrk =  mFirebaseDatabase.getReference().child("IdxExWrk");
            if (tblIdxExWrk != null){
                deleteIdxExWork(stKey, tblIdxExWrk);
            }

            // Remove workout Id Item from Objective Workout Index Table
            tblIdxObjWrk = mFirebaseDatabase.getReference().child("IdxObjWrk");

            if (tblIdxObjWrk != null){
                deleteIdxObjWrk(stKey, tblIdxObjWrk);
            }

            workoutList.remove(index);
            keysList.remove(index);
            workouts.remove(stKey);
            notifyItemRemoved(index);
            tblRecord.getRef().child(stKey).removeValue();

        }
        catch (Exception e){
            Log.d(TAG, mContext.getString(R.string.delete_exception));
        }
    }

    //Get confirmation of delete
    //Cant use fragment as this is not an activity
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
                .setIcon(R.drawable.ic_help_24dp)
                .setTitle(mContext.getString(R.string.confirm_title_bar))
                .setMessage(stMessage)
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


    //Get confirmation of copy
    //Cant use fragment as this is not an activity
    private void getCopyConfirmation(String stConfirmMsg, final int iPos, final View v){
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
                .setIcon(R.drawable.ic_help_24dp)
                .setTitle(mContext.getString(R.string.confirm_title_bar))
                .setMessage(stMessage)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        copyWorkout(iPos, v);
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

    private void deleteIdxExWork(final String ipStKey, final DatabaseReference tblIdxExWrk){
        //Get the current workout id
        Query qryLines = tblRecord.getRef().child(ipStKey).child("WorkoutLine");


        qryLines.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String deleteKey;
                for (DataSnapshot currentItem : dataSnapshot.getChildren()){
                    WorkoutLine currentLIne = currentItem.getValue(WorkoutLine.class);
                    deleteKey = currentLIne.getExerciseId();

                    if (deleteKey != null){
                        tblIdxExWrk.child(deleteKey).child(ipStKey).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "IdxExWrk Delete Error");

            }
        });
    }

    private void deleteIdxObjWrk(final String ipStKey, final DatabaseReference tblIdxObjWrk){
        //Get the current workout id
        Query qryLines = tblRecord.getRef().child(ipStKey).child("WorkoutLine");

        qryLines.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String deleteKey;
                for (DataSnapshot currentItem : dataSnapshot.getChildren()){
                    WorkoutLine currentLIne = currentItem.getValue(WorkoutLine.class);
                    deleteKey = currentLIne.getObjectiveId();

                    if (deleteKey != null){
                        tblIdxObjWrk.child(deleteKey).child(ipStKey).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "IdxObjWrk Delete Error");

            }
        });
    }


    class ItemViewActivity extends RecyclerView.ViewHolder implements View.OnClickListener{
        private LinearLayout placeHolder;
        private TextView tvHeading;
        private TextView tvDetail;
        private ImageView btnDelete;
        private ImageView btnCopy;


        ItemViewActivity(View v ){
            super(v);
            placeHolder = (LinearLayout) v.findViewById(R.id.mainHolder);
            placeHolder.setOnClickListener(this);
            tvHeading = (TextView) v.findViewById(R.id.tvHeading);
            tvDetail  = (TextView) v.findViewById((R.id.tvDetail));
            btnDelete = (ImageView) v.findViewById(R.id.btnDelete);
            btnCopy   = (ImageView) v.findViewById(R.id.btnCopy);
            btnCopy.setVisibility(View.VISIBLE);

        }
        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view,keysList.get(getAdapterPosition()));
            }
        }
    }// End ItemViewAcitivty Class

}
