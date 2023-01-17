package it.unimib.sal.one_two_trip.source;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import it.unimib.sal.one_two_trip.database.ITripsDAO;
import it.unimib.sal.one_two_trip.database.TripsRoomDatabase;
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;

public class TripsLocalDataSource extends BaseTripsLocalDataSource {

    private final ITripsDAO tripsDAO;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    public TripsLocalDataSource(TripsRoomDatabase tripsRoomDatabase,
                                SharedPreferencesUtil sharedPreferencesUtil) {
        this.tripsDAO = tripsRoomDatabase.tripsDAO();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    /**
     * Gets the news from the local database.
     * The method is executed with an ExecutorService defined in NewsRoomDatabase class
     * because the database access cannot been executed in the main thread.
     */
    @Override
    public void getTrips() {
        TripsRoomDatabase.databaseWriteExecutor.execute(
                () -> tripCallback.onSuccessFromLocal(tripsDAO.getAll()));
    }

    @Override
    public void updateTrip(Trip trip) {
        TripsRoomDatabase.databaseWriteExecutor.execute(() -> {
            tripsDAO.updateTrip(trip);
        });
    }

    @Override
    public void insertTrips(List<Trip> tripList) {
        TripsRoomDatabase.databaseWriteExecutor.execute(() -> {
            // Reads the news from the database
            List<Trip> allTrips = tripsDAO.getAll();

            if (tripList != null) {

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
                List<Long> insertedNewsIds = tripsDAO.insertTripList(tripList);
                for (int i = 0; i < tripList.size(); i++) {
                    // Adds the primary key to the corresponding object News just downloaded so that
                    // if the user marks the news as favorite (and vice-versa), we can use its id
                    // to know which news in the database must be marked as favorite/not favorite
                    tripList.get(i).setId(insertedNewsIds.get(i));
                }

                sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE,
                        String.valueOf(System.currentTimeMillis()));

                tripCallback.onSuccessFromLocal(tripList);
            }
        });
    }

    @Override
    public void deleteTrip(Trip trip) {
        TripsRoomDatabase.databaseWriteExecutor.execute(() -> {
            tripsDAO.delete(trip);
            tripCallback.onSuccessFromLocal(Collections.singletonList(trip));
        });
    }
}
