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
import com.example.admin1.gymtracker.models.Objective;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by admin1 on 02/07/2017.
 */

public class ObjectiveRVAdapter extends RecyclerView.Adapter<ObjectiveRVAdapter.ItemViewActivity>{
    private OnItemClickListener mItemClickListener;
    private HashMap<String, Objective> objectives;
    private List<Objective> objectiveList;
    private List<String> keysList;

    private final String TAG = "ObjectiveRVAdapter";
    private DatabaseReference tblRecord;

    public ObjectiveRVAdapter(HashMap<String, Objective> objectives, DatabaseReference tblRecord){
        this.objectives = objectives;
        this.tblRecord = tblRecord;
        keysList = new ArrayList<>(objectives.keySet());
        objectiveList = new ArrayList<>(objectives.values());
    }

    public interface OnItemClickListener {
        void onItemClick(View view, String id);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    @Override
    public int getItemCount() {
        return objectives.size();
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
    public void onBindViewHolder(ItemViewActivity objectiveViewHolder, final int pos) {
        Objective mObjective;
        mObjective= objectiveList.get(pos);
        objectiveViewHolder.tvHeading.setText(mObjective.getName());
        objectiveViewHolder.tvDetail.setText(mObjective.getViewType());

        objectiveViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRow(pos);
            }
        });
    }

    private void deleteRow(int index ){
        String stKey = keysList.get(index);
        try{
            objectiveList.remove(index);
            keysList.remove(index);
            objectives.remove(stKey);
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