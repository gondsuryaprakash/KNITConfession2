package com.example.surya.knitconfession;

/**
 * Created by surya on 24-Jan-19.
 */
public class posts {
    public String uid,fullname,profileimage,postimage,time,date,description;
    public posts(){

    }

    public posts(String uid, String fullname, String profileimage, String postimage, String time, String date, String description) {
        this.uid = uid;
        this.fullname = fullname;
        this.profileimage = profileimage;
        this.postimage = postimage;
        this.time = time;
        this.date = date;
        this.description = description;

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
