package it.unimib.sal.one_two_trip.source;

import java.util.List;

import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.model.TripApiResponse;
import it.unimib.sal.one_two_trip.model.TripsApiResponse;

/**
 * Interface to send data from DataSource to Repositories
 * that implement INewsRepositoryWithLiveData interface.
 */
public interface TripCallback {
    void onSuccessFromRemote(TripsApiResponse tripsApiResponse, long lastUpdate);
    void onSuccessFromRemote(TripApiResponse tripApiResponse, long lastUpdate);

    void onFailureFromRemote(Exception exception);

    void onSuccessFromLocal(List<Trip> newsList);
    void onSuccessFromLocal(Trip trip);

    void onFailureFromLocal(Exception exception);
}
