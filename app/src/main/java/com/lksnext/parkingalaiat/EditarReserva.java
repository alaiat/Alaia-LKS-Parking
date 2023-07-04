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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.lksnext.parkingalaiat.domain.CurrentParking;
import com.lksnext.parkingalaiat.domain.CurrentReserva;
import com.lksnext.parkingalaiat.domain.Reserva;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class EditarReserva extends AppCompatActivity {

    public static final String EXTRA_START="com.lksnext.parkingalaiat.EXTRA_START";
    public static final String EXTRA_END="com.lksnext.parkingalaiat.EXTRA_END";

    Boolean startSel=false;
    Boolean endSel=false;

    TextInputLayout startHour,endHour;
    LinearProgressIndicator progress;
    TextInputLayout date,spotDropDown;
    AutoCompleteTextView dropdownField,spotList;


    TextView action;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_reserva);
        initUi();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_reserva, menu);

        setTitle("Editar reserva");
        return true;
    }

    private void initUi() {
        initView();
       initListeners();
    }

    private void initView() {
        //close = findViewById(R.id.fullscreen_dialog_close);
        action = findViewById(R.id.save_note);
        progress=findViewById(R.id.progress);
        startHour=findViewById(R.id.startHour);
        endHour=findViewById(R.id.endHour);
        date=findViewById(R.id.date);
        spotList=findViewById(R.id.spots);
        spotDropDown=findViewById(R.id.spotDropdwon);
        dropdownField=findViewById(R.id.dropdownField);

        showData();
    }

    private void showData() {
        Reserva r= CurrentReserva.getInstance().getCurrentReserva();
        startHour.getEditText().setText(r.getStartTime());
        endHour.getEditText().setText(r.getEndTime());
        date.getEditText().setText(r.getDate());
        dropdownField.setText(CurrentParking.getInstance().getTypeById(r.getSpot()));
        spotList.setText(CurrentParking.getInstance().getNumById(r.getSpot()));

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

    private void update(String reservaId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reservaRef = db.collection("reservas").document(reservaId);

        reservaRef.update("startTime", startHour.getEditText().getText().toString())
                .addOnSuccessListener(aVoid -> {
                    setResult(RESULT_OK);
                    finish();

                })
                .addOnFailureListener(e -> {
                    // Failed to update start time
                    // Handle the exception
                });
        reservaRef.update("endTime", endHour.getEditText().getText().toString())
                .addOnSuccessListener(aVoid -> {
                    // Start time updated successfully
                    // Proceed with next steps or show a success message
                })
                .addOnFailureListener(e -> {
                    // Failed to update start time
                    // Handle the exception
                });
    }
    private void findReserva() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reservasRef = db.collection("reservation");
        Reserva r=CurrentReserva.getInstance().getCurrentReserva();

        Query query = reservasRef.whereEqualTo("date", r.getDate()).whereEqualTo("startTime",r.getStartTime()).whereEqualTo("endTime",r.getEndTime())
                .whereEqualTo("spot",r.getSpot()).whereEqualTo("user",r.getUser());

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

                    // Iterate through the documents to retrieve the reservas
                    for (DocumentSnapshot document : documents) {
                        // Retrieve the reserva data
                        String reservaId = document.getId();
                        System.out.println(reservaId);
                        // ... Retrieve other reserva data as needed

                        // Perform operations with the retrieved reserva data
                        // For example, update the start time
                        //update(reservaId);
                    }
                })
                .addOnFailureListener(e -> {
                    // Failed to query reservas
                    // Handle the exception
                });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                // Perform the save action here
                findReserva();
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
