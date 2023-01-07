package it.unimib.sal.one_two_trip.service;

import static it.unimib.sal.one_two_trip.util.Constants.PHOTOS_ENDPOINT;
import static it.unimib.sal.one_two_trip.util.Constants.PHOTOS_PER_PAGE;
import static it.unimib.sal.one_two_trip.util.Constants.PHOTOS_QUERY;

import it.unimib.sal.one_two_trip.model.PictureApiResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface PictureApiService {

    @GET(PHOTOS_ENDPOINT)
    Call<PictureApiResponse> getPhotos(
            @Query(PHOTOS_QUERY) String query,
            @Query(PHOTOS_PER_PAGE) int per_page,
            @Header("Authorization") String photoApiKey,
            @Header("Accept-Version") String version
    );
}
