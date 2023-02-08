package it.unimib.sal.one_two_trip.data.database;

import static it.unimib.sal.one_two_trip.util.Constants.TRIPS_DATABASE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.TRIPS_DATABASE_VERSION;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unimib.sal.one_two_trip.data.database.model.Trip;
import it.unimib.sal.one_two_trip.util.Converters;

/**
 * Main access point for the underlying connection to the local database.
 * <a href="https://developer.android.com/reference/kotlin/androidx/room/Database">See here</a>
 */
@Database(entities = {Trip.class}, version = TRIPS_DATABASE_VERSION, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class TripsRoomDatabase extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile TripsRoomDatabase INSTANCE;

    public static TripsRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TripsRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TripsRoomDatabase.class, TRIPS_DATABASE_NAME).build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract ITripsDAO tripsDAO();
}
