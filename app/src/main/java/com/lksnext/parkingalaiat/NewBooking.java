package com.lksnext.parkingalaiat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.lksnext.parkingalaiat.domain.CurrentParking;
import com.lksnext.parkingalaiat.domain.DayHours;
import com.lksnext.parkingalaiat.domain.UserContext;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NewBooking extends AppCompatActivity {

    public static final String EXTRA_DATE="com.lksnext.parkingalaiat.EXTRA_DATE";
    public static final String EXTRA_STATUS="com.lksnext.parkingalaiat.EXTRA_STATUS";
    public static final String EXTRA_START="com.lksnext.parkingalaiat.EXTRA_START";
    public static final String EXTRA_END="com.lksnext.parkingalaiat.EXTRA_END";
    public static final String EXTRA_TYPE="com.lksnext.parkingalaiat.EXTRA_TYPE";
    public static final String EXTRA_SPOT = "com.lksnext.parkingalaiat.EXTRA_SPOT";
    private CurrentParking current=CurrentParking.getInstance();

    private Boolean typeSel=false;
    private Boolean dateSel=false;
    private Boolean startSel=false;
    private Boolean endSel=false;
    private String[] spotTypes ={"CAR","MOTORCYCLE", "ELECTRIC","HANDICAPPED"};
    private String[]spots={"-1"};


    private AutoCompleteTextView spotTypeOptionsText;
    private AutoCompleteTextView availableSpotListText;

    private ArrayAdapter<String> availableSpotAdapter;
    private TextInputLayout date;
    private TextInputLayout availableSpotListDropdown;
    private TextInputLayout startHour;
    private TextInputLayout endHour;
    private LinearProgressIndicator progress;
    private MaterialDatePicker<Long> datePicker;
    private Button search;
    private Button mapSelect;
    private Button change;


    private List<CurrentParking.Area> selectedTypeSpots=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_booking);
        initUi();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_booking, menu);

        setTitle("New booking");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_note) {
            // Perform the save action here
            save();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }
    private void initUi() {
        initView();
        initListeners();
    }


    private void initView() {
        date=findViewById(R.id.date);
        progress=findViewById(R.id.progress);
        startHour=findViewById(R.id.startHour);
        endHour=findViewById(R.id.endHour);
        search=findViewById(R.id.searchButton);
        mapSelect=findViewById(R.id.selectMap);
        change=findViewById(R.id.changeData);
        availableSpotListText =findViewById(R.id.spots);
        availableSpotListDropdown =findViewById(R.id.spotDropdwon);
        spotTypeOptionsText =findViewById(R.id.dropdownField);

        ArrayAdapter<String> typeAdapter= new ArrayAdapter<>(this,R.layout.dropdown_list_item, spotTypes);
        spotTypeOptionsText.setAdapter(typeAdapter);

        availableSpotAdapter =new ArrayAdapter<>(this,R.layout.dropdown_list_item,spots);
        availableSpotListText.setAdapter(availableSpotAdapter);

    }

    private void initListeners() {
        spotTypeOptionsText.setOnItemClickListener((parent, view, position, id) -> {
            parent.getItemAtPosition(position).toString();
            typeSel = true;
            checkAllDataFill();
        });

        availableSpotListText.setOnItemClickListener((parent,view,position,id)->{
                parent.getItemAtPosition(position).toString();
        });
        date.setStartIconOnClickListener(view ->{ date.setError(null); showDatePicker();});
        startHour.setStartIconOnClickListener(view->{ startHour.setError(null); endHour.setError(null); showStartTimePicker();});
        endHour.setStartIconOnClickListener(view->{endHour.setError(null); startHour.setError(null); showEndTimePicker();});
        search.setOnClickListener(view->{showProgressIndicator();});
        change.setOnClickListener(view -> {enable();});
    }

    private void enable() {
        spotTypeOptionsText.clearListSelection();
        date.getEditText().setText("");
        startHour.getEditText().setText("");
        endHour.getEditText().setText("");
        spotTypeOptionsText.setEnabled(true);
        date.setEnabled(true);
        startHour.setEnabled(true);
        endHour.setEnabled(true);

    }

    private void checkAllDataFill() {
        if(typeSel && dateSel && startSel && endSel){
            search.setEnabled(true);
        }
    }
    private void getSpotsForType(String type) {
        selectedTypeSpots.clear();
        List<String> drop=new ArrayList<>();

        switch (type) {
            case "CAR":
                // Code for handling the "CAR" option
                selectedTypeSpots=current.getCar();
                break;
            case "MOTORCYCLE":
                // Code for handling the "MOTORCYCLE" option
                selectedTypeSpots=current.getMotor();
                break;
            case "ELECTRIC":
                // Code for handling the "ELECTRIC" option
                selectedTypeSpots=current.getElec();
                break;
            case "HANDICAPPED":
                // Code for handling the "HANDICAPPED" option
                selectedTypeSpots=current.getHand();
                break;
            default:
                // Code for handling unrecognized options
                break;
        }

        for(CurrentParking.Area s:selectedTypeSpots){
            drop.add(s.getNumber());
        }
        availableSpotAdapter =new ArrayAdapter<>(this,R.layout.dropdown_list_item,drop);
        availableSpotListText.setAdapter(availableSpotAdapter);


    }




    private void showEndTimePicker() {
        endHour.setError(null);
        MaterialTimePicker picker =
                new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(12)
                        .setMinute(10)
                        .setTitleText("Select end time")
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
        if(start.isEmpty() || end.isEmpty()){
            return true;
        }else{
            LocalTime localTime1 = LocalTime.parse(startHour.getEditText().getText(), formatter);
            LocalTime localTime2 = LocalTime.parse(endHour.getEditText().getText(), formatter);
            Duration duration = Duration.between(localTime1, localTime2);

            // Get the number of hours in the duration
            int min = (int) duration.toMinutes();
            if (localTime2.isAfter(localTime1) && min<=480) {
                return true;
            }else if(min>480){
                endHour.setError(" ");
                startHour.setError("You can't book more than 8h");
                return false;

            }else{
                endHour.setError(" ");
                startHour.setError("End must be after start");
                return false;
            }
        }

    }

    private void showProgressIndicator() {
        availableSpotListText.setText("");
        getSpotsForType(spotTypeOptionsText.getEditableText().toString());
        String newS=startHour.getEditText().getText().toString();
        String newE=endHour.getEditText().getText().toString();
        String dat=date.getEditText().getText().toString();

        if(deleteNotAvailableSpots(dat,newS,newE)){
            progress.setVisibility(View.VISIBLE);

            // Start the animation
            progress.setProgressCompat(0, true);

           new Handler(Looper.getMainLooper()).postDelayed(()-> {

                    // Hide the progress indicator
                    progress.setVisibility(View.INVISIBLE);
                    mapSelect.setVisibility(View.VISIBLE);
                    availableSpotListDropdown.setVisibility(View.VISIBLE);

            }, 3000);
            search.setEnabled(false);
            spotTypeOptionsText.setEnabled(false);
            date.setEnabled(false);
            startHour.setEnabled(false);
            endHour.setEnabled(false);
        }


    }




    private void showStartTimePicker() {
        startHour.setError(null);
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
            if(compareHours()){
                endHour.setError(null);
                startHour.setError(null);
                startSel=true;
                checkAllDataFill();
            }


        });

    }
    public boolean checkOverlap(String oldEnd,String oldStart,String newEnd,String newStart){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime end1 = LocalTime.parse(oldEnd, formatter);
        LocalTime start1 = LocalTime.parse(oldStart, formatter);
        LocalTime end2 = LocalTime.parse(newEnd, formatter);
        LocalTime start2 = LocalTime.parse(newStart, formatter);
        return !(end1.isBefore(start2) || start1.isAfter(end2));


    }


    private boolean deleteNotAvailableSpots(String dat,String startT,String endT) {
        List<CurrentParking.Area> toRemove=new ArrayList<>();
        List<String> libreak=new ArrayList<>();
        for(CurrentParking.Area s:selectedTypeSpots){
            DayHours dh=s.getDayHoursByDate(dat);
            if(dh!=null){
            for(int i=0; i<dh.getStartHours().size();i++){
                if(!checkOverlap(dh.getStartHours().get(i),dh.getEndHours().get(i),startT,endT)){
                    toRemove.add(s);
                    break;
                }
            }
            }

        }
        for(CurrentParking.Area s: selectedTypeSpots){
            if(!toRemove.contains(s)){
                libreak.add(s.getNumber());
            }
        }
        if(libreak.isEmpty()){
            showDialog();
            return false;
        }
        availableSpotAdapter =new ArrayAdapter<>(this,R.layout.dropdown_list_item,libreak);
        availableSpotListText.setAdapter(availableSpotAdapter);
        return true;
    }
    private void showDialog() {
        new MaterialAlertDialogBuilder(this)
                .setMessage("There are not free spots for the introduced data.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Handle positive button click
                    dialog.dismiss();
                    setResult(-7);
                    finish();
                })
                .show();
    }

    private void showDatePicker() {
        date.setError(null);
        Calendar calendarStart = Calendar.getInstance();

        long today = MaterialDatePicker.todayInUtcMilliseconds();
        calendarStart.setTimeInMillis(today);

        calendarStart.add(Calendar.DAY_OF_MONTH, 7);
        long endDate = calendarStart.getTimeInMillis();
        CalendarConstraints.Builder constraints = new CalendarConstraints.Builder();
        constraints.setStart(today);
        constraints.setEnd(endDate);
        CalendarConstraints.DateValidator dateValidator = new CalendarConstraints.DateValidator() {
            @Override
            public boolean isValid(long date) {
                return date >= today && date <= endDate;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int flags) {
                // No-op
            }
        };

// Set the custom date validator
        constraints.setValidator(dateValidator);


        datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraints.build())
                .build();


        datePicker.show(getSupportFragmentManager(),"tag");
        datePicker.addOnPositiveButtonClickListener(selection->{
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year);


            date.getEditText().setText(formattedDate);
            if(UserContext.getInstance().getMinutesOfDay(formattedDate)>=480){
                date.setError("You already have 8h reserved for this day.");
            }else{
                dateSel=true;
                checkAllDataFill();
            }

        });

    }

    private void save() {
        current.addSpotsById();
        String date2=this.date.getEditText().getText().toString();
        String startH=this.startHour.getEditText().getText().toString();
        String endH=this.endHour.getEditText().getText().toString();
        String type=this.spotTypeOptionsText.getEditableText().toString();
        String spot=this.availableSpotListText.getEditableText().toString();
        Boolean status=true;

        if(date2.isEmpty() || startH.isEmpty() || endH.isEmpty()){
            availableSpotListDropdown.setError(" ");
            this.date.setError(" ");
            this.startHour.setError(" ");
            this.endHour.setError(" ");
        }else{
            Intent booking=getIntent();
            booking.putExtra(EXTRA_DATE,date2);
            booking.putExtra(EXTRA_START,startH);
            booking.putExtra(EXTRA_END,endH);
            booking.putExtra(EXTRA_STATUS,status);
            booking.putExtra(EXTRA_TYPE,type);
            booking.putExtra(EXTRA_SPOT,current.getIdByNum(spot));
            setResult(RESULT_OK, booking);
            finish();
        }





    }





}
