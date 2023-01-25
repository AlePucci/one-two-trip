package it.unimib.sal.one_two_trip.util;


public abstract class BaseGeocodingUtility {

    protected GeocodingUtilityCallback geocodingUtilityCallback;

    public void setGeocodingUtilityCallback(GeocodingUtilityCallback geocodingUtilityCallback) {
        this.geocodingUtilityCallback = geocodingUtilityCallback;
    }

    public abstract void search(String query, int limit);
}
