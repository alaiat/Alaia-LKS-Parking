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
import com.lksnext.parkingalaiat.domain.CurrentBooking;
import com.lksnext.parkingalaiat.domain.Booking;
import com.lksnext.parkingalaiat.domain.UserContext;
import com.lksnext.parkingalaiat.interfaces.OnBookingUpdatedListener;
import com.lksnext.parkingalaiat.interfaces.OnBookingsListener;
import com.lksnext.parkingalaiat.interfaces.OnSpotsIdLoadedListener;
import com.lksnext.parkingalaiat.interfaces.OnSpotsLoaderByTypeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FirebaseManager {
    private static final String EXTRA_LOGIN_SUCCESS =
            "com.lksnext.parkingalaiat.EXTRA_LOGIN_SUCCESS";

    private static FirebaseManager instance;
    private FirebaseFirestore db;
    private CollectionReference userRef;
    private CollectionReference reservRef;
    private CollectionReference spotRef;
    private UserContext user;
    private CurrentBooking currentBooking;


    private FirebaseManager(){
        db = FirebaseFirestore.getInstance();
        reservRef = db.collection("reservations");
        userRef = db.collection("users");
        spotRef = db.collection("spots");

        user = UserContext.getInstance();
        currentBooking = CurrentBooking.getInstance();

    }
    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public void getAllBookingsForUser(OnBookingsListener listener) {
        String userId = user.getCurrentUser().getUid();
        Query query = reservRef.whereEqualTo("user", userId);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Booking> reservations = new ArrayList<>();

            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                String date = documentSnapshot.getString("date");
                String endT = documentSnapshot.getString("endTime");
                String startT = documentSnapshot.getString("startTime");
                String spot = documentSnapshot.getString("spot");

                Booking reservation = new Booking(spot, startT, endT, date, userId);
                user.addMinutesToDay(date, startT, endT);
                reservations.add(reservation);
            }

            // Invoke the listener with the loaded reservations
            listener.onBookingsLoaded(reservations);
        }).addOnFailureListener(e -> {
            // Invoke the listener with null to indicate failure
            listener.onBookingsLoaded(null);
        });
    }
    public void deleteBookingByAllData(Booking r) {
        String userId = user.getCurrentUser().getUid();

        reservRef
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
                                    System.out.println("Booking deleted: " + documentSnapshot.getId());
                                })
                                .addOnFailureListener(e -> {
                                    // Error occurred while deleting the reservation
                                    System.out.println("Failed to delete booking: " + documentSnapshot.getId());
                                    System.out.println(e.toString());

                                    // Handle the exception
                                });

                        DocumentReference userRef =
                                db.collection("users").document(UserContext.getInstance().getCurrentUser().getUid());

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

                        // String must be "reservas" since it's the original database name in firebase
                        spotRef.update("reservas", FieldValue.arrayRemove(documentSnapshot.getId()))
                                .addOnSuccessListener(aVoid2 -> {
                                    // Reservation ID removed from spot document
                                    System.out.println("Booking ID removed from spot: " );
                                })
                                .addOnFailureListener(e -> {
                                    // Error occurred while updating spot document
                                    System.out.println(e.toString());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Error occurred while querying reservations
                    System.out.println("Failed to query reservations: " + e.toString());
                });
    }
    public void addBookingInFirebase(Booking res) {
        String userId = user.getCurrentUser().getUid();

        reservRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        db.collection("reservations").document();
                    }

                    reservRef.add(res)
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
    public void getAllSpotsId(OnSpotsIdLoadedListener listener) {
        List<String> spotListId = new ArrayList<>();
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
                    spotListId.add(spotId);
                }

                // Invoke the listener with the loaded spot IDs
                listener.onSpotIdsLoaded(spotListId);
            } else {
                // Handle the failure case
                listener.onSpotIdsLoaded(null);
            }
        });
    }
    public void sortList(List<String> spotListId, OnSpotsLoaderByTypeListener listener) {
        List<CurrentParking.Area> all = new ArrayList<>();
        int totalQueries = spotListId.size();
        AtomicInteger completedQueries = new AtomicInteger();

        for (String id : spotListId) {
            DocumentReference spotRef = FirebaseFirestore.getInstance().collection("spots").document(id);

            spotRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        String type = (String) documentSnapshot.get("type");
                        Object num = documentSnapshot.get("number");
                        CurrentParking.Area s = new CurrentParking.Area(num.toString(), id, type);
                        all.add(s);
                        System.out.println(s + "\n");
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
    public void setCurrentBooking(Booking r){
        currentBooking.setCurrent(r);
        String userId = user.getCurrentUser().getUid();

        reservRef
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
                            currentBooking.setId(documentId);
                            System.out.println("Document ID: " + documentId);
                        }
                    }
                });
    }
    public void updateBooking(String reservaId, String start, String end, OnBookingUpdatedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reservaRef = db.collection("reservations").document(reservaId);

        reservaRef.update("startTime", start)
                .addOnSuccessListener(aVoid -> {
                    reservaRef.update("endTime", end)
                            .addOnSuccessListener(aVoid1 -> {
                                // Reservation updated successfully
                                listener.OnBookingUpdatedListener(true);
                            })
                            .addOnFailureListener(e -> {
                                // Error occurred while updating endTime
                                listener.OnBookingUpdatedListener(false);
                            });
                })
                .addOnFailureListener(e -> {
                    // Error occurred while updating startTime
                    listener.OnBookingUpdatedListener(false);
                });
    }
    public void getBookingsForSpot(String spotId, OnBookingsListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reservationsRef = db.collection("reservations");

        Query query = reservationsRef.whereEqualTo("spot", spotId);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Booking> reservations = new ArrayList<>();
                QuerySnapshot querySnapshot = task.getResult();

                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    // Get the reservation data from the document
                    String date = documentSnapshot.getString("date");
                    String endT = documentSnapshot.getString("endTime");
                    String startT = documentSnapshot.getString("startTime");
                    String userId = documentSnapshot.getString("user");

                    // Create a Reserva object and add it to the list
                    Booking reservation = new Booking(spotId, startT, endT, date, userId);
                    reservations.add(reservation);
                }

                // Pass the reservations list to the listener
                listener.onBookingsLoaded(reservations);
            } else {
                // Handle the failure case
                Exception exception = task.getException();
                // Handle the exception
            }
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
        return reservRef;
    }

    public void setReservaRef(CollectionReference reservaRef) {
        this.reservRef = reservaRef;
    }

    public CollectionReference getSpotRef() {
        return spotRef;
    }

    public void setSpotRef(CollectionReference spotRef) {
        this.spotRef = spotRef;
    }
}
