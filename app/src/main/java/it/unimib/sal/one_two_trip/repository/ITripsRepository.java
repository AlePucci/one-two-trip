package it.unimib.sal.one_two_trip.repository;

import it.unimib.sal.one_two_trip.model.Trip;

public interface ITripsRepository {

    void fetchTrips(long lastUpdate);

    void updateTrip(Trip trip);
}
