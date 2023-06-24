package com.lksnext.parkingalaiat;

import com.lksnext.parkingalaiat.domain.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReservaSorter {
    public ReservaSorter(){

    }

    public void sortElementsByProximity(List<Reserva> elements) {
        // Get today's date
        LocalDate today = LocalDate.now();

        // Sort elements by proximity to today's date and final hour
        elements.sort(new ElementComparator(today));
    }



    private static class ElementComparator implements Comparator<Reserva> {
        private final LocalDate today;

        public ElementComparator(LocalDate today) {
            this.today = today;
        }

        @Override
        public int compare(Reserva element1, Reserva element2) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate date1 = LocalDate.parse(element1.getDate(),formatter);
            LocalDate date2 = LocalDate.parse(element2.getDate(),formatter);

            LocalTime time=LocalTime.parse(element1.getEndHour());
            LocalTime time2=LocalTime.parse(element2.getEndHour());

            // Calculate the difference between the dates and final hours
            int dateDifference1 = Math.abs(today.compareTo(date1));
            int dateDifference2 = Math.abs(today.compareTo(date2));


            // Compare the differences, giving priority to proximity to today's date
            if (dateDifference1 != dateDifference2) {
                return Integer.compare(dateDifference1, dateDifference2);
            } else {
                return time.compareTo(time2);
            }
        }

    }

}
