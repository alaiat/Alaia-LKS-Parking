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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.lksnext.parkingalaiat.domain.CurrentParking;
import com.lksnext.parkingalaiat.domain.Reserva;
import com.lksnext.parkingalaiat.domain.UserContext;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NuevaReserva extends AppCompatActivity {

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
    String[] spotTypes ={"CAR","MOTORCYCLE", "ELECTRIC","HANDICAPPED"};
    String[]spots={"-1"};


    String typeToFound="";
    UserContext userContext;

    AutoCompleteTextView spotTypeOptionsText, availableSpotListText;

    ArrayAdapter<String> availableSpotAdapter;
    TextInputLayout date, availableSpotListDropdown;
    TextInputLayout startHour,endHour;
    LinearProgressIndicator progress;
    MaterialDatePicker<Long> datePicker;
    Button search, mapSelect;

    TextView action;

    List<String> notAvailable=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nueva_reserva);
        initUi();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_reserva, menu);

        setTitle("Nueva reserva");
        return true;
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
    private void initUi() {
        initView();
        initListeners();
        initData();
    }

    private void initData() {
        userContext=UserContext.getInstance();
    }

    private void initView() {
        action = findViewById(R.id.save_note);
        date=findViewById(R.id.date);
        progress=findViewById(R.id.progress);
        startHour=findViewById(R.id.startHour);
        endHour=findViewById(R.id.endHour);
        search=findViewById(R.id.searchButton);
        mapSelect=findViewById(R.id.selectMap);
        availableSpotListText =findViewById(R.id.spots);
        availableSpotListDropdown =findViewById(R.id.spotDropdwon);
        spotTypeOptionsText =findViewById(R.id.dropdownField);

        ArrayAdapter<String> typeAdapter= new ArrayAdapter<String>(this,R.layout.dropdown_list_item, spotTypes);
        spotTypeOptionsText.setAdapter(typeAdapter);

        availableSpotAdapter =new ArrayAdapter<String>(this,R.layout.dropdown_list_item,spots);
        availableSpotListText.setAdapter(availableSpotAdapter);

    }

    private void initListeners() {
        spotTypeOptionsText.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                String item=parent.getItemAtPosition(position).toString();
                typeSel=true;
                checkAllDataFill();
            }
        });
        availableSpotListText.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                String item=parent.getItemAtPosition(position).toString();
                typeToFound=item;
            }
        });
        date.setStartIconOnClickListener(view ->{ date.setError(null); showDatePicker();});
        startHour.setStartIconOnClickListener(view->{ startHour.setError(null); endHour.setError(null); showStartTimePicker();});
        endHour.setStartIconOnClickListener(view->{endHour.setError(null); startHour.setError(null); showEndTimePicker();});
        search.setOnClickListener(view->{showProgressIndicator();});
    }

    private void checkAllDataFill() {
        if(typeSel && dateSel && startSel && endSel){
            search.setEnabled(true);
        }
    }
    private void getSpotsForType(String type) {
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
        availableSpotAdapter =new ArrayAdapter<String>(this,R.layout.dropdown_list_item,drop);
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
        if(start.isEmpty() | end.isEmpty()){
            return true;
        }else{
            LocalTime localTime1 = LocalTime.parse(startHour.getEditText().getText(), formatter);
            LocalTime localTime2 = LocalTime.parse(endHour.getEditText().getText(), formatter);
            Duration duration = Duration.between(localTime1, localTime2);

            // Get the number of hours in the duration
            int min = (int) duration.toMinutes();
            System.out.println(min);
            if (localTime2.isAfter(localTime1) && min<=480) {
                System.out.println("Time 2 is later than Time 1");
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
        getSpotsForType(typeToFound);
        noAvailableSpots(date.getEditText().getText().toString(),startHour.getEditText().getText().toString(),endHour.getEditText().getText().toString());
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
                availableSpotListDropdown.setVisibility(View.VISIBLE);
            }
        }, 3000);
    }

    private void noAvailableSpots(String date, String start,String end) {
        getSpotsForType(this.spotTypeOptionsText.getEditableText().toString());
        notAvailable.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Construct the query
        CollectionReference reservationsRef = db.collection("reservations");
        Query query = reservationsRef
                .whereEqualTo("date", date);

        // Execute the query
        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Convert each document to a Reservation object
                        String endT= documentSnapshot.getString("endTime");
                        String startT= documentSnapshot.getString("startTime");

                        String spot= documentSnapshot.getString("spot");
                        System.out.println(date+" "+endT+" "+startT+ " "+spot+"\n");
                        Reserva reservation = new Reserva(spot,startT,endT,date,UserContext.getInstance().getCurrentUser().getUid());
                        checkOverlapp(reservation,start,end);
                    }


        });
    }

    private void checkOverlapp(Reserva reservation,String start,String end) {
        LocalTime s2 = LocalTime.parse(start);
        LocalTime e2 = LocalTime.parse(end);
        LocalTime s1 = LocalTime.parse(reservation.getStartTime());
        LocalTime e1 = LocalTime.parse(reservation.getEndTime());

        String tipoa= spotTypeOptionsText.getEditableText().toString();

        if(s2.isBefore(e1) && e2.isAfter(s1)){
            DocumentReference ref=FirebaseFirestore.getInstance().collection("spots").document(reservation.getSpot());
            ref.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                Object number = documentSnapshot.get("number");
                                String type=(String) documentSnapshot.get("type");
                                if(type.equals(tipoa)){
                                    notAvailable.add(number.toString());
                                    System.out.println(number);
                                }

                                // Use the retrieved values as needed
                            }
                        } else {
                            // Error occurred while fetching the user document
                            Exception exception = task.getException();
                            // Handle the exception
                        }
                    });
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
            if(!compareHours()){


            }else{
                endHour.setError(null);
                startHour.setError(null);
                startSel=true;
                checkAllDataFill();
            }

        });
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
            System.out.println(UserContext.getInstance().getMinutesOfDay(formattedDate));
            if(UserContext.getInstance().getMinutesOfDay(formattedDate)>=480){
                date.setError("You already have 8h reserved for this day.");
            }else{
                dateSel=true;
                checkAllDataFill();
            }

        });

    }

    private void save() {
        String date=this.date.getEditText().getText().toString();
        String startHour=this.startHour.getEditText().getText().toString();
        String endHour=this.endHour.getEditText().getText().toString();
        String type=this.spotTypeOptionsText.getEditableText().toString();
        String spot=this.availableSpotListText.getEditableText().toString();
        Boolean status=true;

        if(date.isEmpty() | startHour.isEmpty() | endHour.isEmpty()){
            availableSpotListDropdown.setError(" ");
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





}
