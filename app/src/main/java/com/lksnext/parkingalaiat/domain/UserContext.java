package com.lksnext.parkingalaiat.domain;

import com.google.firebase.auth.FirebaseUser;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class UserContext {
    private static UserContext instance;
    private FirebaseUser currentUser;
    private List<DayHours> hours=new ArrayList<>();

    private UserContext() {
        // Private constructor to prevent direct instantiation
    }

    public static synchronized UserContext getInstance() {
        if (instance == null) {
            instance = new UserContext();
        }
        return instance;
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(FirebaseUser user) {
        currentUser = user;
    }

    public List<DayHours> getHours() {
        return hours;
    }
    public int getMinutesOfDay(String date){
        boolean found=false;
        for(DayHours dh:this.hours){
            if(dh.getDate().equals(date)){
                return dh.getMinutesPerDay();
            }
        }

            return 0;

    }

    public void addDayAndOrHours(String date, String start,String end) {
        DayHours d=null;
        boolean found=false;
        for(DayHours dh:this.hours){
            if(dh.getDate().equals(date)){
                found=true;
               d=dh;
            }
        }
        if(!found){
                d= new DayHours(date);
                this.hours.add(d);

        }
        d.addAndCalculateNewTime(start,end);

    }

    private class DayHours {
        private String date;
        private int minutesPerDay;

        public DayHours(String date) {
            this.date = date;
            this.minutesPerDay =0;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getMinutesPerDay() {
            return minutesPerDay;
        }

        public void setMinutesPerDay(int minutesPerDay) {
            this.minutesPerDay = minutesPerDay;
        }

        public void addMinutesPerDay(int min) {
            int m=getMinutesPerDay()+min;
            setMinutesPerDay(m);
            System.out.println(this.minutesPerDay);
            System.out.println(min);
        }
        public void addAndCalculateNewTime(String time1,String time2){
            LocalTime start = LocalTime.parse(time1);
            LocalTime end = LocalTime.parse(time2);
            Duration duration = Duration.between(start, end);

            // Get the number of hours in the duration
            int hours = (int) duration.toMinutes();

            addMinutesPerDay(hours);


        }
    }
}
