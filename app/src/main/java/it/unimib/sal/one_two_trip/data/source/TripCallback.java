package it.unimib.sal.one_two_trip.data.source;

import java.util.List;

import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.model.TripsApiResponse;

/**
 * Interface to send data from DataSource to Repositories
 * that implement INewsRepositoryWithLiveData interface.
 */
public interface TripCallback {
    void onSuccessFromRemote(TripsApiResponse tripsApiResponse, long lastUpdate);

    void onFailureFromRemote(Exception exception);

    void onSuccessFromLocal(List<Trip> newsList);

    void onFailureFromLocal(Exception exception);
}
