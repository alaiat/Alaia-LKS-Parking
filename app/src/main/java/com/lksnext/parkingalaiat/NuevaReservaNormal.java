package com.lksnext.parkingalaiat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.lksnext.parkingalaiat.domain.Reserva;

import org.checkerframework.common.returnsreceiver.qual.This;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NuevaReservaNormal extends AppCompatActivity {

    public static final String EXTRA_DATE="com.lksnext.parkingalaiat.EXTRA_DATE";
    public static final String EXTRA_STATUS="com.lksnext.parkingalaiat.EXTRA_STATUS";
    public static final String EXTRA_START="com.lksnext.parkingalaiat.EXTRA_START";
    public static final String EXTRA_END="com.lksnext.parkingalaiat.EXTRA_END";

    String[] items={"Normal","Motor", "Electric"};
    String[] spots={"1","2","3","4"};
    AutoCompleteTextView dropdownField,spotList;
    ArrayAdapter<String> adapterItems;
    ArrayAdapter<String> adapterItemsSpot;
    TextInputLayout date;
    TextInputLayout startHour,endHour;
    LinearProgressIndicator progress;
    MaterialDatePicker<Long> datePicker;
    ImageButton close;
    Button search, mapSelect;

    TextView action;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nueva_reserva);
        initUi();
    }

    private void initUi() {
        initView();
        initListeners();
    }

    private void initView() {
        close = findViewById(R.id.fullscreen_dialog_close);
        action = findViewById(R.id.fullscreen_dialog_action);
        search=findViewById(R.id.searchButton);
        date=findViewById(R.id.date);
        mapSelect=findViewById(R.id.selectMap);
        progress=findViewById(R.id.progress);
        startHour=findViewById(R.id.startHour);
        endHour=findViewById(R.id.endHour);
        spotList=findViewById(R.id.spots);
        dropdownField=findViewById(R.id.dropdownField);

        adapterItems= new ArrayAdapter<String>(this,R.layout.dropdown_list_item,items);

        dropdownField.setAdapter(adapterItems);

        adapterItemsSpot=new ArrayAdapter<String>(this,R.layout.dropdown_list_item,spots);
        spotList.setAdapter(adapterItemsSpot);
    }

    private void initListeners() {
        close.setOnClickListener(view -> {close();});
        action.setOnClickListener(view -> {save();});
        dropdownField.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                String item=parent.getItemAtPosition(position).toString();
               // Toast.makeText(getContext(),"Item "+item,Toast.LENGTH_SHORT).show();
            }
        });
        spotList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                String item=parent.getItemAtPosition(position).toString();
               // Toast.makeText(this,"Item "+item,Toast.LENGTH_SHORT).show();
            }
        });
        date.setStartIconOnClickListener(view ->{showDatePicker();});
        startHour.setStartIconOnClickListener(view->{showStartTimePicker();});
        endHour.setStartIconOnClickListener(view->{showEndTimePicker();});
        search.setOnClickListener(view->{showProgressIndicator();});
    }

    private void showEndTimePicker() {
        MaterialTimePicker picker =
                new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(12)
                        .setMinute(10)
                        .setTitleText("Select Appointment time")
                        .build();
        picker.show(getSupportFragmentManager(),"tag");

        picker.addOnPositiveButtonClickListener(selection ->{
            int hour = picker.getHour();
            int minute = picker.getMinute();
            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            endHour.getEditText().setText(formattedTime);});

    }
    private void showProgressIndicator() {
        progress.setVisibility(View.VISIBLE);

        // Start the animation
        progress.setProgressCompat(0, true);

        // Delay the stop of the animation by 3 seconds (3000 milliseconds)
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Hide the progress indicator
                progress.setVisibility(View.INVISIBLE);
                mapSelect.setVisibility(View.VISIBLE);
                spotList.setVisibility(View.VISIBLE);
            }
        }, 3000);
    }

    private void showStartTimePicker() {
        MaterialTimePicker picker =
                new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(12)
                        .setMinute(10)
                        .setTitleText("Select Appointment time")
                        .build();
        picker.show(getSupportFragmentManager(),"tag");

        picker.addOnPositiveButtonClickListener(selection ->{
            int hour = picker.getHour();
            int minute = picker.getMinute();
            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            startHour.getEditText().setText(formattedTime);});
    }

    private void showDatePicker() {
        CalendarConstraints constraints =
                new CalendarConstraints.Builder()
                        .setStart( MaterialDatePicker.todayInUtcMilliseconds())
                        .setEnd( MaterialDatePicker.todayInUtcMilliseconds() + TimeUnit.DAYS.toMillis(14))
                        .build();

        datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraints)
                .build();

        datePicker.show(getSupportFragmentManager(),"tag");
        datePicker.addOnPositiveButtonClickListener(selection->{
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year);


            date.getEditText().setText(formattedDate);});
    }

    private void save() {
        String date=this.date.getEditText().getText().toString();
        String startHour=this.startHour.getEditText().getText().toString();
        String endHour=this.endHour.getEditText().getText().toString();
        Boolean status=true;



        Intent reserva=new Intent();
        reserva.putExtra(EXTRA_DATE,date);
        reserva.putExtra(EXTRA_START,startHour);
        reserva.putExtra(EXTRA_END,endHour);
        reserva.putExtra(EXTRA_STATUS,status);
        setResult(RESULT_OK, reserva);
        finish();


    }

    private void close(){
        Intent intent = new Intent(NuevaReservaNormal.this, VerReservasPruebaSQLDatabase.class);
        startActivity(intent);
    }



}
