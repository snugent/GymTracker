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
import com.google.firebase.database.DatabaseReference;

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
        mExerciseObjective = exerciseObjectveList.get(pos);
        exerciseViewHolder.tvHeading.setText(mExerciseObjective.getObjectiveId());
        exerciseViewHolder.tvDetail.setText(keysList.get(pos));

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

}// End ExerciseObjectiveRVAdapter class
