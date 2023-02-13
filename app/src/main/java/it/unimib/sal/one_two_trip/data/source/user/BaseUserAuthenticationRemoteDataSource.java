package it.unimib.sal.one_two_trip.data.source.user;

import it.unimib.sal.one_two_trip.data.database.model.Person;
import it.unimib.sal.one_two_trip.data.repository.user.UserResponseCallback;

/**
 * Base class to perform User Authentication.
 */
public abstract class BaseUserAuthenticationRemoteDataSource {

    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract Person getLoggedUser();

    public abstract void logout();

    public abstract void signUp(String email, String password, String name, String surname);

    public abstract void signIn(String email, String password);

    public abstract void signInWithGoogle(String idToken);

    public abstract void resetPassword(String email);

    public abstract void updateProfile(Person p);
}
