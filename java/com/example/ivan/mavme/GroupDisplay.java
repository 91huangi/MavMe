package com.example.ivan.mavme;

import android.os.Bundle;


import android.view.View;
import android.view.View.OnClickListener;

import android.content.Intent;

import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.EditText;
import android.graphics.Color;
import android.content.Context;



import java.util.Date;
import java.util.ArrayList;
import java.util.List;


/******************************************************************************************
 * GroupDisplay manages the GUI for the viewing an individual group
 * ****************************************************************************************/

public class GroupDisplay extends BaseActivity {

    private int page;
    private Group g;
    private final int groupPageSize = 10;
    private Context context;
    private ArrayList<String> pageTopicIDs;


    /**
     * Inflating display
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_display);
        loadMenu(-1);

        onResume();

    }


    /**
     * Loads the group view by populating the title and path as well and coloring pagination buttons
     */
    public void loadGroup() {
        TextView groupTitle = (TextView) findViewById(R.id.groupTitle);
        TextView groupPath = (TextView) findViewById(R.id.groupPath);
        TextView pgLeft = (TextView) findViewById(R.id.pgLeft);
        TextView pgRight = (TextView) findViewById(R.id.pgRight);

        groupTitle.setText(g.getGroupName());
        groupPath.setText(g.returnPath());


        // displaying page left and page right buttons based on current page position
        if(page==1) {
            pgLeft.setTextColor(Color.parseColor("#888888"));
        } else {
            pgLeft.setTextColor(Color.parseColor("#000000"));
        }

        if(page*groupPageSize>=g.getTopicIDs().size()) {
            pgRight.setTextColor(Color.parseColor("#888888"));
        } else {
            pgRight.setTextColor(Color.parseColor("#000000"));
        }

    }

    /**
     * Loads a layout and sets visibility to true if the group has a pinned topic. Otherwise
     * sets the layout to gone.
     */
    public void loadPinnedTopic() {

        RelativeLayout pinnedContainer = (RelativeLayout) findViewById(R.id.pinnedContainer);
        String pinnedID = g.getPinnedTopicID();

        if(!pinnedID.equals("")) {
            TextView pinnedTitle = (TextView) findViewById(R.id.pinnedTitle);
            TextView pinnedContent = (TextView) findViewById(R.id.pinnedContent);
            TextView pinnedUser = (TextView) findViewById(R.id.pinnedUser);
            TextView pinnedDate = (TextView) findViewById(R.id.pinnedDate);

            Topic t = DBMgr.getTopicByID(pinnedID);
            pinnedTitle.setText(t.getTopicName());

            pinnedContent.setText(t.getPostContent());
            pinnedUser.setText(DBMgr.getUserByID(t.getPostUserID()).getName());
            pinnedDate.setText(t.returnPostDate());

            pinnedContainer.setVisibility(View.VISIBLE);

        } else {
            pinnedContainer.setVisibility(View.GONE);
        }
    }


    /**
     * Paginates topicIDs and then calls a custom adapter on the paginated IDs.
     */
    public void loadTopics() {

        //paginating
        int start=(page-1)*groupPageSize, end=Math.min(g.getTopicIDs().size(), page*groupPageSize);
        pageTopicIDs = new ArrayList<String>();

        for(int i=start; i<end; i++) {
            pageTopicIDs.add(g.getTopicIDs().get(i));
        }

        ListView topicList = (ListView) findViewById(R.id.topicList);
        TopicAdapter topicAdapter = new TopicAdapter(this, R.layout.topic_list, pageTopicIDs);
        topicList.setAdapter(topicAdapter);

        topicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.goToURL(context, "T"+pageTopicIDs.get(position));
            }
        });

    }


    /**
     * Loads the group
     */
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        context=this;

        Intent i = getIntent();
        String groupID = i.getStringExtra("groupID");

        // if no error pulling up group
        if(groupID.length()>0) {
            page = 1;
            g = DBMgr.getGroupByID(groupID);
            loadGroup();
            loadPinnedTopic();
            loadTopics();
            addListenerOnButtons();
        }

    }


    /**
     * Adding listeners to the New Topic && Page Left/Right buttons
     */
    public void addListenerOnButtons() {

        final ImageButton newTopic = (ImageButton) findViewById(R.id.newTopic);
        final TextView pgLeft = (TextView) findViewById(R.id.pgLeft);
        final TextView pgRight = (TextView) findViewById(R.id.pgRight);


        newTopic.setOnClickListener(new OnClickListener () {
            public void onClick(View v) {

                Intent i = new Intent("com.example.ivan.mavme.NewTopicDisplay");
                i.putExtra("groupID", g.getGroupID());
                startActivity(i);

            }
        });

        pgLeft.setOnClickListener(new OnClickListener () {
            public void onClick(View v) {
                if(page>1) {
                    page--;
                    onResume();
                }
            }
        });


        pgRight.setOnClickListener(new OnClickListener () {
            public void onClick(View v) {
                if(page*groupPageSize<g.getTopicIDs().size()){
                    page++;
                    onResume();
                }
            }
        });



    }


}
