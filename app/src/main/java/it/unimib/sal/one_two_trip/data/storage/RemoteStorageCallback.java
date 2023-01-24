package it.unimib.sal.one_two_trip.data.storage;

import java.io.File;

public interface RemoteStorageCallback {

    void onUploadSuccess();

    void onDownloadSuccess();

    void onUploadFailure(Exception exception);

    void onDownloadFailure(Exception exception);

    void onExistsResponse(boolean exists);
}
