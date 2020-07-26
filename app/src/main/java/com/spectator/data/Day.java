package com.spectator.data;

import com.spectator.utils.DateFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Locale;

public class Day implements JsonObjectConvertable {
    private String formattedDate;
    private int count;

    public static final String DAYS_PATH = "days.json";
    public static final String ARRAY_KEY = "days";
    public static final String countKey = "count";
    public static final String dateKey = "formattedDate";
    public static final String[] jsonKeys = new String[] {dateKey, countKey};
    public static final Class[] constructorArgs = new Class[] {String.class, int.class};

    public Day(String formattedDate, int count) {
        this.formattedDate = formattedDate;
        this.count = count;
    }

    public Day(long timestamp, int count) {
        this(DateFormatter.formatDate(timestamp), count);
    }

    public int getCount() {
        return count;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    @Override
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
