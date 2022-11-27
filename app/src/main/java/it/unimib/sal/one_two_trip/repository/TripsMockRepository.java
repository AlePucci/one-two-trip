package it.unimib.sal.one_two_trip.repository;

import static it.unimib.sal.one_two_trip.util.Constants.TRIPS_API_TEST_JSON_FILE;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.List;

import it.unimib.sal.one_two_trip.database.ITripsDAO;
import it.unimib.sal.one_two_trip.database.TripsRoomDatabase;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.model.TripsApiResponse;
import it.unimib.sal.one_two_trip.util.ResponseCallback;
import it.unimib.sal.one_two_trip.util.ServiceLocator;

public class TripsMockRepository implements ITripsRepository{
    private final Application application;
    private final ResponseCallback responseCallback;
    private final ITripsDAO tripsDAO;

    public TripsMockRepository(Application application, ResponseCallback responseCallback) {
        this.application = application;
        this.responseCallback = responseCallback;
        TripsRoomDatabase tripsRoomDatabase = ServiceLocator.getInstance().getTripsDAO(application);
        this.tripsDAO = tripsRoomDatabase.tripsDAO();
    }

    @Override
    public void fetchTrips(long lastUpdate) {
        TripsApiResponse tripsApiResponse = null;

        try {
            tripsApiResponse = this.parseJSON(TRIPS_API_TEST_JSON_FILE);
            Log.d("TripsMockRepository", tripsApiResponse.toString());
        }catch (IOException e) {
            e.printStackTrace();
        }

        if (tripsApiResponse != null) {
            saveDataInDatabase(tripsApiResponse.getTrips());
        } else {
            responseCallback.onFailure("test");
        }
    }

    @Override
    public void updateTrip(Trip trip) {

    }

    public TripsApiResponse parseJSON(String fileName) throws IOException {
        InputStream inputStream = application.getAssets().open(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create().fromJson(bufferedReader, TripsApiResponse.class);
    }

    private void saveDataInDatabase(List<Trip> tripList) {
        TripsRoomDatabase.databaseWriteExecutor.execute(() -> {
            // Reads the news from the database
            List<Trip> allTrips = tripsDAO.getAll();

            // Checks if the news just downloaded has already been downloaded earlier
            // in order to preserve the news status (marked as favorite or not)
            for (Trip trip : allTrips) {
                // This check works because News and NewsSource classes have their own
                // implementation of equals(Object) and hashCode() methods
                if (tripList.contains(trip)) {
                    // The primary key and the favorite status is contained only in the News objects
                    // retrieved from the database, and not in the News objects downloaded from the
                    // Web Service. If the same news was already downloaded earlier, the following
                    // line of code replaces the the News object in newsList with the corresponding
                    // News object saved in the database, so that it has the primary key and the
                    // favorite status.
                    tripList.set(tripList.indexOf(trip), trip);
                }
            }

            // Writes the news in the database and gets the associated primary keys
            List<Long> insertedTripsIds = tripsDAO.insertTripList(tripList);
            for (int i = 0; i < tripList.size(); i++) {
                // Adds the primary key to the corresponding object News just downloaded so that
                // if the user marks the news as favorite (and vice-versa), we can use its id
                // to know which news in the database must be marked as favorite/not favorite
                tripList.get(i).setId(insertedTripsIds.get(i));
            }

            responseCallback.onSuccess(tripList, System.currentTimeMillis());
        });
    }

    /**
     * Gets the news from the local database.
     * The method is executed in a Runnable because the database access
     * cannot been executed in the main thread.
     */
    private void readDataFromDatabase(long lastUpdate) {
        TripsRoomDatabase.databaseWriteExecutor.execute(() -> {
            responseCallback.onSuccess(tripsDAO.getAll(), lastUpdate);
        });
    }
}
