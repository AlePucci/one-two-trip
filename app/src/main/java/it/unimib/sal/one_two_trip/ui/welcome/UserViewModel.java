package it.unimib.sal.one_two_trip.ui.welcome;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.sal.one_two_trip.data.database.model.Person;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.repository.user.IUserRepository;

/**
 * ViewModel to manage the list of Person objects.
 */
public class UserViewModel extends ViewModel {

    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private boolean authenticationError;

    public UserViewModel(IUserRepository userRepository) {
        super();
        this.userRepository = userRepository;
        this.authenticationError = false;
    }

    /**
     * Method to get the user data from the repository.
     *
     * @param email    email of the user
     * @param password password of the user
     * @return the LiveData object associated with the user data.
     */
    public MutableLiveData<Result> getUserMutableLiveData(String email, String password) {
        if (this.userMutableLiveData == null) {
            getUserData(email, password);
        }
        return this.userMutableLiveData;
    }

    /**
     * Method to get the user data from the repository.
     *
     * @param email    email of the user
     * @param password password of the user
     * @param name     name of the user
     * @param surname  surname of the user
     * @return the LiveData object associated with the user data.
     */
    public MutableLiveData<Result> getUserMutableLiveData(String email, String password,
                                                          String name, String surname) {
        if (this.userMutableLiveData == null) {
            getUserData(email, password, name, surname);
        }
        return this.userMutableLiveData;
    }

    /**
     * Method to get the user data from the repository using the Google token.
     *
     * @param token Google token
     * @return the LiveData object associated with the user data.
     */
    public MutableLiveData<Result> getGoogleUserMutableLiveData(String token) {
        if (this.userMutableLiveData == null) {
            getUserData(token);
        }
        return this.userMutableLiveData;
    }

    /**
     * Method to get the logged user.
     *
     * @return the logged user object.
     */
    public Person getLoggedUser() {
        return this.userRepository.getLoggedUser();
    }

    /**
     * Method to logout the user.
     *
     * @return the LiveData object associated with the user data.
     */
    public MutableLiveData<Result> logout() {
        if (this.userMutableLiveData == null) {
            this.userMutableLiveData = this.userRepository.logout();
        } else {
            this.userRepository.logout();
        }

        return this.userMutableLiveData;
    }

    /**
     * Method to get user data in the repository, updating it.
     *
     * @param email    email of the user
     * @param password password of the user
     */
    public void getUser(String email, String password) {
        this.userRepository.getUser(email, password);
    }

    /**
     * Method to get the user data in the repository, updating it.
     *
     * @param email    email of the user
     * @param password password of the user
     * @param name     name of the user
     * @param surname  surname of the user
     */
    public void getUser(String email, String password, String name, String surname) {
        this.userRepository.getUser(email, password, name, surname);
    }

    /**
     * Get authentication error state.
     *
     * @return true if there is an authentication error, false otherwise.
     */
    public boolean isAuthenticationError() {
        return this.authenticationError;
    }

    /**
     * Set authentication error state.
     *
     * @param authenticationError true if there is an authentication error, false otherwise.
     */
    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }

    /**
     * Method to get the user data from the repository, updating the userMutableLiveData object.
     *
     * @param email    email of the user
     * @param password password of the user
     */
    private void getUserData(String email, String password) {
        this.userMutableLiveData = this.userRepository.getUser(email, password);
    }

    /**
     * Method to get the user data from the repository, updating the userMutableLiveData object.
     *
     * @param email    email of the user
     * @param password password of the user
     * @param name     name of the user
     * @param surname  surname of the user
     */
    private void getUserData(String email, String password, String name, String surname) {
        this.userMutableLiveData = this.userRepository.getUser(email, password, name, surname);
    }

    /**
     * Method to get the user data from the repository, updating the userMutableLiveData object.
     *
     * @param token Google token
     */
    private void getUserData(String token) {
        this.userMutableLiveData = this.userRepository.getGoogleUser(token);
    }

    /**
     * Method to update the user data.
     *
     * @param p new user data
     * @return the LiveData object associated with the user data.
     */
    public MutableLiveData<Result> updateUserData(Person p) {
        this.userMutableLiveData = this.userRepository.updateUserData(p);

        return this.userMutableLiveData;
    }

    /**
     * Method to get the user data from the repository.
     *
     * @param id id of the user
     * @return the LiveData object associated with the user data.
     */
    public MutableLiveData<Result> getUser(String id) {
        this.userMutableLiveData = this.userRepository.getUser(id);

        return this.userMutableLiveData;
    }

    /**
     * Method to reset the password of the user.
     *
     * @param email email of the user
     * @return the LiveData object associated with the user data.
     */
    public MutableLiveData<Result> resetPassword(String email) {
        return this.userRepository.resetPassword(email);
    }
}
