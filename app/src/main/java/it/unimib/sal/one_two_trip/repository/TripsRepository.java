package it.unimib.sal.one_two_trip.repository;

import static it.unimib.sal.one_two_trip.util.Constants.FRESH_TIMEOUT;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.model.TripsApiResponse;
import it.unimib.sal.one_two_trip.model.TripsResponse;
import it.unimib.sal.one_two_trip.source.BaseTripsLocalDataSource;
import it.unimib.sal.one_two_trip.source.BaseTripsRemoteDataSource;
import it.unimib.sal.one_two_trip.source.TripCallback;

public class TripsRepository implements ITripsRepository, TripCallback {
    private final MutableLiveData<Result> allTripsMutableLiveData;
    private final BaseTripsRemoteDataSource tripsRemoteDataSource;
    private final BaseTripsLocalDataSource tripsLocalDataSource;

    public TripsRepository(BaseTripsRemoteDataSource tripsRemoteDataSource,
                           BaseTripsLocalDataSource tripsLocalDataSource) {

        allTripsMutableLiveData = new MutableLiveData<>();
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
        } else {
            tripsLocalDataSource.getTrips();
        }
        return allTripsMutableLiveData;
    }

    @Override
    public void updateTrip(Trip trip) {
        tripsLocalDataSource.updateTrip(trip);
    }

    @Override
    public void onSuccessFromRemote(TripsApiResponse tripsApiResponse, long lastUpdate) {
        tripsLocalDataSource.insertTrips(tripsApiResponse.getTrips());
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allTripsMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocal(List<Trip> newsList) {
        Result.Success result = new Result.Success(new TripsResponse(newsList));
        allTripsMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        allTripsMutableLiveData.postValue(resultError);
    }
}