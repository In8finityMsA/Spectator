package com.spectator.data;

import android.content.Context;

import androidx.annotation.IntDef;

import com.spectator.R;
import com.spectator.utils.DateFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DateFormat;
import java.util.Locale;

public class Day implements JsonObjectConvertable, Serializable {

    private String name;
    private String yik;
    private String formattedDate;
    private int count;
    private int bands;
    private int mode;

    public static final int PRESENCE = 0;
    public static final int BANDS = 1;
    public static final int PRESENCE_BANDS = 2;

    @IntDef({PRESENCE, BANDS, PRESENCE_BANDS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    public static final String DAYS_PATH = "days.json";
    public static final String ARRAY_KEY = "days";
    public static final String nameKey = "name";
    public static final String yikKey = "yik number";
    public static final String countKey = "count";
    public static final String bandsKey = "bands";
    public static final String dateKey = "formattedDate";
    public static final String modeKey = "mode";
    public static final String[] jsonKeys1 = new String[] {dateKey, countKey};
    public static final Class[] constructorArgs1 = new Class[] {String.class, int.class};
    public static final String[] jsonKeys2 = new String[] {nameKey, yikKey, dateKey, countKey, bandsKey, modeKey};
    public static final Class[] constructorArgs2 = new Class[] {String.class, String.class, String.class, int.class, int.class, int.class};
    public static final Object[] defValues = new Object[] {"Untitled", "0", "01.01.1970", 0, 0, PRESENCE};

    /*public Day(String formattedDate, int count) {
        this.name = formattedDate;
        this.formattedDate = formattedDate;
        this.count = count;
        this.bands = 0;
        this.mode = MODE.PRESENCE;
    }*/

    private Day(String name, String yik, String formattedDate, @Mode int mode) {
        this.name = name;
        this.yik = yik;
        this.formattedDate = formattedDate;
        this.mode = mode;
    }

    public Day(String name, String yik, String formattedDate, int count, int bands, @Mode int mode) {
        this(name, yik, formattedDate, mode);
        this.count = count;
        this.bands = bands;
    }

    public Day(String name, String yik, long timestamp, int count, int bands, @Mode int mode) {
        this(name, yik,  DateFormatter.formatDate(timestamp, "dd.MM.yyyy"), mode);
        this.count = count;
        this.bands = bands;
    }

    public String getName() {
        return this.name;
    }

    public int getCount() {
        return count;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public int getBands() {
        return bands;
    }

    public int getMode() {
        return mode;
    }

    public String getYik() {
        return yik;
    }

    public void setYik(String yik) {
        this.yik = yik;
    }

    public Day getDayWithChanged(int count, int bands) {
        if (this.mode == PRESENCE) {
            return new Day(this.name, this.yik, this.formattedDate, count, 0, this.mode);
        }
        else if (this.mode == PRESENCE_BANDS) {
            return new Day(this.name, this.yik, this.formattedDate, count, bands, this.mode);
        }
        else if (this.mode == BANDS) {
            return new Day(this.name, this.yik, this.formattedDate, 0, bands, this.mode);
        }
        return null;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject object = new JSONObject();
        try {
            object.put(nameKey, name);
            object.put(yikKey, yik);
            object.put(dateKey, formattedDate);
            object.put(countKey, count);
            object.put(bandsKey, bands);
            object.put(modeKey, mode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public String toString() {
        String str  = "Count: " + getCount() + "\nBands: " + getBands() + "\nDate: " + getFormattedDate() + "\nMode: " + getMode();
        return str;
    }
}
