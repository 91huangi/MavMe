package com.example.ivan.mavme;

import java.util.Date;
import java.util.ArrayList;

import java.text.SimpleDateFormat;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

/******************************************************************************************
 * Post is parent a class used for collecting comment methods and attributes
 * @author Ivan Huang
 * ****************************************************************************************/
public class Post {

    private String postUserID, postContent;
    private ArrayList<String> flagUserIDs;
    long timeStamp;
    private boolean isActive=true;


    public Post() {}

    /**
     * Constructor
     * @param timeStamp
     * @param postUserID
     * @param postContent
     * @param flagUserIDs: ArrayList of users who have flagged/reported the post
     * @param isActive: Display post in system
     */
    public Post(long timeStamp,
                String postUserID,
                String postContent,
                ArrayList<String> flagUserIDs,
                boolean isActive) {
        this.timeStamp = timeStamp;
        this.postUserID = postUserID;
        this.postContent = postContent;
        this.flagUserIDs = flagUserIDs;
        this.isActive=isActive;

    }


    /******************************************************************************************
     * Getters
     * ****************************************************************************************/
    public String getPostContent() {
        return postContent;
    }
    public String getPostUserID() {return postUserID;}
    public long getTimeStamp() {
        return timeStamp;
    }
    public ArrayList<String> getFlagUserIDs() {
        return flagUserIDs;
    }
    public boolean getIsActive() {return isActive;}

    /******************************************************************************************
     * Setters
     * ****************************************************************************************/
    public void setPostContent(String postContent) {
        this.postContent=postContent;
    }
    public void setPostUserID(String postUserID) { this.postUserID=postUserID;}
    public void setTimeStamp(long timeStamp) {
        this.timeStamp=timeStamp;
    }
    public void setFlagUserIDs(ArrayList<String> flagUserIDs) {
        this.flagUserIDs=flagUserIDs;
    }
    public void setIsActive(boolean isActive) {this.isActive=isActive;}


    /******************************************************************************************
     * Returning functions
     * ****************************************************************************************/
    public String returnPostDate() {
        Date postDate = new Date(timeStamp);
        SimpleDateFormat dFormatter = new SimpleDateFormat("MM/dd/yyyy 'at' h:mm a");
        String dateString = dFormatter.format(postDate);
        return dateString;
    }

    public int returnFlagCount() { return flagUserIDs.size(); }

    public boolean returnIsFlaggedBy(String userID) {
        boolean isFlagged=false;
        for(int i=0; i<flagUserIDs.size(); i++) {
            if(flagUserIDs.get(i).equals(userID)) {
                isFlagged=true;
            }
        }
        return isFlagged;
    }


    /******************************************************************************************
     * Functional methods
     * ****************************************************************************************/
    public void toggleFlag(String userID) {

        boolean isFlagged=returnIsFlaggedBy(userID);

        if(isFlagged == false) {
            flagUserIDs.add(userID);
        } else {
            flagUserIDs.remove(userID);
        }
    }

}
