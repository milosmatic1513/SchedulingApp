package com.example.scheduleme.DataClasses;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class CalendarEntryBuilder {
    public static int TYPE_EVENT=0;
    public static int TYPE_REMINDER=1;
    private String databaseID="";
    private String title="";
    private String description="";
    private String publicCode="";
    private long date=0;
    private long timeStart;
    private long timeEnd;
    private int type=TYPE_EVENT;
    private boolean important=false;
    private boolean requireIdScan=false;
    private int repeating;
    private String base64Image;
    private double locationLat=0;
    private double locationLong=0;
    private boolean reminder=false;
    private int reminderTime = CalendarEntry.REMINDER_TIME_FIVE_MINUTES;

    public CalendarEntryBuilder(String databaseID, String title, String description,String publicCode, long date, int type, boolean important,boolean requireIdScan, long timeStart, long timeEnd, int repeating, String base64Image,double locationLat,double locationLong,boolean reminder,int reminderTime)
    {
        this.databaseID = databaseID;
        this.title = title;
        this.description = description;
        this.publicCode = publicCode;
        this.date = date;
        this.type = type;
        this.important = important;
        this.requireIdScan = requireIdScan;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.repeating=repeating;
        this.base64Image = base64Image;
        this.locationLat=locationLat;
        this.locationLong=locationLong;
        this.reminder=reminder;
        this.reminderTime=reminderTime;
    }
    public CalendarEntryBuilder()
    {
        this.databaseID = databaseID;
        this.title = title;
        this.description = description;
        this.publicCode = publicCode;
        this.date = date;
        this.type = type;
        this.important = important;
        this.requireIdScan = requireIdScan;
        this.timeStart = 0;
        this.timeEnd = 0;
        this.repeating=0;
        this.base64Image="";
        this.locationLat=locationLat;
        this.locationLong=locationLong;
        this.reminder = reminder;
        this.reminderTime=reminderTime;

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
    public CalendarEntryBuilder setPublicCode(String publicCode)
    {
        this.publicCode = publicCode;
        return this;
    }
    public CalendarEntryBuilder setDate(long date)
    {
        this.date = date;
        return this;
    }
    public CalendarEntryBuilder setTimeStart(long timeStart)
    {
        this.timeStart = timeStart;
        return this;
    }
    public CalendarEntryBuilder setTimeEnd(long timeEnd)
    {
        this.timeEnd = timeEnd;
        return this;
    }
    public CalendarEntryBuilder setType(int type)
    {
        this.type = type;
        return this;
    }
    public CalendarEntryBuilder setImportant(boolean important)
    {
        this.important = important;
        return this;
    }
    public CalendarEntryBuilder setRequireIdScan(boolean requireIdScan)
    {
        this.requireIdScan = requireIdScan;
        return this;
    }

    public CalendarEntryBuilder setRepeating(int repeating)
    {
        this.repeating = repeating;
        return this;
    }
    public CalendarEntryBuilder setBase64Image(String base64Image)
    {
        this.base64Image = base64Image;
        return this;
    }

    public CalendarEntryBuilder setLocationLat(double locationLat)
    {
        this.locationLat = locationLat;
        return this;
    }
    public CalendarEntryBuilder setLocationLong(double locationLong)
    {
        this.locationLong = locationLong;
        return this;
    }

    public CalendarEntryBuilder setReminder(boolean reminder)
    {
        this.reminder = reminder;
        return this;
    }
    public CalendarEntryBuilder setReminderTime(int reminderTime)
    {
        this.reminderTime = reminderTime;
        return this;
    }


    public CalendarEntry build()
    {
        return new CalendarEntry(databaseID,title,description,publicCode,date,type,important,requireIdScan,timeStart,timeEnd,repeating,base64Image,locationLat,locationLong,reminder,reminderTime);
    }

}
