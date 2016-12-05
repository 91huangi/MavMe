package com.example.ivan.mavme;

import java.util.ArrayList;

/******************************************************************************************
 * Group is a class of groups & subgroups that contains an ArrayList of topicIDs
 * ****************************************************************************************/
public class Group extends Object {

    private String groupID;
    private String groupName;
    private String parentGroupID;
    private String pinnedTopicID;
    private ArrayList<String> topicIDs;
    private ArrayList<String> subGroupIDs;
    private boolean isActive = true;

    public Group() {}

    /**
     * Constructor
     * @param groupID
     * @param groupName
     * @param parentGroupID
     * @param pinnedTopicID
     * @param topicIDs
     * @param subGroupIDs: ArrayList pointing to subgroups
     */
    public Group(String groupID, String groupName, String parentGroupID, String pinnedTopicID,
                 ArrayList<String> topicIDs, ArrayList<String> subGroupIDs) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.parentGroupID = parentGroupID;
        this.pinnedTopicID = pinnedTopicID;
        this.topicIDs = topicIDs;
        this.subGroupIDs = subGroupIDs;
    }

    /******************************************************************************************
     * Getters
     * ****************************************************************************************/
    public String getGroupID() { return groupID; }
    public String getGroupName() { return groupName; }
    public String getParentGroupID() { return parentGroupID; }
    public ArrayList<String> getSubGroupIDs() { return subGroupIDs; }
    public String getPinnedTopicID() { return pinnedTopicID; }
    public ArrayList<String> getTopicIDs() { return topicIDs; }
    public boolean getIsActive() { return isActive; }


    /******************************************************************************************
     * Setters
     * ****************************************************************************************/
    public void setGroupID(String groupID) {
        this.groupID=groupID;
    }
    public void setGroupName(String groupName) {this.groupName = groupName;}
    public void setParentGroupID(String parentID) {this.parentGroupID = parentID;}
    public void setSubGroupIDs(ArrayList<String> subGroupIDs) { this.subGroupIDs = subGroupIDs; }
    public void setPinnedTopicID(String pinnedTopicID) { this.pinnedTopicID=pinnedTopicID; }
    public void setTopicIDs(ArrayList<String> topicIDs) { this.topicIDs=topicIDs; }
    public void setIsActive(boolean isActive) { this.isActive=isActive; }


    /******************************************************************************************
     * Returning methods
     * ****************************************************************************************/
    public boolean returnIsParentGroup() { return parentGroupID.equals(""); }

    /**
     * Returns a string that is parentGroup/subGroup (without strings)
     * @return String: path of group
     */
    public String returnPath() {
        String path="/";
        if(!returnIsParentGroup()) {
            path+=DBMgr.getGroupByID(parentGroupID).getGroupName()+"/";
        }
        path+=groupName;
        path=path.replace(" ","-");
        path=path.toLowerCase().replaceAll("[^a-z0-9-/]", "");
        return path;
    }

    /******************************************************************************************
     * Functional methods
     * ****************************************************************************************/

    /**
     * Creates new topic and points group<-->topic
     * @param userID
     * @param topicTitle
     * @param topicText
     */
    public void addTopic(String userID, String topicTitle, String topicText) {
        Topic t = Topic.newTopic(userID, topicTitle, topicText, groupID);
        addTopicID(t.getTopicID());
    }

    /**
     * Adds a pointer from group-->topic and puts the ID in reverse chron order
     * @param topicID
     */
    public void addTopicID(String topicID) {

        boolean inserted = false;
        Topic newTopic = DBMgr.getTopicByID(topicID);
        int max=topicIDs.size();

        // adding topic ID in order of date
        for(int i=0; i<max; i++) {
            if(!inserted) {
                Topic t = DBMgr.getTopicByID(topicIDs.get(i));
                if (newTopic.getTimeStamp() > t.getTimeStamp()) {
                    topicIDs.add(i, topicID);
                    inserted = true;
                }
            }
        }

        if(!inserted) topicIDs.add(topicID);        // if topic is oldest add at end

    }

    /**
     * Creates subgroup and sets pointer parent<-->subgroup
     * @param subGroupName
     */
    public void addSubgroup(String subGroupName) {
        Group subGroup = newGroup(subGroupName, groupID);
        addSubgroupID(subGroup.getGroupID());
    }

    public void addSubgroupID(String id) {
        subGroupIDs.add(id);
    }

    public void removeSubgroupID(String id) {
        subGroupIDs.remove(id);
    }


    /**
     * Removes pointer from group to topic
     * @param topicID
     */
    public void removeTopic(String topicID) {
        if(topicID.equals(pinnedTopicID)) {
            pinnedTopicID="";
        }
        topicIDs.remove(topicID);

    }

    /**
     * Deletes all topics in a group, then recursively deletes all subgroups
     */
    public void delete() {

        // deleting all topics within group

        for(String tID:topicIDs) {
            Topic t=DBMgr.getTopicByID(tID);
            t.delete();
        }

        isActive=false;

        // deleting all subgroups if parent group
        if(returnIsParentGroup()) {
            for(String gID:subGroupIDs) {
                Group g=DBMgr.getGroupByID(gID);
                g.delete();
            }
            subGroupIDs=new ArrayList<String>();
        }
    }

    /******************************************************************************************
     * Static methods
     * ****************************************************************************************/
    /**
     * Creates a new group with auto-generated ID and returns it
     * @param groupName
     * @param parentGroupID
     * @return Group: newly created group
     */
    public static Group newGroup(String groupName, String parentGroupID) {
        String gID = DBMgr.generateNewGroupID();
        Group g = new Group(gID, groupName, parentGroupID, "", new ArrayList<String>(),
                new ArrayList<String>());
        DBMgr.saveGroup(g);
        return g;
    }
}
