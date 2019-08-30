package com.example.surya.knitconfession;

/**
 * Created by surya on 28-Jan-19.
 */
public class search {
    String username,Status,usercollege,profileImage;
    public search(){

    }

    public search(String username, String status, String usercollege, String profileImage) {
        this.username = username;
        Status = status;
        this.usercollege = usercollege;
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getUsercollege() {
        return usercollege;
    }

    public void setUsercollege(String usercollege) {
        this.usercollege = usercollege;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}