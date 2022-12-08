package it.unimib.sal.one_two_trip.ui.trip;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.sal.one_two_trip.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModel;

public class TripViewModelFactory implements ViewModelProvider.Factory {
    private final ITripsRepository tripsRepository;

    public TripViewModelFactory(ITripsRepository tripsRepository) {
        this.tripsRepository = tripsRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TripViewModel(tripsRepository);
    }
}
