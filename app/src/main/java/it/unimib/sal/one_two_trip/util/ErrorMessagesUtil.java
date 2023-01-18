package it.unimib.sal.one_two_trip.util;

import static it.unimib.sal.one_two_trip.util.Constants.API_KEY_ERROR;
import static it.unimib.sal.one_two_trip.util.Constants.RETROFIT_ERROR;

import android.app.Application;

import androidx.annotation.NonNull;

import it.unimib.sal.one_two_trip.R;

public class ErrorMessagesUtil {

    private final Application application;

    public ErrorMessagesUtil(Application application) {
        this.application = application;
    }

    /**
     * Returns a message to inform the user about the error.
     *
     * @param errorType The type of error.
     * @return The message to be shown to the user.
     */
    public String getErrorMessage(@NonNull String errorType) {
        switch (errorType) {
            case RETROFIT_ERROR:
                return application.getString(R.string.error_retrieving_trips);
            case API_KEY_ERROR:
                return application.getString(R.string.api_key_error);
            default:
                return application.getString(R.string.unexpected_error);
        }
    }
}
