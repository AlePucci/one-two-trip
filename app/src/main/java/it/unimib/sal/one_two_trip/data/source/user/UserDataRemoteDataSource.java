package it.unimib.sal.one_two_trip.data.source.user;

import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_USERS_COLLECTION;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.unimib.sal.one_two_trip.data.database.model.User;

public class UserDataRemoteDataSource extends BaseUserDataRemoteDataSource {

    private final DatabaseReference databaseReference;

    public UserDataRemoteDataSource() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference().getRef();
    }

    @Override
    public void saveUserData(@NonNull User user) {
        //TODO FIX THIS
        this.databaseReference.child(FIREBASE_USERS_COLLECTION + "-temp")
                .child(user.getIdToken()).get().addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                DataSnapshot dataSnapshot = task.getResult();
                                if (dataSnapshot.exists()) {
                                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                                } else {
                                    databaseReference.child(FIREBASE_USERS_COLLECTION + "-temp")
                                            .child(user.getIdToken()).setValue(user)
                                            .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                                            .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabase(e.getLocalizedMessage()));
                                }
                            } else {
                                databaseReference.child(FIREBASE_USERS_COLLECTION + "-temp")
                                        .child(user.getIdToken()).setValue(user)
                                        .addOnSuccessListener(aVoid -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                                        .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabase(e.getLocalizedMessage()));
                            }
                        });
    }
}