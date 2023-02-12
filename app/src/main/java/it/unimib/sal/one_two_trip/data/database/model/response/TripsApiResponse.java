package it.unimib.sal.one_two_trip.data.database.model.response;

import androidx.annotation.NonNull;

import java.util.List;

import it.unimib.sal.one_two_trip.data.database.model.Trip;

/**
 * This class represents the response of the API call to get the list of trips from
 * our Firebase Cloud Firestore.
 */
public class TripsApiResponse extends TripsResponse {

    private String status;
    private int totalResults;

    public TripsApiResponse() {
        super();
    }

    public TripsApiResponse(String status, int totalResults, List<Trip> trips) {
        super(trips);
        this.status = status;
        this.totalResults = totalResults;
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

    @Override
    @NonNull
    public String toString() {
        return "TripsApiResponse{" + "status='" + status + '\'' + ", totalResults=" + totalResults +
                ", trips=" + super.toString() + '}';
    }
}
