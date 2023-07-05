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








}
