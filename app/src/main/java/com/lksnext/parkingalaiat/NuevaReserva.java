package com.lksnext.parkingalaiat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lksnext.parkingalaiat.domain.CurrentParking;
import com.lksnext.parkingalaiat.domain.ReservaOld;
import com.lksnext.parkingalaiat.domain.Spot;
import com.lksnext.parkingalaiat.domain.UserContext;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NuevaReserva extends DialogFragment implements View.OnClickListener {
    public static final String EXTRA_DATE="com.lksnext.parkingalaiat.EXTRA_DATE";
    public static final String EXTRA_START="com.lksnext.parkingalaiat.EXTRA_START";
    public static final String EXTRA_END="com.lksnext.parkingalaiat.EXTRA_END";


    private Callback callback;
    String[] items={"Normal","Motor", "Electric"};
    String[] spots={"1","2","3","4"};
    List<String> spotLista=CurrentParking.getInstance().getSpotListaId();
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


    static NuevaReserva newInstance() {
        return new NuevaReserva();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);

    }
    public void initListeners(){
        close.setOnClickListener(this);
        action.setOnClickListener(view -> {save();});
        dropdownField.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                String item=parent.getItemAtPosition(position).toString();
                Toast.makeText(getContext(),"Item "+item,Toast.LENGTH_SHORT).show();
            }
        });
        spotList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                String item=parent.getItemAtPosition(position).toString();
                Toast.makeText(getContext(),"Item "+item,Toast.LENGTH_SHORT).show();
            }
        });
        date.setStartIconOnClickListener(view ->{showDatePicker();});
        startHour.setStartIconOnClickListener(view->{showStartTimePicker();});
        endHour.setStartIconOnClickListener(view->{showEndTimePicker();});
        search.setOnClickListener(view->{showProgressIndicator();});

        System.out.println(spotLista+"\n");


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

        datePicker.show(getParentFragmentManager(),"tag");
        datePicker.addOnPositiveButtonClickListener(selection->{
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year);


            date.getEditText().setText(formattedDate);});
    }
    private void showStartTimePicker(){
        MaterialTimePicker picker =
                new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(12)
                        .setMinute(10)
                        .setTitleText("Select Appointment time")
                        .build();
        picker.show(getParentFragmentManager(),"tag");

        picker.addOnPositiveButtonClickListener(selection ->{
            int hour = picker.getHour();
            int minute = picker.getMinute();
            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            startHour.getEditText().setText(formattedTime);});

    }
    private void showEndTimePicker(){
        MaterialTimePicker picker =
                new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(12)
                        .setMinute(10)
                        .setTitleText("Select Appointment time")
                        .build();
        picker.show(getParentFragmentManager(),"tag");

        picker.addOnPositiveButtonClickListener(selection ->{
            int hour = picker.getHour();
            int minute = picker.getMinute();
            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            endHour.getEditText().setText(formattedTime);});

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nueva_reserva, container, false);

        close = view.findViewById(R.id.fullscreen_dialog_close);
        action = view.findViewById(R.id.fullscreen_dialog_action);
        search=view.findViewById(R.id.searchButton);
        date=view.findViewById(R.id.date);
        mapSelect=view.findViewById(R.id.selectMap);
        progress=view.findViewById(R.id.progress);
        startHour=view.findViewById(R.id.startHour);
        endHour=view.findViewById(R.id.endHour);
        spotList=view.findViewById(R.id.spots);
        dropdownField= view.findViewById(R.id.dropdownField);

        adapterItems= new ArrayAdapter<String>(this.getContext(),R.layout.dropdown_list_item,items);

        dropdownField.setAdapter(adapterItems);

        adapterItemsSpot=new ArrayAdapter<String>(this.getContext(),R.layout.dropdown_list_item,spots);
        spotList.setAdapter(adapterItemsSpot);

        initListeners();
        for(String id: CurrentParking.getInstance().getSpotListaId()){
           getSpotNumberByIdFromFirebase(id);

        }


        return view;
    }
    public void getSpotNumberByIdFromFirebase(String spotId) {
        Query databaseReference = FirebaseDatabase.getInstance().getReference("spots");
        Query query = databaseReference.orderByChild("id").equalTo(spotId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Spot spot = snapshot.getValue(Spot.class);
                        int spotNumber = spot.getNumber();
                        System.out.println("Spot Number: " + spotNumber);
                    }
                } else {
                    System.out.println("Spot not found.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {

            case R.id.fullscreen_dialog_close:
                dismiss();
                break;

            case R.id.fullscreen_dialog_action:
                callback.onActionClick("Whatever");
                dismiss();
                break;

        }

    }

    public interface Callback {

        void onActionClick(String name);

    }

    public void save(){
        String date=this.date.getEditText().getText().toString();
        String startHour=this.startHour.getEditText().getText().toString();
        String endHour=this.endHour.getEditText().getText().toString();
        Boolean status=true;

        Intent reserva=new Intent();
        reserva.putExtra(EXTRA_DATE,date);
        reserva.putExtra(EXTRA_START,startHour);
        reserva.putExtra(EXTRA_END,endHour);
        reserva.putExtra(EXTRA_DATE,date);
        //setResult();

        this.dismiss();


    }










    private void addReservaToUser(ReservaOld reserva) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user= UserContext.getInstance().getCurrentUser();
        DocumentReference userRef = db.collection("users").document(user.getUid());

        // Update the user document to add the reserva to the reservas list
        userRef.update("reservas", FieldValue.arrayUnion(reserva))
                .addOnSuccessListener(aVoid -> {
                    // Reservation added successfully
                    // Proceed with next steps or show a success message
                })
                .addOnFailureListener(e -> {
                    // Reservation addition failed
                    // Handle the failure
                    System.out.println("Failed to add reservation: " + e.toString());
                });
    }

    private void retrieveParkingSpots() {
        System.out.println(CurrentParking.getInstance().getSpotListaId());

    }
    private void retrieveAllParkingDocuments() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference parkingCollectionRef = db.collection("parking");

        parkingCollectionRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        // Process each document here
                        String documentId = documentSnapshot.getId();
                        // Retrieve other fields as needed
                    }
                })
                .addOnFailureListener(e -> {
                    // Failed to retrieve the "parking" collection
                    System.out.println("Failed to retrieve parking collection: " + e.toString());
                });
    }





}
