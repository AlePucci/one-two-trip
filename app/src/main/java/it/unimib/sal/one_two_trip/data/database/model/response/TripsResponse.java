package it.unimib.sal.one_two_trip.data.database.model.response;

import java.util.List;

import it.unimib.sal.one_two_trip.data.database.model.Trip;

/**
 * Class simulating the response of the API call to get the trips.
 */
public class TripsResponse {

    private List<Trip> tripList;

    public TripsResponse() {
    }

    public TripsResponse(List<Trip> tripList) {
        this.tripList = tripList;
    }

    public List<Trip> getTripList() {
        return tripList;
    }

    public void setTripList(List<Trip> tripList) {
        this.tripList = tripList;
    }
}
