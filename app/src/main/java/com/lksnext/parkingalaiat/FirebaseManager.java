package com.lksnext.parkingalaiat;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lksnext.parkingalaiat.domain.CurrentParking;
import com.lksnext.parkingalaiat.domain.CurrentReserva;
import com.lksnext.parkingalaiat.domain.Reserva;
import com.lksnext.parkingalaiat.domain.UserContext;
import com.lksnext.parkingalaiat.interfaces.OnReservationUpdatedListener;
import com.lksnext.parkingalaiat.interfaces.OnReservationsListener;
import com.lksnext.parkingalaiat.interfaces.OnSpotIdsLoadedListener;
import com.lksnext.parkingalaiat.interfaces.OnSpotsLoaderByTypeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FirebaseManager {
    private static final String EXTRA_LOGIN_SUCCESS="com.lksnext.parkingalaiat.EXTRA_LOGIN_SUCCESS";

    private static FirebaseManager instance;
    private FirebaseFirestore db;
    private CollectionReference userRef;
    private CollectionReference reservaRef;
    private CollectionReference spotRef;
    private UserContext user;
    private CurrentReserva currentReserva;


    private FirebaseManager(){
        db = FirebaseFirestore.getInstance();
        reservaRef = db.collection("reservations");
        userRef = db.collection("users");
        spotRef = db.collection("spots");
        user=UserContext.getInstance();
        currentReserva=CurrentReserva.getInstance();

    }
    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public void getAllReservationsForUser(OnReservationsListener listener) {
        String userId = user.getCurrentUser().getUid();
        Query query = reservaRef.whereEqualTo("user", userId);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Reserva> reservations = new ArrayList<>();

            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                String date = documentSnapshot.getString("date");
                String endT = documentSnapshot.getString("endTime");
                String startT = documentSnapshot.getString("startTime");
                String spot = documentSnapshot.getString("spot");

                Reserva reservation = new Reserva(spot, startT, endT, date, userId);
                user.addMinutesToDay(date, startT, endT);
                reservations.add(reservation);
            }

            // Invoke the listener with the loaded reservations
            listener.onReservationsLoaded(reservations);
        }).addOnFailureListener(e -> {
            // Invoke the listener with null to indicate failure
            listener.onReservationsLoaded(null);
        });
    }
    public void deleteReservationByAllData(Reserva r) {
        String userId=user.getCurrentUser().getUid();
        reservaRef
                .whereEqualTo("user", userId)
                .whereEqualTo("spot", r.getSpot())
                .whereEqualTo("date",r.getDate())
                .whereEqualTo("startTime",r.getStartTime())
                .whereEqualTo("endTime",r.getEndTime())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        DocumentReference reservationRef = documentSnapshot.getReference();
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
    public void addReservaInFirebase(Reserva res) {
        String userId=user.getCurrentUser().getUid();
        reservaRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        db.collection("reservations").document();
                    }
                    reservaRef.add(res)
                            .addOnSuccessListener(documentReference -> {
                                String reservationId = documentReference.getId();
                                //System.out.println("Reservation added with ID: " + reservationId);

                                DocumentReference spotRef = db.collection("spots").document(res.getSpot());
                                spotRef.update("reservas", FieldValue.arrayUnion(reservationId));

                                // Update the user's reservas list
                                DocumentReference userRef = db.collection("users").document(userId);
                                userRef.update("reservas", FieldValue.arrayUnion(reservationId));

                                // Perform additional tasks or show success message
                            });
                });


    }
    public void getAllSpotsId(OnSpotIdsLoadedListener listener) {
        List<String> spotListaId = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get a reference to the "spots" collection
        CollectionReference spotsRef = db.collection("spots");

        // Query all documents in the "spots" collection
        spotsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    // Get the spot ID from the document
                    String spotId = documentSnapshot.getId();

                    // Add the spot ID to the list
                    spotListaId.add(spotId);
                }

                // Invoke the listener with the loaded spot IDs
                listener.onSpotIdsLoaded(spotListaId);
            } else {
                // Handle the failure case
                listener.onSpotIdsLoaded(null);
            }
        });
    }
    public void sortList(List<String> spotListaId, OnSpotsLoaderByTypeListener listener) {
        List<CurrentParking.Spota> all = new ArrayList<>();
        int totalQueries = spotListaId.size();
        AtomicInteger completedQueries = new AtomicInteger();

        for (String id : spotListaId) {
            DocumentReference spotRef = FirebaseFirestore.getInstance().collection("spots").document(id);

            spotRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        String type = (String) documentSnapshot.get("type");
                        Object num = documentSnapshot.get("number");
                        all.add(new CurrentParking.Spota(num.toString(), id, type));
                    }
                }

                // Increase the completed queries count
                completedQueries.getAndIncrement();

                // Check if all queries have completed
                if (completedQueries.get() == totalQueries) {
                    // Invoke the listener with the sorted list
                    listener.OnSpotsLoaderByTypeListener(all);
                }
            });
        }
    }
    public void setCurrentReserva(Reserva r){
        currentReserva.setCurrent(r);
        String userId=user.getCurrentUser().getUid();
        reservaRef
                .whereEqualTo("user", userId)
                .whereEqualTo("spot", r.getSpot())
                .whereEqualTo("date", r.getDate())
                .whereEqualTo("startTime", r.getStartTime())
                .whereEqualTo("endTime", r.getEndTime())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            String documentId = documentSnapshot.getId();
                            // Use the document ID as needed
                            currentReserva.setId(documentId);
                            System.out.println("Document ID: " + documentId);
                        }
                    }
                });
    }
    public void updateReserva(String reservaId, String start, String end, OnReservationUpdatedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reservaRef = db.collection("reservations").document(reservaId);

        reservaRef.update("startTime", start)
                .addOnSuccessListener(aVoid -> {
                    reservaRef.update("endTime", end)
                            .addOnSuccessListener(aVoid1 -> {
                                // Reservation updated successfully
                                listener.OnReservationUpdatedListener(true);
                            })
                            .addOnFailureListener(e -> {
                                // Error occurred while updating endTime
                                listener.OnReservationUpdatedListener(false);
                            });
                })
                .addOnFailureListener(e -> {
                    // Error occurred while updating startTime
                    listener.OnReservationUpdatedListener(false);
                });
    }






    public FirebaseFirestore getDb() {
        return db;
    }

    public void setDb(FirebaseFirestore db) {
        this.db = db;
    }

    public CollectionReference getUserRef() {
        return userRef;
    }

    public void setUserRef(CollectionReference userRef) {
        this.userRef = userRef;
    }

    public CollectionReference getReservaRef() {
        return reservaRef;
    }

    public void setReservaRef(CollectionReference reservaRef) {
        this.reservaRef = reservaRef;
    }

    public CollectionReference getSpotRef() {
        return spotRef;
    }

    public void setSpotRef(CollectionReference spotRef) {
        this.spotRef = spotRef;
    }
}
