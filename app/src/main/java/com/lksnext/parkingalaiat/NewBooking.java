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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

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

    public static final String EXTRA_DATE = "com.lksnext.parkingalaiat.EXTRA_DATE";
    public static final String EXTRA_STATUS = "com.lksnext.parkingalaiat.EXTRA_STATUS";
    public static final String EXTRA_START = "com.lksnext.parkingalaiat.EXTRA_START";
    public static final String EXTRA_END = "com.lksnext.parkingalaiat.EXTRA_END";
    public static final String EXTRA_TYPE = "com.lksnext.parkingalaiat.EXTRA_TYPE";
    public static final String EXTRA_SPOT = "com.lksnext.parkingalaiat.EXTRA_SPOT";
    private final CurrentParking current = CurrentParking.getInstance();

    private Boolean typeSel = false;
    private Boolean dateSel = false;
    private Boolean startSel = false;
    private Boolean endSel = false;
    private final String[] spotTypes = {"CAR", "MOTORCYCLE", "ELECTRIC", "HANDICAPPED"};
    private final String[] spots = {"-1"};


    private AutoCompleteTextView spotTypeOptionsText;
    private AutoCompleteTextView availableSpotListText;

    private ArrayAdapter<String> availableSpotAdapter;
    private TextInputLayout inputDate;
    private TextInputLayout availableSpotListDropdown;
    private TextInputLayout inputStartHour;
    private TextInputLayout inputEndHour;
    private LinearProgressIndicator progress;
    private Button search;
    private Button mapSelect;
    private Button change;


    private List<CurrentParking.Area> selectedTypeSpots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_booking);
        initUi();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        inputDate = findViewById(R.id.date);
        progress = findViewById(R.id.progress);
        inputStartHour = findViewById(R.id.startHour);
        inputEndHour = findViewById(R.id.endHour);
        search = findViewById(R.id.searchButton);
        mapSelect = findViewById(R.id.selectMap);
        change = findViewById(R.id.changeData);
        availableSpotListText = findViewById(R.id.spots);
        availableSpotListDropdown = findViewById(R.id.spotDropdwon);
        spotTypeOptionsText = findViewById(R.id.dropdownField);

        ArrayAdapter<String> typeAdapter =
                new ArrayAdapter<>(this, R.layout.dropdown_list_item, spotTypes);
        spotTypeOptionsText.setAdapter(typeAdapter);

        availableSpotAdapter =
                new ArrayAdapter<>(this, R.layout.dropdown_list_item, spots);
        availableSpotListText.setAdapter(availableSpotAdapter);

    }

    private void initListeners() {
        spotTypeOptionsText.setOnItemClickListener((parent, view, position, id) -> {
            parent.getItemAtPosition(position).toString();
            typeSel = true;
            checkAllDataFill();
        });

        availableSpotListText.setOnItemClickListener((parent, view, position, id) -> {
            parent.getItemAtPosition(position).toString();
        });

        inputDate.setStartIconOnClickListener(view -> {
            inputDate.setError(null);
            showDatePicker();
        });

        inputStartHour.setStartIconOnClickListener(view -> {
            inputStartHour.setError(null);
            inputEndHour.setError(null);
            showStartTimePicker();
        });

        inputEndHour.setStartIconOnClickListener(view -> {
            inputEndHour.setError(null);
            inputStartHour.setError(null);
            showEndTimePicker();
        });

        search.setOnClickListener(view -> {
            showProgressIndicator();
        });
        change.setOnClickListener(view -> {
            enable();
        });
    }

    private void enable() {
        spotTypeOptionsText.clearListSelection();
        inputDate.getEditText().setText("");
        inputStartHour.getEditText().setText("");
        inputEndHour.getEditText().setText("");
        spotTypeOptionsText.setEnabled(true);
        inputDate.setEnabled(true);
        inputStartHour.setEnabled(true);
        inputEndHour.setEnabled(true);

    }

    private void checkAllDataFill() {
        if (typeSel && dateSel && startSel && endSel) {
            search.setEnabled(true);
        }
    }

    private void getSpotsForType(String type) {
        selectedTypeSpots.clear();
        List<String> drop = new ArrayList<>();

        switch (type) {
            case "CAR":
                // Code for handling the "CAR" option
                selectedTypeSpots = current.getCar();
                break;
            case "MOTORCYCLE":
                // Code for handling the "MOTORCYCLE" option
                selectedTypeSpots = current.getMotor();
                break;
            case "ELECTRIC":
                // Code for handling the "ELECTRIC" option
                selectedTypeSpots = current.getElec();
                break;
            case "HANDICAPPED":
                // Code for handling the "HANDICAPPED" option
                selectedTypeSpots = current.getHand();
                break;
            default:
                // Code for handling unrecognized options
                break;
        }

        for (CurrentParking.Area s : selectedTypeSpots) {
            drop.add(s.getNumber());
        }
        availableSpotAdapter = new ArrayAdapter<>(this, R.layout.dropdown_list_item, drop);
        availableSpotListText.setAdapter(availableSpotAdapter);


    }


    private void showEndTimePicker() {
        inputEndHour.setError(null);
        MaterialTimePicker picker =
                new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(12)
                        .setMinute(10)
                        .setTitleText("Select end time")
                        .build();
        picker.show(getSupportFragmentManager(), "tag");

        picker.addOnPositiveButtonClickListener(selection -> {
            int hour = picker.getHour();
            int minute = picker.getMinute();
            String formattedTime =
                    String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            inputEndHour.getEditText().setText(formattedTime);
            if (!compareHours()) {

                inputEndHour.setError(" ");
                inputStartHour.setError(" ");
            } else {
                inputEndHour.setError(null);
                inputStartHour.setError(null);
                endSel = true;
                checkAllDataFill();
            }

            endSel = true;
            checkAllDataFill();
        });

    }

    private boolean compareHours() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String start = inputStartHour.getEditText().getText().toString();
        String end = inputEndHour.getEditText().getText().toString();

        if (start.isEmpty() || end.isEmpty()) {
            return true;
        } else {
            LocalTime localTime1 = LocalTime.parse(inputStartHour.getEditText().getText(), formatter);
            LocalTime localTime2 = LocalTime.parse(inputEndHour.getEditText().getText(), formatter);
            Duration duration = Duration.between(localTime1, localTime2);

            // Get the number of hours in the duration
            int min = (int) duration.toMinutes();
            if (localTime2.isAfter(localTime1) && min <= 480) {
                return true;
            } else if (min > 480) {
                inputEndHour.setError(" ");
                inputStartHour.setError("You can't book more than 8h");
                return false;

            } else {
                inputEndHour.setError(" ");
                inputStartHour.setError("End must be after start");
                return false;
            }
        }

    }

    private void showProgressIndicator() {
        availableSpotListText.setText("");
        getSpotsForType(spotTypeOptionsText.getEditableText().toString());
        String newS = inputStartHour.getEditText().getText().toString();
        String newE = inputEndHour.getEditText().getText().toString();
        String dat = inputDate.getEditText().getText().toString();

        if (deleteNotAvailableSpots(dat, newS, newE)) {
            progress.setVisibility(View.VISIBLE);

            // Start the animation
            progress.setProgressCompat(0, true);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {

                // Hide the progress indicator
                progress.setVisibility(View.INVISIBLE);
                mapSelect.setVisibility(View.VISIBLE);
                availableSpotListDropdown.setVisibility(View.VISIBLE);

            }, 3000);
            search.setEnabled(false);
            spotTypeOptionsText.setEnabled(false);
            inputDate.setEnabled(false);
            inputStartHour.setEnabled(false);
            inputEndHour.setEnabled(false);
        }


    }


    private void showStartTimePicker() {
        inputStartHour.setError(null);
        MaterialTimePicker picker =
                new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(12)
                        .setMinute(10)
                        .setTitleText("Select Appointment time")
                        .build();
        picker.show(getSupportFragmentManager(), "tag");

        picker.addOnPositiveButtonClickListener(selection -> {
            int hour = picker.getHour();
            int minute = picker.getMinute();
            String formattedTime =
                    String.format(Locale.getDefault(), "%02d:%02d", hour, minute);

            inputStartHour.getEditText().setText(formattedTime);
            if (compareHours()) {
                inputEndHour.setError(null);
                inputStartHour.setError(null);
                startSel = true;
                checkAllDataFill();
            }


        });

    }

    public boolean checkOverlap(String oldEnd, String oldStart, String newEnd, String newStart) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime end1 = LocalTime.parse(oldEnd, formatter);
        LocalTime start1 = LocalTime.parse(oldStart, formatter);
        LocalTime end2 = LocalTime.parse(newEnd, formatter);
        LocalTime start2 = LocalTime.parse(newStart, formatter);
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }


    private boolean deleteNotAvailableSpots(String dat, String startT, String endT) {
        List<CurrentParking.Area> toRemove = new ArrayList<>();
        List<String> libreak = new ArrayList<>();

        for (CurrentParking.Area s : selectedTypeSpots) {
            DayHours dh = s.getDayHoursByDate(dat);
            if (dh != null) {
                for (int i = 0; i < dh.getStartHours().size(); i++) {
                    if (!checkOverlap(dh.getStartHours().get(i), dh.getEndHours().get(i), startT, endT)) {
                        toRemove.add(s);
                        break;
                    }
                }
            }

        }
        for (CurrentParking.Area s : selectedTypeSpots) {
            if (!toRemove.contains(s)) {
                libreak.add(s.getNumber());
            }
        }
        if (libreak.isEmpty()) {
            showDialog();
            return false;
        }
        availableSpotAdapter = new ArrayAdapter<>(this, R.layout.dropdown_list_item, libreak);
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
        inputDate.setError(null);
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


        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraints.build())
                .build();


        datePicker.show(getSupportFragmentManager(), "tag");
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String formattedDate =
                    String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year);


            inputDate.getEditText().setText(formattedDate);
            if (UserContext.getInstance().getMinutesOfDay(formattedDate) >= 480) {
                inputDate.setError("You already have 8h reserved for this day.");
            } else {
                dateSel = true;
                checkAllDataFill();
            }

        });

    }

    private void save() {
        current.addSpotsById();
        String date = this.inputDate.getEditText().getText().toString();
        String startHour = this.inputStartHour.getEditText().getText().toString();
        String endHour = this.inputEndHour.getEditText().getText().toString();
        String type = this.spotTypeOptionsText.getEditableText().toString();
        String spot = this.availableSpotListText.getEditableText().toString();
        Boolean status = true;

        if (date.isEmpty() || startHour.isEmpty() || endHour.isEmpty()) {
            availableSpotListDropdown.setError(" ");
            this.inputDate.setError(" ");
            this.inputStartHour.setError(" ");
            this.inputEndHour.setError(" ");
        } else {
            Intent booking = getIntent();
            booking.putExtra(EXTRA_DATE, date);
            booking.putExtra(EXTRA_START, startHour);
            booking.putExtra(EXTRA_END, endHour);
            booking.putExtra(EXTRA_STATUS, status);
            booking.putExtra(EXTRA_TYPE, type);
            booking.putExtra(EXTRA_SPOT, current.getIdByNum(spot));
            setResult(RESULT_OK, booking);
            finish();
        }
    }
}
