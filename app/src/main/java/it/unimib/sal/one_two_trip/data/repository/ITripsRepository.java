package it.unimib.sal.one_two_trip.data.repository;

import androidx.lifecycle.MutableLiveData;

import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;

/**
 * Interface for Repositories that manage Trip objects.
 */
public interface ITripsRepository {

    MutableLiveData<Result> fetchTrips(long lastUpdate);

    void updateTrip(Trip trip);

    void deleteTrip(Trip trip);

    void insertTrip(Trip trip);
}
