package it.unimib.sal.one_two_trip.ui.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.sal.one_two_trip.data.repository.ITripsRepository;

/**
 * Custom ViewModelProvider to be able to have a custom constructor
 * for the {@link TripsViewModel} class.
 */
public class TripsViewModelFactory implements ViewModelProvider.Factory {

    private final ITripsRepository tripsRepository;

    public TripsViewModelFactory(ITripsRepository tripsRepository) {
        super();
        this.tripsRepository = tripsRepository;
    }

    @SuppressWarnings("unchecked") // This is safe because of the type parameter.
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TripsViewModel(this.tripsRepository);
    }
}
