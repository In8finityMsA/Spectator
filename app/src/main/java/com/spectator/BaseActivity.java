package com.spectator;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.spectator.utils.PreferencesIO;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    private PreferencesIO preferencesIO;
    private SharedPreferences.OnSharedPreferenceChangeListener nightListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesIO = new PreferencesIO(this);

        boolean isNightMode = preferencesIO.getBoolean(PreferencesIO.IS_NIGHT_MODE, true);
        setNightTheme(isNightMode);

        int localeIndex = preferencesIO.getInt(PreferencesIO.LANG_RADIOBUTTON_INDEX, 0);
        setLocale(localeIndex);

        nightListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                Log.e("OnSharedChangeBaseNight", s);
                Log.e(getLocalClassName(), "class");
                if (s.equals(PreferencesIO.IS_NIGHT_MODE)) {
                    boolean isNightMode = sharedPreferences.getBoolean(s, true);
                    setNightTheme(isNightMode);
                }
            }
        };
        preferencesIO.setOnChangeListener(nightListener);

    }

    private void setLocale(int localeIndex) {
        Locale locale;
        //Log.e("setLocale", String.valueOf(localeIndex));
        switch (localeIndex) {
            case 0:
                locale = new Locale("en");
                break;
            case 1:
                locale = new Locale("ru");
                break;
            case 2:
                locale = new Locale("be");
                break;
            default:
                locale = new Locale("en");
        }
        Locale.setDefault(locale);
        Configuration config = this.getResources().getConfiguration();
        config.locale = locale;
        this.getResources().updateConfiguration(config,
                this.getResources().getDisplayMetrics());
        Log.e("setLocale", getLocale().toString());
    }

    private Locale getLocale() {
        return this.getResources().getConfiguration().locale;
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
