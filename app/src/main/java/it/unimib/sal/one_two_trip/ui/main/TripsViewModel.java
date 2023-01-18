package it.unimib.sal.one_two_trip.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.data.repository.ITripsRepository;

public class TripsViewModel extends ViewModel {

    private final ITripsRepository tripsRepository;
    private MutableLiveData<Result> tripListLiveData;

    public TripsViewModel(ITripsRepository tripsRepository) {
        this.tripsRepository = tripsRepository;
    }

    /**
     * Returns the LiveData object associated with the
     * trip list to the Fragment/Activity.
     *
     * @return The LiveData object associated with the trip list.
     */
    public MutableLiveData<Result> getTrips(long lastUpdate) {
        if (this.tripListLiveData == null) {
            this.fetchTrips(lastUpdate);
        }
        return this.tripListLiveData;
    }

    /**
     * Updates the trips status.
     *
     * @param trip The trip to be updated.
     */
    public void updateTrip(Trip trip) {
        this.tripsRepository.updateTrip(trip);
    }

    /**
     * It uses the Repository to download the trip list
     * and to associate it with the LiveData object.
     */
    public void fetchTrips(long lastUpdate) {
        this.tripListLiveData = this.tripsRepository.fetchTrips(lastUpdate);
    }

    public void deleteTrip(Trip trip) {
        tripsRepository.deleteTrip(trip);
    }
}
