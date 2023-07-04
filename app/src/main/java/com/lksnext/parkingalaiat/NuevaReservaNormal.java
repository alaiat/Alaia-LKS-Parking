package com.lksnext.parkingalaiat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lksnext.parkingalaiat.domain.CurrentParking;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NuevaReservaNormal extends AppCompatActivity {

    public static final String EXTRA_DATE="com.lksnext.parkingalaiat.EXTRA_DATE";
    public static final String EXTRA_STATUS="com.lksnext.parkingalaiat.EXTRA_STATUS";
    public static final String EXTRA_START="com.lksnext.parkingalaiat.EXTRA_START";
    public static final String EXTRA_END="com.lksnext.parkingalaiat.EXTRA_END";
    public static final String EXTRA_TYPE="com.lksnext.parkingalaiat.EXTRA_TYPE";
    public static final String EXTRA_SPOT = "com.lksnext.parkingalaiat.EXTRA_SPOT";
    CurrentParking current=CurrentParking.getInstance();

    Boolean typeSel=false;
    Boolean dateSel=false;
    Boolean startSel=false;
    Boolean endSel=false;
    String[] items={"CAR","MOTORCYCLE", "ELECTRIC","HANDICAPPED"};
    String[]spots={"-1"};




    AutoCompleteTextView dropdownField,spotList;
    ArrayAdapter<String> adapterItems;
    ArrayAdapter<String> adapterItemsSpot;
    TextInputLayout date,spotDropDown;
    TextInputLayout startHour,endHour;
    LinearProgressIndicator progress;
    MaterialDatePicker<Long> datePicker;
    Button search, mapSelect;

    TextView action;

    private void getSpots(String type) {
        List<CurrentParking.Spota> a=new ArrayList<>();
        List<String> drop=new ArrayList<>();

        switch (type) {
            case "CAR":
                // Code for handling the "CAR" option
                a=current.getCar();
                break;
            case "MOTORCYCLE":
                // Code for handling the "MOTORCYCLE" option
                a=current.getMotor();
                break;
            case "ELECTRIC":
                // Code for handling the "ELECTRIC" option
                a=current.getElec();
                break;
            case "HANDICAPPED":
                // Code for handling the "HANDICAPPED" option
                a=current.getHand();
                break;
            default:
                // Code for handling unrecognized options
                break;
        }

        for(CurrentParking.Spota s:a){
            drop.add(s.getNum());
        }
        adapterItemsSpot=new ArrayAdapter<String>(this,R.layout.dropdown_list_item,drop);
        spotList.setAdapter(adapterItemsSpot);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nueva_reserva_sin_top_bar);
        initUi();
        getSpots("CAR");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_reserva, menu);

        setTitle("Nueva reserva");
        return true;
    }

    private void initUi() {
        initView();
        initListeners();
    }

    private void initView() {
        //close = findViewById(R.id.fullscreen_dialog_close);
        action = findViewById(R.id.save_note);
        search=findViewById(R.id.searchButton);
        date=findViewById(R.id.date);
        mapSelect=findViewById(R.id.selectMap);
        progress=findViewById(R.id.progress);
        startHour=findViewById(R.id.startHour);
        endHour=findViewById(R.id.endHour);
        spotList=findViewById(R.id.spots);
        spotDropDown=findViewById(R.id.spotDropdwon);
        dropdownField=findViewById(R.id.dropdownField);

        adapterItems= new ArrayAdapter<String>(this,R.layout.dropdown_list_item,items);

        dropdownField.setAdapter(adapterItems);

        adapterItemsSpot=new ArrayAdapter<String>(this,R.layout.dropdown_list_item,spots);
        spotList.setAdapter(adapterItemsSpot);

        dropdownField.setBackgroundColor(date.getBoxBackgroundColor());
    }

    private void initListeners() {
       // close.setOnClickListener(view -> {close();});

       // action.setOnClickListener(view -> {/*save();*/});
        dropdownField.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                String item=parent.getItemAtPosition(position).toString();
                getSpots(item);
               // Toast.makeText(getContext(),"Item "+item,Toast.LENGTH_SHORT).show();
                typeSel=true;
                checkAllDataFill();
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

    private void checkAllDataFill() {
        if(typeSel && dateSel && startSel && endSel){
            search.setEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                // Perform the save action here
                save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                mapSelect.setVisibility(View.VISIBLE);
                spotDropDown.setVisibility(View.VISIBLE);
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

    private void showDatePicker() {
        Calendar calendarStart = Calendar.getInstance();

        long today = MaterialDatePicker.todayInUtcMilliseconds();
        calendarStart.setTimeInMillis(today);

        calendarStart.add(Calendar.DAY_OF_MONTH, 14);
        long endDate = calendarStart.getTimeInMillis();
        CalendarConstraints.Builder constraints = new CalendarConstraints.Builder();
                       // .setStart( MaterialDatePicker.todayInUtcMilliseconds())
                        //.setEnd( MaterialDatePicker.todayInUtcMilliseconds() + TimeUnit.DAYS.toMillis(14))
                        //.build();
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
            dateSel=true;
            checkAllDataFill();
        });

    }

    private void save() {
        String date=this.date.getEditText().getText().toString();
        String startHour=this.startHour.getEditText().getText().toString();
        String endHour=this.endHour.getEditText().getText().toString();
        String type=this.dropdownField.getEditableText().toString();
        String spot=this.spotList.getEditableText().toString();
        Boolean status=true;

        if(date.isEmpty() | startHour.isEmpty() | endHour.isEmpty()){
            spotDropDown.setError(" ");
            this.date.setError(" ");
            this.startHour.setError(" ");
            this.endHour.setError(" ");
        }else{
            Intent reserva=new Intent();
            reserva.putExtra(EXTRA_DATE,date);
            reserva.putExtra(EXTRA_START,startHour);
            reserva.putExtra(EXTRA_END,endHour);
            reserva.putExtra(EXTRA_STATUS,status);
            reserva.putExtra(EXTRA_TYPE,type);
            reserva.putExtra(EXTRA_SPOT,current.getIdByNum(spot));
            setResult(RESULT_OK, reserva);
            finish();
        }





    }
    public void saveInFirebase(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String num=this.spotList.getEditableText().toString();
        current.getIdByNum("2");
        //ReservaOld r=new ReservaOld()
    }/*
    private void createReservation(String userId, String spotId, String date, String startTime, String endTime) {
        // Get an instance of the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new reservation object
        Reservation reservation = new Reservation(userId, spotId, date, startTime, endTime);

        // Get a reference to the "reservations" collection
        CollectionReference reservationsRef = db.collection("reservations");

        // Add the reservation to the "reservations" collection
        reservationsRef.add(reservation)
                .addOnSuccessListener(documentReference -> {
                    // Reservation added successfully
                    String reservationId = documentReference.getId();
                    System.out.println("Reservation added with ID: " + reservationId);

                    // Perform additional tasks or show success message
                })
                .addOnFailureListener(e -> {
                    // Error occurred while adding the reservation
                    System.out.println("Failed to add reservation");
                    System.out.println(e.toString());

                    // Handle the exception
                });
    }*/




}
