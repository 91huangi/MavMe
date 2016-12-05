package com.example.ivan.mavme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/******************************************************************************************
 * NewTopicDisplay handles the GUI for creating a new topic in a group
 * @author Ivan Huang
 * ****************************************************************************************/

public class NewTopicDisplay extends BaseActivity {

    private User activeUser;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_topic_display);

        onResume();

    }

    @Override
    public void onResume() {
        super.onResume();

        loadMenu(-1);
        DBMgr.updateAll();
        context=this;
        Intent i = getIntent();
        String groupID = i.getStringExtra("groupID");
        activeUser = DBMgr.getUserByID(HomeDisplay.userID);

        addListenerOnButtons(groupID);
    }

    public void addListenerOnButtons(String groupID) {

        final Group g = DBMgr.getGroupByID(groupID);

        final EditText topicTitle = (EditText) findViewById(R.id.topicTitle);
        final EditText topicContent = (EditText) findViewById(R.id.topicContent);
        final TextView submit = (TextView) findViewById(R.id.submit);
        final TextView cancel = (TextView) findViewById(R.id.cancel);


        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(topicTitle.getText().toString().length()>0) {
                    g.addTopic(activeUser.getUserID(), topicTitle.getText().toString(), topicContent.getText().toString());
                    finish();
                } else {
                    Utils.alertDialog(context, "Topic title cannot be empty", new Utils.dialogHandler() {
                        @Override
                        public void onButtonClick(boolean click) { finish(); }
                    });
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

    }

}
