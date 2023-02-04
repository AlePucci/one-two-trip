package it.unimib.sal.one_two_trip.data.repository.trips;

import static it.unimib.sal.one_two_trip.util.Constants.FRESH_TIMEOUT;
import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.database.model.Trip;
import it.unimib.sal.one_two_trip.data.database.model.response.TripsApiResponse;
import it.unimib.sal.one_two_trip.data.database.model.response.TripsResponse;
import it.unimib.sal.one_two_trip.data.source.trips.BaseTripsLocalDataSource;
import it.unimib.sal.one_two_trip.data.source.trips.BaseTripsRemoteDataSource;
import it.unimib.sal.one_two_trip.data.source.trips.TripCallback;
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

    @Override
    public void updateTrip(Trip trip) {
        this.tripsRemoteDataSource.updateTrip(trip);
    }

    @Override
    public void deleteTrip(Trip trip) {
        this.tripsRemoteDataSource.deleteTrip(trip);
        this.tripsLocalDataSource.deleteTrip(trip);
    }

    @Override
    public void insertTrip(Trip trip) {
        this.tripsRemoteDataSource.insertTrip(trip);
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
