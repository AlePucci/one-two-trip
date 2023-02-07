package it.unimib.sal.one_two_trip.data.source.trips;

import java.util.List;

import it.unimib.sal.one_two_trip.data.database.model.Trip;

/**
 * Base class to get Trips from a local source.
 */
public abstract class BaseTripsLocalDataSource {

    protected TripCallback tripCallback;

    public void setTripCallback(TripCallback tripCallback) {
        this.tripCallback = tripCallback;
    }

    public abstract void getTrips();

    public abstract void updateTrip(Trip trip);

    public abstract void insertTrips(List<Trip> tripList);

    public abstract void deleteTrip(Trip trip);
}
