package com.example.surya.knitconfession;

/**
 * Created by surya on 29-Jan-19.
 */
public class commentclass {
    public commentclass(){

    }
    String comment,date,username,time;

    public commentclass(String comment, String date, String username, String time) {
        this.comment = comment;
        this.date = date;
        this.username = username;
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
