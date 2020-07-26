package com.spectator;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class Spectator extends Application {

    private static final String MY_SETTINGS = "Settings";
    private static final String IS_NIGHT_MODE =  "Night Mode";

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sp = getSharedPreferences(MY_SETTINGS, MODE_PRIVATE);
        boolean isNightMode = sp.getBoolean(IS_NIGHT_MODE, false);
        if(isNightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

    }
}
