package com.spectator;

import java.text.DateFormat;
import java.util.Locale;

public class Voter {

    private long time;
    private String formattedDate;
    private String formattedTime;
    private int count;
    private boolean isFlagged;
    private String text;

    public Voter(long time, int count) {
        this.time = time;
        this.formattedDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRENCH).format(time);
        this.formattedTime = DateFormat.getTimeInstance().format(time);
        this.count = count;
        this.isFlagged = false;
    }

    public Voter(long time, int count, String text) {
        this(time, count);
        this.isFlagged = true;
        this.text = text;
    }

    public long getTime() {
        return time;
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
}
