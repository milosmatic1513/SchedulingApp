package com.example.scheduleme.DataClasses;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalendarEntry implements Serializable ,Comparable<CalendarEntry> {
    public static int TYPE_EVENT=0;
    public static int TYPE_REMINDER=1;
    private String databaseID;
    private String title;
    private String description;
    private String publicCode;
    private long date;
    private long timeStart;
    private long timeEnd;
    private int type;
    private boolean important;
    private boolean requireIdScan;
    private int repeating;
    private String base64Image;
    private double locationLat;
    private double locationLong;


    public CalendarEntry(String databaseID,String title,String description,String publicCode,long date,int type,boolean important,boolean requireIdScan,long timeStart,long timeEnd,int repeating, String base64Image,double locationLat,double locationLong) {
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
        this.repeating = repeating;
        this.base64Image = base64Image;
        this.locationLat=locationLat;
        this.locationLong=locationLong;
    }

    public CalendarEntry() {
        this.databaseID = "";
        this.title = "";
        this.description = "";
        this.publicCode = "";
        this.date = 0;
        this.type = TYPE_EVENT;
        this.important = false;
        this.requireIdScan = false;
        this.timeStart = 0;
        this.timeEnd = 0;
        this.repeating = 0;
        this.base64Image = "";
        this.locationLat=0;
        this.locationLong=0;
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

    public String getPublicCode() { return publicCode; }

    public void setPublicCode(String publicCode) {
        this.publicCode = publicCode;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public boolean isRequireIdScan() {
        return requireIdScan;
    }

    public void setRequireIdScan(boolean requireIdScan) {
        this.requireIdScan = requireIdScan;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public int getRepeating() {
        return repeating;
    }

    public void setRepeating(int repeating) {
        this.repeating = repeating;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(double locationLat) { this.locationLat = locationLat; }

    public double getLocationLong() {
        return locationLong;
    }

    public void setLocationLong(double locationLong) { this.locationLong = locationLong; }

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

    public String calculateHourFrom()
    {
        SimpleDateFormat dfHourFrom = new SimpleDateFormat("k", Locale.getDefault());
        String formattedHourFrom = dfHourFrom.format(date+timeStart);

        if(formattedHourFrom.length()==1) formattedHourFrom="0"+formattedHourFrom;

        if(formattedHourFrom.equalsIgnoreCase("24"))formattedHourFrom="00";

        return formattedHourFrom;
    }
    public String calculateMinuteFrom()
    {
        SimpleDateFormat dfMinuteFrom = new SimpleDateFormat("m", Locale.getDefault());
        String formattedMinuteFrom = dfMinuteFrom.format(date+timeStart);
        if(formattedMinuteFrom.length()==1) formattedMinuteFrom="0"+formattedMinuteFrom;

        return formattedMinuteFrom;
    }
    public String calculateHourTo()
    {
        SimpleDateFormat dfHourTo = new SimpleDateFormat("k", Locale.getDefault());
        String formattedHourTo = dfHourTo.format(date+timeEnd);

        if(formattedHourTo.length()==1) formattedHourTo="0"+formattedHourTo;

        if(formattedHourTo.equalsIgnoreCase("24"))formattedHourTo="00";

        return formattedHourTo;
    }
    public String calculateMinuteTo()
    {
        SimpleDateFormat dfMinuteTo = new SimpleDateFormat("m", Locale.getDefault());
        String formattedMinuteTo = dfMinuteTo.format(date+timeEnd);

        if(formattedMinuteTo.length()==1) formattedMinuteTo="0"+formattedMinuteTo;

        return formattedMinuteTo;
    }

    @Override
    public int compareTo(CalendarEntry ce) {
        if(getTimeStart()==ce.getTimeStart())
            return 0;
        else if(getTimeStart()<ce.getTimeStart())
            return 1;
        else
            return -1;
    }
}
