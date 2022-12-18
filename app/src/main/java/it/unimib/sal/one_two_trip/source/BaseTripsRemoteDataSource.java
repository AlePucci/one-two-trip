package it.unimib.sal.one_two_trip.source;

import java.util.List;

import it.unimib.sal.one_two_trip.model.Trip;

public abstract class BaseTripsRemoteDataSource {
    protected TripCallback tripCallback;

    public void setTripCallback(TripCallback tripCallback) {
        this.tripCallback = tripCallback;
    }

    public abstract void getTrips();

    public abstract void updateTrip(Trip trip);

    public abstract void insertTrips(List<Trip> tripList);
}
