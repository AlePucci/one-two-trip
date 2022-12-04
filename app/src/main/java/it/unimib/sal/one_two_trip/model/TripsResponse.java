package it.unimib.sal.one_two_trip.model;

import java.util.List;

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
