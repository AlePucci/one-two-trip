package it.unimib.sal.one_two_trip.source;

import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_REALTIME_DATABASE;
import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_TRIPS_COLLECTION;
import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_USER_COLLECTION;

import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.model.TripsApiResponse;

public class TripsRemoteDataSource extends BaseTripsRemoteDataSource {

    private final DatabaseReference databaseReference;
    private String idToken;

    public TripsRemoteDataSource(String idToken) {
        super();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.idToken = idToken;
    }


    @Override
    public void getTrips() {
        databaseReference.child(FIREBASE_USER_COLLECTION).child(idToken)
                .child(FIREBASE_TRIPS_COLLECTION).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d("A", "Error getting data", task.getException());
            }
            else {
                Log.d("A", "Successful read: " + task.getResult().getValue());

                List<Trip> tripList = new ArrayList<>();
                for(DataSnapshot ds : task.getResult().getChildren()) {
                    Trip trip = ds.getValue(Trip.class);
                    tripList.add(trip);
                }

                tripCallback.onSuccessFromRemote(new TripsApiResponse("ok", tripList.size(),
                        tripList), System.currentTimeMillis());
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
