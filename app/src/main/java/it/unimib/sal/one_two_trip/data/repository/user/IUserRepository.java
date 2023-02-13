package it.unimib.sal.one_two_trip.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import it.unimib.sal.one_two_trip.data.database.model.Person;
import it.unimib.sal.one_two_trip.data.database.model.Result;

/**
 * Interface for Repositories that manage Person objects.
 */
import it.unimib.sal.one_two_trip.data.database.model.User;

public interface IUserRepository {

    MutableLiveData<Result> getUser(String email, String password);

    MutableLiveData<Result> getUser(String email, String password, String name, String surname);

    MutableLiveData<Result> getGoogleUser(String idToken);

    MutableLiveData<Result> resetPassword(String email);

    MutableLiveData<Result> logout();

    Person getLoggedUser();

    void signUp(String email, String password, String name, String surname);

    void signIn(String email, String password);

    void signInWithGoogle(String token);

    MutableLiveData<Result> changeEmail(String email);

    MutableLiveData<Result> updateUserData(Person p);

    MutableLiveData<Result> deleteUser();

    public MutableLiveData<Result> getUser(String id);
}
