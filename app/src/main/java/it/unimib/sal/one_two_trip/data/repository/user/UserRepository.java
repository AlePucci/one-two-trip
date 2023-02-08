package it.unimib.sal.one_two_trip.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.database.model.User;
import it.unimib.sal.one_two_trip.data.source.trips.BaseTripsLocalDataSource;
import it.unimib.sal.one_two_trip.data.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.sal.one_two_trip.data.source.user.BaseUserDataRemoteDataSource;

public class UserRepository implements IUserRepository, UserResponseCallback {

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final BaseTripsLocalDataSource tripsLocalDataSource;
    private final MutableLiveData<Result> userMutableLiveData;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource,
                          BaseTripsLocalDataSource tripsLocalDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.tripsLocalDataSource = tripsLocalDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
    }

    @Override
    public MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered) {
        if (isUserRegistered) {
            signIn(email, password);
        } else {
            signUp(email, password);
        }
        return this.userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getGoogleUser(String idToken) {
        signInWithGoogle(idToken);
        return this.userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> resetPassword(String email) {
        this.userRemoteDataSource.resetPassword(email);
        return this.userMutableLiveData;
    }

    @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
            this.userDataRemoteDataSource.saveUserData(user);
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        Result.Error result = new Result.Error(message);
        this.userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(user);
        this.userMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromRemoteDatabase(String message) {
        Result.Error result = new Result.Error(message);
        this.userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromPasswordReset() {
        this.userMutableLiveData.postValue(new Result.PasswordResetSuccess(true));
    }

    @Override
    public void onFailureFromPasswordReset(String message) {
        Result.Error result = new Result.Error(message);
        this.userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessLogout() {
        this.tripsLocalDataSource.deleteAllTrips();
    }

    @Override
    public void signUp(String email, String password) {
        this.userRemoteDataSource.signUp(email, password);
    }

    @Override
    public void signIn(String email, String password) {
        this.userRemoteDataSource.signIn(email, password);
    }

    @Override
    public void signInWithGoogle(String token) {
        this.userRemoteDataSource.signInWithGoogle(token);
    }

    @Override
    public User getLoggedUser() {
        return this.userRemoteDataSource.getLoggedUser();
    }

    @Override
    public MutableLiveData<Result> logout() {
        this.userRemoteDataSource.logout();
        return this.userMutableLiveData;
    }
}
