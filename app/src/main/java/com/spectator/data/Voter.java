package com.spectator.data;

import com.spectator.utils.DateFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Locale;

public class Voter implements JsonObjectConvertable {

    public static final String ARRAY_KEY = "voters";
    public static final String timestampKey = "timestamp";
    public static final String dateKey = "formattedDate";
    public static final String timeKey = "formattedTime";
    public static final String countKey = "count";
    public static final String textKey = "text";
    public static final String[] jsonKeys = new String[] {timestampKey, countKey};
    public static final Class[] constructorArgs = new Class[] {long.class, int.class};

    private long timestamp;
    private String formattedDate;
    private String formattedTime;
    private int count;
    private boolean isFlagged = false;
    private String text;

    //class for storing info (timestamp, number and comment) about a voter
    public Voter(long timestamp, int count) {
        this.timestamp = timestamp;
        this.formattedDate = DateFormatter.formatDate(timestamp, "dd.MM.yy");
        this.formattedTime = DateFormatter.formatTime(timestamp, "HH:mm:ss");
        this.count = count;
    }

    public Voter(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getLong("timestamp"), jsonObject.getInt("count"));
        if (jsonObject.has("text")) {
            this.text = jsonObject.getString("text");
            this.isFlagged = true;
        }
    }

    public Voter(long timestamp, int count, String text) {
        this(timestamp, count);
        this.isFlagged = true;
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public int getCount() {
        return count;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public String getText() {
        return text;
    }

    //Creating a Json Object from Voter
    @Override
    public JSONObject toJSONObject() {
        JSONObject JSONObjectVoter = new JSONObject();
        try {
            JSONObjectVoter.put(timestampKey, this.getTimestamp());
            JSONObjectVoter.put(countKey, this.getCount());
            JSONObjectVoter.put(dateKey, this.getFormattedDate());
            JSONObjectVoter.put(timeKey, this.getFormattedTime());
            JSONObjectVoter.put(textKey, this.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return JSONObjectVoter;
    }

    public String toString() {
        String str = this.getFormattedDate() + this.getCount();
        return str;
    }
}
