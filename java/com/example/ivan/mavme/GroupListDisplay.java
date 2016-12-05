package com.example.ivan.mavme;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/******************************************************************************************
 * GroupListDisplay is the GUI handler for the list of MavMe groups
 * @author Ivan Huang
 * ****************************************************************************************/

public class GroupListDisplay extends BaseActivity {

    private User activeUser;
    GroupListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> parentGroupIDs;
    HashMap<String, List<String>> subGroupIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.grouplist_display);


        onResume();

    }

    @Override
    public void onResume() {
        super.onResume();

        loadMenu(1);
        activeUser = DBMgr.getUserByID(HomeDisplay.userID);
        DBMgr.updateAll();

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();


        final TextView addGroup = (TextView) findViewById(R.id.addGroup);

        // adding group option if admin
        if(activeUser.getIsAdmin()) {
            addGroup.setVisibility(View.VISIBLE);

            addGroup.setOnClickListener( new OnClickListener() {
                public void onClick(View v) {

                    Intent i = new Intent("com.example.ivan.mavme.EditGroupDisplay");
                    i.putExtra("groupID", "");
                    startActivity(i);

                }
            });

        } else {
            addGroup.setVisibility(View.GONE);
        }


        listAdapter = new GroupListAdapter(this, parentGroupIDs, subGroupIDs);
        // setting list adapter
        expListView.setAdapter(listAdapter);
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {

        ArrayList<String> groupIDs = DBMgr.returnSortedGroupIDs();

        parentGroupIDs = new ArrayList<String>();
        subGroupIDs = new HashMap<String, List<String>>();

        for(int i=0; i<groupIDs.size(); i++) {
            String gID = groupIDs.get(i);
            Group g = DBMgr.getGroupByID(gID);
            if(g.returnIsParentGroup() && g.getIsActive()) {
                parentGroupIDs.add(g.getGroupID());
                subGroupIDs.put(g.getGroupID(), g.getSubGroupIDs());
            }
        }

    }
}
