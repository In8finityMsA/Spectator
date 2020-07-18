package com.spectator.menu;

import android.content.Context;
import android.util.DisplayMetrics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Locale;

public class Day implements Serializable {
    private String formattedDate;
    private int count;

    public static final String DAYS_PATH = "days.json";
    public static final String ARRAY_KEY = "days";
    public static final String countKey = "count";
    public static final String dateKey = "FormattedDate";

    public Day(String formattedDate, int count) {
        this.formattedDate = formattedDate;
        this.count = count;
    }

    Day(long timestamp, int count) {
        this(DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN).format(timestamp), count);
    }

    public int getCount() {
        return count;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public JSONObject toJSONObject() {
        JSONObject object = new JSONObject();
        try {
            object.put(countKey, count);
            object.put(dateKey, formattedDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
}
