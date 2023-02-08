package it.unimib.sal.one_two_trip.ui.welcome;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.sal.one_two_trip.data.repository.user.IUserRepository;


/**
 * Custom ViewModelProvider to be able to have a custom constructor
 * for the {@link UserViewModel} class.
 */
public class UserViewModelFactory implements ViewModelProvider.Factory {

    private final IUserRepository userRepository;

    public UserViewModelFactory(IUserRepository userRepository) {
        super();
        this.userRepository = userRepository;
    }

    @SuppressWarnings("unchecked") // This is safe because of the type parameter.
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new UserViewModel(this.userRepository);
    }
}
