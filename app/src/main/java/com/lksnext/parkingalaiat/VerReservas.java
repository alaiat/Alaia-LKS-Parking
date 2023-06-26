package com.lksnext.parkingalaiat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.lksnext.parkingalaiat.domain.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VerReservas extends AppCompatActivity {
    public static final int ADD_RESERVA_REQUEST = 1;


    List<Reserva> reservas;
    List<Reserva> allReservas;

    List<Reserva> activasList;
    List<Reserva> inactivasList;
    TabItem activas,inactivas;
    TabLayout tabs;
    private FloatingActionButton nReservaButton;
    ReservaAdapter adapter;

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
    private void sortReservas(){
        ReservaSorter rs = new ReservaSorter();
        rs.sortElementsByProximity(allReservas);
        rs.sortElementsByProximity(activasList);
        rs.sortElementsByProximity(inactivasList);
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


    }
    private void showErrorDialog(LocalDate donde){
        new MaterialAlertDialogBuilder(this)
                .setTitle(donde.toString())
                .setMessage("The email or password introduced are wrong. Please check and try again")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Handle positive button click
                    dialog.dismiss();


                })
                .show();
    }

    private void changeToNuevaReserva() {
       /* DialogFragment dialog = NuevaReserva.newInstance();
        ((NuevaReserva) dialog).setCallback(new NuevaReserva.Callback() {
            @Override
            public void onActionClick(String name) {
                Toast.makeText(VerReservas.this, name, Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getSupportFragmentManager(), "tag");*/
        Intent intent = new Intent(VerReservas.this, NuevaReservaNormal.class);
        startActivityForResult(intent,ADD_RESERVA_REQUEST);
    }

    public void initData(){
        allReservas=new ArrayList<>();
        reservas=new ArrayList<>();
        activasList=new ArrayList<>();
        inactivasList=new ArrayList<>();
        allReservas.add(new Reserva("Electric","08:00","13:00","23/06/2023","Inactivo",3));
        allReservas.add(new Reserva("Electric","08:00","13:00","22/06/2023","Inactivo",3));
        allReservas.add(new Reserva("Electric","16:00","19:00","21/06/2023","Inactivo",2));
        allReservas.add(new Reserva("Electric","11:00","19:00","23/06/2023","Inactivo",1));
        allReservas.add(new Reserva("Electric","15:00","09:00","21/06/2023","Inactivo",1));
        allReservas.add(new Reserva("Electric","08:00","13:00","19/06/2023","Inactivo",1));
        allReservas.add(new Reserva("Electric","10:00","15:00","17/06/2023","Inactivo",1));
        allReservas.add(new Reserva("Electric","08:00","10:00","03/07/2023","Activo",1));
        allReservas.add(new Reserva("Electric","12:00","19:00","03/07/2023","Activo",1));
        allReservas.add(new Reserva("Electric","08:00","12:00","01/07/2023","Activo",1));
        allReservas.add(new Reserva("Electric","08:00","19:00","30/06/2023","Activo",1));
        allReservas.add(new Reserva("Electric","07:00","11:00","29/06/2023","Activo",1));
        allReservas.add(new Reserva("Electric","09:00","18:00","05/07/2023","Activo",1));
        allReservas.add(new Reserva("Electric","08:30","17:10","08/07/2023","Activo",1));

        sortReservas();
        for(Reserva a : allReservas){
            checkElementStatus(a);
        }


    }

    public void changeDataToActive(){
        reservas.clear();
        reservas.addAll(activasList);
        adapter.notifyDataSetChanged();
    }

    public void changeDataToInactive(){
        reservas.clear();
        reservas.addAll(inactivasList);
        adapter.notifyDataSetChanged();
    }

    public void initView(){
        activas=findViewById(R.id.activas);
        inactivas=findViewById(R.id.pasadas);
        tabs=findViewById(R.id.tabs);
        nReservaButton=findViewById(R.id.nReservaButton);

        setAdapter();

    }
    public void setAdapter(){
        adapter=new ReservaAdapter(reservas,this);
        RecyclerView recyclerView= findViewById(R.id.listRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                allReservas.remove(allReservas.get(viewHolder.getAdapterPosition()));
                changeDataToActive();

            }
        }).attachToRecyclerView(recyclerView);

        changeDataToActive();
    }
    public void checkElementStatus(Reserva element) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate elementDate = LocalDate.parse(element.getDate(), formatter);

        if (elementDate.isEqual(currentDate)) {
            LocalTime now=LocalTime.now();
            LocalTime time=LocalTime.parse(element.getEndHour());

            if(now.isAfter(time)){
                element.setStatus("Inactivo");
                inactivasList.add(element);
            }else{
                element.setStatus("Activo");
                activasList.add(element);
                System.out.println("Añadido\n\n\n\n\n\n");
            }

        } else if (elementDate.isAfter(currentDate)) {
            element.setStatus("Activo");
            activasList.add(element);

        } else {
            element.setStatus("Inactivo");
            inactivasList.add(element);

        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_RESERVA_REQUEST && resultCode == RESULT_OK) {
            String date = data.getStringExtra(NuevaReservaNormal.EXTRA_DATE);
            String start = data.getStringExtra(NuevaReservaNormal.EXTRA_START);
            String end = data.getStringExtra(NuevaReservaNormal.EXTRA_END);
            String type=data.getStringExtra(NuevaReservaNormal.EXTRA_TYPE);

            Reserva res=new Reserva(type,start,end,date,"Activo",5);
            allReservas.add(res);
            checkElementStatus(res);
            sortReservas();
            changeDataToActive();
            //toggleGroup.check(R.id.activas);
            //toggleGroup.uncheck(R.id.pasadas);
            adapter.notifyDataSetChanged();


            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show();
        }
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
}
