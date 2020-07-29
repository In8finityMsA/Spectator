package com.spectator;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.spectator.utils.PreferencesIO;

public class Spectator extends Application {

    private PreferencesIO preferencesIO;
    private SharedPreferences.OnSharedPreferenceChangeListener nightListener;

    @Override
    public void onCreate() {
        super.onCreate();
        preferencesIO = new PreferencesIO(this);

        boolean isNightMode = preferencesIO.getBoolean(PreferencesIO.IS_NIGHT_MODE, true);
        setNightTheme(isNightMode);

        nightListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                Log.e("OnSharedChangeBaseNight", s);
                Log.e(getPackageName(), "class");
                if (s.equals(PreferencesIO.IS_NIGHT_MODE)) {
                    boolean isNightMode = sharedPreferences.getBoolean(s, true);
                    setNightTheme(isNightMode);
                }
            }
        };
        preferencesIO.setOnChangeListener(nightListener);

    }

    private void setNightTheme(boolean isNightMode) {
        Log.e("Night", String.valueOf(isNightMode));
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

}
