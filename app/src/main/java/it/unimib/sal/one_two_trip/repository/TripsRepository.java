package it.unimib.sal.one_two_trip.repository;

import static it.unimib.sal.one_two_trip.util.Constants.FRESH_TIMEOUT;
import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.model.TripApiResponse;
import it.unimib.sal.one_two_trip.model.TripResponse;
import it.unimib.sal.one_two_trip.model.TripsApiResponse;
import it.unimib.sal.one_two_trip.model.TripsResponse;
import it.unimib.sal.one_two_trip.source.BaseTripsLocalDataSource;
import it.unimib.sal.one_two_trip.source.BaseTripsRemoteDataSource;
import it.unimib.sal.one_two_trip.source.TripCallback;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;

public class TripsRepository implements ITripsRepository, TripCallback {
    private final MutableLiveData<Result> allTripsMutableLiveData;
    private final MutableLiveData<Result> tripMutableLiveData;
    private final BaseTripsRemoteDataSource tripsRemoteDataSource;
    private final BaseTripsLocalDataSource tripsLocalDataSource;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    public TripsRepository(BaseTripsRemoteDataSource tripsRemoteDataSource,
                           BaseTripsLocalDataSource tripsLocalDataSource,
                           SharedPreferencesUtil sharedPreferencesUtil) {

        allTripsMutableLiveData = new MutableLiveData<>();
        tripMutableLiveData = new MutableLiveData<>();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
        this.tripsRemoteDataSource = tripsRemoteDataSource;
        this.tripsLocalDataSource = tripsLocalDataSource;
        this.tripsRemoteDataSource.setTripCallback(this);
        this.tripsLocalDataSource.setTripCallback(this);
    }

    @Override
    public MutableLiveData<Result> fetchTrips(long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        // It gets the trips from the Web Service if the last download
        // of the news has been performed more than FRESH_TIMEOUT value ago
        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            tripsRemoteDataSource.getTrips();
            Log.d("AAA", "REMOTE ALL");
        } else {
            tripsLocalDataSource.getTrips();
            Log.d("AAA", "LOCAL ALL");
        }
        return allTripsMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> fetchTrip(long id, long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        if(currentTime - lastUpdate > FRESH_TIMEOUT) {
            tripsRemoteDataSource.getTrip(id);
            Log.d("AAA", "REMOTE " + id);
        } else {
            tripsLocalDataSource.getTrip(id);
            Log.d("AAA", "LOCAL " + id);
        }

        return tripMutableLiveData;
    }

    @Override
    public void updateTrip(Trip trip) {
        tripsLocalDataSource.updateTrip(trip);
    }

    @Override
    public void onSuccessFromRemote(TripsApiResponse tripsApiResponse, long lastUpdate) {
        tripsLocalDataSource.insertTrips(tripsApiResponse.getTrips());
        sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE, Long.toString(lastUpdate));
    }

    @Override
    public void onSuccessFromRemote(TripApiResponse tripApiResponse, long lastUpdate) {
        tripsLocalDataSource.insertTrip(tripApiResponse.getTrip());
        sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE, Long.toString(lastUpdate));
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allTripsMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocal(List<Trip> newsList) {
        Result.Success<TripsResponse> result = new Result.Success<>(new TripsResponse(newsList));
        allTripsMutableLiveData.postValue(result);
        Log.d("AAA", newsList.toString());
    }

    @Override
    public void onSuccessFromLocal(Trip trip) {
        Result.Success<TripResponse> result = new Result.Success<>(new TripResponse(trip));
        tripMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        allTripsMutableLiveData.postValue(resultError);
    }
}
