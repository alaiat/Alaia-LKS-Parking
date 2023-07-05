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
import com.lksnext.parkingalaiat.domain.CurrentReserva;
import com.lksnext.parkingalaiat.domain.Reserva;
import com.lksnext.parkingalaiat.domain.UserContext;
import com.lksnext.parkingalaiat.interfaces.OnReservationsListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VerReservas extends AppCompatActivity {
    public static final int ADD_RESERVA_REQUEST = 1;
    public static final int EDIT_RESERVA_REQUEST = 2;

    private FirebaseManager fm;



    private List<Reserva> reservasToShow;
    private List<Reserva> allReservas;
    private List<Reserva> activasList;
    private List<Reserva> inactivasList;

    private ReservaAdapter adapter;

    private RecyclerView recyclerView;
    private TabLayout tabs;
    private FloatingActionButton nReservaButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_reserva_view);
        initUi();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_bar_app, menu);

        setTitle("Mis reservas");
        return true;
    }

    public void initUi(){
        initData();
        initView();
        initListeners();

    }
    public void initData(){
        allReservas=new ArrayList<>();
        reservasToShow =new ArrayList<>();
        activasList=new ArrayList<>();
        inactivasList=new ArrayList<>();
        fm=FirebaseManager.getInstance();

        getAllReservationsForUser();

        sortReservas();
        for(Reserva a : allReservas){
            checkElementStatus(a);
        }


    }
    public void initView(){
        tabs=findViewById(R.id.tabs);
        nReservaButton=findViewById(R.id.nReservaButton);
        setAdapter();

    }
    private void initListeners() {
        nReservaButton.setOnClickListener(view ->{ changeToNuevaReserva();});
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
        adapter.setOnItemClickListener(new ReservaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click event here
                Reserva r;
                if(tabs.getSelectedTabPosition()==0){
                    r=activasList.get(position);
                    fm.setCurrentReserva(r);
                    Intent intent = new Intent(VerReservas.this, EditarReserva.class);
                    startActivityForResult(intent,EDIT_RESERVA_REQUEST);
                }


            }
        });


    }


    private void sortReservas(){
        ReservaSorter rs = new ReservaSorter();
        rs.sortElementsByProximity(allReservas);
        rs.sortElementsByProximity(activasList);
        rs.sortElementsByProximity(inactivasList);
    }

    private void changeToNuevaReserva() {
        Intent intent = new Intent(VerReservas.this, NuevaReserva.class);
        startActivityForResult(intent,ADD_RESERVA_REQUEST);
    }



    public void changeDataToActive(){
        reservasToShow.clear();
        reservasToShow.addAll(activasList);
        adapter.notifyDataSetChanged();
    }

    public void changeDataToInactive(){
        reservasToShow.clear();
        reservasToShow.addAll(inactivasList);
        adapter.notifyDataSetChanged();
    }


    public void setAdapter(){
        adapter=new ReservaAdapter(reservasToShow,this);
        recyclerView= findViewById(R.id.listRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        changeDataToActive();
    }
    public void checkElementStatus(Reserva element) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate elementDate = LocalDate.parse(element.getDate(), formatter);

        if (elementDate.isEqual(currentDate)) {
            LocalTime now=LocalTime.now();
            LocalTime time=LocalTime.parse(element.getEndTime());

            if(now.isAfter(time)){
                element.setActivo("Inactivo");
                inactivasList.add(element);
            }else{
                element.setActivo("Activo");
                activasList.add(element);
            }

        } else if (elementDate.isAfter(currentDate)) {
            element.setActivo("Activo");
            activasList.add(element);

        } else {
            element.setActivo("Inactivo");
            inactivasList.add(element);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_RESERVA_REQUEST && resultCode == RESULT_OK) {
            String date = data.getStringExtra(NuevaReserva.EXTRA_DATE);
            String start = data.getStringExtra(NuevaReserva.EXTRA_START);
            String end = data.getStringExtra(NuevaReserva.EXTRA_END);
            String spot=data.getStringExtra(NuevaReserva.EXTRA_SPOT);

            Reserva res=new Reserva(spot,start,end,date,UserContext.getInstance().getCurrentUser().getUid());
            addReservaInFirebase(res);
            allReservas.add(res);
            checkElementStatus(res);
            sortReservas();
            changeDataToActive();
            adapter.notifyDataSetChanged();


            Toast.makeText(this, "Reserva saved", Toast.LENGTH_SHORT).show();
        } else if(requestCode == EDIT_RESERVA_REQUEST && resultCode == RESULT_OK){
            Reserva berria= CurrentReserva.getInstance().getCurrent();
            berria.setStartTime(data.getStringExtra(NuevaReserva.EXTRA_START));
            berria.setEndTime(data.getStringExtra(NuevaReserva.EXTRA_END));
            sortReservas();
            changeDataToActive();

        }else {
            Toast.makeText(this, "Reserva not saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void addReservaInFirebase(Reserva res) {
        fm.addReservaInFirebase(res);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.perfil:
                Intent intent = new Intent(VerReservas.this, Perfil.class);
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
                    deleteReservation(position);
                    dialog.dismiss();
                }).setNegativeButton("CANCEL", (dialog,which)->{
                    dialog.dismiss();
                })
                .show();
    }
    private void deleteReservationByAllData(Reserva r) {
        fm.deleteReservationByAllData(r);
    }


    private void deleteReservation(int position) {
        Reserva r;
        if(tabs.getSelectedTabPosition()==0){
            r=activasList.get(position);
            activasList.remove(position);
            allReservas.remove(r);
            changeDataToActive();
        }else{
            r=inactivasList.get(position);
            inactivasList.remove(position);
            allReservas.remove(r);
            changeDataToInactive();
        }
        deleteReservationByAllData(r);
        Snackbar.make(findViewById(R.id.snackbar), "Reservation deleted successfully", Snackbar.LENGTH_LONG).setDuration(7000).setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReservaInFirebase(r);
                allReservas.add(r);
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
    private void getAllReservationsForUser() {
        fm.getAllReservationsForUser(new OnReservationsListener() {
            @Override
            public void onReservationsLoaded(List<Reserva> reservations) {
                if (reservations != null) {
                    allReservas.clear();
                    allReservas.addAll(reservations);
                    sortReservas();
                    for(Reserva a : allReservas){
                        checkElementStatus(a);
                    }
                    changeDataToActive();
                }
            }
        });

    }













}
