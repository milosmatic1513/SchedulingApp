package com.example.scheduleme.Utilities;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.example.scheduleme.DataClasses.CalendarEntry;

import java.util.Date;

public class ReminderUtilities {
    static AlarmManager alarmManager;
    Context context;
    NotificationManager notificationManager;

    public ReminderUtilities(Context context) {
        this.context = context;
        createNotificationChannel(context);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "scheduleMe2";
            String description ="scheduleMe2";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("scheduleMe2", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    public void clearNotifications(){
        notificationManager.cancelAll();
    }

    public void startAlarm(String title,String body , int id,long time,int reminderTime) {
        Intent alarmIntent = new Intent(context, ReminderBroadcast.class);
        alarmIntent.putExtra("title_text",title);
        alarmIntent.putExtra("body_text",body);
        alarmIntent.putExtra("id",id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        switch(reminderTime) {
            case 0:
                time = time - (5*60 * 1000);
                break;
            case 1:
                time = time - (10*60 * 1000);
                break;
            case 2:
                time = time - (15*60 * 1000);
                break;
            case 3:
                time = time - (30*60 * 1000);
                break;
            case 4:
                time = time - (60*60 * 1000);
                break;
        }

        Log.e("tag","Alarm Set At :"+time);



        //alarmManager.cancel(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);

        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
    }

    public void startAlarmRepeating(String title,String body , int id,long time){


        Intent alarmIntent = new Intent(context, ReminderBroadcast.class);
        alarmIntent.putExtra("title_text",title);
        alarmIntent.putExtra("body_text",body);
        alarmIntent.putExtra("id",id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.e("tag","Alarm Repeating Set "+time);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),1000 * 60 * 10, pendingIntent);

    }

    public void cancelAlarm(int id){
        Log.e("tag","Alarm Canceled");
        Intent alarmIntent = new Intent(context, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    public void clearAllAlarms(){
        for(int i = 0; i<10 ;i++){
            cancelAlarm(i);
        }
    }

}
