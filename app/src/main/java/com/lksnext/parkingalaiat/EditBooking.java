package com.lksnext.parkingalaiat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.lksnext.parkingalaiat.domain.CurrentParking;
import com.lksnext.parkingalaiat.domain.CurrentBooking;
import com.lksnext.parkingalaiat.domain.Booking;
import com.lksnext.parkingalaiat.interfaces.OnBookingUpdatedListener;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class EditBooking extends AppCompatActivity {

    public static final String EXTRA_START="com.lksnext.parkingalaiat.EXTRA_START";
    public static final String EXTRA_END="com.lksnext.parkingalaiat.EXTRA_END";

    private Boolean startSel=false;
    private Boolean endSel=false;

    private TextInputLayout startHour,endHour;
    private LinearProgressIndicator progress;
    private TextInputLayout date;
    private TextInputLayout spotDropDown;
    private AutoCompleteTextView dropdownField,spotList;
    private FirebaseManager fm;
    private TextView action;
    private CurrentBooking currentBooking;
    private CurrentParking currentParking;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_booking);
        initUi();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_booking, menu);

        setTitle("Edit booking");
        return true;
    }

    private void initUi() {
        initView();
       initListeners();
       initData();
    }

    private void initView() {
        action = findViewById(R.id.save_note);
        progress=findViewById(R.id.progress);
        startHour=findViewById(R.id.startHour);
        endHour=findViewById(R.id.endHour);
        date=findViewById(R.id.date);
        spotList=findViewById(R.id.spots);
        spotDropDown=findViewById(R.id.spotDropdwon);
        dropdownField=findViewById(R.id.dropdownField);

    }

    private void initData() {
        fm=FirebaseManager.getInstance();
        currentParking=CurrentParking.getInstance();
        currentBooking = CurrentBooking.getInstance();
        Booking r= currentBooking.getCurrent();
        startHour.getEditText().setText(r.getStartTime());
        endHour.getEditText().setText(r.getEndTime());
        date.getEditText().setText(r.getDate());
        dropdownField.setText(currentParking.getTypeById(r.getSpot()));
        spotList.setText(currentParking.getNumById(r.getSpot()));

    }

    private void initListeners() {
        startHour.setStartIconOnClickListener(view->{showStartTimePicker();});
        endHour.setStartIconOnClickListener(view->{showEndTimePicker();});
    }

    private void checkAllDataFill() {
        if(startSel && endSel){
            showProgressIndicator();
        }
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
            endHour.getEditText().setText(formattedTime);
            if(!compareHours()){

                endHour.setError(" ");
                startHour.setError(" ");
            }else{
                endHour.setError(null);
                startHour.setError(null);
                endSel=true;
                checkAllDataFill();
            }
            endSel=true;
            checkAllDataFill();
        });

    }

    private boolean compareHours() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String start=startHour.getEditText().getText().toString();
        String end=endHour.getEditText().getText().toString();
        if(start.isEmpty() | end.isEmpty()){
            return true;
        }else{
            LocalTime localTime1 = LocalTime.parse(startHour.getEditText().getText(), formatter);
            LocalTime localTime2 = LocalTime.parse(endHour.getEditText().getText(), formatter);
            if (localTime2.isAfter(localTime1)) {
                System.out.println("Time 2 is later than Time 1");
                return true;
            } else {
                return false;
            }
        }

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
                showErrorDialog();
                startHour.setError(" ");
                endHour.setError(" ");
            }
        }, 3000);
    }

    private void update(String bookingID) {
        String start=startHour.getEditText().getText().toString();
        String end=endHour.getEditText().getText().toString();
        fm.updateBooking(bookingID,start,end, new OnBookingUpdatedListener() {
            @Override
            public void OnBookingUpdatedListener(boolean success) {
                if (success) {
                    // Reservation updated successfully
                    setResult(RESULT_OK);
                    finish();
                } else {
                    // Error occurred while updating the reservation
                    // Handle the failure case
                }
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                // Perform the save action here
                update(currentBooking.getId());
                Intent reserva=new Intent();
                reserva.putExtra(EXTRA_START,startHour.getEditText().getText().toString());
                reserva.putExtra(EXTRA_END,endHour.getEditText().getText().toString());
                setResult(RESULT_OK, reserva);
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

            startHour.getEditText().setText(formattedTime);
            if(!compareHours()){

                endHour.setError(" ");
                startHour.setError(" ");
            }else{
                endHour.setError(null);
                startHour.setError(null);
                startSel=true;
                checkAllDataFill();
            }

        });
    }
    private void showErrorDialog() {
        new MaterialAlertDialogBuilder(this)
                .setMessage("Reserve could not be edited. It is not free for the selected hours.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Handle positive button click
                    dialog.dismiss();
                })
                .show();
    }




}
