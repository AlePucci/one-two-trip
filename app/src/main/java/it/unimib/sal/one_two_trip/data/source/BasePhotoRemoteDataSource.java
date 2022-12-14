package it.unimib.sal.one_two_trip.data.source;

import java.io.IOException;

public abstract class BasePhotoRemoteDataSource {

    protected PhotoCallback photoCallback;

    public void setPhotoCallback(PhotoCallback photoCallback) {
        this.photoCallback = photoCallback;
    }

    public abstract void getPhoto(String query) throws IOException;
}
