package it.unimib.sal.one_two_trip.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.sal.one_two_trip.data.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;

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
     * @param lastUpdate The last update time read from the SharedPreferences.
     * @return the LiveData object associated with the trip list.
     */
    public MutableLiveData<Result> getTrips(long lastUpdate) {
        if (this.tripListLiveData == null) {
            this.fetchTrips(lastUpdate);
        }
        return this.tripListLiveData;
    }

    /**
     * It uses the Repository to download the trip list
     * and to associate it with the LiveData object.
     *
     * @param lastUpdate The last update time read from the SharedPreferences.
     */
    public void fetchTrips(long lastUpdate) {
        this.tripListLiveData = this.tripsRepository.fetchTrips(lastUpdate);
    }

    /**
     * Updates the trip. For example an activity is added or trip title changed.
     *
     * @param trip The trip to be updated.
     */
    public void updateTrip(Trip trip) {
        this.tripsRepository.updateTrip(trip);
    }

    /**
     * Deletes the trip.
     *
     * @param trip The trip to be deleted.
     */
    public void deleteTrip(Trip trip) {
        this.tripsRepository.deleteTrip(trip);
    }

    /**
     * Inserts a new trip.
     *
     * @param trip The trip to be inserted
     */
    public void insertTrip(Trip trip) {
        this.tripsRepository.insertTrip(trip);
    }
}
