package it.unimib.sal.one_two_trip.util;

import android.app.Application;

import it.unimib.sal.one_two_trip.database.TripsRoomDatabase;

public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {
    }

    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized (ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    /*
     * It creates an instance of NewsApiService using Retrofit.
     *
     * @return an instance of NewsApiService.
     *
     * public TripsApiService getNewsApiService() {
     * return null;
     * }
     */

    public TripsRoomDatabase getTripsDAO(Application application) {
        return TripsRoomDatabase.getDatabase(application);
    }
}
