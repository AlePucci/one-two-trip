package it.unimib.sal.one_two_trip.model;

import androidx.annotation.NonNull;

import java.util.List;

public class TripsApiResponse {
    private String status;
    private int totalResults;
    private List<Trip> trips;

    public TripsApiResponse() {
    }

    public TripsApiResponse(String status, int totalResults, List<Trip> trips) {
        this.status = status;
        this.totalResults = totalResults;
        this.trips = trips;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    @Override
    @NonNull
    public String toString() {
        return "TripsApiResponse{" + "status='" + status + '\'' + ", totalResults=" + totalResults +
                ", trips=" + trips.toString() + '}';
    }
}
