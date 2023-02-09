package it.unimib.sal.one_two_trip.data.source.trips;

import static it.unimib.sal.one_two_trip.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.unimib.sal.one_two_trip.data.database.ITripsDAO;
import it.unimib.sal.one_two_trip.data.database.TripsRoomDatabase;
import it.unimib.sal.one_two_trip.data.database.model.Trip;
import it.unimib.sal.one_two_trip.util.DataEncryptionUtil;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;

/**
 * Class to get Trips from a local source using Room.
 */
public class TripsLocalDataSource extends BaseTripsLocalDataSource {

    private final ITripsDAO tripsDAO;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    private final DataEncryptionUtil dataEncryptionUtil;

    public TripsLocalDataSource(@NonNull TripsRoomDatabase tripsRoomDatabase,
                                SharedPreferencesUtil sharedPreferencesUtil,
                                DataEncryptionUtil dataEncryptionUtil) {
        super();
        this.tripsDAO = tripsRoomDatabase.tripsDAO();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
        this.dataEncryptionUtil = dataEncryptionUtil;
    }

    /**
     * Gets the trips from the local database.
     * The method is executed with an ExecutorService defined in TripsRoomDatabase class
     * because the database access cannot been executed in the main thread.
     */
    @Override
    public void getTrips() {
        TripsRoomDatabase.databaseWriteExecutor.execute(
                () -> tripCallback.onSuccessFromLocal(this.tripsDAO.getAll())
        );
    }

    /**
     * Updates the trip in the local database.
     * The method is executed with an ExecutorService defined in TripsRoomDatabase class
     * because the database access cannot been executed in the main thread.
     */
    @Override
    public void updateTrip(Trip trip) {
        TripsRoomDatabase.databaseWriteExecutor.execute(
                () -> this.tripsDAO.updateTrip(trip)
        );
    }

    /**
     * Insert a list of trips in the local database.
     * The method is executed with an ExecutorService defined in TripsRoomDatabase class
     * because the database access cannot been executed in the main thread.
     */
    @Override
    public void insertTrips(List<Trip> tripList) {
        TripsRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Trip> allTrips = new ArrayList<>(this.tripsDAO.getAll());
            List<Trip> temp = new ArrayList<>(tripList);
            Log.d("TripsLocalDataSource", "insertTrips: " + temp);
            if (temp != null) {
//                for (Trip trip : temp) {
//                    if (!trip.isParticipating()) {
//                        // or is deleted TODO
//                        this.tripsDAO.delete(trip);
//                        return;
//                    }
//                }

                for (Trip trip : temp) {
                    if (allTrips.contains(trip)) {
                        allTrips.set(allTrips.indexOf(trip), trip);
                    }
                    else{
                        allTrips.add(trip);
                    }
                }
                Log.d("TripsLocalDataSource", "allTrips: " + allTrips);

                this.tripsDAO.insertTripList(allTrips);

                this.sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME,
                        LAST_UPDATE, String.valueOf(System.currentTimeMillis()));

                tripCallback.onSuccessFromLocal(allTrips);
            }
        });
    }

    @Override
    public void insertTrip(Trip trip) {
        TripsRoomDatabase.databaseWriteExecutor.execute(() -> {
            Log.d("TripsLocalDataSource", "insertTrip");
            List<Trip> allTrips = new ArrayList<>(this.tripsDAO.getAll());

            if (trip != null) {
                if (!trip.isParticipating()) {
                    // or is deleted TODO
                    this.tripsDAO.delete(trip);
                    tripCallback.onSuccessFromLocal(allTrips);
                    return;
                }

                boolean found = false;
                for (Trip t : allTrips) {
                    if (t.getId().equals(trip.getId())) {
                        Log.d("TripsLocalDataSource", "id: " + trip.getId() + " all trips: " + allTrips.toString());
                        allTrips.set(allTrips.indexOf(t), trip);
                        this.tripsDAO.updateTrip(trip);
                        Log.d("TripsLocalDataSource", "found in db: " + trip.getTitle());
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Log.d("TripsLocalDataSource", "not found in db: " + trip.getTitle());
                    this.tripsDAO.insertTrip(trip);
                    allTrips.add(trip);
                }

                this.sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME,
                        LAST_UPDATE, String.valueOf(System.currentTimeMillis()));
                Log.d("TripsLocalDataSource", allTrips.toString());
                tripCallback.onSuccessFromLocal(allTrips);
            }
        });
    }

    /**
     * Deletes the trip from the local database.
     * The method is executed with an ExecutorService defined in TripsRoomDatabase class
     * because the database access cannot been executed in the main thread.
     */
    @Override
    public void deleteTrip(Trip trip) {
        TripsRoomDatabase.databaseWriteExecutor.execute(
                () -> this.tripsDAO.delete(trip)
        );
    }

    @Override
    public void deleteAllTrips() {
        TripsRoomDatabase.databaseWriteExecutor.execute(
                () -> {
                    int tripsCount = this.tripsDAO.getAll().size();
                    int tripsDeleted = this.tripsDAO.deleteAll();

                    if (tripsCount == tripsDeleted) {
                        sharedPreferencesUtil.deleteAll(SHARED_PREFERENCES_FILE_NAME);
                        dataEncryptionUtil.deleteAll(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME,
                                ENCRYPTED_DATA_FILE_NAME);
                    }
                }
        );
    }
}
