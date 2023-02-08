package it.unimib.sal.one_two_trip.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import it.unimib.sal.one_two_trip.data.database.model.Person;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.source.trips.BaseTripsLocalDataSource;
import it.unimib.sal.one_two_trip.data.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.sal.one_two_trip.data.source.user.BaseUserDataRemoteDataSource;

/**
 * Repository class to get the Person objects from local or from a remote source.
 */
public class UserRepository implements IUserRepository, UserResponseCallback {

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final BaseTripsLocalDataSource tripsLocalDataSource;
    private final MutableLiveData<Result> userMutableLiveData;
    private final MutableLiveData<Result> passwordResetMutableLiveData;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource,
                          BaseTripsLocalDataSource tripsLocalDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.tripsLocalDataSource = tripsLocalDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.passwordResetMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
    }

    @Override
    public MutableLiveData<Result> getUser(String email, String password) {
        signIn(email, password);
        return this.userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUser(String email, String password, String name,
                                           String surname) {
        signUp(email, password, name, surname);
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
        return this.passwordResetMutableLiveData;
    }

    @Override
    public void onSuccessFromAuthentication(Person person) {
        if (person != null) {
            this.userDataRemoteDataSource.saveUserData(person);
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        Result.Error result = new Result.Error(message);
        this.userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(Person person) {
        Result.PersonResponseSuccess result = new Result.PersonResponseSuccess(person);
        this.userMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromRemoteDatabase(String message) {
        Result.Error result = new Result.Error(message);
        this.userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromPasswordReset() {
        this.passwordResetMutableLiveData.postValue(new Result.PasswordResetSuccess(true));
    }

    @Override
    public void onFailureFromPasswordReset(String message) {
        Result.Error result = new Result.Error(message);
        this.passwordResetMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessLogout() {
        this.tripsLocalDataSource.deleteAllTrips();
    }

    @Override
    public void signUp(String email, String password, String name, String surname) {
        this.userRemoteDataSource.signUp(email, password, name, surname);
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
    public Person getLoggedUser() {
        return this.userRemoteDataSource.getLoggedUser();
    }

    @Override
    public MutableLiveData<Result> logout() {
        this.userRemoteDataSource.logout();
        return this.userMutableLiveData;
    }
}
