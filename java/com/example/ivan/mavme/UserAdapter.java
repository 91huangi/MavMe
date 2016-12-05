package com.example.ivan.mavme;


import android.widget.ArrayAdapter;
import android.widget.TextView;


import android.content.Context;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;


import java.util.ArrayList;


/******************************************************************************************
 * UserAdapter is to inflate a ListView of Users for the SearchDisplay class
 * @author Ivan Huang
 * ****************************************************************************************/

public class UserAdapter extends ArrayAdapter<String> {

    private ArrayList<String> userIDs;
    private LayoutInflater inflater;
    private Context context;
    private int resource;

    public UserAdapter(Context context, int resource, ArrayList<String> topicIDs) {
        super(context, resource, topicIDs);
        this.context=context;
        this.userIDs = topicIDs;
        this.resource = resource;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(resource, null);
        }


        // loading TextViews and buttons
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView gender = (TextView) convertView.findViewById(R.id.gender);
        TextView degree = (TextView) convertView.findViewById(R.id.degree);
        TextView major = (TextView) convertView.findViewById(R.id.major);


        User u = DBMgr.getUserByID(userIDs.get(position));

        name.setText(u.getName());
        gender.setText(u.returnGender());
        degree.setText("Deg: "+u.getDegree());
        major.setText("Maj: "+u.getMajor());


        return convertView;

    }


}

