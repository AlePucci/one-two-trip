package it.unimib.sal.one_two_trip.repository;

import static it.unimib.sal.one_two_trip.util.Constants.TRIPS_API_TEST_JSON_FILE;

import android.app.Application;

import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.database.ITripsDAO;
import it.unimib.sal.one_two_trip.database.TripsRoomDatabase;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.model.TripsApiResponse;
import it.unimib.sal.one_two_trip.util.ResponseCallback;
import it.unimib.sal.one_two_trip.util.ServiceLocator;

public class TripsMockRepository implements ITripsRepository {
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (tripsApiResponse != null) {
            saveDataInDatabase(tripsApiResponse.getTrips());
        } else {
            responseCallback.onFailure(application.getString(R.string.error_json_parsing));
        }
    }

    @Override
    public void updateTrip(Trip trip) {
        /* TO DO */
    }

    public TripsApiResponse parseJSON(String fileName) throws IOException {
        InputStream inputStream = application.getAssets().open(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create().fromJson(bufferedReader, TripsApiResponse.class);
    }

    /**
     * Saves trips into the local database.
     *
     * @param tripList list of trips to save
     */
    private void saveDataInDatabase(List<Trip> tripList) {
        TripsRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Trip> allTrips = tripsDAO.getAll();
            for (Trip trip : allTrips) {
                if (tripList.contains(trip)) {
                    tripList.set(tripList.indexOf(trip), trip);
                }
            }

            List<Long> insertedTripsIds = tripsDAO.insertTripList(tripList);
            for (int i = 0; i < tripList.size(); i++) {
                tripList.get(i).setId(insertedTripsIds.get(i));
            }

            responseCallback.onSuccess(tripList, System.currentTimeMillis());
        });
    }

    /**
     * Gets trips from the local database.
     * The method is executed in a Runnable because the database access
     * cannot been executed in the main thread.
     */
    private void readDataFromDatabase(long lastUpdate) {
        TripsRoomDatabase.databaseWriteExecutor.execute(() -> responseCallback.onSuccess(tripsDAO.getAll(), lastUpdate));
    }
}
