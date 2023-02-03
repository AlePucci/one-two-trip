package it.unimib.sal.one_two_trip.data.source.geocoding;

import it.unimib.sal.one_two_trip.util.geocodingUtility.GeocodingUtility;

/**
 * Interface to send geocoding data from remote source to {@link GeocodingUtility} class.
 */
public interface GeocodingCallback {

    void onSuccess(String lat, String lon);

    void onFailure(Exception exception);
}
