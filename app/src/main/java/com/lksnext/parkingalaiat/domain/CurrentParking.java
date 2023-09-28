package com.lksnext.parkingalaiat.domain;

import com.lksnext.parkingalaiat.FirebaseManager;

import java.util.ArrayList;
import java.util.List;

public class CurrentParking {
    private static CurrentParking instance;
    private List<String> spotListId;
    private List<Area> allVehicles = new ArrayList<>();
    private final List<Area> combustionVehicle = new ArrayList<>();
    private final List<Area> handicappedVehicle = new ArrayList<>();
    private final List<Area> electricVehicle = new ArrayList<>();
    private final List<Area> motorCycle = new ArrayList<>();
    FirebaseManager fm;

    private CurrentParking() {
        fm = FirebaseManager.getInstance();
        spotListId = new ArrayList<>();
        addSpotsById();
    }

    public void addSpotsById() {
        fm.getAllSpotsId(spotIds -> {
            if (spotIds != null) {
                spotListId.clear();
                combustionVehicle.clear();
                motorCycle.clear();
                handicappedVehicle.clear();
                electricVehicle.clear();
                spotListId = spotIds;
                addSpotToCorrespondingList();
            } else {
                // TODO: Handle the failure case
            }
        });
    }

    private void addSpotToCorrespondingList() {
        fm.sortList(spotListId, sortedList -> {
            allVehicles = sortedList;
            for (Area s : allVehicles) {
                switch (s.type) {
                    case "CAR":
                        combustionVehicle.add(s);
                        break;
                    case "HANDICAPPED":
                        handicappedVehicle.add(s);
                        break;
                    case "MOTORCYCLE":
                        motorCycle.add(s);
                        break;
                    case "ELECTRIC":
                        electricVehicle.add(s);
                        break;
                    default:
                        break;
                }
            }
        });

    }


    public String getIdByNum(String num) {

        for (Area area : allVehicles) {
            if (area.getNumber().equals(num)) {
                return area.getId();
            }
        }
        return null; // Return null if no matching num is found

    }

    public String getNumById(String id) {

        for (Area area : allVehicles) {
            if (area.getId().equals(id)) {
                return area.getNumber();
            }
        }
        return null; // Return null if no matching num is found

    }

    public String getTypeById(String id) {

        for (Area area : allVehicles) {
            if (area.getId().equals(id)) {
                return area.getType();
            }
        }
        return null; // Return null if no matching num is found

    }

    public static synchronized CurrentParking getInstance() {

        if (instance == null) {
            instance = new CurrentParking();
        }


        return instance;
    }

    public List<Area> getCombustionVehicle() {
        return combustionVehicle;
    }

    public List<Area> getHandicappedVehicle() {
        return handicappedVehicle;
    }

    public List<Area> getElectricVehicle() {
        return electricVehicle;
    }

    public List<Area> getMotorCycle() {
        return motorCycle;
    }

    public static class Area {
        private String number;
        private String id;
        private final String type;
        private final List<DayHours> listDayTime;
        FirebaseManager fm = FirebaseManager.getInstance();

        public Area(String number, String id, String type) {
            this.number = number;
            this.id = id;
            this.type = type;
            listDayTime = new ArrayList<>();
            findBookings(id);
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }


        private void findBookings(String id) {
            fm.getBookingsForSpot(id, (exception, bookings) -> {
                if (exception != null) {
                    System.out.println(exception);
                    // TODO: show error to the user
                    return;
                }

                // Handle the reservations list
                addListToList(bookings);
                System.out.println(listDayTime);
            });

        }

        public void addListToList(List<Booking> bookings) {

            for (Booking b : bookings) {
                boolean found = false;
                DayHours dayHours = null;
                for (DayHours localDayHours : listDayTime) {
                    if (localDayHours.getDate().equals(b.getDate())) {
                        found = true;
                        dayHours = localDayHours;
                    }
                }
                if (!found)
                    dayHours = new DayHours(b.getDate());

                dayHours.addStartHour(b.getStartTime());
                dayHours.addEndHour(b.getEndTime());
                dayHours.addAndCalculateNewTime(b.getStartTime(), b.getEndTime());
                System.out.println(dayHours.getStartHours());
                listDayTime.add(dayHours);
            }
        }

        public DayHours getDayHoursByDate(String date) {
            for (DayHours dh : listDayTime) {
                if (dh.getDate().equals(date)) {
                    return dh;
                }
            }
            return null;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return "Area{" +
                    "num='" + number + '\'' +
                    ", id='" + id + '\'' +
                    ", type='" + type + '\'' +
                    ", listDayTime=" + listDayTime +
                    '}';
        }
    }


}

