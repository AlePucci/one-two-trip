package it.unimib.sal.one_two_trip.util;

import static it.unimib.sal.one_two_trip.util.Constants.GEOCODING_BASE_URL;
import static it.unimib.sal.one_two_trip.util.Constants.PHOTOS_BASE_URL;

import android.app.Application;
import android.content.Context;

import it.unimib.sal.one_two_trip.data.database.TripsRoomDatabase;
import it.unimib.sal.one_two_trip.data.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.data.repository.TripsRepository;
import it.unimib.sal.one_two_trip.data.source.BaseTripsLocalDataSource;
import it.unimib.sal.one_two_trip.data.source.BaseTripsRemoteDataSource;
import it.unimib.sal.one_two_trip.data.source.GeocodingRemoteDataSource;
import it.unimib.sal.one_two_trip.data.source.PhotoRemoteDataSource;
import it.unimib.sal.one_two_trip.data.source.TripsLocalDataSource;
import it.unimib.sal.one_two_trip.data.source.TripsRemoteDataSource;
import it.unimib.sal.one_two_trip.data.source.storage.RemoteStorage;
import it.unimib.sal.one_two_trip.service.GeocodingApiService;
import it.unimib.sal.one_two_trip.service.PictureApiService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Registry to provide the dependencies for the classes
 * used in the application.
 */
public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {
    }

    /**
     * Returns an instance of ServiceLocator class.
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
     * Returns an instance of ITripsRepository.
     *
     * @param application Param for accessing the global application state.
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
     * Returns an instance of PhotoRemoteDataSource.
     *
     * @return an instance of PhotoRemoteDataSource.
     */
    public PhotoRemoteDataSource getPhotoRemoteDataSource(Context context) {
        return new PhotoRemoteDataSource(context);
    }

    public GeocodingApiService getGeocodingApiService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(GEOCODING_BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(GeocodingApiService.class);
    }

    public GeocodingRemoteDataSource getGeocodingRemoteDataSource(Context context) {
        return new GeocodingRemoteDataSource(context);
    }

    public RemoteStorage getRemoteStorage(Application application) {
        return new RemoteStorage(application);
    }
}
