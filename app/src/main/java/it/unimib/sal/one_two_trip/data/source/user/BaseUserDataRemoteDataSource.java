package it.unimib.sal.one_two_trip.data.source.user;

import it.unimib.sal.one_two_trip.data.database.model.User;
import it.unimib.sal.one_two_trip.data.repository.user.UserResponseCallback;

public abstract class BaseUserDataRemoteDataSource {

    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract void saveUserData(User user);
}
