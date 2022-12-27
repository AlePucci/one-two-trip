package it.unimib.sal.one_two_trip.source;

public interface PhotoCallback {
    void onSuccess(String photoUrl);

    void onFailure(Exception exception);
}
