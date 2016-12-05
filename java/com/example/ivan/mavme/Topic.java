package com.example.ivan.mavme;
import java.util.Date;
import java.util.ArrayList;

import android.text.TextUtils;


/******************************************************************************************
 * Topic is a class that extends post. It points to a list of children (comments)
 * @author Ivan Huang
 * ****************************************************************************************/
public class Topic extends Post {

    private String topicName;
    private String topicID;
    private String groupID;
    private ArrayList<String> commentIDs;
    private ArrayList<String> mavLoveUserIDs;

    public Topic() {}

    /**
     * Constructor
     * @param topicID
     * @param timeStamp
     * @param postUserID
     * @param topicName
     * @param groupID: ID of the group
     * @param postContent
     * @param mavLoveUserIDs: ArrayList of userIDs who have "loved" the topic
     * @param flagUserIDs
     * @param commentIDs: ArrayList of children IDs
     * @param isActive
     */
    public Topic(String topicID,
                 long timeStamp,
                 String postUserID,
                 String topicName,
                 String groupID,
                 String postContent,
                 ArrayList<String> mavLoveUserIDs,
                 ArrayList<String> flagUserIDs,
                 ArrayList<String> commentIDs,
                 boolean isActive) {

        super(timeStamp, postUserID, postContent, flagUserIDs, isActive);
        this.topicID = topicID;
        this.topicName = topicName;
        this.groupID = groupID;
        this.mavLoveUserIDs = mavLoveUserIDs;
        this.commentIDs = commentIDs;
    }

    /******************************************************************************************
     * Getting methods
     * ****************************************************************************************/
    public String getTopicID() { return topicID; }
    public String getTopicName() {
        return topicName;
    }
    public String getGroupID() { return groupID; }
    public ArrayList<String> getMavLoveUserIDs() {
        return mavLoveUserIDs;
    }
    public ArrayList<String> getCommentIDs() {
        return commentIDs;
    }


    /******************************************************************************************
     * Setting methods
     * ****************************************************************************************/
    public void setTopicID(String topicID) { this.topicID=topicID; }
    public void setTopicName(String topicName) {
        this.topicName=topicName;
    }
    public void setGroupID(String groupID) { this.groupID=groupID; }
    public void setMavLoveUserIDs(ArrayList<String> mavLoveUserIDs) {this.mavLoveUserIDs=mavLoveUserIDs;}
    public void setCommentIDs(ArrayList<String> commentIDs) {
        this.commentIDs=commentIDs;
    }



    /******************************************************************************************
     * Returning methods
     * ****************************************************************************************/

    public String returnGroupPath() {
        Group g = DBMgr.getGroupByID(groupID);
        return g.returnPath(); }

    public int returnMavLoveCount() {
        return mavLoveUserIDs.size();
    }

    /**
     * returnTrendIndex calculates the number of mavLoves/topicAge (in days).
     * @return
     */
    public double returnTrendIndex() {
        long now=new Date().getTime();
        double age = (double) (now-timeStamp)/24/3600;
        return -(returnMavLoveCount()+1)/age;
    }

    public boolean returnIsLovedBy(String userID) {
        return mavLoveUserIDs.contains(userID);
    }

    public boolean returnIsPinned() {
        Group g = DBMgr.getGroupByID(groupID);
        return topicID.equals(g.getPinnedTopicID());
    }

    public String toString() {
        String topicObject;

        topicObject=getTopicID()+", "+returnPostDate()+", "+getPostUserID()+", "+topicName+", "+
                getPostContent()+", {"+TextUtils.join(", ", mavLoveUserIDs)+"}, " +
                "{" + TextUtils.join(", ", commentIDs)+"}";


        return topicObject;
    }



    /******************************************************************************************
     * Functional methods
     * ****************************************************************************************/

    /**
     * addComment is what is called when the active users submits a comment on the topic thread. It
     * creates a new comment and then sets both pointers topic <---> comment. It then notifies the topic
     * owner that someone has commented on his topic
     * @param cUserID: poster ID
     * @param commentMsg: comment content
     */
    public void addComment(String cUserID, String commentMsg) {

        String notificationMsg;
        ArrayList<String> sendToArray = new ArrayList<String>();
        sendToArray.add(getPostUserID());

        // creating new comment and storing to DB
        Comment newComment = Comment.newComment(cUserID, commentMsg, topicID);

        // adding commentID to array in topic
        commentIDs.add(newComment.getCommentID());

        // creating a new notification and sending to owner of topic
        User cUser = DBMgr.getUserByID(cUserID);
        notificationMsg = commentMsg.substring(0, Math.min(70, commentMsg.length()));
        Notification n=Notification.newNotification(cUser.getUserID(), "RE: "+getTopicName() + "\n"+
                                                    notificationMsg, "T"+getTopicID());
        n.sendNotificationTo(sendToArray);

    }

    /**
     * removes pointer from topic to comment
     * @param id
     */
    public void removeComment(String id) {
        commentIDs.remove(id);
    }

    /**
     * Toggles whether a user has "loved" a topic
     * @param userID
     */
    public void toggleLove(String userID) {
        boolean isLoved=returnIsLovedBy(userID);
        if(!isLoved) {
            mavLoveUserIDs.add(userID);
        } else {
            mavLoveUserIDs.remove(userID);
        }
    }

    /**
     * Removes the pointer from group-->topic, sets isActive to false
     */
    public void delete() {
        Group g = DBMgr.getGroupByID(groupID);
        g.removeTopic(topicID);
        setIsActive(false);
    }

    /**
     * Pins topic to a group
     */
    public void pinTopic() {
        Group g = DBMgr.getGroupByID(groupID);

        if(topicID.equals(g.getPinnedTopicID())) {
            g.setPinnedTopicID("");
        } else {
            g.setPinnedTopicID(topicID);
        }

    }

    /******************************************************************************************
     * Static methods
     * ****************************************************************************************/
    public static Topic newTopic(String activeUserID, String topicName, String content, String groupID) {

        // creating a new topic with standard parameters
        Date today = new Date();
        Topic t = new Topic(DBMgr.generateNewTopicID(), today.getTime(), activeUserID, topicName, groupID,
                            content, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(),
                            true);

        // saving notification database
        DBMgr.saveTopic(t);

        return t;
    }

}
