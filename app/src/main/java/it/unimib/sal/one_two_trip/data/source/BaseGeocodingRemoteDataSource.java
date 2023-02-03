package it.unimib.sal.one_two_trip.data.source;

import java.io.IOException;

/**
 * Base class to get geocoding data from a remote source.
 */
public abstract class BaseGeocodingRemoteDataSource {

    protected GeocodingCallback geocodingCallback;

    public void setGeocodingCallback(GeocodingCallback geocodingCallback) {
        this.geocodingCallback = geocodingCallback;
    }

    public abstract void search(String query, int limit) throws IOException;
}
