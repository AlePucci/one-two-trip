package it.unimib.sal.one_two_trip.util;

/**
 * Constants used in the application.
 */
public class Constants {
    public static final String TRIPS_DATABASE_NAME = "TripsDatabase";
    public static final int TRIPS_DATABASE_VERSION = 1;

    public static final String TRIPS_API_TEST_JSON_FILE = "tripsapi-test.json";

    public static final int MAX_ACTIVITIES_PER_TRIP_HOME = 3;
    public static final String MOVING_ACTIVITY_TYPE_NAME = "moving";

    // Constants for SharedPreferences
    public static final String SHARED_PREFERENCES_FILE_NAME = "it.unimib.sal.one_two_trip.preferences";

    // Constants for refresh rate of trips
    public static final String LAST_UPDATE = "last_update";
    public static final int FRESH_TIMEOUT = 60*1000; // 1 minute (in milliseconds)

    public static final String RETROFIT_ERROR = "retrofit_error";
    public static final String API_KEY_ERROR = "api_key_error";
    public static final String UNEXPECTED_ERROR = "unexpected_error";

    public static final String SELECTED_TRIP_ID = "tripId";
}
