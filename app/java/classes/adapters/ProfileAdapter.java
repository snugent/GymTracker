package com.example.admin1.gymtracker.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.admin1.gymtracker.models.Member;

import java.util.List;

/**
 * Created by admin1 on 27/06/2017.
 */

public class ProfileAdapter extends ArrayAdapter<Member>{
    private Context         context;
    private int             resourceId;
    private List<Member>    members;


    // Default Constructor
    public ProfileAdapter(Context context, int resourceId, List<Member> members){
        super(context, resourceId, members);
        this.context    = context;
        this.resourceId = resourceId;
        this.members    = members;
    }

    public void updateList(List<Member> memberList){
        members.clear();
        memberList.addAll(memberList);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return convertView;
    }

}
