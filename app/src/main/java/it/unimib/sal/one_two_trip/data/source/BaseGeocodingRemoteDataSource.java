package it.unimib.sal.one_two_trip.data.source;

import java.io.IOException;

public abstract class BaseGeocodingRemoteDataSource {

    protected GeocodingCallback geocodingCallback;

    public void setGeocodingCallback(GeocodingCallback geocodingCallback) {
        this.geocodingCallback = geocodingCallback;
    }

    public abstract void search(String query, int limit) throws IOException;
}
