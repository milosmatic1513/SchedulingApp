package com.example.scheduleme.Utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.scheduleme.R;

public class ReminderBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("test","test");
        if(intent.getStringExtra("title_text")!=null && intent.getStringExtra("body_text")!=null) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "scheduleMe2");
            mBuilder.setSmallIcon(R.drawable.logo_transparent);
            mBuilder.setContentTitle(intent.getStringExtra("title_text"));
            mBuilder.setContentText(intent.getStringExtra("body_text"));
            mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(intent.getIntExtra("id",0), mBuilder.build());
        }
    }


}

