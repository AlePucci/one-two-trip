package it.unimib.sal.one_two_trip.data.source;

import java.io.IOException;

import it.unimib.sal.one_two_trip.BuildConfig;
import it.unimib.sal.one_two_trip.model.PictureApiResponse;
import it.unimib.sal.one_two_trip.service.PictureApiService;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Response;

public class PhotoRemoteDataSource extends BasePhotoRemoteDataSource {
    private final PictureApiService pictureApiService;
    private final String PHOTOS_KEY;

    public PhotoRemoteDataSource() {
        this.pictureApiService = ServiceLocator.getInstance().getPictureApiService();
        this.PHOTOS_KEY = BuildConfig.PHOTOS_KEY;
    }

    @Override
    public void getPhoto(String location) throws IOException {
        if (location == null || location.isEmpty()) {
            return;
        }

        Call<PictureApiResponse> pictureApiCall = pictureApiService.getPhotos(location.trim(),
                1,
                PHOTOS_KEY,
                "v1");

        Response<PictureApiResponse> response = pictureApiCall.execute();

        if (response.body() != null && response.isSuccessful() &&
                (response.body().getErrors() == null || response.body().getErrors().length == 0)) {
            photoCallback.onSuccess(response.body().getResults()[0].getUrls().getRegular());
        } else {
            photoCallback.onFailure(new Exception(response.code() + " " + response.message()));
        }
    }
}
