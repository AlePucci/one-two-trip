package it.unimib.sal.one_two_trip.data.service;

import static it.unimib.sal.one_two_trip.util.Constants.ACCEPT_VERSION;
import static it.unimib.sal.one_two_trip.util.Constants.AUTHORIZATION;
import static it.unimib.sal.one_two_trip.util.Constants.PHOTOS_ENDPOINT;
import static it.unimib.sal.one_two_trip.util.Constants.PHOTOS_PER_PAGE;
import static it.unimib.sal.one_two_trip.util.Constants.PHOTOS_QUERY;

import it.unimib.sal.one_two_trip.data.database.model.response.PhotoApiResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Interface for Service to get photos URL from the Web Service.
 */
public interface PictureApiService {

    @GET(PHOTOS_ENDPOINT)
    Call<PhotoApiResponse> getPhotos(
            @Query(PHOTOS_QUERY) String query,
            @Query(PHOTOS_PER_PAGE) int per_page,
            @Header(AUTHORIZATION) String photoApiKey,
            @Header(ACCEPT_VERSION) String version
    );
}
