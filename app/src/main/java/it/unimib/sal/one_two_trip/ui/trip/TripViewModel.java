package it.unimib.sal.one_two_trip.ui.trip;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.repository.ITripsRepository;

public class TripViewModel extends ViewModel {

    private final ITripsRepository tripsRepository;
    private long id;
    private MutableLiveData<Result> tripLiveData;

    public TripViewModel(ITripsRepository tripsRepository) {
        this.tripsRepository = tripsRepository;
    }

    public MutableLiveData<Result> getTrip(long lastUpdate) {
        if (tripLiveData == null) {
            fetchTrip(id, lastUpdate);
        }
        return tripLiveData;
    }

    public void setId(long id) {
        this.id = id;
        tripLiveData = null;
    }

    private void fetchTrip(long id, long lastUpdate) {
        tripLiveData = tripsRepository.fetchTrip(id, lastUpdate);
    }
}
