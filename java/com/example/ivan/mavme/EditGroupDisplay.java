package com.example.ivan.mavme;

import android.content.Intent;
import android.content.Context;

import android.view.View;
import android.view.View.OnClickListener;

import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;


/******************************************************************************************
 * EditGroupDisplay manages the GUI for group management
 * ****************************************************************************************/

public class EditGroupDisplay extends BaseActivity {

    private String groupID;
    private Context context;
    private boolean createMode;
    private ArrayList<String> parentNames;
    private ArrayList<String> parentIDs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_group_display);

        onResume();

    }

    /**
     * onResume populates the spinner of parent groups and retrieves the groupID to be edited. If no
     * groupID is passed through Intents, the display enables the admin to create a new group.
     */
    @Override
    public void onResume() {
        super.onResume();

        loadMenu(-1);

        //getting groupID or setting createMode to true
        Intent i = getIntent();
        groupID = i.getStringExtra("groupID");
        createMode=(groupID.length()==0);
        context=this;
        DBMgr.updateAll();


        TextView save = (TextView) findViewById(R.id.saveChanges);
        TextView deleteGroup = (TextView) findViewById(R.id.deleteGroup);
        EditText editGroupName = (EditText) findViewById(R.id.editGroupName);
        final Spinner parentGroup = (Spinner) findViewById(R.id.parentGroup);

        // loading spinner of parent groups
        parentNames=new ArrayList<String>(Arrays.asList("<NONE>"));
        parentIDs= new ArrayList<String>(Arrays.asList(""));
        ArrayList<String> idsByAlphabet = DBMgr.returnSortedGroupIDs();
        for (String gID : idsByAlphabet) {
            Group g = DBMgr.getGroupByID(gID);
            if (g.returnIsParentGroup() && !g.getGroupID().equals(groupID) & g.getIsActive()) {
                parentNames.add(g.getGroupName());
                parentIDs.add(g.getGroupID());
            }
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item,
                parentNames);
        parentGroup.setAdapter(arrayAdapter);


        // creating new group or editing existing group?
        if (createMode) {
            deleteGroup.setVisibility(View.GONE);
            save.setText(">> create");
        } else {
            Group g = DBMgr.getGroupByID(groupID);
            deleteGroup.setVisibility(View.VISIBLE);
            editGroupName.setText(g.getGroupName());
            parentGroup.setSelection(parentIDs.indexOf(g.getParentGroupID()));
            save.setText(">> save changes");
        }

        addListenerOnButtons();
    }

    /**
     * This adds listeners to the delete, save, and cancel buttons
     */
    public void addListenerOnButtons() {
        final TextView deleteGroup = (TextView) findViewById(R.id.deleteGroup);
        final TextView save = (TextView) findViewById(R.id.saveChanges);
        final TextView cancel = (TextView) findViewById(R.id.cancel);

        deleteGroup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Group g = DBMgr.getGroupByID(groupID);
                Utils.confirmationDialog(context, "Are you sure you want to delete this group (and its subgroups)?",
                        new Utils.dialogHandler() {
                           public void onButtonClick(boolean click) {
                               if(click) {
                                   g.delete();
                                   finish();
                               }
                           }
                        });
            }
        });


        save.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {

                String action="saved";
                if(createMode) action="created";
                Utils.alertDialog(context, "Group "+action+" successfully", new Utils.dialogHandler() {
                    @Override
                    public void onButtonClick(boolean click) {
                        saveGroup();
                        finish();
                    }
                });
            }
        });


        cancel.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    /**
     * This method either saves or creates a group based on the fields in the form.
     */
    public void saveGroup() {

        String groupName = ((EditText) findViewById(R.id.editGroupName)).getText().toString();
        int spinnerIndex = ((Spinner) findViewById(R.id.parentGroup)).getSelectedItemPosition();
        String parentID = parentIDs.get(spinnerIndex);

        if(createMode) {
            if(parentID.length()==0) {
                Group.newGroup(groupName, "");
            } else {
                Group parent = DBMgr.getGroupByID(parentID);
                parent.addSubgroup(groupName);
                DBMgr.saveGroup(parent);
            }
        } else {
            Group g = DBMgr.getGroupByID(groupID);
            String oldParentID=g.getParentGroupID();

            if(parentID.length()>0 && g.getSubGroupIDs().size()>0) {

                Utils.alertDialog(context, "Error: Parent cannot be assigned to "+groupName+" since it contains subgroups.",
                        new Utils.dialogHandler() {
                            @Override
                            public void onButtonClick(boolean click) {}
                        });
            } else {

                g.setGroupName(groupName);
                g.setParentGroupID(parentID);

                // if switching parent groups
                if(parentID.length()>0) {
                    Group oldParent = DBMgr.getGroupByID(oldParentID);
                    Group parent = DBMgr.getGroupByID(parentID);
                    if(oldParent!=null) oldParent.removeSubgroupID(g.getGroupID());
                    parent.addSubgroupID(g.getGroupID());
                }
            }
        }


    }

}
