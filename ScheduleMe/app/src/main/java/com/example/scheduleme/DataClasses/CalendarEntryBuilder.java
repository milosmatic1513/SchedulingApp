package com.example.scheduleme.DataClasses;

import java.util.Date;

public class CalendarEntryBuilder {

    private String databaseID="";
    private String title="";
    private String description="";
    private long date=0;
    private String timeStart;
    private String timeEnd;
    private String type="";
    private boolean important=false;
    private int repeating;

    public CalendarEntryBuilder(String databaseID,String title,String description,long date,String type,boolean important,String timeStart,String timeEnd,int repeating)
    {
        this.databaseID = databaseID;
        this.title = title;
        this.description = description;
        this.date = date;
        this.type = type;
        this.important = important;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.repeating=repeating;

    }
    public CalendarEntryBuilder()
    {
        this.databaseID = databaseID;
        this.title = title;
        this.description = description;
        this.date = date;
        this.type = type;
        this.important = important;
        this.timeStart = "0000";
        this.timeEnd = "0000";
        this.repeating=0;

    }
    public CalendarEntryBuilder setDatabaseID(String databaseID)
    {
        this.databaseID = databaseID;
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
    public CalendarEntryBuilder setRepeating(int repeating)
    {
        this.repeating = repeating;
        return this;
    }
    public CalendarEntry build()
    {
        return new CalendarEntry(databaseID,title,description,date,type,important,timeStart,timeEnd,repeating);
    }
}
