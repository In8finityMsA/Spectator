package com.spectator;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Locale;

public class Voter {

    private long timestamp;
    private String formattedDate;
    private String formattedTime;
    private int count;
    private boolean isFlagged = false;
    private String text;

    //class for storing info (timestamp, number and comment) about a voter
    public Voter(long timestamp, int count) {
        this.timestamp = timestamp;
        this.formattedDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRENCH).format(timestamp);
        this.formattedTime = DateFormat.getTimeInstance().format(timestamp);
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
    public JSONObject toJSONObject() {
        JSONObject JSONObjectVoter = new JSONObject();
        try {
            JSONObjectVoter.put("timestamp", this.getTimestamp());
            JSONObjectVoter.put("count", this.getCount());
            JSONObjectVoter.put("formattedDate", this.getFormattedDate());
            JSONObjectVoter.put("formattedTime", this.getFormattedTime());
            JSONObjectVoter.put("text", this.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return JSONObjectVoter;
    }
}
