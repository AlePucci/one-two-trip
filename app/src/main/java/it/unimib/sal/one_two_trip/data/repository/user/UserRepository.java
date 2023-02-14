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

    /**
     * Method to sign in a user.
     *
     * @param email    email of the user
     * @param password password of the user
     * @return a {@link MutableLiveData MutableLiveData} object that contains a {@link Result Result} object.
     */


    @Override
    public MutableLiveData<Result> getUser(String email, String password) {
        signIn(email, password);
        return this.userMutableLiveData;
    }

    /**
     * Method to sign up a user.
     *
     * @param email    email of the user
     * @param password password of the user
     * @param name     name of the user
     * @param surname  surname of the user
     * @return a {@link MutableLiveData MutableLiveData} object that contains a {@link Result Result} object.
     */
    @Override
    public MutableLiveData<Result> getUser(String email, String password, String name,
                                           String surname) {
        signUp(email, password, name, surname);
        return this.userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUser(String id) {
        userDataRemoteDataSource.getUserData(id);
        return this.userMutableLiveData;
    }

    /**
     * Method to sign up/sign in a user using Google One-Tap Sign-In.
     *
     * @param idToken idToken of the user (provided by Google)
     * @return a {@link MutableLiveData MutableLiveData} object that contains a {@link Result Result} object.
     */
    @Override
    public MutableLiveData<Result> getGoogleUser(String idToken) {
        signInWithGoogle(idToken);
        return this.userMutableLiveData;
    }

    /**
     * Method to reset the password of a user.
     *
     * @param email email of the user to reset the password
     * @return a {@link MutableLiveData MutableLiveData} object that contains a {@link Result Result} object.
     */
    @Override
    public MutableLiveData<Result> resetPassword(String email) {
        this.userRemoteDataSource.resetPassword(email);
        return this.passwordResetMutableLiveData;
    }

    /**
     * Method to logout a user.
     *
     * @return a {@link MutableLiveData MutableLiveData} object that contains a {@link Result Result} object.
     */
    @Override
    public MutableLiveData<Result> logout() {
        this.userRemoteDataSource.logout();
        return this.userMutableLiveData;
    }

    /**
     * Method to get the logged user.
     *
     * @return a {@link Person Person} object that represents the logged user.
     */
    @Override
    public Person getLoggedUser() {
        return this.userRemoteDataSource.getLoggedUser();
    }

    @Override
    public void onSuccessLogout() {
        this.tripsLocalDataSource.deleteAllTrips();
    }

    public MutableLiveData<Result> updateUserData(Person p) {
        this.userDataRemoteDataSource.updateUserData(p);
        this.userRemoteDataSource.updateProfile(p);
        return this.userMutableLiveData;
    }

    /**
     * Method to sign up a user.
     *
     * @param email    email of the user
     * @param password password of the user
     * @param name     name of the user
     * @param surname  surname of the user
     */
    @Override
    public void signUp(String email, String password, String name, String surname) {
        this.userRemoteDataSource.signUp(email, password, name, surname);
    }

    /**
     * Method to sign in a user.
     *
     * @param email    email of the user
     * @param password password of the user
     */
    @Override
    public void signIn(String email, String password) {
        this.userRemoteDataSource.signIn(email, password);
    }

    /**
     * Method to sign in a user using Google One-Tap Sign-In.
     *
     * @param token idToken of the user (provided by Google)
     */
    @Override
    public void signInWithGoogle(String token) {
        this.userRemoteDataSource.signInWithGoogle(token);
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
}
