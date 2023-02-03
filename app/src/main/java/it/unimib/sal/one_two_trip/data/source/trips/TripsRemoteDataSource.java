package it.unimib.sal.one_two_trip.data.source.trips;

import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_REALTIME_DATABASE;
import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_TRIPS_COLLECTION;
import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_USER_COLLECTION;
import static it.unimib.sal.one_two_trip.util.Constants.STATUS_OK;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.unimib.sal.one_two_trip.data.database.model.Trip;
import it.unimib.sal.one_two_trip.data.database.model.response.TripsApiResponse;

/**
 * Class to get Trips from a remote source using Firebase Realtime Database.
 */
public class TripsRemoteDataSource extends BaseTripsRemoteDataSource {

    private final DatabaseReference tripsCollectionReference;

    public TripsRemoteDataSource(String idToken) {
        super();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        DatabaseReference databaseReference = firebaseDatabase.getReference().getRef();
        this.tripsCollectionReference = databaseReference.child(FIREBASE_USER_COLLECTION)
                .child(idToken).child(FIREBASE_TRIPS_COLLECTION);

        this.addTripListener();
    }

    private void addTripListener() {
        ValueEventListener tripListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Trip> tripList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Trip trip = ds.getValue(Trip.class);
                    tripList.add(trip);
                }

                tripCallback.onSuccessFromRemote(new TripsApiResponse(STATUS_OK, tripList.size(),
                        tripList), System.currentTimeMillis());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tripCallback.onFailureFromRemote(error.toException());
            }
        };

        this.tripsCollectionReference.addValueEventListener(tripListener);
    }

    /**
     * Get all trips from Firebase Realtime Database.
     */
    @Override
    public void getTrips() {
        this.tripsCollectionReference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                tripCallback.onFailureFromRemote(task.getException());
            } else {
                List<Trip> tripList = new ArrayList<>();
                for (DataSnapshot ds : task.getResult().getChildren()) {
                    Trip trip = ds.getValue(Trip.class);
                    tripList.add(trip);
                }

                tripCallback.onSuccessFromRemote(new TripsApiResponse(STATUS_OK,
                        tripList.size(), tripList), System.currentTimeMillis());
            }
        });
    }

    /**
     * Update a trip in Firebase Realtime Database.
     */
    @Override
    public void updateTrip(@NonNull Trip trip) {
        this.tripsCollectionReference.child(trip.getId()).setValue(trip);
    }

    /**
     * Insert a trip in Firebase Realtime Database.
     */
    @Override
    public void insertTrip(@NonNull Trip trip) {
        this.tripsCollectionReference.child(trip.getId()).setValue(trip);
    }

    /**
     * Delete a trip in Firebase Realtime Database.
     */
    @Override
    public void deleteTrip(@NonNull Trip trip) {
        this.tripsCollectionReference.child(trip.getId()).removeValue();
    }
}
