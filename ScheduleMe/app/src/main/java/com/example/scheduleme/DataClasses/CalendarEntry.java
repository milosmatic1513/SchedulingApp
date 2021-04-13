package com.example.scheduleme.DataClasses;

import java.io.Serializable;
import java.util.Date;

public class CalendarEntry implements Serializable {
    private String userID;
    private String title;
    private String description;
    private long date;
    private String timeStart;
    private String timeEnd;
    private String type;
    private boolean important;
    private int repeating;

    public CalendarEntry(String userID,String title,String description,long date,String type,boolean important,String timeStart,String timeEnd,int repeating)
    {
        this.userID = userID;
        this.title = title;
        this.description = description;
        this.date = date;
        this.type = type;
        this.important = important;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.repeating = repeating;
    }

    public CalendarEntry()
    {
        this.userID = "";
        this.title = "";
        this.description = "";
        this.date = 0;
        this.type = "";
        this.important = false;
        this.timeStart = "0000";
        this.timeEnd = "0000";
        this.repeating = 0;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public int getRepeating() {
        return repeating;
    }

    public void setRepeating(int repeating) {
        this.repeating = repeating;
    }
}
