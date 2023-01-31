package it.unimib.sal.one_two_trip.data.source;

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

import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.model.TripsApiResponse;

public class TripsRemoteDataSource extends BaseTripsRemoteDataSource {

    private final DatabaseReference databaseReference;
    private final DatabaseReference tripsCollectionReference;

    public TripsRemoteDataSource(String idToken) {
        super();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        this.databaseReference = firebaseDatabase.getReference().getRef();
        this.tripsCollectionReference = this.databaseReference.child(FIREBASE_USER_COLLECTION)
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

    @Override
    public void updateTrip(Trip trip) {
        // TODO fix path (trip id)
        this.tripsCollectionReference.child(String.valueOf(trip.getId() - 1)).setValue(trip);
    }

    @Override
    public void insertTrip(Trip trip) {
        this.tripsCollectionReference.setValue(trip);
    }

    @Override
    public void deleteTrip(Trip trip) {
        // TODO fix path (trip id)
        this.tripsCollectionReference.child(String.valueOf(trip.getId() - 1)).removeValue();
    }
}
