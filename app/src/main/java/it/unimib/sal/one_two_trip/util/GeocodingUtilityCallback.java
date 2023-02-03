package it.unimib.sal.one_two_trip.util;

/**
 * Interface to send geocoding utility data from {@link GeocodingUtility} to the activities.
 */
public interface GeocodingUtilityCallback {

    void onGeocodingSuccess(String lat, String lon);

    void onGeocodingFailure(Exception exception);
}
