package it.unimib.sal.one_two_trip.data.source.user;

import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_USERS_COLLECTION;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.unimib.sal.one_two_trip.data.database.model.User;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;

public class UserDataRemoteDataSource extends BaseUserDataRemoteDataSource {
    private static final String TAG = UserDataRemoteDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    public UserDataRemoteDataSource(SharedPreferencesUtil sharedPreferencesUtil) {
        databaseReference = FirebaseDatabase.getInstance().getReference().getRef();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }


    @Override
    public void saveUserData(User user) {
        Log.d(TAG, "Saving user data in Firebase Realtime Database");
        databaseReference.child(FIREBASE_USERS_COLLECTION + "-temp").child(user.getIdToken()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        Log.d(TAG, "User already present in Firebase Realtime Database");
                        userResponseCallback.onSuccessFromRemoteDatabase(user);
                    } else {
                        Log.d(TAG, "User not present in Firebase Realtime Database");
                        databaseReference.child(FIREBASE_USERS_COLLECTION + "-temp").child(user.getIdToken()).setValue(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        userResponseCallback.onSuccessFromRemoteDatabase(user);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        userResponseCallback.onFailureFromRemoteDatabase(e.getLocalizedMessage());
                                    }
                                });
                    }
                } else {
                    Log.d(TAG, "User not present in Firebase Realtime Database");
                    databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken()).setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    userResponseCallback.onFailureFromRemoteDatabase(e.getLocalizedMessage());
                                }
                            });
                }
            }

        });
    }
}