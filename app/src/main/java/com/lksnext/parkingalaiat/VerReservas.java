package com.lksnext.parkingalaiat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lksnext.parkingalaiat.domain.CurrentReserva;
import com.lksnext.parkingalaiat.domain.Reserva;
import com.lksnext.parkingalaiat.domain.ReservaOld;
import com.lksnext.parkingalaiat.domain.Spot;
import com.lksnext.parkingalaiat.domain.User;
import com.lksnext.parkingalaiat.domain.UserContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerReservas extends AppCompatActivity {
    public static final int ADD_RESERVA_REQUEST = 1;
    public static final int EDIT_RESERVA_REQUEST = 2;



    List<ReservaOld> reservas;
    List<ReservaOld> allReservas;
    DatabaseReference userRef;
    List<ReservaOld> probaList=new ArrayList<>();

    List<ReservaOld> activasList;
    List<ReservaOld> inactivasList;
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


    private void addReservaToUser(ReservaOld reserva) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user=UserContext.getInstance().getCurrentUser();
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
        allReservas.add(new ReservaOld(new Spot(Spot.SpotType.CAR,1),"08:00","13:00","23/06/2023"));
        allReservas.add(new ReservaOld(new Spot(Spot.SpotType.CAR,3),"08:00","13:00","22/06/2023"));
        allReservas.add(new ReservaOld(new Spot(Spot.SpotType.CAR,4),"16:00","19:00","21/06/2023"));
        allReservas.add(new ReservaOld(new Spot(Spot.SpotType.CAR,1),"11:00","19:00","23/06/2023"));
        allReservas.add(new ReservaOld(new Spot(Spot.SpotType.CAR,5),"15:00","09:00","21/06/2023"));
        allReservas.add(new ReservaOld(new Spot(Spot.SpotType.CAR,5),"08:00","13:00","19/06/2023"));
        allReservas.add(new ReservaOld(new Spot(Spot.SpotType.CAR,5),"10:00","15:00","17/06/2023"));
        allReservas.add(new ReservaOld(new Spot(Spot.SpotType.CAR,5),"08:00","10:00","03/07/2023"));
        allReservas.add(new ReservaOld(new Spot(Spot.SpotType.CAR,5),"12:00","19:00","03/07/2023"));
        allReservas.add(new ReservaOld(new Spot(Spot.SpotType.CAR,5),"08:00","12:00","01/07/2023"));
        allReservas.add(new ReservaOld(new Spot(Spot.SpotType.CAR,5),"08:00","19:00","30/06/2023"));
        allReservas.add(new ReservaOld(new Spot(Spot.SpotType.CAR,5),"07:00","11:00","29/06/2023"));
        allReservas.add(new ReservaOld(new Spot(Spot.SpotType.CAR,5),"09:00","18:00","05/07/2023"));
        allReservas.add(new ReservaOld(new Spot(Spot.SpotType.CAR,5),"08:30","17:10","08/07/2023"));


        sortReservas();
        for(ReservaOld a : allReservas){
            checkElementStatus(a);
        }/*
        addReservaToUser(new ReservaOld(new Spot(Spot.SpotType.CAR),"08:00","16:15","23/06/2023"));
        addReservaToUser(new ReservaOld(new Spot(Spot.SpotType.CAR),"11:00","17:15","23/06/2023"));
        addReservaToUser(new ReservaOld(new Spot(Spot.SpotType.MOTORCYCLE),"14:00","18:30", "23/06/2023"));
        getAllReservasForUser();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Hide the progress indicator

                //System.out.println("Reserva" + allReservas.get(1) + "\n\n\n\n\n\n\n\n");
               // sortReservas();
                for(int i=0;i<allReservas.size();i++){
                    //checkElementStatus(allReservas.get(i));
                }

            }
        }, 3000);*/


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

                //allReservas.remove(allReservas.get(viewHolder.getAdapterPosition()));
                int swipedPosition = viewHolder.getAdapterPosition();
                showDeleteDialog(swipedPosition);
                changeDataToActive();

            }

        }).attachToRecyclerView(recyclerView);
        adapter.setOnItemClickListener(new ReservaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click event here
                ReservaOld r;
                if(tabs.getSelectedTabPosition()==0){
                    r=activasList.get(position);
                }else{
                    r=inactivasList.get(position);
                }
               CurrentReserva.getInstance().setCurrentReserva(r);
                Intent intent = new Intent(VerReservas.this, EditarReserva.class);
                startActivityForResult(intent,EDIT_RESERVA_REQUEST);

            }
        });

        changeDataToActive();
    }
    public void checkElementStatus(ReservaOld element) {
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
                System.out.println("Añadido\n\n\n\n\n\n");
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
            String date = data.getStringExtra(NuevaReservaNormal.EXTRA_DATE);
            String start = data.getStringExtra(NuevaReservaNormal.EXTRA_START);
            String end = data.getStringExtra(NuevaReservaNormal.EXTRA_END);
            String type=data.getStringExtra(NuevaReservaNormal.EXTRA_TYPE);

            ReservaOld res=new ReservaOld(new Spot(Spot.SpotType.CAR,3),start,end,date);
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
    private void showDeleteDialog(int position){
        new MaterialAlertDialogBuilder(this)
                .setMessage("Are you sure you want to delete the reservation?")
                .setPositiveButton("DELETE", (dialog, which) -> {
                    // Handle positive button click
                    System.out.println(position);
                    deleteReservation(position);
                    dialog.dismiss();


                }).setNegativeButton("CANCEL", (dialog,which)->{
                    dialog.dismiss();
                })
                .show();
    }

    private void deleteReservation(int position) {

        /*Map<String, Object> reserva = new HashMap<>();
        reserva.put("startHour", "08:00");
        reserva.put("endHour", "10:30");
        reserva.put("date", "07/11/2023");
        reserva.put("status","Activo");
        reserva.put("spot",1);
        addReservaToUserByEmail("a@a.com",reserva);*/
        ReservaOld r;
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


    }
    /*private void addReservaToUserByEmail(String userEmail, Map<String, Object>  reserva) {

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        Query query = usersRef.orderByChild("email").equalTo(userEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();

                    // Update the reservas array list of the user
                    DatabaseReference userReservasRef = usersRef.child(userId).child("reservas");
                    userReservasRef.push().setValue(reserva)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Reserva added to the user successfully
                                } else {
                                    // Failed to add reserva to the user
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error occurred while accessing the database
            }
        });
    }*/
    private void addReservaToUserByEmail(String userEmail, Map<String, Object>   reserva) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        Query query = usersRef.orderByChild("email").equalTo(userEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey();

                        // Update the reservas array list of the user
                        DatabaseReference userReservasRef = usersRef.child(userId).child("reservas");
                        userReservasRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ArrayList<Reserva> reservas;
                                if (dataSnapshot.exists()) {
                                    reservas = dataSnapshot.getValue(new GenericTypeIndicator<ArrayList<Reserva>>() {});
                                } else {
                                    reservas = new ArrayList<>();
                                }

                                // Add the new reserva item to the reservas list
                                //reservas.add(reserva);

                                // Update the user reference with the modified reservas list
                                userReservasRef.setValue(reservas)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                // Reserva added to the user successfully
                                            } else {
                                                // Failed to add reserva to the user
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Error occurred while accessing the database
                            }
                        });
                    }
                } else {
                    // User does not exist in the database
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error occurred while accessing the database
            }
        });
    }
    private void getAllReservasForUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user=UserContext.getInstance().getCurrentUser();
        DocumentReference userRef = db.collection("users").document(user.getUid());


        userRef.get()
        .continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> userData = documentSnapshot.getData();
                        if (userData != null) {
                            //Map<String, Object> reservasMap = (Map<String, Object>) userData.get("reservas");
                            //System.out.println( userData.get("reservas"));
                            List<ReservaOld> lista = (List<ReservaOld>) ((Map<?, ?>) userData.get("reservas")).get(0);
                            allReservas = lista;
                            System.out.println("\n\n\n"+lista);





                        }
                    }
                } else {
                    // User document does not exist
                    System.out.println("User document does not exist.");
                }
            } else {
                // Error occurred while retrieving the document
                Exception exception = task.getException();
                System.out.println("Failed to retrieve document: " + exception.toString());
            }
            return null;
        });
    }








}
