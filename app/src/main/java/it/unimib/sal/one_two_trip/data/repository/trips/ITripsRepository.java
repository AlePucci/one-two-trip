package it.unimib.sal.one_two_trip.data.repository.trips;

import androidx.lifecycle.MutableLiveData;

import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.database.model.Trip;

/**
 * Interface for Repositories that manage Trip objects.
 */
public interface ITripsRepository {

    MutableLiveData<Result> fetchTrips(long lastUpdate);

    MutableLiveData<Result> refreshTrips();

    void updateTrip(Trip trip);

    void deleteTrip(Trip trip);

    void insertTrip(Trip trip);
}
