package it.unimib.sal.one_two_trip.util.geocoding;

import android.app.Application;

import java.io.IOException;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.source.geocoding.GeocodingCallback;
import it.unimib.sal.one_two_trip.data.source.geocoding.GeocodingRemoteDataSource;

/**
 * Utility class to get the geocoding data.
 */
public class GeocodingUtility extends BaseGeocodingUtility {

    private final Application application;
    private final GeocodingRemoteDataSource geocodingRemoteDataSource;

    public GeocodingUtility(Application application) {
        this.application = application;
        this.geocodingRemoteDataSource = new GeocodingRemoteDataSource(this.application);
    }

    @Override
    public void search(String query, int limit) {
        GeoRunnable runnable = new GeoRunnable(query, limit);
        new Thread(runnable).start();
    }

    /**
     * Runnable class to perform the geocoding search in another thread.
     */
    public class GeoRunnable implements Runnable, GeocodingCallback {

        private final String query;
        private final int limit;

        public GeoRunnable(String query, int limit) {
            this.query = query;
            this.limit = limit;
            geocodingRemoteDataSource.setGeocodingCallback(this);

        }

        @Override
        public void run() {
            try {
                geocodingRemoteDataSource.search(query, limit);
            } catch (IOException e) {
                geocodingUtilityCallback.onGeocodingFailure(
                        new Exception(application.getString(R.string.unexpected_error)));
            }
        }

        @Override
        public void onSuccess(String lat, String lon) {
            geocodingUtilityCallback.onGeocodingSuccess(lat, lon);
        }

        @Override
        public void onFailure(Exception exception) {
            geocodingUtilityCallback.onGeocodingFailure(exception);
        }
    }
}
