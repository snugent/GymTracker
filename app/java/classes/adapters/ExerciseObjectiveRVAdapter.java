package com.example.admin1.gymtracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.admin1.gymtracker.R;
import com.example.admin1.gymtracker.models.Exercise;
import com.example.admin1.gymtracker.models.ExerciseObjective;
import com.example.admin1.gymtracker.models.Objective;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by admin1 on 01/07/2017.
 */

public class ExerciseObjectiveRVAdapter extends RecyclerView.Adapter<ExerciseObjectiveRVAdapter.ItemViewActivity>{
    private HashMap<String, ExerciseObjective> exerciseObjectives;
    private List<ExerciseObjective> exerciseObjectveList;
    private List<String> exObjKeysList;

    private HashMap<String, Objective> objectives;
    private List<Objective> objectiveList;
    private List<String> objectiveKeysList;

    private final String TAG = "ExerciseObjRVAdapter";
    private DatabaseReference tblExObj;

    private FirebaseDatabase dbRef = FirebaseDatabase.getInstance();

    public ExerciseObjectiveRVAdapter(HashMap<String, ExerciseObjective> exerciseObjectives,
                                      HashMap<String, Objective> objectives,
                                      DatabaseReference tblExObj){
        this.exerciseObjectives = exerciseObjectives;
        this.objectives         = objectives;
        this.tblExObj           = tblExObj;


        /// Populates Lists for later use
        objectiveKeysList    = new ArrayList<>(objectives.keySet());
        objectiveList        = new ArrayList<>(objectives.values());
        exObjKeysList        = new ArrayList<>(exerciseObjectives.keySet());
        exerciseObjectveList = new ArrayList<>(exerciseObjectives.values());
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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_ex_obj_entry, viewGroup, false);
        return new ItemViewActivity(v);
    }

    @Override
    public void onBindViewHolder(ItemViewActivity objectiveViewHolder, final int pos) {
        Objective mObjective = objectiveList.get(pos);

        if (mObjective != null){
            // Set GUI elements
            objectiveViewHolder.tvObjective.setText(mObjective.getName());
            objectiveViewHolder.chkChosen.setChecked(isObjectiveChosen(objectiveKeysList.get(pos)));
        }

        objectiveViewHolder.chkChosen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateRow(pos, isChecked);
            }
        });

    }

    // Queries the database and gets the current exercise name
    private boolean isObjectiveChosen(String key){
        boolean blChosen = false;
        if (exerciseObjectives != null){
            if (exerciseObjectives.get(key) != null){
                blChosen = true;
            }
        }
        return blChosen;
    }

    //Remove/add exercise objective if the user selects/deselects the checkbox
    private void updateRow(int pos, boolean isChecked){
        ExerciseObjective savingData;
        String objectiveId = objectiveKeysList.get(pos);
        if (isChecked){
            if (exerciseObjectives.get(objectiveId) == null){
                savingData = new ExerciseObjective(objectiveId);
                exerciseObjectives.put(objectiveId, savingData);
                exerciseObjectveList.add(savingData);
                exObjKeysList.add(objectiveId);
            }
        }
        else{
            exerciseObjectives.remove(objectiveId);
            removeItemFromList(objectiveId);
        }
    }

    protected void removeItemFromList(String ipExObjId){
        for (int iCnt = 0; iCnt < objectiveKeysList.size();iCnt++){
            if (objectiveKeysList.get(iCnt).equals(ipExObjId)){
                objectiveKeysList.remove(iCnt);
                objectiveList.remove(iCnt);
            }
        }
    }


    class ItemViewActivity extends RecyclerView.ViewHolder{
        private LinearLayout placeHolder;
        TextView tvObjective;
        CheckBox chkChosen;

        ItemViewActivity(View v ){
            super(v);
            placeHolder = (LinearLayout) v.findViewById(R.id.mainHolder);
            tvObjective = (TextView) v.findViewById((R.id.tvObjective));
            chkChosen   = (CheckBox) v.findViewById(R.id.chkChosen);
         }
    }// End ItemViewAcitivty Class
}// End ExerciseObjectiveRVAdapter class
