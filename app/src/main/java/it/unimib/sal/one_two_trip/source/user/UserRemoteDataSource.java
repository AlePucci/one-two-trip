package it.unimib.sal.one_two_trip.source.user;

import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_REALTIME_DATABASE;
import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_TRIPS_COLLECTION;
import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_USER_COLLECTION;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import it.unimib.sal.one_two_trip.model.Person;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.model.TripsApiResponse;

public class UserRemoteDataSource extends BaseUserRemoteDataSource{

    private final DatabaseReference databaseReference;
    private String idToken;

    public UserRemoteDataSource(String idToken) {
        super();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.idToken = idToken;
    }



    @Override
    public void getUser(long id) {
        databaseReference.child(FIREBASE_USER_COLLECTION).child(idToken).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d("A", "Error getting data", task.getException());
            }
            else {
                Log.d("A", "Successful read: " + task.getResult().getValue());

                DataSnapshot ds = task.getResult();
                Person person = ds.getValue(Person.class);

                /*tripCallback.onSuccessFromRemote(new UserApiResponse("ok", tripList.size(),
                        tripList), System.currentTimeMillis());*/
            }
        });
    }
}
