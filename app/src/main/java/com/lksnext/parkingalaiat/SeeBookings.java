package com.lksnext.parkingalaiat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.lksnext.parkingalaiat.domain.CurrentBooking;
import com.lksnext.parkingalaiat.domain.Booking;
import com.lksnext.parkingalaiat.domain.UserContext;
import com.lksnext.parkingalaiat.interfaces.OnBookingsListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SeeBookings extends AppCompatActivity {
    private static final String OPERATION = "OPERATION";
    private static final String ADD_REQ = "ADD_REQ";
    private static final String EDIT_REQ = "EDIT_REQ";

    private FirebaseManager fm;

    private List<Booking> bookingsToShow;
    private List<Booking> allBookings;
    private List<Booking> activeList;
    private List<Booking> inactiveList;

    private BookingAdapter adapter;

    private RecyclerView recyclerView;
    private TabLayout tabs;
    private FloatingActionButton newBookButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_booking_view);
        initUi();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_bar_app, menu);

        setTitle("My bookings");
        return true;
    }

    public void initUi() {
        initData();
        initView();
        initListeners();

    }

    public void initData() {
        allBookings = new ArrayList<>();
        bookingsToShow = new ArrayList<>();
        activeList = new ArrayList<>();
        inactiveList = new ArrayList<>();
        fm = FirebaseManager.getInstance();

        getAllBookingsForUser();

        sortBookings();
        for (Booking a : allBookings) {
            checkElementStatus(a);
        }


    }

    public void initView() {
        tabs = findViewById(R.id.tabs);
        newBookButton = findViewById(R.id.nReservaButton);
        setAdapter();

    }

    private void initListeners() {
        newBookButton.setOnClickListener(view -> {
            changeToNewBooking();
        });
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectedTabPosition = tab.getPosition();
                if (selectedTabPosition == 0) {
                    changeDataToActive();
                } else if (selectedTabPosition == 1) {
                    changeDataToInactive();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle tab unselect
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle tab reselect
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();
                showDeleteDialog(swipedPosition);
                changeDataToActive();

            }

        }).attachToRecyclerView(recyclerView);
        adapter.setOnItemClickListener(position -> {

            // Handle item click event here
            Booking r;
            if (tabs.getSelectedTabPosition() == 0) {
                r = activeList.get(position);
                fm.setCurrentBooking(r);

                Intent intent = new Intent(SeeBookings.this, EditBooking.class);
                intent.putExtra(OPERATION, EDIT_REQ);
                bookingActivityLauncher.launch(intent);
            }

        });
    }


    private void sortBookings() {
        BookingSorter rs = new BookingSorter();
        rs.sortElementsByProximity(allBookings);
        rs.sortElementsByProximity(activeList);
        rs.sortElementsByProximity(inactiveList);
    }

    private void changeToNewBooking() {
        Intent intent = new Intent(SeeBookings.this, NewBooking.class);
        intent.putExtra(OPERATION, ADD_REQ);
        bookingActivityLauncher.launch(intent);
    }


    public void changeDataToActive() {
        bookingsToShow.clear();
        bookingsToShow.addAll(activeList);
        adapter.notifyDataSetChanged();
    }

    public void changeDataToInactive() {
        bookingsToShow.clear();
        bookingsToShow.addAll(inactiveList);
        adapter.notifyDataSetChanged();
    }


    public void setAdapter() {
        adapter = new BookingAdapter(bookingsToShow, this);
        recyclerView = findViewById(R.id.listRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        changeDataToActive();
    }

    public void checkElementStatus(Booking element) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate elementDate = LocalDate.parse(element.getDate(), formatter);

        if (elementDate.isEqual(currentDate)) {
            LocalTime now = LocalTime.now();
            LocalTime time = LocalTime.parse(element.getEndTime());

            if (now.isAfter(time)) {
                element.setActive("Inactive");
                inactiveList.add(element);
            } else {
                element.setActive("Active");
                activeList.add(element);
            }

        } else if (elementDate.isAfter(currentDate)) {
            element.setActive("Active");
            activeList.add(element);

        } else {
            element.setActive("Inactive");
            inactiveList.add(element);

        }

    }

    ActivityResultLauncher<Intent> bookingActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                int resultCode = result.getResultCode();
                Intent data = result.getData();
                String operation = data.getStringExtra(OPERATION);

                if (operation != null) {
                    if (operation.equals(ADD_REQ) && resultCode == RESULT_OK) {
                        String date = data.getStringExtra(NewBooking.EXTRA_DATE);
                        String start = data.getStringExtra(NewBooking.EXTRA_START);
                        String end = data.getStringExtra(NewBooking.EXTRA_END);
                        String spot = data.getStringExtra(NewBooking.EXTRA_SPOT);

                        Booking res = new Booking(spot, start, end, date, UserContext.getInstance().getCurrentUser().getUid());
                        addBookingInFirebase(res);
                        allBookings.add(res);
                        checkElementStatus(res);
                        sortBookings();
                        changeDataToActive();
                        adapter.notifyDataSetChanged();

                        Toast.makeText(SeeBookings.this, "Booking saved", Toast.LENGTH_SHORT).show();

                    } else if (operation.equals(EDIT_REQ) && resultCode == RESULT_OK) {
                        Booking newB = CurrentBooking.getInstance().getCurrent();
                        newB.setStartTime(data.getStringExtra(NewBooking.EXTRA_START));
                        newB.setEndTime(data.getStringExtra(NewBooking.EXTRA_END));
                        sortBookings();
                        changeDataToActive();

                    } else {
                        Toast.makeText(SeeBookings.this, "Booking not saved", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SeeBookings.this, "Unknown operation", Toast.LENGTH_SHORT).show();
                }

            }
    );


    private void addBookingInFirebase(Booking res) {
        fm.addBookingInFirebase(res);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.perfil) {
            Intent intent = new Intent(SeeBookings.this, Profile.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.logout) {
            logout();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(SeeBookings.this, Login.class);
        startActivity(intent);
    }

    private void showDeleteDialog(int position) {
        new MaterialAlertDialogBuilder(this)
                .setMessage("Are you sure you want to delete this booking?")
                .setPositiveButton("DELETE", (dialog, which) -> {
                    deleteBooking(position);
                    dialog.dismiss();
                }).setNegativeButton("CANCEL", (dialog, which) -> {
                    dialog.dismiss();
                    if (tabs.getSelectedTabPosition() == 0) changeDataToActive();
                    else                                    changeDataToInactive();
                })
                .show();
    }

    private void deleteBookingByAllData(Booking r) {
        fm.deleteBookingByAllData(r);
    }


    private void deleteBooking(int position) {
        Booking r;
        if (tabs.getSelectedTabPosition() == 0) {
            r = activeList.get(position);
            activeList.remove(position);
            allBookings.remove(r);
            changeDataToActive();
        } else {
            r = inactiveList.get(position);
            inactiveList.remove(position);
            allBookings.remove(r);
            changeDataToInactive();
        }
        deleteBookingByAllData(r);
        Snackbar.make(findViewById(R.id.snackbar), "Booking deleted successfully", BaseTransientBottomBar.LENGTH_LONG)
                .setDuration(7000)
                .setAction("Undo", v -> {

                    addBookingInFirebase(r);
                    allBookings.add(r);
                    checkElementStatus(r);
                    sortBookings();
                    if (tabs.getSelectedTabPosition() == 0) {
                        changeDataToActive();
                    } else {
                        changeDataToInactive();
                    }

                })
                .show();
    }

    private void getAllBookingsForUser() {
        fm.getAllBookingsForUser((exception, bookings) -> {
            if (bookings != null) {
                allBookings.clear();
                allBookings.addAll(bookings);
                sortBookings();
                for (Booking a : allBookings) {
                    checkElementStatus(a);
                }
                changeDataToActive();
            }
        });
    }
}