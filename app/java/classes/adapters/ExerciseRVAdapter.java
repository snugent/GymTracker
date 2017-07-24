package com.example.admin1.gymtracker.adapters;

import android.app.DialogFragment;
import android.app.FragmentManager;
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
import com.example.admin1.gymtracker.fragments.MessageDialog;
import com.example.admin1.gymtracker.models.Exercise;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by admin1 on 01/07/2017.
 */

public class ExerciseRVAdapter extends RecyclerView.Adapter<ExerciseRVAdapter.ItemViewActivity>{
    private FirebaseDatabase mFirbaseDatabase;
    private FragmentManager fmError;
    private Context mContext;
    private OnItemClickListener mItemClickListener;
    private HashMap<String, Exercise> exercises;
    private List<Exercise> exerciseList;
    private List<String> keysList;

    private final String TAG = "ExerciseRVAdapter";
    private DatabaseReference tblRecord;
    private HashMap<String, String> exerciseUsedLists;

    public ExerciseRVAdapter(HashMap<String, Exercise> exercises, DatabaseReference tblRecord,
                             Context mContext, FirebaseDatabase mFirbaseDatabase,
                             FragmentManager fmError){
        this.exercises = exercises;
        this.tblRecord = tblRecord;
        this.mContext    = mContext;
        this.mFirbaseDatabase = mFirbaseDatabase;
        this.fmError          = fmError;
        keysList = new ArrayList<>(exercises.keySet());
        exerciseList = new ArrayList<>(exercises.values());
        getUsedExercises();

    }

    public interface OnItemClickListener {
        void onItemClick(View view, String id);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    @Override
    public int getItemCount() {
        return exercises.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ItemViewActivity onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        return new ItemViewActivity(v);
    }

    @Override
    public void onBindViewHolder(ItemViewActivity exerciseViewHolder, final int pos) {
        Exercise mExercise;
        mExercise= exerciseList.get(pos);
        exerciseViewHolder.tvHeading.setText(mExercise.getName());
        exerciseViewHolder.tvDetail.setText(mExercise.getType());

        exerciseViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeleteConfirmation("", pos);
            }
        });
    }

    private void deleteRow(int index ){
        String stKey = keysList.get(index);

        try{
            // Check if exercises that are used in workouts
            if ((exerciseUsedLists == null) || (
                exerciseUsedLists.get(stKey) != null &&
                exerciseUsedLists.get(stKey).equals("false")) ) {

                // Delete the row
                exerciseList.remove(index);
                keysList.remove(index);
                exercises.remove(stKey);
                notifyItemRemoved(index);
                tblRecord.getRef().child(stKey).removeValue();
            }
            // Dont delete exercises that are used in workouts
            else{
                DialogFragment mFragment = MessageDialog.newInstance(R.string.error_title_bar,
                        R.drawable.ic_error_24dp,
                        mContext.getString(R.string.error_exercise_delete1));


                mFragment.show(fmError, "dialog");
            }
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

    //Check if an exercise is used in a workout for delete prevention
    private void getUsedExercises(){
        //Get the current workout id
        Query mQuery = mFirbaseDatabase.getReference().child("IdxExWrk");

        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    exerciseUsedLists = new HashMap<String, String>();
                    for (int iCnt = 0; iCnt < keysList.size(); iCnt++)
                        if (dataSnapshot.hasChild(keysList.get(iCnt))) {
                            exerciseUsedLists.put(keysList.get(iCnt), "true");
                        }
                        else {
                            exerciseUsedLists.put(keysList.get(iCnt), "false");
                        }// else
                } // for
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Data Write Error");

            }
        });

    }// End of method

    class ItemViewActivity extends RecyclerView.ViewHolder implements View.OnClickListener{
        private LinearLayout placeHolder;
        private TextView tvHeading;
        private TextView tvDetail;
        private ImageView btnDelete;


        ItemViewActivity(View v ){
            super(v);
            placeHolder = (LinearLayout) v.findViewById(R.id.mainHolder);
            placeHolder.setOnClickListener(this);
            tvHeading = (TextView) v.findViewById(R.id.tvHeading);
            tvDetail  = (TextView) v.findViewById((R.id.tvDetail));
            btnDelete = (ImageView) v.findViewById(R.id.btnDelete);

        }
        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view,keysList.get(getAdapterPosition()));
            }
        }
    }// End ItemViewAcitivty Class

}// End ExerciseRVAdapter class
