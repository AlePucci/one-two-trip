package it.unimib.sal.one_two_trip.ui.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.sal.one_two_trip.repository.ITripsRepository;

public class TripsViewModelFactory implements ViewModelProvider.Factory {
    private final ITripsRepository tripsRepository;

    public TripsViewModelFactory(ITripsRepository tripsRepository) {
        this.tripsRepository = tripsRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TripsViewModel(tripsRepository);
    }
}
