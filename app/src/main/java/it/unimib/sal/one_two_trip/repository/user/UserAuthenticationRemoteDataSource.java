package it.unimib.sal.one_two_trip.repository.user;

import static it.unimib.sal.one_two_trip.util.Constants.INVALID_CREDENTIALS_ERROR;
import static it.unimib.sal.one_two_trip.util.Constants.INVALID_USER_ERROR;
import static it.unimib.sal.one_two_trip.util.Constants.UNEXPECTED_ERROR;
import static it.unimib.sal.one_two_trip.util.Constants.USER_COLLISION_ERROR;
import static it.unimib.sal.one_two_trip.util.Constants.WEAK_PASSWORD_ERROR;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;


import it.unimib.sal.one_two_trip.model.User;
import it.unimib.sal.one_two_trip.source.user.BaseUserAuthenticationRemoteDataSource;

public class UserAuthenticationRemoteDataSource extends BaseUserAuthenticationRemoteDataSource{
 private static final String TAG = UserAuthenticationRemoteDataSource.class.getSimpleName();
 private final FirebaseAuth firebaseAuth;


 public UserAuthenticationRemoteDataSource() {
  firebaseAuth = FirebaseAuth.getInstance();
 }

 @Override
 public User getLoggedUser() {
  FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
  if (firebaseUser == null) {
   return null;
  } else {
   return new User(firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getUid());
  }
 }

 @Override
 public void logout() {
  FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
   @Override
   public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
    if(firebaseAuth.getCurrentUser() == null){
     firebaseAuth.removeAuthStateListener(this);
     Log.d(TAG, "User logged out");
     userResponseCallback.onSuccessLogout();
    }
   }
  };
  firebaseAuth.addAuthStateListener(authStateListener);
  firebaseAuth.signOut();

 }

 @Override
 public void signUp(String email, String password) {
  firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
   if (task.isSuccessful()) {
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    if (firebaseUser != null) {
     userResponseCallback.onSuccessFromAuthentication(new User(
             firebaseUser.getDisplayName(), email, firebaseUser.getUid()));
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

 }

 @Override
 public void signInWithGoogle(String idToken) {

 }

 @Override
 public void saveUserData(User user) {

 }

 private String getErrorMessage(Exception exception) {
  if (exception instanceof FirebaseAuthWeakPasswordException) {
   return WEAK_PASSWORD_ERROR;
  } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
   return INVALID_CREDENTIALS_ERROR;
  } else if (exception instanceof FirebaseAuthInvalidUserException) {
   return INVALID_USER_ERROR;
  } else if (exception instanceof FirebaseAuthUserCollisionException) {
   return USER_COLLISION_ERROR;
  }
  return UNEXPECTED_ERROR;
 }
}




