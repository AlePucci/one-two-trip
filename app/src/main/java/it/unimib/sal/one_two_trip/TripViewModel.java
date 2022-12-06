package it.unimib.sal.one_two_trip;

import androidx.lifecycle.ViewModel;

import it.unimib.sal.one_two_trip.model.Trip;

public class TripViewModel extends ViewModel {
    Trip trip;

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Trip getTrip() {
        return trip;
    }
}
