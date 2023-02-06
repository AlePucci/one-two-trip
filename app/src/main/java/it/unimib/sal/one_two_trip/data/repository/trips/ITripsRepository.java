package it.unimib.sal.one_two_trip.data.repository.trips;

import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;

import it.unimib.sal.one_two_trip.data.database.model.Activity;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.database.model.Trip;

/**
 * Interface for Repositories that manage Trip objects.
 */
public interface ITripsRepository {

    MutableLiveData<Result> fetchTrips(long lastUpdate);

    MutableLiveData<Result> refreshTrips();

    void updateTrip(HashMap<String, Object> trip, String tripId);

    void updateActivity(HashMap<String, Object> trip, String tripId, String activityId);

    void deleteTrip(Trip trip);

    void deleteActivity(Activity activity, Trip trip);

    void insertTrip(Trip trip);

    void insertActivity(Activity activity, Trip trip);
}
