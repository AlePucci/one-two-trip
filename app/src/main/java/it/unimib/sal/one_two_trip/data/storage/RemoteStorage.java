package it.unimib.sal.one_two_trip.data.storage;

import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_TRIPS_COLLECTION;
import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_USER_COLLECTION;
import static it.unimib.sal.one_two_trip.util.Constants.TRIP_LOGO_NAME;

import android.app.Application;
import android.graphics.Bitmap;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class RemoteStorage extends BaseRemoteStorage {
    private final Application application;

    public RemoteStorage(Application application) {
        this.application = application;
    }

    public void uploadTripLogo(Bitmap bitmap, long tripId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child(FIREBASE_USER_COLLECTION).child("1")
                .child(FIREBASE_TRIPS_COLLECTION)
                .child(String.valueOf(tripId)).child(TRIP_LOGO_NAME);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(exception -> callback.onUploadFailure(exception))
                .addOnSuccessListener(taskSnapshot -> {
                    if (taskSnapshot.getTask().isSuccessful()) {
                        // TODO USER IDs
                        File localFile = new File(application.getFilesDir(), 1 + "-" + tripId + "-" + TRIP_LOGO_NAME);

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

    @Override
    public void downloadTripLogo(long tripId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child(FIREBASE_USER_COLLECTION).child("1")
                .child(FIREBASE_TRIPS_COLLECTION)
                .child(String.valueOf(tripId)).child(TRIP_LOGO_NAME);
        File localFile = new File(application.getFilesDir(), 1 + "-" + tripId + "-" + TRIP_LOGO_NAME);

        storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot ->
                callback.onDownloadSuccess()).addOnFailureListener(exception ->
                callback.onDownloadFailure(exception));
    }

    @Override
    public void tripLogoExists(long tripId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child(FIREBASE_USER_COLLECTION).child("1")
                .child(FIREBASE_TRIPS_COLLECTION)
                .child(String.valueOf(tripId)).child(TRIP_LOGO_NAME);

        // TODO USER IDs
        storageReference.getMetadata().addOnSuccessListener(storageMetadata -> {
            long lastUpdate = storageMetadata.getUpdatedTimeMillis();
            callback.onExistsResponse(lastUpdate);
        }).addOnFailureListener(exception ->
                callback.onExistsResponse(-1));
    }
}
