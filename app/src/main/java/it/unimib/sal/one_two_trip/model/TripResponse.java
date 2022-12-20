package it.unimib.sal.one_two_trip.model;

public class TripResponse {
    private Trip trip;

    public TripResponse() {

    }

    public TripResponse(Trip trip) {
        this.trip = trip;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }
}
