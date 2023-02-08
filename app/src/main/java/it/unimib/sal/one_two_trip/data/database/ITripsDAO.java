package it.unimib.sal.one_two_trip.data.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import it.unimib.sal.one_two_trip.data.database.model.Trip;

/**
 * Data Access Object (DAO) that provides methods that can be used to query,
 * update, insert, and delete data in the database.
 * <a href="https://developer.android.com/training/data-storage/room/accessing-data">See here</a>
 */
@Dao
public interface ITripsDAO {
    
    @Query("SELECT * FROM trip")
    List<Trip> getAll();

    @Query("SELECT * FROM trip WHERE id = :id")
    Trip getTrip(String id);

    @Query("SELECT * FROM trip WHERE tripOwner = :tripOwner")
    List<Trip> getTripsByOwner(String tripOwner);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTripList(List<Trip> tripList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrip(Trip trip);

    @Update
    void updateTrip(Trip trip);

    @Insert
    void insertAll(Trip... trips);

    @Delete
    void delete(Trip trip);

    @Query("DELETE FROM trip")
    int deleteAll();

    @Delete
    void deleteAllWithoutQuery(Trip... trips);
}
