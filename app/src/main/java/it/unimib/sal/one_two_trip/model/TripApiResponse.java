package it.unimib.sal.one_two_trip.model;

public class TripApiResponse {
    private String status;
    private Trip trip;

    public TripApiResponse() {

    }

    public TripApiResponse(String status, Trip trip) {
        this.status = status;
        this.trip = trip;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    @Override
    public String toString() {
        return "TripApiResponse{" +
                "status='" + status + '\'' +
                ", trip=" + trip +
                '}';
    }
}
