package it.unimib.sal.one_two_trip.util;

import android.app.Application;

import it.unimib.sal.one_two_trip.database.TripsRoomDatabase;
import it.unimib.sal.one_two_trip.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.repository.TripsRepository;
import it.unimib.sal.one_two_trip.source.BaseTripsLocalDataSource;
import it.unimib.sal.one_two_trip.source.BaseTripsRemoteDataSource;
import it.unimib.sal.one_two_trip.source.TripsLocalDataSource;
import it.unimib.sal.one_two_trip.source.TripsMockRemoteDataSource;

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

    public TripsRoomDatabase getTripsDAO(Application application) {
        return TripsRoomDatabase.getDatabase(application);
    }

    /**
     * Returns an instance of ITripsRepository.
     *
     * @param application Param for accessing the global application state.
     * @return An instance of ITripsRepository.
     */
    public ITripsRepository getTripsRepository(Application application) {
        BaseTripsRemoteDataSource newsRemoteDataSource;
        BaseTripsLocalDataSource newsLocalDataSource;
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);

        newsRemoteDataSource = new TripsMockRemoteDataSource(new JSONParserUtil(application));

        newsLocalDataSource = new TripsLocalDataSource(getTripsDAO(application),
                sharedPreferencesUtil);

        return new TripsRepository(newsRemoteDataSource, newsLocalDataSource, sharedPreferencesUtil);
    }
}
