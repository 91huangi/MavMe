package com.example.ivan.mavme;

import java.util.Date;
import java.util.ArrayList;


/******************************************************************************************
 * The Comment class contains all the Post class attributes as well as a commentID and a topicID to
 * reference the parent topic
 * @author Ivan Huang
 * ****************************************************************************************/

public class Comment extends Post {

    private String commentID;
    private String topicID;

    public Comment() {}

    /**
     * Constructor
     * @param commentID: ID to reference comment
     * @param timeStamp: Time stamp
     * @param postUserID: The user ID of the owner of the comment
     * @param postContent: Comment message
     * @param topicID: ID to reference the owning topic
     * @param flagUserIDs: List of user IDs to track every flagger
     * @param isActive
     */
    public Comment(String commentID,
                   long timeStamp,
                   String postUserID,
                   String postContent,
                   String topicID,
                   ArrayList<String> flagUserIDs,
                   boolean isActive) {

        super(timeStamp, postUserID, postContent, flagUserIDs, isActive);
        this.commentID = commentID;
        this.topicID = topicID;

    }

    /******************************************************************************************
     * Getting methods
     * ****************************************************************************************/
    public String getCommentID() { return commentID; }
    public String getTopicID() {
        return topicID;
    }

    /******************************************************************************************
     * Setting methods
     * ****************************************************************************************/
    public void setCommentID(String commentID) {this.commentID= commentID;}
    public void setTopicID(String topicID) {this.topicID=topicID;}


    /******************************************************************************************
     * Returning methods
     * ****************************************************************************************/
    public String toString() {
        String commentObject;

        commentObject=getCommentID()+", "+returnPostDate()+", "+getPostUserID()+", "+
                getPostContent()+", {" + topicID+"}";


        return commentObject;
    }


    /******************************************************************************************
     * Functional methods
     * ****************************************************************************************/

    /**
     * This method deletes the comment by removing the ID from the parent topic and setting isActive to false
     */
    public void delete() {
        Topic t = DBMgr.getTopicByID(topicID);
        t.removeComment(commentID);
        topicID="";
        setIsActive(false);
    }


    /******************************************************************************************
     * Static methods
     * ****************************************************************************************/

    /**
     * This comment creates a new comment with auto-filled commentID, timeStamp, and postUserID
     * @return Comment
     */
    public static Comment newComment(String activeUserID, String content, String topicID) {

        // creating a new topic with standard parameters
        Date today = new Date();
        Comment c = new Comment(DBMgr.generateNewCommentID(), today.getTime(), activeUserID,
                content, topicID, new ArrayList<String>(), true);

        // saving notification database
        DBMgr.saveComment(c);
        return c;
    }


}
