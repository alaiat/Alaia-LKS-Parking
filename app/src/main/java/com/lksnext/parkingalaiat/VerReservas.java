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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.lksnext.parkingalaiat.domain.CurrentParking;
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

        //getAllReservasForUserLast();


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
/*        allReservas.add(new ReservaOld(new Spot(Spot.SpotType.CAR,1),"08:00","13:00","23/06/2023"));
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
*/
        getAllReservationsForUser();

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
            String spot=data.getStringExtra(NuevaReservaNormal.EXTRA_SPOT);

            ReservaOld res=new ReservaOld(spot,start,end,date,UserContext.getInstance().getCurrentUser().getUid());
            allReservas.add(res);
            checkElementStatus(res);
            sortReservas();
            changeDataToActive();
            adapter.notifyDataSetChanged();
            addReservaInFirebase(res);


            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void addReservaInFirebase(ReservaOld res) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reservationsRef = db.collection("reservations");

        // Add the reservation to the "reservations" collection
        reservationsRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // The "reservations" collection does not exist
                        // Create the collection dynamically
                        db.collection("reservations").document();
                    }

                    // Add the reservation to the "reservations" collection
                    reservationsRef.add(res)
                            .addOnSuccessListener(documentReference -> {
                                // Reservation added successfully
                                String reservationId = documentReference.getId();
                                System.out.println("Reservation added with ID: " + reservationId);

                                // Update the spot's reservas list
                                DocumentReference spotRef = db.collection("spots").document(res.getSpot());
                                spotRef.update("reservas", FieldValue.arrayUnion(reservationId));

                                // Update the user's reservas list
                                DocumentReference userRef = db.collection("users").document(UserContext.getInstance().getCurrentUser().getUid());
                                userRef.update("reservas", FieldValue.arrayUnion(reservationId));

                                // Perform additional tasks or show success message
                            })
                            .addOnFailureListener(e -> {
                                // Error occurred while adding the reservation
                                System.out.println("Failed to add reservation");
                                System.out.println(e.toString());

                                // Handle the exception
                            });
                })
                .addOnFailureListener(e -> {
                    // Error occurred while checking the existence of the collection
                    System.out.println("Failed to check collection existence");
                    System.out.println(e.toString());

                    // Handle the exception
                });


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
    private void deleteReservationByUserAndSpot(ReservaOld r) {
        // Get an instance of the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query reservations collection to find the reservation
        db.collection("reservations")
                .whereEqualTo("user", UserContext.getInstance().getCurrentUser().getUid())
                .whereEqualTo("spot", r.getSpot())
                .whereEqualTo("date",r.getDate())
                .whereEqualTo("startTime",r.getStartTime())
                .whereEqualTo("endTime",r.getEndTime())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Get the reservation document reference
                        DocumentReference reservationRef = documentSnapshot.getReference();


                        // Delete the reservation document
                        reservationRef.delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Reservation successfully deleted
                                    System.out.println("Reservation deleted: " + documentSnapshot.getId());
                                })
                                .addOnFailureListener(e -> {
                                    // Error occurred while deleting the reservation
                                    System.out.println("Failed to delete reservation: " + documentSnapshot.getId());
                                    System.out.println(e.toString());

                                    // Handle the exception
                                });
                        DocumentReference userRef = db.collection("users").document(UserContext.getInstance().getCurrentUser().getUid());
                        userRef.update("reservas", FieldValue.arrayRemove(documentSnapshot.getId()))
                                .addOnSuccessListener(aVoid1 -> {
                                    // Reservation ID removed from user document
                                    System.out.println("Reservation ID removed from user");
                                })
                                .addOnFailureListener(e -> {
                                    // Error occurred while updating user document
                                    System.out.println(e.toString());

                                    // Handle the exception
                                });
                        DocumentReference spotRef = db.collection("spots").document(r.getSpot());
                        spotRef.update("reservas", FieldValue.arrayRemove(documentSnapshot.getId()))
                                .addOnSuccessListener(aVoid2 -> {
                                    // Reservation ID removed from spot document
                                    System.out.println("Reservation ID removed from spot: " );
                                })
                                .addOnFailureListener(e -> {
                                    // Error occurred while updating spot document
                                    System.out.println(e.toString());

                                    // Handle the exception
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Error occurred while querying reservations
                    System.out.println("Failed to query reservations: " + e.toString());

                    // Handle the exception
                });
    }


    private void deleteReservation(int position) {
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
        deleteReservationByUserAndSpot(r);
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
        // Get an instance of the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get a reference to the "reservations" collection
        CollectionReference reservationsRef = db.collection("reservations");

        // Query the reservations collection based on the user ID
        Query query = reservationsRef.whereEqualTo("user", UserContext.getInstance().getCurrentUser().getUid());

        // Execute the query
        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ReservaOld> reservations = new ArrayList<>();

                    // Iterate through the query results
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Convert each document to a Reservation object
                        String date= documentSnapshot.getString("date");
                        String endT= documentSnapshot.getString("endTime");
                        String startT= documentSnapshot.getString("startTime");
                        String spot= documentSnapshot.getString("spot");
                        System.out.println(date+" "+endT+" "+startT+ " "+spot);
                       ReservaOld reservation = new ReservaOld(spot,startT,endT,date,UserContext.getInstance().getCurrentUser().getUid());
                       reservations.add(reservation);
                    }

                    // Handle the retrieved reservations
                    // You can pass the reservations list to another method for further processing
                    //handleRetrievedReservations(reservations);
                    allReservas.clear();
                    allReservas.addAll(reservations);
                    sortReservas();
                    for(ReservaOld a : allReservas){
                        checkElementStatus(a);
                    }
                    changeDataToActive();

                })
                .addOnFailureListener(e -> {
                    // Error occurred while retrieving the reservations

                    System.out.println(e.toString());

                    // Handle the exception
                });
    }












}
