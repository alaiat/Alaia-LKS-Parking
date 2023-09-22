package com.lksnext.parkingalaiat;

import com.lksnext.parkingalaiat.domain.Booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class BookingSorter {
    public BookingSorter(){
        ///
    }

    public void sortElementsByProximity(List<Booking> elements) {
        // Get today's date
        LocalDate today = LocalDate.now();

        // Sort elements by proximity to today's date and final hour
        elements.sort(new ElementComparator(today));
    }



    private static class ElementComparator implements Comparator<Booking> {
        private final LocalDate today;

        public ElementComparator(LocalDate today) {
            this.today = today;
        }

        @Override
        public int compare(Booking element1, Booking element2) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate date1 = LocalDate.parse(element1.getDate(),formatter);
            LocalDate date2 = LocalDate.parse(element2.getDate(),formatter);

            int dateComparison = date1.compareTo(date2);
            if (dateComparison != 0) {
                // Dates are different, return the result of the date comparison
                return dateComparison;
            } else {
                // Dates are the same, compare the end hours
                LocalTime time1 = LocalTime.parse(element1.getEndTime());
                LocalTime time2 = LocalTime.parse(element2.getEndTime());

                return time1.compareTo(time2);
            }
        }

    }

}
