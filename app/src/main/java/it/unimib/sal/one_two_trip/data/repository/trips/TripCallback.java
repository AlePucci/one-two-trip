package it.unimib.sal.one_two_trip.data.repository.trips;

import java.util.List;

import it.unimib.sal.one_two_trip.data.database.model.Trip;
import it.unimib.sal.one_two_trip.data.database.model.response.TripsApiResponse;

/**
 * Interface to send data from data sources to Repositories
 * that implement {@link ITripsRepository ITripsRepository} interface.
 */
public interface TripCallback {

    void onSuccessFromRemote(TripsApiResponse tripsApiResponse, long lastUpdate);

    void onFailureFromRemote(Exception exception);

    void onSuccessFromLocal(List<Trip> tripList);
}
