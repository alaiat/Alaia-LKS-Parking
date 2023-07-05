package com.lksnext.parkingalaiat.domain;

import com.google.firebase.auth.FirebaseUser;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class UserContext {
    private static UserContext instance;
    private FirebaseUser currentUser;
    private List<DayHours> dateWithHours =new ArrayList<>();

    private UserContext() {
        // Private constructor to prevent direct instantiation
    }

    public static synchronized UserContext getInstance() {
        if (instance == null) {
            instance = new UserContext();
        }
        return instance;
    }
    public int getMinutesOfDay(String date){
        for(DayHours dh:this.dateWithHours){
            if(dh.getDate().equals(date)){
                return dh.getSpentMinutesOfDay();
            }
        }
        return 0;
    }
    public void addMinutesToDay(String date, String start, String end) {
        DayHours d=null;
        boolean found=false;
        for(DayHours dh:this.dateWithHours){
            if(dh.getDate().equals(date)){
                found=true;
                d=dh;
            }
        }
        if(!found){
            d= new DayHours(date);
            this.dateWithHours.add(d);

        }
        d.addAndCalculateNewTime(start,end);
    }


    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(FirebaseUser user) {
        currentUser = user;
    }

    public List<DayHours> getDateWithHours() {
        return dateWithHours;
    }







    private class DayHours {
        private String date;
        private int spentMinutesOfDay;

        public DayHours(String date) {
            this.date = date;
            this.spentMinutesOfDay =0;
        }
        public void addMinutesPerDay(int min) {
            int m= getSpentMinutesOfDay()+min;
            setSpentMinutesOfDay(m);
        }
        public void addAndCalculateNewTime(String time1,String time2){
            LocalTime start = LocalTime.parse(time1);
            LocalTime end = LocalTime.parse(time2);
            Duration duration = Duration.between(start, end);

            // Get the number of hours in the duration
            int hours = (int) duration.toMinutes();

            addMinutesPerDay(hours);


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


    }
}
