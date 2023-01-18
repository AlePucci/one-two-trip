package it.unimib.sal.one_two_trip.data.source;

import static it.unimib.sal.one_two_trip.util.Constants.TRIPS_API_TEST_JSON_FILE;
import static it.unimib.sal.one_two_trip.util.Constants.UNEXPECTED_ERROR;

import java.io.IOException;
import java.util.List;

import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.model.TripsApiResponse;
import it.unimib.sal.one_two_trip.util.JSONParserUtil;

public class TripsMockRemoteDataSource extends BaseTripsRemoteDataSource {

    private final JSONParserUtil jsonParserUtil;

    public TripsMockRemoteDataSource(JSONParserUtil jsonParserUtil) {
        super();
        this.jsonParserUtil = jsonParserUtil;
    }

    @Override
    public void getTrips() {
        TripsApiResponse tripsApiResponse = null;

        try {
            tripsApiResponse = this.jsonParserUtil.parseJSON(TRIPS_API_TEST_JSON_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (tripsApiResponse != null) {
            tripCallback.onSuccessFromRemote(tripsApiResponse, System.currentTimeMillis());
        } else {
            tripCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateTrip(Trip trip) {

    }

    @Override
    public void insertTrips(List<Trip> tripList) {

    }

    @Override
    public void deleteTrip(Trip trip) {

    }
}
