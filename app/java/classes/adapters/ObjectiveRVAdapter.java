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
import com.example.admin1.gymtracker.models.Objective;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by admin1 on 02/07/2017.
 */

public class ObjectiveRVAdapter extends RecyclerView.Adapter<ObjectiveRVAdapter.ItemViewActivity>{
    private FirebaseDatabase mFirbaseDatabase;
    private FragmentManager fmError;
    private Context mContext;
    private OnItemClickListener mItemClickListener;
    private HashMap<String, Objective> objectives;
    private List<Objective> objectiveList;
    private List<String> keysList;

    private final String TAG = "ObjectiveRVAdapter";
    private DatabaseReference tblRecord;

    public ObjectiveRVAdapter(HashMap<String, Objective> objectives, DatabaseReference tblRecord,
                              Context mContext, FirebaseDatabase mFirbaseDatabase,
                              FragmentManager fmError){
        this.objectives = objectives;
        this.tblRecord = tblRecord;
        this.mContext    = mContext;
        this.mFirbaseDatabase = mFirbaseDatabase;
        this.fmError          = fmError;
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
             getDeleteConfirmation("", pos);
            }
        });
    }

    private void deleteRow(int index ){

        String stKey = keysList.get(index);
        try{
            // Check if objective that are used in workouts
            if (mFirbaseDatabase.getReference().child("IdxObjWrk").child(stKey) == null) {
                objectiveList.remove(index);
                keysList.remove(index);
                objectives.remove(stKey);
                notifyItemRemoved(index);
                tblRecord.getRef().child(stKey).removeValue();
            }
            // Dont delete objectives that are used in workouts
            else{
                DialogFragment mFragment = MessageDialog.newInstance(R.string.error_title_bar,
                        R.drawable.ic_error_24dp,
                        mContext.getString(R.string.error_objective_delete1));


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

}
