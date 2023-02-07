package it.unimib.sal.one_two_trip.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import java.util.Set;

import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.database.model.User;


public interface IUserRepository {
        MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered);
        MutableLiveData<Result> getGoogleUser(String idToken);
        MutableLiveData<Result> getUserFavoriteNews(String idToken);
        MutableLiveData<Result> getUserPreferences(String idToken);
        MutableLiveData<Result> logout();
        User getLoggedUser();
        void signUp(String email, String password);
        void signIn(String email, String password);
        void signInWithGoogle(String token);
        void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken);
    }
