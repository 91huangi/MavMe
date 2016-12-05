package com.example.ivan.mavme;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import android.content.Intent;
import android.content.Context;

import android.app.Activity;
import android.os.Bundle;

import android.graphics.Typeface;

import android.content.Context;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

/******************************************************************************************
 * TopicAdapter is used to populate a ListView of topics given an ArrayList of topicIDs
 * @author Ivan Huang
 * ****************************************************************************************/
public class TopicAdapter extends ArrayAdapter<String> {

    private User activeUser;
    private ArrayList<String> topicIDs;
    private LayoutInflater inflater;
    private Context context;
    private int resource;


    public TopicAdapter(Context context, int resource, ArrayList<String> topicIDs) {
        super(context, resource, topicIDs);
        this.context=context;
        this.activeUser = DBMgr.getUserByID(HomeDisplay.userID);
        this.topicIDs = topicIDs;
        this.resource = resource;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(resource, null);
        }

        Topic t = DBMgr.getTopicByID(topicIDs.get(position));

        loadTextViews(convertView, t);
        addListenerOnButtons(convertView, t);
        configureButtons(convertView, t);

        return convertView;

    }

    /**
     * Populating textViews
     * @param convertView
     * @param t: Topic
     */
    public void loadTextViews(View convertView, Topic t) {
        // loading TextViews
        TextView topicTitle = (TextView) convertView.findViewById(R.id.topicTitle);
        TextView topicPoster = (TextView) convertView.findViewById(R.id.topicPoster);
        TextView topicDate = (TextView) convertView.findViewById(R.id.topicDate);
        TextView topicLoveCount = (TextView) convertView.findViewById(R.id.topicLoveCount);
        TextView groupPath = (TextView) convertView.findViewById(R.id.groupPath);

        topicTitle.setText(t.getTopicName());
        topicPoster.setText(DBMgr.getUserByID(t.getPostUserID()).getName());
        topicDate.setText(t.returnPostDate());
        topicLoveCount.setText(""+t.returnMavLoveCount());
        groupPath.setText(t.returnGroupPath());
    }

    /**
     * Configuring buttons for each list item
     * @param convertView
     * @param t
     */
    public void configureButtons(View convertView, Topic t) {
        ImageButton mavLove = (ImageButton) convertView.findViewById(R.id.butLove);
        TextView loveCount = (TextView) convertView.findViewById(R.id.topicLoveCount);
        TextView flag = (TextView) convertView.findViewById(R.id.flagTopic);
        TextView pin = (TextView) convertView.findViewById(R.id.pinTopic);
        TextView delete = (TextView) convertView.findViewById(R.id.deleteTopic);

        // configuring mavLove button and count
        if(t.returnIsLovedBy(activeUser.getUserID())) {
            mavLove.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#ff0000"), PorterDuff.Mode.MULTIPLY));
            loveCount.setTextColor(Color.parseColor("#ff0000"));
        } else {
            mavLove.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#aaaaaa"), PorterDuff.Mode.MULTIPLY));
            loveCount.setTextColor(Color.parseColor("#aaaaaa"));
        }


        // configuring flag option
        if(activeUser.getIsAdmin()) {
            flag.setText("flag ("+t.returnFlagCount() +")");
        }
        if(t.returnIsFlaggedBy(activeUser.getUserID())) {
            flag.setTextColor(Color.RED);
            flag.setTypeface(null, Typeface.BOLD);
        } else {
            flag.setTextColor(Color.GRAY);
            flag.setTypeface(null, Typeface.NORMAL);
        }


        // configuring delete & pin topics based on active user
        if(activeUser.getIsAdmin()) {
            delete.setVisibility(View.VISIBLE);
            pin.setVisibility(View.VISIBLE);

            // configuring pin option
            if(t.returnIsPinned()) {
                pin.setTextColor(Color.RED);
                pin.setTypeface(null, Typeface.BOLD);
            } else {
                pin.setTextColor(Color.GRAY);
                pin.setTypeface(null, Typeface.NORMAL);
            }

        } else if (activeUser.getUserID().equals(t.getPostUserID())) {
            delete.setVisibility(View.VISIBLE);
            pin.setVisibility(View.GONE);
        } else {
            delete.setVisibility(View.GONE);
            pin.setVisibility(View.GONE);
        }

    }

    /**
     * Setting functionality for each listItem
     * @param convertView
     * @param t
     */
    public void addListenerOnButtons(final View convertView, final Topic t) {

        final ImageButton mavLove = (ImageButton) convertView.findViewById(R.id.butLove);
        final TextView topicTitle = (TextView) convertView.findViewById(R.id.topicTitle);
        final TextView mavLoveCount = (TextView) convertView.findViewById(R.id.topicLoveCount);
        final TextView groupPath = (TextView) convertView.findViewById(R.id.groupPath);
        final TextView username = (TextView) convertView.findViewById(R.id.topicPoster);
        final TextView flag = (TextView) convertView.findViewById(R.id.flagTopic);
        final TextView pin = (TextView) convertView.findViewById(R.id.pinTopic);
        final TextView delete = (TextView) convertView.findViewById(R.id.deleteTopic);

        // Go-to Topic view if title is clicked
        topicTitle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utils.goToURL(context, "T"+t.getTopicID());
            }
        });

        // MavLove a topic
        mavLove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                t.toggleLove(activeUser.getUserID());
                mavLoveCount.setText(""+t.returnMavLoveCount());
                configureButtons(convertView, t);
            }
        });


        /* Dubugging purposes... displays the trendIndex for a topic
        mavLoveCount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Date today = new Date();
                String days = ""+(double)(today.getTime()-t.getTimeStamp())/24/3600;
                Toast.makeText(context, ""+t.returnTrendIndex(), Toast.LENGTH_SHORT).show();
            }
        });*/


        groupPath.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utils.goToURL(context, "G"+t.getGroupID());
            }
        });

        username.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utils.goToURL(context, "U"+t.getPostUserID());
            }
        });


        pin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                t.pinTopic();
                configureButtons(convertView, t);
                notifyDataSetChanged();
                if(context instanceof GroupDisplay)
                    ((GroupDisplay)context).onResume();
            }
        });

        // Flagging a topic and sending a notification to all admins
        flag.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // notifying admin for first instance of flag
                if(t.returnFlagCount()==0) {
                    String linkTo = "T" + t.getTopicID();
                    Notification n = Notification.newNotification(Utils.sysID, "The topic: " + t.getTopicName() +
                            " (posted by " + username.getText().toString() + ") was flagged", linkTo);
                    n.sendNotificationTo(DBMgr.getAdminIDs());
                }

                // flagging topic
                t.toggleFlag(activeUser.getUserID());
                configureButtons(convertView, t);
                notifyDataSetChanged();
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Utils.confirmationDialog(context, "Delete '"+t.getTopicName()+"'?", new Utils.dialogHandler() {
                    @Override
                    public void onButtonClick(boolean click) {
                        if(click) {
                            topicIDs.remove(t.getTopicID());
                            t.delete();
                            notifyDataSetChanged();
                        }
                    }
                });

            }
        });
    }

}
