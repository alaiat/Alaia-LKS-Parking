package com.lksnext.parkingalaiat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;


public class NuevaReserva extends AppCompatActivity {

    private String selectedDate;

    private String selectedStart;
    private String selectedEnd;

    private TextView datePicker;
    private Calendar calendar;

    private Button botonReserva;
    private Button horaInicio;
    private Button horaFinal;
    private TextView textViewStartTime;
    private TextView textViewEndTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nueva_reserva);
        initUi();
    }

    private void initUi() {
        initHelpers();
        initView();
        initListeners();
    }
    private void initHelpers(){
        calendar=Calendar.getInstance();
    }

    private void initView() {
        datePicker=findViewById(R.id.textViewDate);
        botonReserva=findViewById(R.id.buttonReserve);
        horaInicio=findViewById(R.id.buttonStartTime);
        horaFinal=findViewById(R.id.buttonEndTime);
        textViewStartTime = findViewById(R.id.textViewStartTime);
        textViewEndTime = findViewById(R.id.textViewEndTime);
    }

    private void initListeners(){
        datePicker.setOnClickListener(view -> {showDatePickerDialog();});
        horaInicio.setOnClickListener(view -> {showTimePickerDialog(true);});
        horaFinal.setOnClickListener(view -> {showTimePickerDialog(false);});
    }

    private void showTimePickerDialog( boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);


        TimePickerDialog timePickerDialog = new TimePickerDialog(
                NuevaReserva.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (isStartTime) {
                            int selectedStartHour = hourOfDay;
                            int selectedStartMinute = minute;
                            selectedStart= String.format(Locale.getDefault(), "%02d:%02d", selectedStartHour, selectedStartMinute);
                            textViewStartTime.setText(selectedStart);
                        } else {
                            int selectedEndHour = hourOfDay;
                            int selectedEndMinute = minute;
                            selectedEnd=String.format(Locale.getDefault(), "%02d:%02d", selectedEndHour, selectedEndMinute);
                            textViewEndTime.setText(selectedEnd);
                        }
                    }
                },
                hour,
                minute,
                DateFormat.is24HourFormat(NuevaReserva.this)
        );

        timePickerDialog.show();
    }

    public void showDatePickerDialog() {
        int year = calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                datePicker.setText(selectedDate);
            }
        }, year, month, day);

        datePickerDialog.show();
    }
}
