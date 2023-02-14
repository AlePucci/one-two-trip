package it.unimib.sal.one_two_trip.data.source.user;

import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_USER_COLLECTION;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import it.unimib.sal.one_two_trip.data.database.model.Person;

/**
 * Class to get Person object from Firebase Cloud Firestore.
 */
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
                        userResponseCallback.onSuccessFromRemoteDatabase(person);
                    } else {
                        userResponseCallback.onFailureFromRemoteDatabase(Objects.requireNonNull(task.getException())
                                .getLocalizedMessage());
                    }
                });
    }

    @Override
    public void getUserData(String id) {
        this.usersReference.document(id).get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        Person p = task.getResult().toObject(Person.class);
                        userResponseCallback.onSuccessFromRemoteDatabase(p);
                    } else {
                        userResponseCallback.onFailureFromRemoteDatabase(Objects.requireNonNull(task.getException())
                                .getLocalizedMessage());
                    }
                });
    }
}
