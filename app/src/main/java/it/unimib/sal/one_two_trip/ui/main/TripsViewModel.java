package it.unimib.sal.one_two_trip.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

import it.unimib.sal.one_two_trip.data.database.model.Activity;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.database.model.Trip;
import it.unimib.sal.one_two_trip.data.repository.trips.ITripsRepository;

/**
 * ViewModel to manage the list of Trips.
 */
public class TripsViewModel extends ViewModel {

    private final ITripsRepository tripsRepository;
    private MutableLiveData<Result> tripListLiveData;

    public TripsViewModel(ITripsRepository tripsRepository) {
        super();
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
    private void fetchTrips(long lastUpdate) {
        this.tripListLiveData = this.tripsRepository.fetchTrips(lastUpdate);
    }

    /**
     * It uses the Repository to download the trip list
     * and to associate it with the LiveData object.
     */
    public void refreshTrips() {
        this.tripListLiveData = this.tripsRepository.refreshTrips();
    }


    /**
     * Updates the trip. For example an activity is added or trip title changed.
     *
     * @param trip The trip to be updated.
     */
    public void updateTrip(HashMap<String, Object> trip, String tripId) {
        this.tripsRepository.updateTrip(trip, tripId);
    }

    public void updateActivity(HashMap<String, Object> trip, String tripId, String activityId) {
        this.tripsRepository.updateActivity(trip, tripId, activityId);
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

    public void insertActivity(Activity activity, Trip trip) {
        this.tripsRepository.insertActivity(activity, trip);
    }

    public void deleteActivity(Activity activity, Trip trip) {
        this.tripsRepository.deleteActivity(activity, trip);
    }
}
