package it.unimib.sal.one_two_trip.data.source.trips;

import static it.unimib.sal.one_two_trip.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import androidx.annotation.NonNull;

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
            List<Trip> allTrips = this.tripsDAO.getAll();

            if (tripList != null) {
//                for (Trip trip : tripList) {
//                    if (!trip.isParticipating()) {
//                        // or is deleted TODO
//                        this.tripsDAO.delete(trip);
//                    }
//                }
                for (Trip trip : allTrips) {
                    if (tripList.contains(trip)) {
                        tripList.set(tripList.indexOf(trip), trip);
                    }
                }

                this.tripsDAO.insertTripList(tripList);

                this.sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME,
                        LAST_UPDATE, String.valueOf(System.currentTimeMillis()));

                tripCallback.onSuccessFromLocal(tripList);
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
                        dataEncryptionUtil.deleteAll(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ENCRYPTED_DATA_FILE_NAME);
                    }
                }
        );
    }
}
