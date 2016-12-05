package com.example.ivan.mavme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.text.TextUtils;
import android.util.Log;


import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by Ivan on 11/3/16.
 */

/******************************************************************************************
 * DBMgr manages the database built via. Google Firebase 10.0.1
 * ****************************************************************************************/
public class DBMgr {


    // local copy of database
    private static HashMap<String, User> users = new HashMap<String, User>();
    private static ArrayList<String> adminIDs = new ArrayList<String>(Arrays.asList("+000000"));
    private static HashMap<String, Group> groups = new HashMap<String, Group>();
    private static HashMap<String, Topic> topics = new HashMap<String, Topic>();
    private static HashMap<String, Comment> comments = new HashMap<String, Comment>();
    private static HashMap<String, Notification> notifications = new HashMap<String, Notification>();
    private static HashMap<String, Inbox> inboxes = new HashMap<String, Inbox>();


    private static FirebaseDatabase database;
    private static boolean initiated = false;



    /******************************************************************************************
     * This method should be called on the start of the application. It initializes the database
     * ****************************************************************************************/
    public static void initiateDB() {
        if(!initiated) {
            initiated = true;
            database = FirebaseDatabase.getInstance();
            syncUsers();
            syncGroups();
            syncNotifications();
            syncInboxes();
            syncTopics();
            syncComments();
            //restoreDefaultSettings();
        }
    }

    /******************************************************************************************
     * Sequential ID generation
     * ****************************************************************************************/
    public static String generateNewUserID() { return indexToID(users.size()); }
    public static String generateNewNotificationID() {
        return indexToID(notifications.size());
    }
    public static String generateNewGroupID() { return indexToID(groups.size()); }
    public static String generateNewTopicID() { return indexToID(topics.size());}
    public static String generateNewCommentID() { return indexToID(comments.size()); }



    /******************************************************************************************
     * These methods take an objects and save them to the database
     * ****************************************************************************************/
    public static void saveUser(User u) {
        DatabaseReference dbRef = database.getReference("users/"+u.getUserID());
        users.put(u.getUserID(), u);
        dbRef.setValue(u);
    }
    public static void saveNotification(Notification n) {
        DatabaseReference dbRef = database.getReference("notifications/"+n.getNotificationID());
        notifications.put(n.getNotificationID(), n);
        dbRef.setValue(n);
    }
    public static void saveInbox(Inbox i) {
        DatabaseReference dbRef = database.getReference("inboxes/"+i.getInboxID());
        inboxes.put(i.getInboxID(), i);
        dbRef.setValue(i);
    }
    public static void saveGroup(Group g) {
        DatabaseReference dbRef = database.getReference("groups/"+g.getGroupID());
        groups.put(g.getGroupID(), g);
        dbRef.setValue(g);
    }
    public static void saveTopic(Topic t) {
        DatabaseReference dbRef = database.getReference("topics/"+t.getTopicID());
        topics.put(t.getTopicID(), t);
        dbRef.setValue(t);
    }
    public static void saveComment(Comment c) {
        DatabaseReference dbRef = database.getReference("comments/"+c.getCommentID());
        comments.put(c.getCommentID(), c);
        dbRef.setValue(c);
    }


