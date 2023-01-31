package it.unimib.sal.one_two_trip.data.storage;

public interface RemoteStorageCallback {

    void onUploadSuccess(long lastUpdate);

    void onDownloadSuccess();

    void onUploadFailure(Exception exception);

    void onDownloadFailure(Exception exception);

    void onExistsResponse(long lastUpdate);
}
