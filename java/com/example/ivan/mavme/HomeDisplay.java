package com.example.ivan.mavme;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.EditText;

import android.graphics.Color;
import android.widget.Toast;

import java.util.ArrayList;

/******************************************************************************************
 * HomeDisplay is the GUI handler for the user's home page. It adapts to whether the user is an admin
 * or an end user.
 * @author Ivan Huang
 * ****************************************************************************************/

public class HomeDisplay extends BaseActivity {

    // GUI variables
    public static String userID;
    private User activeUser;
    private Context context;

    // inbox variables
    private Inbox inbox;

    // pagination variables
    private final int pageSize = 10;
    private int page = 1;
    private ArrayList<String> pageTopicIDs;

    // which tab
    private int tab=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_display);
        onResume();

    }

    @Override
    public void onResume() {
        super.onResume();

        loadMenu(0);
        context=this;
        DBMgr.updateAll();


        activeUser = DBMgr.getUserByID(userID);
        inbox = DBMgr.getInboxByID(userID);

        loadTab();
        addListenerOnTabs();
    }

    /**
     * This method loads the correct information based on the value of the private variable tab.
     */
    public void loadTab() {
        RelativeLayout inbox = (RelativeLayout) findViewById(R.id.inbox);
        RelativeLayout trending = (RelativeLayout) findViewById(R.id.trending);
        RelativeLayout history = (RelativeLayout) findViewById(R.id.history);

        TextView inboxTab = (TextView) findViewById(R.id.inboxLabel);
        TextView trendingTab = (TextView) findViewById(R.id.trendingLabel);
        TextView historyTab = (TextView) findViewById(R.id.historyLabel);

        switch(tab) {
            // tab==inbox
            case 0:
                history.setVisibility(View.GONE);
                trending.setVisibility(View.GONE);
                inbox.setVisibility(View.VISIBLE);
                inboxTab.setTextColor(Color.parseColor("#ffa500"));
                trendingTab.setTextColor(Color.parseColor("#ffffff"));
                historyTab.setTextColor(Color.parseColor("#ffffff"));
                loadNotifications();
                break;
            // tab==trending
            case 1:
                history.setVisibility(View.GONE);
                trending.setVisibility(View.VISIBLE);
                inbox.setVisibility(View.GONE);
                inboxTab.setTextColor(Color.parseColor("#ffffff"));
                trendingTab.setTextColor(Color.parseColor("#ffa500"));
                historyTab.setTextColor(Color.parseColor("#ffffff"));
                loadFeed();
                break;
            // tab==history
            case 2:
                history.setVisibility(View.VISIBLE);
                trending.setVisibility(View.GONE);
                inbox.setVisibility(View.GONE);
                inboxTab.setTextColor(Color.parseColor("#ffffff"));
                trendingTab.setTextColor(Color.parseColor("#ffffff"));
                historyTab.setTextColor(Color.parseColor("#ffa500"));
                loadHistory();
                break;
            // default tab is inbox
            default:
                history.setVisibility(View.GONE);
                trending.setVisibility(View.GONE);
                inbox.setVisibility(View.VISIBLE);
                inboxTab.setTextColor(Color.parseColor("#ffa500"));
                trendingTab.setTextColor(Color.parseColor("#ffffff"));
                historyTab.setTextColor(Color.parseColor("#ffffff"));
                loadNotifications();

        }
    }

    /**
     * This method loads the history tab
     */
    public void loadHistory() {

        ArrayList<String> topicIDs = DBMgr.returnTopicIDsByUser(userID);

        // display topics in reverse chronological orer
        ArrayList<String> reversedTopicIDs = new ArrayList<String>();
        for(int i=0;i<topicIDs.size();i++) {
            reversedTopicIDs.add(0, topicIDs.get(i));
        }

        // calling inflater
        pageTopicIDs = configurePagination(reversedTopicIDs);
        ListView historyList = (ListView) findViewById(R.id.historyList);
        TopicAdapter topicAdapter = new TopicAdapter(this, R.layout.topic_list, pageTopicIDs);
        historyList.setAdapter(topicAdapter);

        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.goToURL(context, "T"+pageTopicIDs.get(position));
            }
        });

    }


    /**
     * This method loads the feed for the trending topics tab.
     */
    public void loadFeed() {

        // creating feed data
        ArrayList<String> topicIDs = new ArrayList<String>();
        ArrayList<Double> trendIndices = new ArrayList<Double>();
        ArrayList<String> sortedTopicIDs = new ArrayList<String>();
        ArrayList<Topic> topics = DBMgr.returnMostRecentTopics(1000);

        Topic t;

        // getting the trend index for each topic and sorting
        for(int i=0;i<topics.size();i++) {
            t = topics.get(i);
            topicIDs.add(t.getTopicID());
            trendIndices.add(t.returnTrendIndex());
        }

        sortedTopicIDs = Utils.pairSort(topicIDs, trendIndices);

        // calling inflater
        pageTopicIDs = configurePagination(sortedTopicIDs);
        ListView feedList = (ListView) findViewById(R.id.feedList);
        final TopicAdapter topicAdapter = new TopicAdapter(this, R.layout.topic_list, pageTopicIDs);
        feedList.setAdapter(topicAdapter);

        feedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.goToURL(context, "T"+pageTopicIDs.get(position));
            }
        });
    }


    /**
     * This method loads the notifications tab
     */
    public void loadNotifications() {

        // inflating notifications ListView
        final TextView unseenCount = (TextView) findViewById(R.id.inboxCount);
        unseenCount.setText(inbox.returnUnseenCount()+" unread");

        final ListView inboxList = (ListView) findViewById(R.id.inboxList);
        pageTopicIDs = configurePagination(inbox.getNotificationIDs());
        final NotificationAdapter notificationAdapter = new NotificationAdapter(this, R.layout.notification_list, pageTopicIDs);
        inboxList.setAdapter(notificationAdapter);

        inboxList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                inbox.toggleSeen(pageTopicIDs.get(position));
                unseenCount.setText(inbox.returnUnseenCount()+" unread");
                notificationAdapter.notifyDataSetChanged();
            }
        });



        // enabling broadcasting feature if active user is an admin
        EditText broadcastContent = (EditText) findViewById(R.id.broadcastContent);
        TextView sendBroadcast = (TextView) findViewById(R.id.sendBroadcast);
        TextView shadow0 = (TextView) findViewById(R.id.shadow0);
        if(activeUser.getIsAdmin()) {
            broadcastContent.setVisibility(View.VISIBLE);
            sendBroadcast.setVisibility(View.VISIBLE);
            shadow0.setVisibility(View.VISIBLE);
        } else {
            broadcastContent.setVisibility(View.GONE);
            sendBroadcast.setVisibility(View.GONE);
            shadow0.setVisibility(View.GONE);
        }
        addListenerOnBroadcast();




    }


    /**
     * This adds listener on the tab buttons so the user can switch views in his home page
     */
    public void addListenerOnTabs() {
        final TextView inboxTab = (TextView) findViewById(R.id.inboxLabel);
        final TextView trendingTab = (TextView) findViewById(R.id.trendingLabel);
        final TextView historyTab = (TextView) findViewById(R.id.historyLabel);

        inboxTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab=0; page=1;
                loadTab();
            }
        });

        trendingTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab=1; page=1;
                loadTab();
            }
        });

        historyTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab=2; page=1;
                loadTab();
            }
        });
    }

    /**
     * This method configures the pagination for the different tabs on the home page
     * @param idList is an ArrayList of ids
     * @return ArrayList containing the section of the idList based on the current page
     */

    public ArrayList<String> configurePagination(ArrayList<String> idList) {

        final int maxSize=idList.size();

        final TextView pgLeft = (TextView) findViewById(R.id.pgLeft);
        final TextView pgRight = (TextView) findViewById(R.id.pgRight);

        // displaying page left and page right buttons based on current page position
        if(page==1) {
            pgLeft.setTextColor(Color.parseColor("#888888"));
        } else {
            pgLeft.setTextColor(Color.parseColor("#000000"));
        }
        if(page*pageSize>=maxSize) {
            pgRight.setTextColor(Color.parseColor("#888888"));
        } else {
            pgRight.setTextColor(Color.parseColor("#000000"));
        }

        pgLeft.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(page>1) {
                    page--;
                    loadTab();
                }
            }
        });

        pgRight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(page*pageSize<maxSize) {
                    page++;
                    loadTab();
                }
            }
        });


        // prepping page IDS
        ArrayList<String> pageIDs = new ArrayList<String>();
        for(int i=(page-1)*pageSize; i<Math.min(maxSize, page*pageSize); i++) {
            pageIDs.add(idList.get(i));
        }

        return pageIDs;
    }


    /**
     * This adds as listener on the broadcast system to enable admin broadcasting
     */
    public void addListenerOnBroadcast() {

        final EditText broadcastContent = (EditText) findViewById(R.id.broadcastContent);
        final TextView sendBroadcast = (TextView) findViewById(R.id.sendBroadcast);


        sendBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=broadcastContent.getText().toString();

                // broadcast cannot be empty
                if(message.length()>0) {

                    Notification n = Notification.newNotification(HomeDisplay.userID, message, "");

                    // adding every user to id-list and sending
                    ArrayList<String> userIDs = new ArrayList<String>();
                    for (int i = 0; i < DBMgr.numberOfUsers(); i++) {
                        userIDs.add(DBMgr.indexToID(i));
                    }
                    n.sendNotificationTo(userIDs);
                    loadNotifications();

                    // closing keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

            }
        });



    }
}
