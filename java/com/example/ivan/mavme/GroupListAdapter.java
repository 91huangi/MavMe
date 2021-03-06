package com.example.ivan.mavme;


import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;


/******************************************************************************************
 * GroupListAdapter inflates an expanded list view with Groups from a parentID ArrayList and a
 * subgroupID ArrayList
 * @author Ivan Huang
 * ****************************************************************************************/
public class GroupListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> parentGroupIDs;
    private HashMap<String, List<String>> subGroupIDs;
    private User activeUser;

    /**
     * Constructor
     * @param context
     * @param parentGroupIDs
     * @param subGroupIDs
     */
    public GroupListAdapter(Context context, List<String> parentGroupIDs,
                            HashMap<String, List<String>> subGroupIDs) {
        this._context = context;
        this.parentGroupIDs = parentGroupIDs;
        this.subGroupIDs = subGroupIDs;
        this.activeUser = DBMgr.getUserByID(HomeDisplay.userID);
    }

    /**
     * Gets the child at childPosition of the group located at groupPosition
     * @param groupPosition
     * @param childPosition
     * @return Group
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.subGroupIDs.get(this.parentGroupIDs.get(groupPosition)).get(childPosition);
    }


    /**
     * Converts groupPosition && childPosition to childPosition
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }



    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childID = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.subgroup_list, null);
        }


        TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);
        txtListChild.setText(DBMgr.getGroupByID(childID).getGroupName());
        TextView editGroup = (TextView) convertView.findViewById(R.id.editLabel);


        if(activeUser.getIsAdmin()) {
            editGroup.setVisibility(View.VISIBLE);
        } else {
            editGroup.setVisibility(View.GONE);
        }

        addListenerOnChildren(convertView, groupPosition, childPosition);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.subGroupIDs.get(this.parentGroupIDs.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.parentGroupIDs.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.parentGroupIDs.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerID = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.parentgroup_list, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        TextView triangle = (TextView) convertView.findViewById(R.id.triangle);
        TextView editGroup = (TextView) convertView.findViewById(R.id.editLabel);

        lblListHeader.setText(DBMgr.getGroupByID(headerID).getGroupName());

        if (getChildrenCount(groupPosition)==0) {
            triangle.setVisibility(View.INVISIBLE);
        } else if(isExpanded) {
            triangle.setVisibility(View.VISIBLE);
            triangle.setText("▼");
        } else {
            triangle.setVisibility(View.VISIBLE);
            triangle.setText("▶");
        }

        if(activeUser.getIsAdmin()) {
            editGroup.setVisibility(View.VISIBLE);
        } else {
            editGroup.setVisibility(View.GONE);
        }

        addListenerOnParents(convertView, groupPosition);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    /**
     * Adds listeners to the buttons on the parent group
     * @param convertView
     * @param position
     */
    public void addListenerOnParents(View convertView, final int position) {

        final TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        final TextView editGroup = (TextView) convertView.findViewById(R.id.editLabel);


        lblListHeader.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utils.goToURL(_context, "G"+getGroup(position));
            }
        });

        editGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent("com.example.ivan.mavme.EditGroupDisplay");
                i.putExtra("groupID", ""+getGroup(position));
                _context.startActivity(i);
            }
        });

    }


    /**
     * Adds listener to buttons for the subgroup
     * @param convertView
     * @param groupPos
     * @param childPos
     */
    public void addListenerOnChildren(View convertView, final int groupPos, final int childPos) {

        final TextView lblListChild = (TextView) convertView.findViewById(R.id.lblListItem);
        final TextView editGroup = (TextView) convertView.findViewById(R.id.editLabel);

        lblListChild.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utils.goToURL(_context, "G"+getChild(groupPos, childPos));
            }
        });

        editGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent("com.example.ivan.mavme.EditGroupDisplay");
                i.putExtra("groupID", ""+getChild(groupPos, childPos));
                _context.startActivity(i);
            }
        });
    }
}