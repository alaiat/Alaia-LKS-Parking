package com.lksnext.parkingalaiat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lksnext.parkingalaiat.domain.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VerReservasPruebaSQLDatabase extends AppCompatActivity {
    public static final int ADD_RESERVA_REQUEST = 1;


    private ReservaViewModel reservaViewModel;


    List<Reserva> reservas;
    List<Reserva> allReservas;

    List<Reserva> activasList=new ArrayList<>();
    List<Reserva> inactivasList=new ArrayList<>();
    Button activas,inactivas;
    MaterialButtonToggleGroup toggleGroup;
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

    public void setAdapter(){
        final ReservaAdapter adapter=new ReservaAdapter();
        RecyclerView recyclerView= findViewById(R.id.listRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        reservaViewModel = new ViewModelProvider(this).get(ReservaViewModel.class);
        reservaViewModel.getAllReservas().observe(this, new Observer<List<Reserva>>() {
            @Override
            public void onChanged(List<Reserva> reservasList) {
                allReservas=reservasList;
                reservas=reservasList;
                for(Reserva a:allReservas){
                    checkElementStatus(a);
                }
                adapter.setItems(reservas);
                //changeDataToActive();
            }
        });


        //changeDataToActive();
    }
    public void initUi(){


        //initData();
        initView();
        initListeners();
    }
    private void sortReservas(List<Reserva> lista){
        ReservaSorter rs = new ReservaSorter();
        rs.sortElementsByProximity(lista);
    }

    private void initListeners() {
        nReservaButton.setOnClickListener(view ->{ changeToNuevaReserva();});
        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (checkedId == R.id.activas) {
                        // Button 1 is checked
                        // Perform actions for Button 1
                        addDataActive();
                    }
                    if (checkedId == R.id.pasadas) {
                        // Button 2 is checked
                        // Perform actions for Button 2
                        addDataInactive();
                    }
                }else{
                    if (checkedId == R.id.activas) {
                        // Button 1 is unchecked
                        // Perform actions for Button 1
                        changeDataToInactive();
                    }
                    if (checkedId == R.id.pasadas) {
                        // Button 2 is unchecked
                        // Perform actions for Button 2
                        changeDataToActive();
                    }
                }
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
                Toast.makeText(VerReservasPruebaSQLDatabase.this, name, Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getSupportFragmentManager(), "tag");*/
        Intent intent = new Intent(VerReservasPruebaSQLDatabase.this, NuevaReservaNormal.class);
        startActivityForResult(intent,ADD_RESERVA_REQUEST);
    }

    public void initData(){
        allReservas=new ArrayList<>();
        reservas=new ArrayList<>();
        activasList=new ArrayList<>();
        inactivasList=new ArrayList<>();
        allReservas.add(new Reserva("Electric","08:00","13:00","23/06/2023","Activo"));
        allReservas.add(new Reserva("Electric","08:00","13:00","22/06/2023","Activo"));
        allReservas.add(new Reserva("Electric","16:00","19:00","21/06/2023","Activo"));
        allReservas.add(new Reserva("Electric","11:00","19:00","23/06/2023","Activo"));
        allReservas.add(new Reserva("Electric","15:00","09:00","21/06/2023","Activo"));
        allReservas.add(new Reserva("Electric","08:00","13:00","19/06/2023","Activo"));
        allReservas.add(new Reserva("Electric","10:00","15:00","17/06/2023","Activo"));
        allReservas.add(new Reserva("Electric","08:00","19:00","03/06/2023","Activo"));

        sortReservas(allReservas);
        for(Reserva a : allReservas){
            checkElementStatus(a);
        }

    }

    public void changeDataToActive(){
        reservas.clear();
        reservas.addAll(activasList);

        adapter.notifyDataSetChanged();
    }
    public void addDataActive(){
        if(!reservas.containsAll(activasList)){
            reservas.addAll(activasList);
            adapter.notifyDataSetChanged();
        }

    }
    public void changeDataToInactive(){
        reservas.clear();
        reservas.addAll(inactivasList);
        adapter.notifyDataSetChanged();
    }
    public void addDataInactive(){
        if(!reservas.containsAll(inactivasList)){
            reservas.addAll(inactivasList);
            adapter.notifyDataSetChanged();
        }

    }
    public void initView(){
        activas=findViewById(R.id.activas);
        inactivas=findViewById(R.id.pasadas);
        toggleGroup=findViewById(R.id.toggleButton);
        toggleGroup.check(R.id.activas);
        nReservaButton=findViewById(R.id.nReservaButton);



        setAdapter();

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

            Reserva res=new Reserva("Normal",start,end,date,"Activo");
            reservaViewModel.insert(res);

            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show();
        }
    }
}
