package com.example.admin1.gymtracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.models.ExerciseObjective;
import com.example.admin1.gymtracker.models.Objective;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by admin1 on 01/07/2017.
 */

public class ExerciseObjectiveRVAdapter extends RecyclerView.Adapter<ExerciseObjectiveRVAdapter.ItemViewActivity>{
    private OnItemClickListener mItemClickListener;
    private HashMap<String, ExerciseObjective> exerciseObjectives;
    private List<ExerciseObjective> exerciseObjectveList;
    private List<String> keysList;

    private final String TAG = "ExerciseObjRVAdapter";
    private DatabaseReference tblRecord;

    private String stExerciseName;
    private String stObjectiveName;
    private FirebaseDatabase dbRef = FirebaseDatabase.getInstance();

    public ExerciseObjectiveRVAdapter(HashMap<String, ExerciseObjective> exerciseObjectives, DatabaseReference tblRecord){
        this.exerciseObjectives = exerciseObjectives;
        this.tblRecord = tblRecord;
        keysList = new ArrayList<>(exerciseObjectives.keySet());
        exerciseObjectveList = new ArrayList<>(exerciseObjectives.values());
    }

    public interface OnItemClickListener {
        void onItemClick(View view, String id);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        return new ItemViewActivity(v);
    }



    @Override
    public void onBindViewHolder(ItemViewActivity exerciseViewHolder, final int pos) {
        ExerciseObjective mExerciseObjective;
        DatabaseReference exerciseRef;
        DatabaseReference objectiveRef = dbRef.getReference().child("Objective").child(keysList.get(pos));


        mExerciseObjective = exerciseObjectveList.get(pos);


        // Get associated exercise
        exerciseRef        = dbRef.getReference().child("Exercise").child(keysList.get(pos));
        getExerciseName(exerciseRef);

        // Get associated Objective
        getObjectiveName(mExerciseObjective.getObjectiveId());
        //Objective obj = objectiveRef.Value;
        Log.d(TAG, "onBind " + stObjectiveName);

        //For now code
        stExerciseName = keysList.get(pos);
        stObjectiveName = mExerciseObjective.getObjectiveId();

        // Set GUI elements
        exerciseViewHolder.tvHeading.setText(stExerciseName);
        exerciseViewHolder.tvDetail.setText(stObjectiveName);
        exerciseViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRow(pos);
            }
        });
    }

    private void deleteRow(int index ){
        String stKey = keysList.get(index);
        try{
            exerciseObjectveList.remove(index);
            keysList.remove(index);
            exerciseObjectives.remove(stKey);
            notifyItemRemoved(index);
            tblRecord.getRef().child(stKey).removeValue();
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
                mItemClickListener.onItemClick(view,keysList.get(getAdapterPosition()));
            }
        }
    }// End ItemViewAcitivty Class

    // Queries the database and gets the current exercise name
    private void getExerciseName(DatabaseReference recordRef){
        stExerciseName = "Unknown";
      /*  recordRef.addListenerForSingleValueEvent(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Exercise exercise = dataSnapshot.getValue(Exercise.class);
                stExerciseName = exercise.getName();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "getName - Database Query Error");

            }
        });
            */
    }

    // Queries the database and gets the current objective name
    private void getObjectiveName(String key){
        DatabaseReference objectiveRef = dbRef.getReference().child("Objective").child(key);

        stObjectiveName = "";
        ValueEventListener velListen = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Objective objective = dataSnapshot.getValue(Objective.class);
                stObjectiveName = objective.getName();
                Log.d(TAG, "VALUE EVENTLISTENER" + objective.getName() + stObjectiveName);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        objectiveRef.addListenerForSingleValueEvent(velListen);
    }


}// End ExerciseObjectiveRVAdapter class
