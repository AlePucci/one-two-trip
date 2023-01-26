package it.unimib.sal.one_two_trip.source.user;

import java.util.Set;

import it.unimib.sal.one_two_trip.model.User;
import it.unimib.sal.one_two_trip.repository.user.UserResponseCallback;

public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback){
        this.userResponseCallback = userResponseCallback;
    }

    public abstract void saveUserData(User user);
}
