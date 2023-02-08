package it.unimib.sal.one_two_trip.data.source.storage;

import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_TRIPS_COLLECTION;
import static it.unimib.sal.one_two_trip.util.Constants.TRIP_LOGO_NAME;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Class to upload and download files from a remote source using Firebase Storage.
 */
public class RemoteStorage extends BaseRemoteStorage {

    private final Application application;

    public RemoteStorage(Application application) {
        super();
        this.application = application;
    }

    /**
     * Upload the trip logo to Firebase Storage.
     *
     * @param bitmap the bitmap to upload
     * @param tripId the trip id
     */
    public void uploadTripLogo(@NonNull Bitmap bitmap, String tripId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child(FIREBASE_TRIPS_COLLECTION)
                .child(TRIP_LOGO_NAME);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(exception -> callback.onUploadFailure(exception))
                .addOnSuccessListener(taskSnapshot -> {
                    if (taskSnapshot.getTask().isSuccessful()) {
                        // TODO USER IDs
                        File localFile = new File(application.getFilesDir(), tripId + "-" + TRIP_LOGO_NAME);

                        storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot1 ->
                        {
                            long lastUpdate = -1;
                            if (taskSnapshot.getMetadata() != null) {
                                lastUpdate = taskSnapshot.getMetadata().getUpdatedTimeMillis();
                            }
                            callback.onUploadSuccess(lastUpdate);
                        }).addOnFailureListener(exception ->
                                callback.onUploadFailure(exception));
                    }
                });
    }

    /**
     * Download the trip logo from Firebase Storage.
     *
     * @param tripId the trip id
     */
    @Override
    public void downloadTripLogo(String tripId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child(FIREBASE_TRIPS_COLLECTION)
                .child(TRIP_LOGO_NAME);
        File localFile = new File(application.getFilesDir(), tripId + "-" + TRIP_LOGO_NAME);

        storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot ->
                callback.onDownloadSuccess()).addOnFailureListener(exception ->
                callback.onDownloadFailure(exception));
    }

    /**
     * Check if the trip logo exists in Firebase Storage.
     *
     * @param tripId the trip id
     */
    @Override
    public void tripLogoExists(String tripId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child(FIREBASE_TRIPS_COLLECTION)
                .child(TRIP_LOGO_NAME);

        storageReference.getMetadata().addOnSuccessListener(storageMetadata -> {
            long lastUpdate = storageMetadata.getUpdatedTimeMillis();
            callback.onExistsResponse(lastUpdate);
        }).addOnFailureListener(exception ->
                callback.onExistsResponse(-1));
    }

    /**
     * Delete the trip logo from Firebase Storage.
     *
     * @param tripId the trip id
     */
    @Override
    public void deleteTripLogo(String tripId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child(FIREBASE_TRIPS_COLLECTION)
                .child(TRIP_LOGO_NAME);

        storageReference.delete();
    }
}
