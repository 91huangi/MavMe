package com.example.ivan.mavme;

import java.util.ArrayList;
import java.util.Arrays;


/******************************************************************************************
 * User is a class for the users of MavMe
 * @author Ivan Huang
 * ****************************************************************************************/
public class User {


    private String userID;
    private String name;
    private String password;
    private String email;
    private boolean isMale;
    private String dob;
    private String major;
    private String degree;
    private String gradYear;
    private String phoneNumber;
    private boolean onCampus;
    private boolean isPrivate;
    private boolean isAdmin;
    private boolean isBlocked = false;


    public User() {}

    /**
     * Constructor
     * @param id
     * @param name
     * @param password
     * @param email
     * @param isMale
     * @param dob
     * @param major
     * @param degree
     * @param gradYear
     * @param phoneNumber
     * @param onCampus
     * @param isPrivate
     * @param isAdmin
     * @param isBlocked
     */
    public User(String id, String name, String password, String email, boolean isMale, String dob, String major, String degree,
                String gradYear, String phoneNumber, boolean onCampus, boolean isPrivate, boolean isAdmin, boolean isBlocked) {
        this.userID = id;
        this.name=name;
        this.password = password;
        this.email=email;
        this.isMale = isMale;
        this.dob = dob;
        this.major = major;
        this.degree = degree;
        this.gradYear = gradYear;
        this.phoneNumber = phoneNumber.replaceAll("[^\\d]", "");
        this.onCampus = onCampus;
        this.isPrivate = isPrivate;
        this.isAdmin = isAdmin;
        this.isBlocked=isBlocked;

    }


    /******************************************************************************************
     * Getters
     * ****************************************************************************************/
    public String getUserID() { return userID; }
    public String getName() {return name;}
    public String getEmail() {return email;}
    public boolean getIsMale() {
        return isMale;
    }
    public String getDob() { return dob; }
    public String getPassword() { return password; }
    public String getMajor() { return major; }
    public String getDegree() { return degree; }
    public String getGradYear() { return gradYear; }
    public String getPhoneNumber() { return phoneNumber; }
    public boolean getOnCampus() { return onCampus; }
    public boolean getIsPrivate() { return isPrivate; }
    public boolean getIsAdmin() { return isAdmin; }
    public boolean getIsBlocked() {return isBlocked;}



    /******************************************************************************************
     * Setters
     * ****************************************************************************************/
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email=email; }
    public void setIsMale(boolean isMale) {this.isMale=isMale;}
    public void setDob(String dob) {this.dob = dob;}
    public void setMajor(String major) {this.major=major;}
    public void setDegree(String degree) {this.degree=  degree;}
    public void setGradYear(String gradYear) {this.gradYear = gradYear;}
    public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber.replaceAll("[^\\d]", "");}
    public void setOnCampus(boolean onCampus) {this.onCampus = onCampus;}
    public void setIsPrivate(boolean isPrivate) {this.isPrivate=isPrivate; }
    public void setPassword(String password) {this.password=password; }
    public void setIsBlocked(boolean isBlocked) {this.isBlocked=isBlocked;}



    /******************************************************************************************
     * Returning functions
     * ****************************************************************************************/
    public String returnGender() {
        if(isMale) {
            return "Male";
        } else {
            return "Female";
        }
    }

    /**
     * Returns formatted phone number
     * @return String: XXX-XXX-XXXX
     */
    public String returnPhoneNumber() {
        int length=phoneNumber.length();
        String start = phoneNumber.substring(0, Math.min(3, length));
        String middle = phoneNumber.substring(Math.min(3,length), Math.min(6, length));
        String end = phoneNumber.substring(Math.min(6, length));

        String phone = start;
        if(middle.length()>0) phone = phone+"-"+middle;
        if(end.length()>0) phone= phone+"-"+end;

        return phone;
    }

    public String returnResidence() {
        if(onCampus) {
            return "On Campus";
        } else {
            return "Off Campus";
        }
    }

    /**
     * Returns the index of the major... used for spinner purposes
     * @return
     */
    public int returnMajorIndex() {
        return Utils.majors.indexOf(major);
    }

    public String returnDOB() {
        return dob.substring(0,2)+"/"+dob.substring(2,4)+"/"+dob.substring(4);
    }




    /******************************************************************************************
     * Functional methods
     * ****************************************************************************************/
    public void toggleIsBlocked() {isBlocked=!isBlocked;}


    /******************************************************************************************
     * Static methods
     * ****************************************************************************************/
    public static User newUser(String name, String password, String email, boolean isMale, String dob, String major,
                               String degree, String gradYear, String phoneNumber, boolean onCampus, boolean isPrivate) {

        // creating a new topic with standard parameters
        User u = new User(DBMgr.generateNewUserID(), name, password, email, isMale, dob, major, degree,
                            gradYear, phoneNumber, onCampus, isPrivate, false, false);
        Inbox i=new Inbox(u.getUserID(), new ArrayList<String>(Arrays.asList("+000000")),
                new ArrayList<Boolean>(Arrays.asList(false)));

        // saving notification database
        DBMgr.saveUser(u);
        DBMgr.saveInbox(i);

        return u;
    }


}
