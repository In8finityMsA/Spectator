package com.spectator;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.spectator.utils.PreferencesIO;

import static com.spectator.utils.PreferencesIO.IS_NIGHT_MODE;
import static com.spectator.utils.PreferencesIO.MY_SETTINGS;

public class Spectator extends Application {

    private PreferencesIO preferencesIO;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    public void onCreate() {
        super.onCreate();

        preferencesIO = new PreferencesIO(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode());

        boolean isNightMode = preferencesIO.getBoolean(IS_NIGHT_MODE, false);
        setNightTheme(isNightMode);

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                Log.e("OnSharedChange", s);
                if (s.equals(IS_NIGHT_MODE)) {
                    boolean isNightMode = sharedPreferences.getBoolean(s, false);
                    setNightTheme(isNightMode);
                }
            }
        };
        preferencesIO.setOnChangeListener(listener);
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
