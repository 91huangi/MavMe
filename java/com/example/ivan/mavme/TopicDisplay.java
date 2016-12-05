package com.example.ivan.mavme;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Color;
import android.os.Bundle;


import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import android.content.Intent;

import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.Toast;

/******************************************************************************************
 * LoginDisplay is the app starting GUI. It handles login and authentication methods
 * @author Ivan Huang
 * ****************************************************************************************/
public class TopicDisplay extends BaseActivity {

    private User activeUser;
    private Topic t;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_display);
        onResume();
    }

    @Override
    public void onResume(){
        super.onResume();
        loadMenu(-1);
        context=this;

        Intent i = getIntent();
        String topicID = i.getStringExtra("topicID");
        activeUser = DBMgr.getUserByID(HomeDisplay.userID);

        if(topicID!=null) {
            t = DBMgr.getTopicByID(topicID);
            loadTopic();
            loadComments();
            addListenerOnButtons();
        }



    }


    /**
     * Loading TextViews based on the topic being viewed
     */
    public void loadTopic() {

        TextView topicTitle = (TextView) findViewById(R.id.topicTitle);
        TextView topicContent = (TextView) findViewById(R.id.topicContent);
        TextView topicUser = (TextView) findViewById(R.id.topicPoster);
        TextView topicDate = (TextView) findViewById(R.id.topicDate);
        TextView groupPath = (TextView) findViewById(R.id.groupPath);
        EditText commentField = (EditText) findViewById(R.id.commentField);
        TextView submitComment = (TextView) findViewById(R.id.submit);
        ImageButton reply = (ImageButton) findViewById(R.id.butReply);




        commentField.setVisibility(View.GONE);
        submitComment.setVisibility(View.GONE);
        reply.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#aaaaaa"), PorterDuff.Mode.MULTIPLY));

        // showing topic title, subtitle, and content
        User u = DBMgr.getUserByID(t.getPostUserID());
        topicTitle.setText(t.getTopicName());
        topicContent.setText(t.getPostContent());
        topicUser.setText(u.getName());
        topicDate.setText(t.returnPostDate());
        groupPath.setText(t.returnGroupPath());



        // configuring mavLove button and count
        configureButtons();
    }


    /**
     * Loading ListView of comments
     */
    public void loadComments() {

        ListView commentList = (ListView) findViewById(R.id.commentList);
        CommentAdapter commentAdapter = new CommentAdapter(this, R.layout.comment_list, t.getCommentIDs());
        commentList.setAdapter(commentAdapter);


    }

    /**
     * Configuring MavLove, Flag, and Delete Buttons
     */
    public void configureButtons() {

        TextView mavLoveCount = (TextView) findViewById(R.id.loveCount);
        ImageButton mavLove = (ImageButton) findViewById(R.id.butLove);
        ImageButton flag = (ImageButton) findViewById(R.id.butFlag);
        ImageButton delete = (ImageButton) findViewById(R.id.butDelete);

        mavLoveCount.setText(""+t.returnMavLoveCount());
        if(t.returnIsLovedBy(activeUser.getUserID())) {
            mavLove.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#ff0000"), PorterDuff.Mode.MULTIPLY));
            mavLoveCount.setTextColor(Color.parseColor("#ff0000"));
        } else {
            mavLove.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#aaaaaa"), PorterDuff.Mode.MULTIPLY));
            mavLoveCount.setTextColor(Color.parseColor("#aaaaaa"));
        }

        if(t.returnIsFlaggedBy(activeUser.getUserID())) {
            flag.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#ff0000"), PorterDuff.Mode.MULTIPLY));
        } else {
            flag.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#aaaaaa"), PorterDuff.Mode.MULTIPLY));
        }

        if(t.getPostUserID().equals(activeUser.getUserID()) || activeUser.getIsAdmin()) {
            delete.setVisibility(View.VISIBLE);
            if(!t.getIsActive()) delete.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#ff0000"), PorterDuff.Mode.MULTIPLY));
        } else {
            delete.setVisibility(View.GONE);
        }

    }

    /**
     * Adding functionality to buttons
     */
    public void addListenerOnButtons() {

        final ImageButton mavLove = (ImageButton) findViewById(R.id.butLove);
        final TextView userName = (TextView) findViewById(R.id.topicPoster);
        final ImageButton flag = (ImageButton) findViewById(R.id.butFlag);
        final ImageButton reply = (ImageButton) findViewById(R.id.butReply);
        final ImageButton delete = (ImageButton) findViewById(R.id.butDelete);
        final EditText commentField = (EditText) findViewById(R.id.commentField);
        final TextView submitComment = (TextView) findViewById(R.id.submit);
        final TextView groupPath = (TextView) findViewById(R.id.groupPath);


        groupPath.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utils.goToURL(getApplicationContext(), "G"+t.getGroupID());

            }
        });

        userName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utils.goToURL(getApplicationContext(), "U"+t.getPostUserID());
            }
        });


        mavLove.setOnClickListener(new OnClickListener () {
            public void onClick(View v) {

                t.toggleLove(activeUser.getUserID());
                configureButtons();

            }
        });


        flag.setOnClickListener(new OnClickListener () {
            public void onClick(View v) {

                // notifying admin for first instance of flag
                if(t.returnFlagCount()==0) {
                    String linkTo = "T" + t.getTopicID();
                    Notification n = Notification.newNotification(Utils.sysID, "The topic: " + t.getTopicName() +
                            " (posted by " + userName.getText().toString() + ") was flagged", linkTo);
                    n.sendNotificationTo(DBMgr.getAdminIDs());
                }

                t.toggleFlag(activeUser.getUserID());
                configureButtons();
            }
        });

        delete.setOnClickListener(new OnClickListener () {
            public void onClick(View v) {
                if(t.getIsActive())
                    Utils.confirmationDialog(TopicDisplay.this, "Delete '"+t.getTopicName()+"'?",
                            new Utils.dialogHandler() {
                                    @Override
                                    public void onButtonClick(boolean click) {
                                        if(click) {
                                            t.delete();
                                            finish();
                                        }
                                    }

                            });
            };
        });

        reply.setOnClickListener(new OnClickListener () {
            public void onClick(View v) {

                if(commentField.getVisibility()==View.GONE) {
                    commentField.setVisibility(View.VISIBLE);
                    submitComment.setVisibility(View.VISIBLE);
                    reply.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#ff0000"), PorterDuff.Mode.MULTIPLY));
                } else {
                    commentField.setVisibility(View.GONE);
                    submitComment.setVisibility(View.GONE);
                    reply.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#aaaaaa"), PorterDuff.Mode.MULTIPLY));

                }

                loadComments();

            }
        });


        // when a comment is submitted
        submitComment.setOnClickListener(new OnClickListener () {
            public void onClick(View v) {

                if(commentField.getText().toString().length()>0) {
                    // adding calling the add comment method
                    t.addComment(activeUser.getUserID(), commentField.getText().toString());

                    // hiding commenting widgets
                    commentField.setVisibility(View.GONE);
                    submitComment.setVisibility(View.GONE);
                    reply.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#aaaaaa"), PorterDuff.Mode.MULTIPLY));

                    // reloading topic display
                    loadComments();
                } else {
                    Utils.alertDialog(context, "Please enter a comment", new Utils.dialogHandler() {
                        public void onButtonClick(boolean click) {}
                    });
                }
            }
        });

    }
}
