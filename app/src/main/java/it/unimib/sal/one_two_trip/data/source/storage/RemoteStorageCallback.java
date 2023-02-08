package it.unimib.sal.one_two_trip.data.source.storage;

import it.unimib.sal.one_two_trip.ui.trip.TripSettingsFragment;

/**
 * Interface to send data from remote storage to {@link TripSettingsFragment} class.
 */
public interface RemoteStorageCallback {

    void onUploadSuccess(long lastUpdate);

    void onDownloadSuccess();

    void onUploadFailure(Exception exception);

    void onDownloadFailure(Exception exception);

    void onExistsResponse(long lastUpdate);
}
