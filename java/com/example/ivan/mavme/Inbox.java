package com.example.ivan.mavme;

import java.util.ArrayList;

/******************************************************************************************
 * Inbox is a class unique to each user and contains his notifications paired with a boolean
 * if his notification is marked as read or unread
 * @author Ivan Huang
 * ****************************************************************************************/

public class Inbox {

    private String inboxID;
    private ArrayList<String> notificationIDs;
    private ArrayList<Boolean> isSeenList;

    public Inbox() {}

    public Inbox(String inboxID, ArrayList<String> notificationIDs, ArrayList<Boolean> isSeenList) {
        this.inboxID= inboxID;
        this.notificationIDs=notificationIDs;
        this.isSeenList = isSeenList;
    }

    /******************************************************************************************
     * Getters
     * ****************************************************************************************/

    public String getInboxID() { return inboxID; }
    public ArrayList<String> getNotificationIDs() { return notificationIDs; }
    public ArrayList<Boolean> getIsSeenList() { return isSeenList;}

    /******************************************************************************************
     * Setters
     * ****************************************************************************************/
    public void setInboxID(String inboxID) { this.inboxID=inboxID; }
    public void setNotificationIDs(ArrayList<String> notificationIDs) { this.notificationIDs=notificationIDs; }
    public void setIsSeenList(ArrayList<Boolean> isSeen) { this.isSeenList=isSeen;}



    /******************************************************************************************
     * Returning functions
     * ****************************************************************************************/
    /**
     * returnIsRead returns whether a notification has been marked as read
     * @param notificationID
     * @return boolean: true if marked as read.
     */
    public boolean returnIsRead(String notificationID) {
        int inboxIndex = notificationIDs.indexOf(notificationID);
        if(inboxIndex>=0) {
            return isSeenList.get(inboxIndex);
        } else {
            return false;
        }
    }

    /**
     * returnUnseenCount returns the number of unread messages in the inbox
     * @return int: number of unread messages
     */
    public int returnUnseenCount() {
        int count=0;
        for(int i=0;i<notificationIDs.size();i++) {
            if(!isSeenList.get(i)) count++;
        }
        return count;
    }


    /******************************************************************************************
     * Functional methods
     * ****************************************************************************************/
    public void addNotification(String id) {
        notificationIDs.add(0, id);
        isSeenList.add(0, false);
    }

    public void toggleSeen(String nID) {
        int inboxIndex = notificationIDs.indexOf(nID);
        if(inboxIndex>=0) {
            if(isSeenList.get(inboxIndex)) {
                isSeenList.set(inboxIndex, false);
            } else {
                isSeenList.set(inboxIndex, true);
            }
        }
        DBMgr.saveInbox(this);
    }


}
