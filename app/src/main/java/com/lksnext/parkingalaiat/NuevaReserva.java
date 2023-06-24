package com.lksnext.parkingalaiat;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NuevaReserva extends DialogFragment implements View.OnClickListener {
    private Callback callback;
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
        action.setOnClickListener(this);
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

        return view;
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
    }



}
