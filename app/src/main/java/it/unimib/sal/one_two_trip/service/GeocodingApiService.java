package it.unimib.sal.one_two_trip.service;

import static it.unimib.sal.one_two_trip.util.Constants.GEOCODING_ENDPOINT;
import static it.unimib.sal.one_two_trip.util.Constants.GEOCODING_FORMAT;
import static it.unimib.sal.one_two_trip.util.Constants.GEOCODING_LANGUAGE;
import static it.unimib.sal.one_two_trip.util.Constants.GEOCODING_LIMIT;
import static it.unimib.sal.one_two_trip.util.Constants.GEOCODING_QUERY;

import it.unimib.sal.one_two_trip.model.GeocodingApiResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface for Service to get geocoding data from the Web Service.
 */
public interface GeocodingApiService {

    @GET(GEOCODING_ENDPOINT)
    Call<GeocodingApiResponse[]> search(
            @Query(GEOCODING_QUERY) String query,
            @Query(GEOCODING_LIMIT) int limit,
            @Query(GEOCODING_FORMAT) String format,
            @Query(GEOCODING_LANGUAGE) String language
    );
}
