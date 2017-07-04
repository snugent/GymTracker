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
import com.example.admin1.gymtracker.models.Workout;
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

public class WorkoutRVAdapter extends RecyclerView.Adapter<WorkoutRVAdapter.ItemViewActivity>{
    private WorkoutRVAdapter.OnItemClickListener mItemClickListener;
    private HashMap<String, Workout> workouts;
    private List<Workout> workoutList;
    private List<String> keysList;

    private final String TAG = "WorkoutRVAdapter";
    private DatabaseReference tblRecord;

    public WorkoutRVAdapter(HashMap<String, Workout> workouts, DatabaseReference tblRecord){
        this.workouts = workouts;
        this.tblRecord = tblRecord;
        keysList = new ArrayList<>(workouts.keySet());
        workoutList = new ArrayList<>(workouts.values());
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
                deleteRow(pos);
            }
        });
    }

    private void deleteRow(int index ){
        String stKey = keysList.get(index);
        try{
            workoutList.remove(index);
            keysList.remove(index);
            workouts.remove(stKey);
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
