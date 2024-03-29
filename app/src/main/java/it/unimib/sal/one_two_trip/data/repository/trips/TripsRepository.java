package it.unimib.sal.one_two_trip.data.repository.trips;

import static it.unimib.sal.one_two_trip.util.Constants.FRESH_TIMEOUT;
import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.List;

import it.unimib.sal.one_two_trip.data.database.model.Activity;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.database.model.Trip;
import it.unimib.sal.one_two_trip.data.database.model.response.TripsApiResponse;
import it.unimib.sal.one_two_trip.data.database.model.response.TripsResponse;
import it.unimib.sal.one_two_trip.data.source.trips.BaseTripsLocalDataSource;
import it.unimib.sal.one_two_trip.data.source.trips.BaseTripsRemoteDataSource;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;

/**
 * Repository class to get the Trips from local or from a remote source.
 */
public class TripsRepository implements ITripsRepository, TripCallback {

    private final MutableLiveData<Result> allTripsMutableLiveData;
    private final BaseTripsRemoteDataSource tripsRemoteDataSource;
    private final BaseTripsLocalDataSource tripsLocalDataSource;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    public TripsRepository(BaseTripsRemoteDataSource tripsRemoteDataSource,
                           BaseTripsLocalDataSource tripsLocalDataSource,
                           SharedPreferencesUtil sharedPreferencesUtil) {
        this.allTripsMutableLiveData = new MutableLiveData<>();
        this.tripsRemoteDataSource = tripsRemoteDataSource;
        this.tripsLocalDataSource = tripsLocalDataSource;
        this.sharedPreferencesUtil = sharedPreferencesUtil;
        this.tripsRemoteDataSource.setTripCallback(this);
        this.tripsLocalDataSource.setTripCallback(this);
    }

    /**
     * Fetch the trips from the local source if the last update is less than FRESH_TIMEOUT.
     *
     * @param lastUpdate the last update time
     * @return the list of trips as a MutableLiveData
     */
    @Override
    public MutableLiveData<Result> fetchTrips(long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdate >= FRESH_TIMEOUT) {
            this.tripsRemoteDataSource.getTrips();
        } else {
            this.tripsLocalDataSource.getTrips();
        }
        return this.allTripsMutableLiveData;
    }

    /**
     * Refresh the trips from the remote source.
     *
     * @return the list of trips as a MutableLiveData
     */
    @Override
    public MutableLiveData<Result> refreshTrips() {
        this.tripsRemoteDataSource.getTrips();
        return this.allTripsMutableLiveData;
    }

    /**
     * Update the trip in the remote source.
     *
     * @param trip   the trip to update as a map.
     *               The key is the field to update and the value is the new value.
     * @param tripId the id of the trip to update
     */
    @Override
    public void updateTrip(HashMap<String, Object> trip, String tripId) {
        this.tripsRemoteDataSource.updateTrip(trip, tripId);
    }

    /**
     * Updates the activity
     *
     * @param activity   the activity to update as a map.
     *                   The key is the field to update and the value is the new value.
     * @param tripId     the id of the trip to which the activity belongs
     * @param activityId the id of the activity to update
     */
    @Override
    public void updateActivity(HashMap<String, Object> activity, String tripId, String activityId) {
        this.tripsRemoteDataSource.updateActivity(activity, tripId, activityId);
    }

    /**
     * Delete the trip from the remote source and from the local source.
     *
     * @param trip the trip to delete
     */
    @Override
    public void deleteTrip(Trip trip) {
        this.tripsRemoteDataSource.deleteTrip(trip);
        this.tripsLocalDataSource.deleteTrip(trip);
    }

    /**
     * Deletes an activity.
     *
     * @param activity the activity to be deleted
     * @param trip     the trip to which the activity belongs
     */
    @Override
    public void deleteActivity(Activity activity, Trip trip) {
        this.tripsRemoteDataSource.deleteActivity(activity, trip);
    }

    /**
     * Insert the trip in the remote source.
     *
     * @param trip the trip to insert
     */
    @Override
    public void insertTrip(Trip trip) {
        this.tripsRemoteDataSource.insertTrip(trip);
    }

    /**
     * Inserts a new activity.
     *
     * @param activity the activity to be inserted
     * @param trip     the trip in which the activity should be inserted
     */
    @Override
    public void insertActivity(Activity activity, Trip trip) {
        this.tripsRemoteDataSource.insertActivity(activity, trip);
    }

    @Override
    public void onSuccessFromRemote(@NonNull TripsApiResponse tripsApiResponse, long lastUpdate) {
        this.tripsLocalDataSource.insertTrips(tripsApiResponse.getTripList());
        this.sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE,
                Long.toString(lastUpdate));
    }

    @Override
    public void onFailureFromRemote(@NonNull Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        this.allTripsMutableLiveData.postValue(resultError);
    }

    @Override
    public void onSuccessFromLocal(List<Trip> tripList) {
        Result.TripSuccess result = new Result.TripSuccess(new TripsResponse(tripList));
        this.allTripsMutableLiveData.postValue(result);
    }
}
