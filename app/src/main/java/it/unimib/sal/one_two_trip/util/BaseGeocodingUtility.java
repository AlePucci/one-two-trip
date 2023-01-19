package it.unimib.sal.one_two_trip.util;

import java.io.IOException;

public abstract class BaseGeocodingUtility {

    protected GeocodingUtilityCallback geocodingUtilityCallback;

    public void setGeocodingUtilityCallback(GeocodingUtilityCallback geocodingUtilityCallback) {
        this.geocodingUtilityCallback = geocodingUtilityCallback;
    }

    public abstract void search(String query, int limit) throws IOException;
}
