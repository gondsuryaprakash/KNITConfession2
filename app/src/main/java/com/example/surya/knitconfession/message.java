package com.example.surya.knitconfession;

/**
 * Created by surya on 04-Feb-19.
 */
public class message {
    public String message,date,from,type,time;
    public message(){

    }

    public message(String message, String date, String from, String type, String time) {
        this.message = message;
        this.date = date;
        this.from = from;
        this.type = type;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
