package it.unimib.sal.one_two_trip.data.source.photo;

import it.unimib.sal.one_two_trip.util.PhotoWorker;

/**
 * Interface to send photos URL from remote source to {@link PhotoWorker} class.
 */
public interface PhotoCallback {

    void onSuccess(String photoUrl);

    void onFailure(Exception exception);
}
