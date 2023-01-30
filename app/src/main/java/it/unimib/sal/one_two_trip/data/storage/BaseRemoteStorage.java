package it.unimib.sal.one_two_trip.data.storage;

import android.graphics.Bitmap;


public abstract class BaseRemoteStorage {
    protected RemoteStorageCallback callback;

    public void setRemoteStorageCallback(RemoteStorageCallback callback) {
        this.callback = callback;
    }

    public abstract void uploadTripLogo(Bitmap bitmap, long tripId);

    public abstract void downloadTripLogo(long tripId);

    public abstract void tripLogoExists(long tripId);
}
