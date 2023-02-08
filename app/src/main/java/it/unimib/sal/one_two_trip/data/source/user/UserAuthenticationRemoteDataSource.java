package it.unimib.sal.one_two_trip.data.source.user;

import static it.unimib.sal.one_two_trip.util.Constants.INVALID_CREDENTIALS_ERROR;
import static it.unimib.sal.one_two_trip.util.Constants.UNEXPECTED_ERROR;
import static it.unimib.sal.one_two_trip.util.Constants.USER_COLLISION_ERROR;
import static it.unimib.sal.one_two_trip.util.Constants.WEAK_PASSWORD_ERROR;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import it.unimib.sal.one_two_trip.data.database.model.Person;


public class UserAuthenticationRemoteDataSource extends BaseUserAuthenticationRemoteDataSource {

    private final FirebaseAuth firebaseAuth;

    public UserAuthenticationRemoteDataSource() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public Person getLoggedUser() {
        FirebaseUser firebaseUser = this.firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            return null;
        } else {
            Person person = new Person();
            person.setId(firebaseUser.getUid());
            person.setEmail_address(firebaseUser.getEmail());
            if (firebaseUser.getDisplayName() != null) {
                person.setName(firebaseUser.getDisplayName().trim().split(" ")[0]);
                person.setSurname(firebaseUser.getDisplayName().trim().split(" ")[1]);
            }
            return person;
        }
    }

    @Override
    public void logout() {
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    firebaseAuth.removeAuthStateListener(this);
                    userResponseCallback.onSuccessLogout();
                }
            }
        };

        this.firebaseAuth.addAuthStateListener(authStateListener);
        this.firebaseAuth.signOut();

    }

    @Override
    public void signUp(String email, String password, String name, String surname) {
        this.firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = this.firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Person person = new Person();
                    person.setEmail_address(email);
                    person.setId(firebaseUser.getUid());
                    person.setName(name);
                    person.setSurname(surname);
                    person.setProfile_picture("");
                    firebaseUser.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(name + " " + surname).build());
                    this.userResponseCallback.onSuccessFromAuthentication(person);
                } else {
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            } else {
                userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
            }
        });

    }

    @Override
    public void signIn(String email, String password) {
        this.firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = this.firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Person person = new Person();
                    person.setId(firebaseUser.getUid());
                    person.setEmail_address(email);
                    //TODO FIX THIS
                    if (firebaseUser.getDisplayName() != null) {
                        person.setName(firebaseUser.getDisplayName().trim().split(" ")[0]);
                        person.setSurname(firebaseUser.getDisplayName().trim().split(" ")[1]);
                    }
                    userResponseCallback.onSuccessFromAuthentication(person);
                } else {
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            } else {
                userResponseCallback.onFailureFromRemoteDatabase(getErrorMessage(task.getException()));
            }
        });

    }

    @Override
    public void signInWithGoogle(String idToken) {
        if (idToken != null) {
            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
            this.firebaseAuth.signInWithCredential(firebaseCredential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = this.firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        Person person = new Person();
                        person.setId(firebaseUser.getUid());
                        person.setEmail_address(firebaseUser.getEmail());
                        if (firebaseUser.getDisplayName() != null) {
                            person.setName(firebaseUser.getDisplayName().trim().split(" ")[0]);
                            person.setSurname(firebaseUser.getDisplayName().trim().split(" ")[1]);
                        }
                        userResponseCallback.onSuccessFromAuthentication(person);
                    } else {
                        userResponseCallback.onFailureFromAuthentication(
                                getErrorMessage(task.getException()));
                    }
                } else {
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            });
        }
    }

    @Override
    public void resetPassword(String email) {
        this.firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(
                task -> {
                    if (!task.isSuccessful()) {
                        userResponseCallback.onFailureFromPasswordReset(getErrorMessage(task.getException()));
                    } else {
                        userResponseCallback.onSuccessFromPasswordReset();
                    }
                }
        );
    }

    private String getErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthWeakPasswordException) {
            return WEAK_PASSWORD_ERROR;
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException ||
                exception instanceof FirebaseAuthInvalidUserException) {
            return INVALID_CREDENTIALS_ERROR;
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            return USER_COLLISION_ERROR;
        }
        Log.d("ERROR", exception.getMessage());
        return UNEXPECTED_ERROR;
    }
}
