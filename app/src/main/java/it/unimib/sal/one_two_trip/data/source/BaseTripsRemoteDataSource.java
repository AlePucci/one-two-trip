package it.unimib.sal.one_two_trip.data.source;

import it.unimib.sal.one_two_trip.model.Trip;

/**
 * Base class to get Trips from a remote source.
 */
public abstract class BaseTripsRemoteDataSource {

    protected TripCallback tripCallback;

    public void setTripCallback(TripCallback tripCallback) {
        this.tripCallback = tripCallback;
    }

    public abstract void getTrips();

    public abstract void updateTrip(Trip trip);

    public abstract void insertTrip(Trip trip);

    public abstract void deleteTrip(Trip trip);
}
