package com.lksnext.parkingalaiat.domain;

public class ReservaManager {
    private String startTime;
    private String endTime;
    private String date;

    public ReservaManager(String startTime, String endTime, String date) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
