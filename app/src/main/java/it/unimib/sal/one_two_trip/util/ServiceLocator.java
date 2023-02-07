package it.unimib.sal.one_two_trip.util;

import static it.unimib.sal.one_two_trip.util.Constants.GEOCODING_BASE_URL;
import static it.unimib.sal.one_two_trip.util.Constants.PHOTOS_BASE_URL;

import android.app.Application;

import it.unimib.sal.one_two_trip.data.database.TripsRoomDatabase;
import it.unimib.sal.one_two_trip.data.repository.trips.ITripsRepository;
import it.unimib.sal.one_two_trip.data.repository.trips.TripsRepository;
import it.unimib.sal.one_two_trip.data.repository.user.IUserRepository;
import it.unimib.sal.one_two_trip.data.repository.user.UserRepository;
import it.unimib.sal.one_two_trip.data.source.trips.BaseTripsLocalDataSource;
import it.unimib.sal.one_two_trip.data.source.trips.BaseTripsRemoteDataSource;
import it.unimib.sal.one_two_trip.data.source.trips.TripsLocalDataSource;
import it.unimib.sal.one_two_trip.data.source.trips.TripsRemoteDataSource;
import it.unimib.sal.one_two_trip.service.GeocodingApiService;
import it.unimib.sal.one_two_trip.service.PictureApiService;
import it.unimib.sal.one_two_trip.data.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.sal.one_two_trip.data.source.user.BaseUserDataRemoteDataSource;
import it.unimib.sal.one_two_trip.data.source.user.UserAuthenticationRemoteDataSource;
import it.unimib.sal.one_two_trip.data.source.user.UserDataRemoteDataSource;
import it.unimib.sal.one_two_trip.ui.welcome.DataEncryptionUtil;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Registry to provide the dependencies for the classes used in the application.
 */
public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {
    }

    /**
     * Creates an instance of ServiceLocator class.
     *
     * @return An instance of ServiceLocator.
     */
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

    /**
     * Returns an instance of TripsRoomDatabase class to manage Room database.
     *
     * @param application application context
     * @return An instance of TripsRoomDatabase.
     */
    public TripsRoomDatabase getTripsDAO(Application application) {
        return TripsRoomDatabase.getDatabase(application);
    }

    /**
     * Returns an instance of PictureApiService class using Retrofit.
     *
     * @return an instance of PictureApiService.
     */
    public PictureApiService getPictureApiService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(PHOTOS_BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(PictureApiService.class);
    }

    /**
     * Returns an instance of GeocodingApiService class using Retrofit.
     *
     * @return An instance of GeocodingApiService.
     */
    public GeocodingApiService getGeocodingApiService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(GEOCODING_BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(GeocodingApiService.class);
    }

    /**
     * Returns an instance of ITripsRepository.
     *
     * @param application application context
     * @return An instance of ITripsRepository.
     */
    public ITripsRepository getTripsRepository(Application application) {
        BaseTripsRemoteDataSource tripsRemoteDataSource;
        BaseTripsLocalDataSource tripsLocalDataSource;
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);

        tripsRemoteDataSource = new TripsRemoteDataSource("1"); // TODO - get user id from shared preferences
        tripsLocalDataSource = new TripsLocalDataSource(getTripsDAO(application),
                sharedPreferencesUtil);

        return new TripsRepository(tripsRemoteDataSource, tripsLocalDataSource,
                sharedPreferencesUtil);
    }

    public IUserRepository getUserRepository(Application application) {
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);

        BaseUserAuthenticationRemoteDataSource userRemoteAuthenticationDataSource =
                new UserAuthenticationRemoteDataSource();

        BaseUserDataRemoteDataSource userDataRemoteDataSource =
                new UserDataRemoteDataSource(sharedPreferencesUtil);
        DataEncryptionUtil dataEncryptionUtil = new DataEncryptionUtil(application);


        return new UserRepository(userRemoteAuthenticationDataSource, userDataRemoteDataSource);

    }
}


