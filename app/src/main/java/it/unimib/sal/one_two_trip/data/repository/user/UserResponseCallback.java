package it.unimib.sal.one_two_trip.data.repository.user;

import it.unimib.sal.one_two_trip.data.database.model.Person;

/**
 * Interface to send data from data sources to Repositories
 * that implement {@link IUserRepository IUserRepository} interface.
 */
public interface UserResponseCallback {

    void onSuccessFromAuthentication(Person person);

    void onFailureFromAuthentication(String message);

    void onSuccessFromRemoteDatabase(Person person);

    void onFailureFromRemoteDatabase(String message);

    void onSuccessFromPasswordReset();

    void onFailureFromPasswordReset(String message);

    void onSuccessLogout();
}
