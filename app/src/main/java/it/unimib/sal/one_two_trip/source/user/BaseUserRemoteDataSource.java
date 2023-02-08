package it.unimib.sal.one_two_trip.source.user;

public abstract class BaseUserRemoteDataSource {
    //protected UserCallback userCallback;

    //public void setUserCallback(UserCallback userCallback) { this.userCallback = userCallback; }

    public abstract void getUser(long id);
}
