package it.unimib.sal.one_two_trip.data.source.storage;

import android.graphics.Bitmap;

/**
 * Base class for remote storage.
 */
public abstract class BaseRemoteStorage {

    protected RemoteStorageCallback callback;

    public void setRemoteStorageCallback(RemoteStorageCallback callback) {
        this.callback = callback;
    }

    public abstract void uploadTripLogo(Bitmap bitmap, String tripId);

    public abstract void uploadProfilePicture(Bitmap bitmap, String idToken);

    public abstract void downloadTripLogo(String tripId);

    public abstract void downloadProfilePicture(String idToken);

    public abstract void tripLogoExists(String tripId);

    public abstract void profilePictureExists(String idToken);

    public abstract void deleteTripLogo(String tripId);
}
