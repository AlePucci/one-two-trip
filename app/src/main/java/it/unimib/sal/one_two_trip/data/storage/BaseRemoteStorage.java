package it.unimib.sal.one_two_trip.data.storage;

import android.graphics.Bitmap;

import java.io.IOException;


public abstract class BaseRemoteStorage {
    protected RemoteStorageCallback callback;

    public void setRemoteStorageCallback(RemoteStorageCallback callback) {
        this.callback = callback;
    }

    public abstract void uploadTripLogo(Bitmap bitmap, long tripId);

    public abstract void downloadTripLogo(long tripId) throws IOException;
    public abstract void tripLogoExists(long tripId);
}
