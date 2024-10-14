package com.blockafeller.util.gson;

public class LocalDateTimeGsonResource {
    private LocalDateGsonResource date;
    private LocalTimeGsonResource time;

    public LocalDateTimeGsonResource(int year, int month, int day, int hour, int minute, int second, int nano) {
        this.date = new LocalDateGsonResource(year, month, day);
        this.time = new LocalTimeGsonResource(hour, minute, second, nano);
    }

    public LocalDateGsonResource getDate() {
        return date;
    }

    public LocalTimeGsonResource getTime() {
        return time;
    }
}
