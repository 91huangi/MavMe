package com.example.ivan.mavme;

import android.content.Intent;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;



/******************************************************************************************
 * Utils contains a list of resources and methods for the overall functionality of the application
 * @author Ivan Huang
 * ****************************************************************************************/

public class Utils {


    /******************************************************************************************
     * Resources
     ******************************************************************************************/
    public final static ArrayList<String> genders = new ArrayList<String>(Arrays.asList("Male", "Female"));
    public final static ArrayList<String> degrees = new ArrayList<String>(Arrays.asList("Bachelors", "Masters", "Doctorate", "Faculty"));
    public final static ArrayList<String> residences = new ArrayList<String>(Arrays.asList("On Campus", "Off Campus"));
    public final static ArrayList<String> majors = new ArrayList<String>(
            Arrays.asList("Accounting",
                    "Aerospace Engineering",
                    "Anthropology",
                    "Architecture",
                    "Art",
                    "Athletic Training",
                    "Biochemistry",
                    "Biology",
                    "BusinessAdministration",
                    "Chemistry",
                    "Child/Bilingual Studies",
                    "Civil Engineering",
                    "Communication",
                    "Computer Science",
                    "Computer Science and Engineering",
                    "Criminology and Criminal Justice",
                    "Economics",
                    "Electrical Engineering",
                    "English",
                    "Exercise Science",
                    "Geology",
                    "History",
                    "Industrial Engineering",
                    "Information Systems",
                    "Interdisciplinary Studies",
                    "Interdisciplinary Studies",
                    "Interior Design",
                    "Kinesiology",
                    "Mathematics",
                    "Mechanical Engineering",
                    "Medical Technology",
                    "Microbiology",
                    "Modern Languages",
                    "Music",
                    "Nursing",
                    "Philosophy",
                    "Physics",
                    "Political Science",
                    "Psychology",
                    "Social Work",
                    "Sociology",
                    "Software Engineering",
                    "Theatre Arts",
                    "University Studies",
                    "Undecided"));
    public final static String sysID = "+000000";





    /******************************************************************************************
     * Sorting methods
     * ****************************************************************************************/
    /**
     * PairSort does descend sort of L1 by the order given in L2
     * @param l1
     * @param l2: List to be sorted by
     * @param <T>: Object
     * @param <V>: Object & Comparable
     * @return ArrayList<T>: sorted list
     */
    public static <T,V extends Object & Comparable<V>> ArrayList<T> pairSort(ArrayList<T> l1, ArrayList<V> l2) {
        int size=l1.size();
        ArrayList<V> sortedL2 = new ArrayList<V>();
        ArrayList<T> sortedL1 = new ArrayList<T>();

        // sorting by List2
        for(int i=0;i<size;i++) {
            sortedL2.add(l2.get(i));
        }
        Collections.sort(sortedL2);

        // Using SortedList2 to map to List1
        for(int i=0;i<size;i++) {
            V sortedValue = sortedL2.get(i);
            sortedL1.add(l1.get(l2.indexOf(sortedValue)));
        }

        return sortedL1;

    }


    /******************************************************************************************
     * Navigation methods
     * ****************************************************************************************/
    /**
     * GoToURL changes the activity based on the given url
     * @param context
     * @param url: 'T+ID' for topic, 'U+ID' for user, 'G+ID' for group
     */
    public static void goToURL(Context context, String url) {

        User activeUser = DBMgr.getUserByID(HomeDisplay.userID);
        String errorObj="";

        //going to link
        if(url.length()!=0) {
            String id = url.substring(1);

            // linking to topic
            if (url.charAt(0) == 'T') {
                Topic t = DBMgr.getTopicByID(id);
                if(t==null) {
                    errorObj="Topic";
                } else if(!t.getIsActive() && !activeUser.getIsAdmin()) {
                    errorObj="Topic";
                } else {
                    Intent i = new Intent("com.example.ivan.mavme.TopicDisplay");
                    i.putExtra("topicID", id);
                    context.startActivity(i);
                }
            }

            // linking to user
            if (url.charAt(0) == 'U') {
                User u = DBMgr.getUserByID(id);

                if(u==null) {
                    errorObj="User";
                } else if(u.getIsBlocked() && !activeUser.getIsAdmin()) {
                    errorObj="User";
                } else {
                    Intent i = new Intent("com.example.ivan.mavme.UserDisplay");
                    i.putExtra("userID", id);
                    context.startActivity(i);
                }

            }


            // linking to group
            if (url.charAt(0) == 'G') {
                Group g = DBMgr.getGroupByID(id);

                if(g==null) {
                    errorObj="Group";
                } else if(!g.getIsActive() && !activeUser.getIsAdmin()) {
                    errorObj="Group";
                } else {
                    Intent i = new Intent("com.example.ivan.mavme.GroupDisplay");
                    i.putExtra("groupID", id);
                    context.startActivity(i);
                }
            }

            if(errorObj.length()>0) alertDialog(context, errorObj+" is no longer active", new dialogHandler() {
                public void onButtonClick(boolean click) {}
            });
        }
    }


    /******************************************************************************************
     * Dialog/Alert methods
     * ****************************************************************************************/
    public interface dialogHandler {
        void onButtonClick(boolean click);
    }

    /**
     * Confirmation dialog with Yes/No options
     * @param context
     * @param message
     * @param d
     */
    public static void confirmationDialog(Context context, String message, final dialogHandler d) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                d.onButtonClick(true);
            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        d.onButtonClick(false);
                    }
                });

        // Create the AlertDialog object and return it
        builder.create().show();
    }

    /**
     * Basic alert dialog
     * @param context
     * @param message
     * @param d
     */
    public static void alertDialog(Context context, String message, final dialogHandler d) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                d.onButtonClick(true);
            }
        });

        // Create the AlertDialog object and return it
        builder.create().show();

    }

}
