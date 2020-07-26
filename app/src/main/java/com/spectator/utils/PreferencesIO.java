package com.spectator.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PreferencesIO {

    public static final String MY_SETTINGS = "Settings";
    public static final String IS_FIRST_TIME = "Is first time";
    public static final String LANG_RADIOBUTTON_INDEX = "Saved language button index";
    public static final String IS_NIGHT_MODE = "Night mode";
    public static final String WALLPAPERS_INDEX = "Wallpaper";
    public final static String TEXT_RADIOBUTTON_INDEX = "Text size button index";

    private SharedPreferences sp;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    public PreferencesIO(Context context) {
        sp = context.getSharedPreferences(MY_SETTINGS, MODE_PRIVATE);
    }

    public int getInt(String key, int value) {
        return sp.getInt(key, value);
    }

    public boolean getBoolean(String key, boolean value) {
        return sp.getBoolean(key, value);
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void setOnChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        //Needed because listener can be garbage collected
        this.listener = listener;
        sp.registerOnSharedPreferenceChangeListener(listener);
    }

}
