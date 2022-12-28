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
    private final String idToken;

    public TripsRemoteDataSource(String idToken) {
        super();
        this.idToken = idToken;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        this.databaseReference = firebaseDatabase.getReference().getRef();

        addTripListener();
    }

    private void addTripListener() {
        ValueEventListener tripListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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

        databaseReference.child(FIREBASE_USER_COLLECTION).child(idToken)
                .child(FIREBASE_TRIPS_COLLECTION).addValueEventListener(tripListener);
    }

    @Override
    public void getTrips() {
        databaseReference.child(FIREBASE_USER_COLLECTION).child(idToken)
                .child(FIREBASE_TRIPS_COLLECTION).get().addOnCompleteListener(task -> {
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

    }

    @Override
    public void insertTrips(List<Trip> tripList) {

    }
}
