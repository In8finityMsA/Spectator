package com.spectator.data;

import com.spectator.utils.DateFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Locale;

public class Day implements JsonObjectConvertable, Serializable {

    private String formattedDate;
    private int count;
    private int bands;
    private MODE mode = MODE.PRESENCE;

    public static final String DAYS_PATH = "days.json";
    public static final String ARRAY_KEY = "days";
    public static final String countKey = "count";
    public static final String bandsKey = "bands";
    public static final String dateKey = "formattedDate";
    public static final String modeKey = "mode";
    public static final String[] jsonKeys1 = new String[] {dateKey, countKey};
    public static final Class[] constructorArgs1 = new Class[] {String.class, int.class};
    public static final String[] jsonKeys2 = new String[] {dateKey, countKey, bandsKey, modeKey};
    public static final Class[] constructorArgs2 = new Class[] {String.class, int.class, int.class, MODE.class};
    public static final Object[] defValues = new Object[] {"01.01.1970", 0, 0, MODE.PRESENCE};

    public Day(String formattedDate, int count) {
        this.formattedDate = formattedDate;
        this.count = count;
    }

    public Day(String formattedDate, int count, MODE mode) {
        this(formattedDate, count);
        this.bands = 0;
        this.mode = mode;
    }

    public Day(String formattedDate, MODE mode, int bands) {
        this.formattedDate = formattedDate;
        this.bands = bands;
        this.mode = mode;
    }

    public Day(String formattedDate, int count, int bands, MODE mode) {
        this(formattedDate, count);
        this.bands = bands;
        this.mode = mode;
    }

    public Day(long timestamp, int count) {
        this(DateFormatter.formatDate(timestamp, "dd.MM.yyyy"), count);
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

    public MODE getMode() {
        return mode;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject object = new JSONObject();
        try {
            if (mode == MODE.PRESENCE || mode == MODE.PRESENCE_BANDS)
            object.put(countKey, count);
            if (mode == MODE.BANDS || mode == MODE.PRESENCE_BANDS) {
                object.put(bandsKey, bands);
            }
            object.put(dateKey, formattedDate);
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

    public enum MODE {
        PRESENCE,
        BANDS,
        PRESENCE_BANDS
    }
}
