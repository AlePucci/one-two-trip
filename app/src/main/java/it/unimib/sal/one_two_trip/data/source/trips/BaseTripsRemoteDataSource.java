package it.unimib.sal.one_two_trip.data.source.trips;

import java.util.HashMap;

import it.unimib.sal.one_two_trip.data.database.model.Activity;
import it.unimib.sal.one_two_trip.data.database.model.Trip;
import it.unimib.sal.one_two_trip.data.repository.trips.TripCallback;

/**
 * Base class to get Trips from a remote source.
 */
public abstract class BaseTripsRemoteDataSource {

    protected TripCallback tripCallback;

    public void setTripCallback(TripCallback tripCallback) {
        this.tripCallback = tripCallback;
    }

    public abstract void getTrips();

    public abstract void updateTrip(HashMap<String, Object> trip, String tripId);

    public abstract void updateActivity(HashMap<String, Object> activity, String tripId, String activityId);

    public abstract void insertActivity(Activity activity, Trip trip);

    public abstract void insertTrip(Trip trip);

    public abstract void deleteTrip(Trip trip);

    public abstract void deleteActivity(Activity activity, Trip trip);
}
