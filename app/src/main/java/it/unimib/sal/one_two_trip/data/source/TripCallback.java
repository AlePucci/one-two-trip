package it.unimib.sal.one_two_trip.data.source;

import java.util.List;

import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.model.TripsApiResponse;

/**
 * Interface to send data from DataSource to Repositories
 * that implement
 * {@link it.unimib.sal.one_two_trip.data.repository.ITripsRepository ITripsRepository} interface.
 */
public interface TripCallback {
    void onSuccessFromRemote(TripsApiResponse tripsApiResponse, long lastUpdate);

    void onFailureFromRemote(Exception exception);

    void onSuccessFromLocal(List<Trip> tripList);

    void onFailureFromLocal(Exception exception);
}
