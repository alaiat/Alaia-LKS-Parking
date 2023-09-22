package com.lksnext.parkingalaiat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.lksnext.parkingalaiat.domain.CurrentBooking;
import com.lksnext.parkingalaiat.domain.Booking;
import com.lksnext.parkingalaiat.domain.UserContext;
import com.lksnext.parkingalaiat.interfaces.OnBookingsListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SeeBookings extends AppCompatActivity {
    public static final int ADD_RESERVA_REQUEST = 1;
    public static final int EDIT_RESERVA_REQUEST = 2;

    private FirebaseManager fm;



    private List<Booking> bookinhgsToShow;
    private List<Booking> allBookings;
    private List<Booking> activasList;
    private List<Booking> inactivasList;

    private BookingAdapter adapter;

    private RecyclerView recyclerView;
    private TabLayout tabs;
    private FloatingActionButton nReservaButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_booking_view);
        initUi();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_bar_app, menu);

        setTitle("My bookings");
        return true;
    }

    public void initUi(){
        initData();
        initView();
        initListeners();

    }
    public void initData(){
        allBookings =new ArrayList<>();
        bookinhgsToShow =new ArrayList<>();
        activasList=new ArrayList<>();
        inactivasList=new ArrayList<>();
        fm=FirebaseManager.getInstance();

        getAllBookingsForUser();

        sortReservas();
        for(Booking a : allBookings){
            checkElementStatus(a);
        }


    }
    public void initView(){
        tabs=findViewById(R.id.tabs);
        nReservaButton=findViewById(R.id.nReservaButton);
        setAdapter();

    }
    private void initListeners() {
        nReservaButton.setOnClickListener(view ->{ changeToNewBooking();});
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
        adapter.setOnItemClickListener(new BookingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click event here
                Booking r;
                if(tabs.getSelectedTabPosition()==0){
                    r=activasList.get(position);
                    fm.setCurrentBooking(r);
                    Intent intent = new Intent(SeeBookings.this, EditBooking.class);
                    startActivityForResult(intent,EDIT_RESERVA_REQUEST);
                }


            }
        });


    }


    private void sortReservas(){
        BookingSorter rs = new BookingSorter();
        rs.sortElementsByProximity(allBookings);
        rs.sortElementsByProximity(activasList);
        rs.sortElementsByProximity(inactivasList);
    }

    private void changeToNewBooking() {
        Intent intent = new Intent(SeeBookings.this, NewBooking.class);
        startActivityForResult(intent,ADD_RESERVA_REQUEST);
    }



    public void changeDataToActive(){
        bookinhgsToShow.clear();
        bookinhgsToShow.addAll(activasList);
        adapter.notifyDataSetChanged();
    }

    public void changeDataToInactive(){
        bookinhgsToShow.clear();
        bookinhgsToShow.addAll(inactivasList);
        adapter.notifyDataSetChanged();
    }


    public void setAdapter(){
        adapter=new BookingAdapter(bookinhgsToShow,this);
        recyclerView= findViewById(R.id.listRecyclerView);
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
            LocalTime now=LocalTime.now();
            LocalTime time=LocalTime.parse(element.getEndTime());

            if(now.isAfter(time)){
                element.setActive("Inactivo");
                inactivasList.add(element);
            }else{
                element.setActive("Activo");
                activasList.add(element);
            }

        } else if (elementDate.isAfter(currentDate)) {
            element.setActive("Activo");
            activasList.add(element);

        } else {
            element.setActive("Inactivo");
            inactivasList.add(element);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_RESERVA_REQUEST && resultCode == RESULT_OK) {
            String date = data.getStringExtra(NewBooking.EXTRA_DATE);
            String start = data.getStringExtra(NewBooking.EXTRA_START);
            String end = data.getStringExtra(NewBooking.EXTRA_END);
            String spot=data.getStringExtra(NewBooking.EXTRA_SPOT);

            Booking res=new Booking(spot,start,end,date,UserContext.getInstance().getCurrentUser().getUid());
            addBookingInFirebase(res);
            allBookings.add(res);
            checkElementStatus(res);
            sortReservas();
            changeDataToActive();
            adapter.notifyDataSetChanged();


            Toast.makeText(this, "Reserva saved", Toast.LENGTH_SHORT).show();
        } else if(requestCode == EDIT_RESERVA_REQUEST && resultCode == RESULT_OK){
            Booking berria= CurrentBooking.getInstance().getCurrent();
            berria.setStartTime(data.getStringExtra(NewBooking.EXTRA_START));
            berria.setEndTime(data.getStringExtra(NewBooking.EXTRA_END));
            sortReservas();
            changeDataToActive();

        }else {
            Toast.makeText(this, "Reserva not saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void addBookingInFirebase(Booking res) {
        fm.addBookingInFirebase(res);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.perfil:
                Intent intent = new Intent(SeeBookings.this, Profile.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void showDeleteDialog(int position){
        new MaterialAlertDialogBuilder(this)
                .setMessage("Are you sure you want to delete the reservation?")
                .setPositiveButton("DELETE", (dialog, which) -> {
                    deleteBooking(position);
                    dialog.dismiss();
                }).setNegativeButton("CANCEL", (dialog,which)->{
                    dialog.dismiss();
                })
                .show();
    }
    private void deleteBookingByAllData(Booking r) {
        fm.deleteBookingByAllData(r);
    }


    private void deleteBooking(int position) {
        Booking r;
        if(tabs.getSelectedTabPosition()==0){
            r=activasList.get(position);
            activasList.remove(position);
            allBookings.remove(r);
            changeDataToActive();
        }else{
            r=inactivasList.get(position);
            inactivasList.remove(position);
            allBookings.remove(r);
            changeDataToInactive();
        }
        deleteBookingByAllData(r);
        Snackbar.make(findViewById(R.id.snackbar), "Reservation deleted successfully", Snackbar.LENGTH_LONG).setDuration(7000).setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookingInFirebase(r);
                allBookings.add(r);
                checkElementStatus(r);
                sortReservas();
                if(tabs.getSelectedTabPosition()==0){
                    changeDataToActive();
                }else{
                    changeDataToInactive();
                }

            }
        }).show();


    }
    private void getAllBookingsForUser() {
        fm.getAllBookingsForUser(new OnBookingsListener() {
            @Override
            public void onBookingsLoaded(List<Booking> bookings) {
                if (bookings != null) {
                    allBookings.clear();
                    allBookings.addAll(bookings);
                    sortReservas();
                    for(Booking a : allBookings){
                        checkElementStatus(a);
                    }
                    changeDataToActive();
                }
            }
        });

    }













}
