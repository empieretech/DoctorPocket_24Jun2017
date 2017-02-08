package com.docpoc.doctor.classes;

/**
 * Created by Badruduja on 3/21/16.
 */
public class User {

    String userEmail;

    public String getCurrent_zone_date_time() {
        return current_zone_date_time;
    }

    public void setCurrent_zone_date_time(String current_zone_date_time) {
        this.current_zone_date_time = current_zone_date_time;
    }

    String current_zone_date_time;
    String userName;
    String profileUrl;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String userID="";
    String user_DeviceID ="";
   String user_Type;

    public String getMessageBadgeVal() {
        return messageBadgeVal;
    }

    public void setMessageBadgeVal(String messageBadgeVal) {
        this.messageBadgeVal = messageBadgeVal;
    }

    String messageBadgeVal="";

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUser_DeviceID() {
        return user_DeviceID;
    }

    public void setUser_DeviceID(String user_DeviceID) {
        this.user_DeviceID = user_DeviceID;
    }

    public String getUser_Type() {
        return user_Type;
    }

    public void setUser_Type(String user_Type) {
        this.user_Type = user_Type;
    }

}
