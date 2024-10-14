package com.blockafeller.util.gson;

public class LocalTimeGsonResource {
    private int hour;
    private int minute;
    private int second;
    private int nano;

    public LocalTimeGsonResource(int hour, int minute, int second, int nano) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.nano = nano;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public int getNano() {
        return nano;
    }
}
