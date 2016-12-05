package com.example.ivan.mavme;

import android.content.Intent;
import android.graphics.Color;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import android.app.Activity;

import android.graphics.Typeface;

import android.content.Context;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;


import java.util.ArrayList;


public class NotificationAdapter extends ArrayAdapter<String> {

    private ArrayList<String> notificationIDs;
    private User activeUser;
    private LayoutInflater inflater;
    private int resource;
    private Context context;

    public NotificationAdapter(Context context, int resource, ArrayList<String> notificationIDs) {
        super(context, resource, notificationIDs);
        this.context = context;
        this.activeUser = DBMgr.getUserByID(HomeDisplay.userID);
        this.notificationIDs = notificationIDs;
        this.resource = resource;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(resource, null);
        }

        String nID = notificationIDs.get(position);
        Notification n = DBMgr.getNotificationByID(nID);
        Inbox inbox=DBMgr.getInboxByID(HomeDisplay.userID);

        // loading TextViews and buttons
        RelativeLayout background = (RelativeLayout) convertView.findViewById(R.id.RelativeLayout);
        TextView message = (TextView) convertView.findViewById(R.id.notificationMessage);
        TextView subtitle = (TextView) convertView.findViewById(R.id.notificationSubtitle);
        ImageButton link = (ImageButton) convertView.findViewById(R.id.link);


        message.setText(n.getMessage());
        subtitle.setText(n.returnSenderName()+" - "+n.returnSendDate());

        // if the notification contains a link
        if(n.getLinkTo().length()!=0) {
            link.setVisibility(View.VISIBLE);
        } else {
            link.setVisibility(View.GONE);
        }

        // if user has seen notification...
        if(!inbox.returnIsRead(nID)) {
            background.setBackgroundColor(Color.parseColor("#c3e8ff"));
            message.setTextColor(Color.parseColor("#d66a00"));
            subtitle.setTextColor(Color.parseColor("#d66a00"));
        } else {
            background.setBackgroundColor(Color.WHITE);
            message.setTextColor(Color.parseColor("#000000"));
            subtitle.setTextColor(Color.parseColor("#000000"));
        }

        addListenerOnButtons(convertView, nID, position);

        return convertView;

    }

    public void addListenerOnButtons(View convertView, final String nID, final int position) {

        final ImageButton link = (ImageButton) convertView.findViewById(R.id.link);

        link.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Inbox i=DBMgr.getInboxByID(activeUser.getUserID());
                if(!i.returnIsRead(nID)) i.toggleSeen(nID);
                String linkTo = DBMgr.getNotificationByID(nID).getLinkTo();
                Utils.goToURL(context, linkTo);

            }
        });


    }


}
