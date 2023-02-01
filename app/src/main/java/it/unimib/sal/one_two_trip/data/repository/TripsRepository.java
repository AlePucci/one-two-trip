package it.unimib.sal.one_two_trip.data.repository;

import static it.unimib.sal.one_two_trip.util.Constants.FRESH_TIMEOUT;
import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.sal.one_two_trip.data.source.BaseTripsLocalDataSource;
import it.unimib.sal.one_two_trip.data.source.BaseTripsRemoteDataSource;
import it.unimib.sal.one_two_trip.data.source.TripCallback;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.model.TripsApiResponse;
import it.unimib.sal.one_two_trip.model.TripsResponse;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;

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
    }


    @Override
    public void onSuccessFromRemote(TripsApiResponse tripsApiResponse, long lastUpdate) {
        this.tripsLocalDataSource.insertTrips(tripsApiResponse.getTrips());
        this.sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE,
                Long.toString(lastUpdate));
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        this.allTripsMutableLiveData.postValue(resultError);
    }

    @Override
    public void onSuccessFromLocal(List<Trip> tripList) {
        Result.Success result = new Result.Success(new TripsResponse(tripList));
        this.allTripsMutableLiveData.postValue(result);
    }

}
