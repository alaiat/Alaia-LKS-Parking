package com.lksnext.parkingalaiat.domain;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DayHours {
    private String date;
    private int spentMinutesOfDay;

    private final List<String> startHours;
    private final List<String> endHours;

    public DayHours(String date) {
        this.date = date;
        this.spentMinutesOfDay = 0;
        startHours = new ArrayList<>();
        endHours = new ArrayList<>();
    }

    public void addMinutesPerDay(int min) {
        int m = getSpentMinutesOfDay() + min;
        setSpentMinutesOfDay(m);
    }

    public void addAndCalculateNewTime(String time1, String time2) {
        LocalTime start = LocalTime.parse(time1);
        LocalTime end = LocalTime.parse(time2);
        Duration duration = Duration.between(start, end);

        // Get the number of hours in the duration
        int hours = (int) duration.toMinutes();

        addMinutesPerDay(hours);


    }

    public void addEndHour(String end) {
        endHours.add(end);
    }

    public void addStartHour(String start) {
        startHours.add(start);
    }

    public List<String> getStartHours() {
        return startHours;
    }

    public List<String> getEndHours() {
        return endHours;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSpentMinutesOfDay() {
        return spentMinutesOfDay;
    }

    public void setSpentMinutesOfDay(int spentMinutesOfDay) {
        this.spentMinutesOfDay = spentMinutesOfDay;
    }

    @Override
    public String toString() {
        return "DayHours{" +
                "date='" + date + '\'' +
                ", startHours=" + startHours +
                ", endHours=" + endHours +
                '}';
    }
}
