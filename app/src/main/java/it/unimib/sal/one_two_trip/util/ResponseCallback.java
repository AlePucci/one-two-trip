package it.unimib.sal.one_two_trip.util;

import java.util.List;

import it.unimib.sal.one_two_trip.model.Trip;

/**
 * Interface to send data from Repositories to Activity/Fragment.
 */
public interface ResponseCallback {
    void onSuccess(List<Trip> tripsList, long lastUpdate);
    void onFailure(String errorMessage);
}
