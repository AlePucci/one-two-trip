package it.unimib.sal.one_two_trip.source;

public abstract class BaseTripsRemoteDataSource {
    protected TripCallback tripCallback;

    public void setTripCallback(TripCallback tripCallback) {
        this.tripCallback = tripCallback;
    }

    public abstract void getTrips();
}
