package it.unimib.sal.one_two_trip.data.source.user;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_USER_COLLECTION;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import it.unimib.sal.one_two_trip.data.database.model.Person;

/**
 * Class to get Person object from a remote source using Firebase Cloud Firestore
 */
import it.unimib.sal.one_two_trip.data.database.model.User;


public class UserDataRemoteDataSource extends BaseUserDataRemoteDataSource {

    private final CollectionReference usersReference;

    public UserDataRemoteDataSource() {
        super();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.usersReference = db.collection(FIREBASE_USER_COLLECTION);
    }

    @Override
    public void saveUserData(@NonNull Person person) {
        String idToken = person.getId();
        this.usersReference.document(idToken).get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            userResponseCallback.onSuccessFromRemoteDatabase(person);
                        } else {
                            usersReference.document(idToken).set(person)
                                    .addOnSuccessListener(aVoid ->
                                            userResponseCallback.onSuccessFromRemoteDatabase(person))
                                    .addOnFailureListener(e ->
                                            userResponseCallback.onFailureFromRemoteDatabase(
                                                    e.getLocalizedMessage()));
                        }
                    } else {
                        usersReference.document(idToken).set(person)
                                .addOnSuccessListener(aVoid ->
                                        userResponseCallback.onSuccessFromRemoteDatabase(person))
                                .addOnFailureListener(e ->
                                        userResponseCallback.onFailureFromRemoteDatabase(
                                                e.getLocalizedMessage()));
                        }
                });
    }

    @Override
    public void updateUserData(@NonNull Person person) {
        String idToken = person.getId();
        this.usersReference.document(idToken).set(person).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        Log.d("passed here", "ciao");
                        userResponseCallback.onSuccessFromRemoteDatabase(person);
                    } else {
                        Log.d("passed here", "fail");
                        userResponseCallback.onFailureFromRemoteDatabase(task.getException().toString());
                    }
                });
    }

    public void getUserData(String id){
        this.usersReference.document(id).get().addOnCompleteListener(
                task -> {
                    if(task.isSuccessful()){
                        Person p = task.getResult().toObject(Person.class);
                        userResponseCallback.onSuccessFromRemoteDatabase(p);
                    }
                    else {
                        userResponseCallback.onFailureFromRemoteDatabase(task.getException().toString());
                    }
                });
    }
}