    /******************************************************************************************
     * These methods add listeners on the database so that it is synchronized with local variables
     * ****************************************************************************************/
    public static void syncUsers() {
        DatabaseReference dbRef = database.getReference("users");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                for(DataSnapshot s:snap.getChildren()) {
                    User u = s.getValue(User.class);

                    users.put(s.getKey(), u);
                    //adding admin IDs to system
                    if(u.getIsAdmin() && !adminIDs.contains(s.getKey())) adminIDs.add(s.getKey());
                }
            }
            @Override
            public void onCancelled(DatabaseError e) { }
        });
    }

    public static void syncNotifications() {
        DatabaseReference dbRef = database.getReference("notifications");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                for(DataSnapshot s:snap.getChildren()) {
                    Notification n = s.getValue(Notification.class);
                    notifications.put(s.getKey(), n);
                }
            }
            @Override
            public void onCancelled(DatabaseError e) { }
        });
    }

    public static void syncInboxes() {
        DatabaseReference dbRef = database.getReference("inboxes");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                for(DataSnapshot s:snap.getChildren()) {
                    Inbox i = s.getValue(Inbox.class);
                    if(i.getNotificationIDs()==null) i.setNotificationIDs(new ArrayList<String>());
                    if(i.getIsSeenList()==null) i.setIsSeenList(new ArrayList<Boolean>());
                    inboxes.put(s.getKey(), i);
                }
            }
            @Override
            public void onCancelled(DatabaseError e) { }
        });
    }

    public static void syncGroups() {
        DatabaseReference dbRef = database.getReference("groups");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                for(DataSnapshot s:snap.getChildren()) {
                    Group g = s.getValue(Group.class);
                    if(g.getSubGroupIDs()==null) g.setSubGroupIDs(new ArrayList<String>());
                    if(g.getTopicIDs()==null) g.setTopicIDs(new ArrayList<String>());
                    groups.put(s.getKey(), g);
                }
            }
            @Override
            public void onCancelled(DatabaseError e) { }
        });
    }

    public static void syncTopics() {
        DatabaseReference dbRef = database.getReference("topics");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                for(DataSnapshot s:snap.getChildren()) {
                    Topic t = s.getValue(Topic.class);
                    if(t.getCommentIDs()==null) t.setCommentIDs(new ArrayList<String>());
                    if(t.getMavLoveUserIDs()==null) t.setMavLoveUserIDs(new ArrayList<String>());
                    if(t.getFlagUserIDs()==null) t.setFlagUserIDs(new ArrayList<String>());
                    topics.put(s.getKey(), t);
                }
            }
            @Override
            public void onCancelled(DatabaseError e) { }
        });
    }

    public static void syncComments() {
        DatabaseReference dbRef = database.getReference("comments");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                for(DataSnapshot s:snap.getChildren()) {
                    Comment c = s.getValue(Comment.class);
                    if(c.getFlagUserIDs()==null) c.setFlagUserIDs(new ArrayList<String>());
                    comments.put(s.getKey(), c);
                }
            }
            @Override
            public void onCancelled(DatabaseError e) { }
        });
    }




    /******************************************************************************************
     * These methods accept an object ID and returns it from the database
     * @param id
     * @return object
     * ****************************************************************************************/

    public static User getUserByID(String id) {
        return users.get(id);
    }
    public static User getUserByEmail(String email) {
        for(int i=0;i<users.size();i++) {
            User u=users.get(indexToID(i));
            if(u.getEmail().equals(email)) return u;
        }
        return null;
    }
    public static Group getGroupByID(String id) {
        return groups.get(id);
    }
    public static Topic getTopicByID(String id) {
        return topics.get(id);
    }
    public static Comment getCommentByID(String id) {
        return comments.get(id);
    }
    public static Notification getNotificationByID(String id) {
        return notifications.get(id);
    }
    public static Inbox getInboxByID(String id) {
        return inboxes.get(id);
    }


    /******************************************************************************************
     * Getting methods
     ******************************************************************************************/
    public static ArrayList<String> getAdminIDs() { return adminIDs; }
    public static HashMap<String, Topic> getTopics() {return topics;}



    /******************************************************************************************
     * Returning methods
     ******************************************************************************************/

    /**
     * This method is used by the history tab on the home page. It returns a list of topicIDs where
     * the activeUserID matches the postUserID
     * @param uID
     * @return ArrayList of topicIDs
     */
    public static ArrayList<String> returnTopicIDsByUser(String uID) {
        ArrayList<String> topicIDs = new ArrayList<String>();
        for(int i=0;i<topics.size();i++) {
            Topic t=topics.get(indexToID(i));
            if(t.getIsActive() && t.getPostUserID().equals(uID))
                topicIDs.add(t.getTopicID());
        }

        return topicIDs;
    }


    /**
     * This method is used by the group list display page. It returns a list of groupIDs sorted by the
     * groupName field.
     * @return ArrayList of groupIDs
     */
    public static ArrayList<String> returnSortedGroupIDs() {
        ArrayList<String> sortedGroupIDs = new ArrayList<String>();
        ArrayList<String> groupIDs = new ArrayList<String>();
        ArrayList<String> groupNames = new ArrayList<String>();

        for(int i=0;i<groups.size(); i++) {
            Group g=groups.get(indexToID(i));
            groupIDs.add(g.getGroupID());
            groupNames.add(g.getGroupName());
        }

        sortedGroupIDs=Utils.pairSort(groupIDs, groupNames);
        return sortedGroupIDs;
    }


    /**
     * This method returns the most recently created topics
     * @param n: the number of topics to be returned
     * @return ArrayList of 'n' members containing Topics
     */
    public static ArrayList<Topic> returnMostRecentTopics(int n) {
        ArrayList<Topic> topicList = new ArrayList<Topic>();
        int i=topics.size()-1, count=0;

        // preventing index out of bounds error
        if(n<0||n>topics.size()) n = topics.size();

        // getting the most recent 'n' topics
        while(i>=topics.size()-n && count<n) {
            if(topics.get(indexToID(i)).getIsActive()) {
                topicList.add(topics.get(indexToID(i)));
                count++;
            }
            i--;
        }

        return topicList;
    }

    /**
     * This method converts an integer to the database ID format
     * @param i
     * @return String of the form '+XXXXXX'
     */
    public static String indexToID(int i) {
        String id="000000"+i;
        return "+"+id.substring(id.length()-6);
    }

    /**
     * This method takes an database ID string and converts it to an integer
     * @param id
     * @return int value of id string
     */
    public static int idToIndex(String id) {
        return Integer.parseInt(id.substring(1));
    }

    /**
     * Returns number of users in system. Can be used to iterate through all users
     * @return int of number of users.
     */
    public static int numberOfUsers() { return users.size(); }



    public static void restoreDefaultSettings() {
        // adding default system values
        User u = new User("+000000", "System Admin", "abc123", "mavme@mavs.uta.edu", true, "01011900", "Undecided", "Faculty",
                "Faculty", "0000000000", false, true, true, false);
        DBMgr.saveUser(u);

        Notification n = new Notification("+000000","+000000", new Date(116,11,1).getTime(),
                "Welcome to MavMe, the social network designed by and for UTA Mavericks!", "");
        DBMgr.saveNotification(n);

        Topic t = new Topic("+000000", new Date(116,11,1).getTime(), "+000000", "First MavMe Topic", "+000000",
                "This is a auto-generated topic from the System Admin", new ArrayList<String>(Arrays.asList("+000000")),
                new ArrayList<String>(Arrays.asList("+000000")), new ArrayList<String>(Arrays.asList("+000000")),
                true);
        DBMgr.saveTopic(t);

        Comment c=new Comment("+000000", new Date(116,11,1).getTime(), "+000000", "Testing the comment functionality",
                "+000000", new ArrayList<String>(), true);
        DBMgr.saveComment(c);

        resetGroups();

        updateAll();
    }



    /******************************************************************************************
     * Functional methods
     ******************************************************************************************/

    /**
     * Resets the groups to their default settings and empties them.
     */
    public static void resetGroups() {
        groups.clear();
        Group g;
        g=Group.newGroup("Academics","");
        g.addSubgroup("Business");
        g.addSubgroup("Education");
        g.addSubgroup("Engineering");
        g.addSubgroup("Health & Nursing");
        g.addSubgroup("Liberal Arts");
        g.addSubgroup("Natural Sciences");
        g.addSubgroup("Social Work");
        g = Group.newGroup("Athletics", "");
        g.addSubgroup("NCAA");
        g.addSubgroup("Club Sports");
        g=Group.newGroup("Clubs", "");
        g=Group.newGroup("Dating", "");
        g.addSubgroup("Singles");
        g.addSubgroup("Couples");
        g=Group.newGroup("Events", "");
        g=Group.newGroup("Explore Arlington", "");
        g=Group.newGroup("Food", "");
        g=Group.newGroup("Faculty", "");
        g=Group.newGroup("Gaming", "");
        g=Group.newGroup("Graduate Students", "");
        g=Group.newGroup("Greek Life", "");
        g=Group.newGroup("Housing", "");
        g=Group.newGroup("Incoming Mavericks", "");
        g=Group.newGroup("Off Campus Life", "");
        g=Group.newGroup("Miscellaneous", "");
        updateAll();
    }


    /**
     * Uploads the remote database with information from the local memory
     */
    public static void updateAll() {
        for(Map.Entry<String, User> e:users.entrySet()) {
            saveUser(e.getValue());
        }
        for(Map.Entry<String, Notification> e:notifications.entrySet()) {
            saveNotification(e.getValue());
        }
        for(Map.Entry<String, Inbox> e:inboxes.entrySet()) {
            saveInbox(e.getValue());
        }
        for(Map.Entry<String, Group> e:groups.entrySet()) {
            saveGroup(e.getValue());
        }
        for(Map.Entry<String, Topic> e:topics.entrySet()) {
            saveTopic(e.getValue());
        }
        for(Map.Entry<String, Comment> e:comments.entrySet()) {
            saveComment(e.getValue());
        }
    }


}
