package com.example.scheduleme.DataClasses;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalendarEntry implements Serializable {
    private String databaseID;
    private String title;
    private String description;
    private long date;
    private String timeStart;
    private String timeEnd;
    private String type;
    private boolean important;
    private int repeating;

    public CalendarEntry(String databaseID,String title,String description,long date,String type,boolean important,String timeStart,String timeEnd,int repeating) {
        this.databaseID = databaseID;
        this.title = title;
        this.description = description;
        this.date = date;
        this.type = type;
        this.important = important;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.repeating = repeating;
    }

    public CalendarEntry() {
        this.databaseID = "";
        this.title = "";
        this.description = "";
        this.date = 0;
        this.type = "";
        this.important = false;
        this.timeStart = "0000";
        this.timeEnd = "0000";
        this.repeating = 0;
    }

    public String getDatabaseID() {
        return databaseID;
    }

    public void setDatabaseID(String databaseID) {
        this.databaseID = databaseID;
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

    //Custom functions
    public String getDayOfMonth() {
        SimpleDateFormat dfday = new SimpleDateFormat("dd", Locale.getDefault());
        String formattedDateDay = dfday.format(date);
        return formattedDateDay;
    }

    public String getDayOfWeek() {
        SimpleDateFormat dfdayOfTheWeek = new SimpleDateFormat("E", Locale.getDefault());
        String formattedDateDayOfTheWeek = dfdayOfTheWeek .format(date);
        return formattedDateDayOfTheWeek;
    }

    public String getMonth() {
        SimpleDateFormat dfmonth = new SimpleDateFormat("MMM", Locale.getDefault());
        String formattedDateMonth = dfmonth.format(date);
        return formattedDateMonth;
    }

    public String getMonthNumeric() {
        SimpleDateFormat dfmonth = new SimpleDateFormat("MM", Locale.getDefault());
        String formattedDateMonth = dfmonth.format(date);
        return formattedDateMonth;
    }

    public String getYear() {
        SimpleDateFormat dfyear = new SimpleDateFormat("yyyy", Locale.getDefault());
        String formattedDateYear = dfyear.format(date);
        return formattedDateYear;
    }
}
