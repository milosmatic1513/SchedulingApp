package com.example.scheduleme.DataClasses;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.Locale;

public class Preferences {
    public static void setLocale(Activity activity, String activityLocale) {
        Locale activityLoc = new Locale(activityLocale);

        Locale newLocale = new Locale(getLanguage(activity.getApplicationContext()));

        Locale current = activity.getResources().getConfiguration().getLocales().get(0);

        if (!newLocale.equals(current) || !activityLoc.equals(current))
        {
            Locale.setDefault(newLocale);
            Resources resources = activity.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(newLocale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());

        }
    }

    public static String getLanguage(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPreferences.getString("language","en");
    }

    public static void setLanguage(Context ctx, String language) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("language", language);
        editor.apply();
    }
}