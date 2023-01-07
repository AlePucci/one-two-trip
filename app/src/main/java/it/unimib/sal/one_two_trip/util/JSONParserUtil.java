package it.unimib.sal.one_two_trip.util;

import android.app.Application;

import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import it.unimib.sal.one_two_trip.model.TripsApiResponse;

public class JSONParserUtil {

    private final Application application;

    public JSONParserUtil(Application application) {
        this.application = application;
    }

    public TripsApiResponse parseJSON(String fileName) throws IOException {
        InputStream inputStream = application.getAssets().open(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        TripsApiResponse tripsApiResponse = new GsonBuilder()
                .create()
                .fromJson(bufferedReader, TripsApiResponse.class);

        bufferedReader.close();
        inputStream.close();

        return tripsApiResponse;
    }
}
