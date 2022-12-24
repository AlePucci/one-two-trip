package it.unimib.sal.one_two_trip.util;

import android.app.Application;

import it.unimib.sal.one_two_trip.data.database.TripsRoomDatabase;
import it.unimib.sal.one_two_trip.data.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.data.repository.TripsRepository;
import it.unimib.sal.one_two_trip.service.PictureApiService;
import it.unimib.sal.one_two_trip.data.source.BaseTripsLocalDataSource;
import it.unimib.sal.one_two_trip.data.source.BaseTripsRemoteDataSource;
import it.unimib.sal.one_two_trip.data.source.PhotoRemoteDataSource;
import it.unimib.sal.one_two_trip.data.source.TripsLocalDataSource;
import it.unimib.sal.one_two_trip.data.source.TripsRemoteDataSource;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

        newsRemoteDataSource = new TripsRemoteDataSource("1");
        newsLocalDataSource = new TripsLocalDataSource(getTripsDAO(application),
                sharedPreferencesUtil);

        return new TripsRepository(newsRemoteDataSource, newsLocalDataSource, sharedPreferencesUtil);
    }

    /**
     * Returns an instance of PictureApiService class using Retrofit.
     *
     * @return an instance of PictureApiService.
     */
    public PictureApiService getPictureApiService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.PHOTOS_BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(PictureApiService.class);
    }

    /**
     * Returns an instance of PhotoRemoteDataSource.
     *
     * @return an instance of PhotoRemoteDataSource.
     */
    public PhotoRemoteDataSource getPhotoRemoteDataSource() {
        return new PhotoRemoteDataSource();
    }
}
