package it.unimib.sal.one_two_trip.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.unimib.sal.one_two_trip.model.Trip;

@Dao
public interface ITripsDAO {
    @Query("SELECT * FROM trip")
    List<Trip> getAll();

    @Query("SELECT * FROM trip WHERE id = :id")
    Trip getTrip(long id);

    @Query("SELECT * FROM trip WHERE tripOwner = :tripOwner")
    List<Trip> getTripsByOwner(String tripOwner);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertTripList(List<Trip> tripList);

    @Insert
    void insertAll(Trip... trips);

    @Delete
    void delete(Trip trip);

    @Query("DELETE FROM trip")
    void deleteAll();

    @Delete
    void deleteAllWithoutQuery(Trip... trips);
}
