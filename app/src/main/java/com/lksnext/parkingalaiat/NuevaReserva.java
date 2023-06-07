package com.lksnext.parkingalaiat;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;


public class NuevaReserva extends AppCompatActivity {

    private TextView datePicker;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nueva_reserva);
        initHelpers();
        initUi();
    }

    private void initUi() {
        initView();
        initListeners();
    }
    private void initHelpers(){
        calendar=Calendar.getInstance();
    }

    private void initView() {
        datePicker=findViewById(R.id.textViewDate);
    }

    private void initListeners(){
        datePicker.setOnClickListener(view -> {showDatePickerDialog(view);});
    }

    public void showDatePickerDialog(View view) {
        int year = calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                datePicker.setText(selectedDate);
            }
        }, year, month, day);

        datePickerDialog.show();
    }
}
