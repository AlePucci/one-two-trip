package it.unimib.sal.one_two_trip.data.source.geocoding;

import static it.unimib.sal.one_two_trip.util.Constants.GEOCODING_FORMAT_VALUE;

import android.content.Context;

import java.io.IOException;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.database.model.response.GeocodingApiResponse;
import it.unimib.sal.one_two_trip.data.service.GeocodingApiService;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Class to get geocoding data from a remote source using Retrofit.
 */
public class GeocodingRemoteDataSource extends BaseGeocodingRemoteDataSource {

    private final Context context;
    private final GeocodingApiService geocodingApiService;

    public GeocodingRemoteDataSource(Context context) {
        super();
        this.context = context;
        this.geocodingApiService = ServiceLocator.getInstance().getGeocodingApiService();
    }

    /**
     * Get the geocoding data from a remote source.
     *
     * @param query the location to search for.
     * @param limit the number of results to return.
     * @throws IOException if the request fails.
     */
    @Override
    public void search(String query, int limit) throws IOException {
        if (query == null || query.isEmpty()) {
            geocodingCallback.onFailure(new Exception(context.getString(R.string.unexpected_error)));
            return;
        }
        String language = this.context.getResources().getConfiguration().getLocales().get(0).getLanguage();

        Call<GeocodingApiResponse[]> geocodingApiCall = this.geocodingApiService.search(query.trim(),
                limit,
                GEOCODING_FORMAT_VALUE,
                language);

        Response<GeocodingApiResponse[]> response = geocodingApiCall.execute();

        if (response.body() != null && response.isSuccessful() && response.body().length > 0) {
            geocodingCallback.onSuccess(response.body()[0].getLat(), response.body()[0].getLon());
        } else {
            geocodingCallback.onFailure(new Exception(response.code() + " " + response.message()));
        }
    }
}
