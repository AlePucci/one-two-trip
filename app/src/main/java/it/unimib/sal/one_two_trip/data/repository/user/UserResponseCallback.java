package it.unimib.sal.one_two_trip.data.repository.user;

import it.unimib.sal.one_two_trip.data.database.model.User;

public interface UserResponseCallback {

    void onSuccessFromAuthentication(User user);

    void onFailureFromAuthentication(String message);

    void onSuccessFromRemoteDatabase(User user);

    void onFailureFromRemoteDatabase(String message);

    void onSuccessLogout();
}
