package com.example.ivan.mavme;

import java.util.Date;
import java.util.ArrayList;
import java.text.SimpleDateFormat;


import android.content.Intent;
import android.content.Context;

/******************************************************************************************
 * Notification is a class that describes a message that can be sent from admin to user
 * or from a user action to another user. It contains a message and an optional link which will
 * redirect the user to a particular activity.
 * @author Ivan Huang
 * ****************************************************************************************/

public class Notification {

    private String notificationID;
    private String senderID;
    private long timeStamp;
    private String message;
    private String linkTo;

    public Notification() {}

    /**
     * Constructor
     * @param notificationID
     * @param senderID
     * @param timeStamp
     * @param message
     * @param linkTo: a path that when clicked from the user's inbox will redirect him to a screen.
     */
    public Notification(String notificationID, String senderID, long timeStamp, String message, String linkTo) {
        this.notificationID = notificationID;
        this.senderID=senderID;
        this.timeStamp=timeStamp;
        this.message=message;
        this.linkTo=linkTo;
    }

    /******************************************************************************************
     * Getters
     * ****************************************************************************************/
    public String getNotificationID() {return notificationID;}
    public String getSenderID() {return senderID;}
    public String getLinkTo() { return linkTo; }
    public long getTimeStamp() {return timeStamp;}
    public String getMessage() {return message;}


    /******************************************************************************************
     * Setters
     * ****************************************************************************************/
    public void setNotificationID(String notificationID) {this.notificationID=notificationID;}
    public void setSenderID(String senderID) {this.senderID=senderID;}
    public void setLinkTo(String linkTo) { this.linkTo=linkTo; }
    public void setTimeStamp(long timeStamp) {this.timeStamp=timeStamp;}
    public void setMessage(String message) {this.message=message;}



    /******************************************************************************************
     * Returning methods
     * ****************************************************************************************/

    public String returnSenderName() {return DBMgr.getUserByID(senderID).getName();}

    public String returnSendDate() {
        Date sendDate = new Date(timeStamp);
        SimpleDateFormat dFormatter = new SimpleDateFormat("MM/dd/yyyy 'at' h:mm a");
        String dateString = dFormatter.format(sendDate);
        return dateString;
    }



    /******************************************************************************************
     * Functional methods
     * ****************************************************************************************/
    /**
     * sendNotificationTo adds a notification to each inbox listed in an ArrayList
     * @param userIDs: ArrayList of userIDs to send notification to
     */
    public void sendNotificationTo(ArrayList<String> userIDs) {
        for(String id:userIDs){
            Inbox i = DBMgr.getInboxByID(id);
            i.addNotification(notificationID);
        }
    }


    /******************************************************************************************
     * Static methods
     * ****************************************************************************************/
    /**
     * newNotification create a new notification with default ID and timeStamp
     * @param senderID
     * @param message
     * @param linkTo
     * @return Notification
     */
    public static Notification newNotification(String senderID, String message, String linkTo) {
        Notification n = new Notification(DBMgr.generateNewNotificationID(), senderID, new Date().getTime(), message, linkTo);

        // saving notification database
        DBMgr.saveNotification(n);
        return n;
    }



}
