package it.unimib.sal.one_two_trip.ui.welcome;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.database.model.User;
import it.unimib.sal.one_two_trip.data.repository.user.IUserRepository;

public class UserViewModel extends ViewModel {

    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private boolean authenticationError;

    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
        this.authenticationError = false;
    }

    public MutableLiveData<Result> getUserMutableLiveData(String email, String password,
                                                          boolean isUserRegistered) {
        if (this.userMutableLiveData == null) {
            getUserData(email, password, isUserRegistered);
        }
        return this.userMutableLiveData;
    }

    public MutableLiveData<Result> getGoogleUserMutableLiveData(String token) {
        if (this.userMutableLiveData == null) {
            getUserData(token);
        }
        return this.userMutableLiveData;
    }

    public User getLoggedUser() {
        return this.userRepository.getLoggedUser();
    }

    public MutableLiveData<Result> logout() {
        if (this.userMutableLiveData == null) {
            this.userMutableLiveData = this.userRepository.logout();
        } else {
            this.userRepository.logout();
        }

        return this.userMutableLiveData;
    }

    public void getUser(String email, String password, boolean isUserRegistered) {
        this.userRepository.getUser(email, password, isUserRegistered);
    }

    public boolean isAuthenticationError() {
        return this.authenticationError;
    }

    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }

    private void getUserData(String email, String password, boolean isUserRegistered) {
        this.userMutableLiveData = this.userRepository.getUser(email, password, isUserRegistered);
    }

    private void getUserData(String token) {
        this.userMutableLiveData = this.userRepository.getGoogleUser(token);
    }

    public MutableLiveData<Result> resetPassword(String email) {
        this.userMutableLiveData = this.userRepository.resetPassword(email);

        return this.userMutableLiveData;
    }
}