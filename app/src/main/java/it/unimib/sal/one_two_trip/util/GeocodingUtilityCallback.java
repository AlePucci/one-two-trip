package it.unimib.sal.one_two_trip.util;

public interface GeocodingUtilityCallback {

    void onGeocodingSuccess(String lat, String lon);

    void onGeocodingFailure(Exception exception);
}
