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
import com.example.admin1.gymtracker.models.WorkoutEntry;
import com.google.firebase.database.DatabaseReference;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin1 on 04/07/2017.
 */

public class WorkoutEntryRVAdapter extends RecyclerView.Adapter<WorkoutEntryRVAdapter.ItemViewActivity>{
    private WorkoutEntryRVAdapter.OnItemClickListener mItemClickListener;
    private HashMap<String, WorkoutEntry> workoutEntrys;
    private List<WorkoutEntry> workoutEntryList;
    private List<String> keysList;

    private final String TAG = "WorkoutEntryRVAdapter";
    private DatabaseReference tblRecord;

    public WorkoutEntryRVAdapter(HashMap<String, WorkoutEntry> workoutEntrys, DatabaseReference tblRecord){
        this.workoutEntrys = workoutEntrys;
        this.tblRecord = tblRecord;
        keysList = new ArrayList<>(workoutEntrys.keySet());
        workoutEntryList = new ArrayList<>(workoutEntrys.values());
    }

    public interface OnItemClickListener {
        void onItemClick(View view, String id);
    }

    public void setOnItemClickListener(final WorkoutEntryRVAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    @Override
    public int getItemCount() {
        return workoutEntrys.size();
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
        WorkoutEntry mWorkoutEntry;
        mWorkoutEntry= workoutEntryList.get(pos);

        workoutEntryViewHolder.tvHeading.setText(mWorkoutEntry.getEntryId());
        workoutEntryViewHolder.tvDetail.setText(mWorkoutEntry.getExerciseId());
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
            workoutEntryList.remove(index);
            keysList.remove(index);
            workoutEntrys.remove(stKey);
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

}
