package com.example.scheduleme.DataClasses;

import java.util.Date;

public class CalendarEntryBuilder {

    private String userID="";
    private String title="";
    private String description="";
    private long date=0;
    private String timeStart;
    private String timeEnd;
    private String type="";
    private boolean important=false;

    public CalendarEntryBuilder(String userID,String title,String description,long date,String type,boolean important,String timeStart,String timeEnd)
    {
        this.userID = userID;
        this.title = title;
        this.description = description;
        this.date = date;
        this.type = type;
        this.important = important;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;

    }
    public CalendarEntryBuilder()
    {
        this.userID = userID;
        this.title = title;
        this.description = description;
        this.date = date;
        this.type = type;
        this.important = important;
        this.timeStart = "0000";
        this.timeEnd = "0000";

    }
    public CalendarEntryBuilder setUserID(String userID)
    {
        this.userID = userID;
        return this;
    }
    public CalendarEntryBuilder setTitle(String title)
    {
        this.title = title;
        return this;
    }
    public CalendarEntryBuilder setDescription(String description)
    {
        this.description = description;
        return this;
    }
    public CalendarEntryBuilder setDate(long date)
    {
        this.date = date;
        return this;
    }
    public CalendarEntryBuilder setTimeStart(String timeStart)
    {
        this.timeStart = timeStart;
        return this;
    }
    public CalendarEntryBuilder setTimeEnd(String timeEnd)
    {
        this.timeEnd = timeEnd;
        return this;
    }
    public CalendarEntryBuilder setType(String type)
    {
        this.type = type;
        return this;
    }
    public CalendarEntryBuilder setImportant(boolean important)
    {
        this.important = important;
        return this;
    }
    public CalendarEntry build()
    {
        return new CalendarEntry(userID,title,description,date,type,important,timeStart,timeEnd);
    }
}
