package it.unimib.sal.one_two_trip.data.source;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import java.util.List;

import it.unimib.sal.one_two_trip.data.database.ITripsDAO;
import it.unimib.sal.one_two_trip.data.database.TripsRoomDatabase;
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
     * Gets the trips from the local database.
     * The method is executed with an ExecutorService defined in TripsRoomDatabase class
     * because the database access cannot been executed in the main thread.
     */
    @Override
    public void getTrips() {
        TripsRoomDatabase.databaseWriteExecutor.execute(
                () -> tripCallback.onSuccessFromLocal(this.tripsDAO.getAll()));
    }

    @Override
    public void updateTrip(Trip trip) {
        TripsRoomDatabase.databaseWriteExecutor.execute(
                () -> this.tripsDAO.updateTrip(trip));
    }

    @Override
    public void insertTrips(List<Trip> tripList) {
        TripsRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Trip> allTrips = this.tripsDAO.getAll();

            if (tripList != null) {
                for (Trip trip : allTrips) {
                    if (tripList.contains(trip)) {
                        tripList.set(tripList.indexOf(trip), trip);
                    }
                }

                List<Long> insertedTripIds = this.tripsDAO.insertTripList(tripList);
                for (int i = 0; i < tripList.size(); i++) {
                    tripList.get(i).setId(insertedTripIds.get(i));
                }

                this.sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME,
                        LAST_UPDATE, String.valueOf(System.currentTimeMillis()));

                tripCallback.onSuccessFromLocal(tripList);
            }
        });
    }
}
