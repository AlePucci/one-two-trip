package it.unimib.sal.one_two_trip.data.source;

public interface GeocodingCallback {

    void onSuccess(String lat, String lon);

    void onFailure(Exception exception);
}
