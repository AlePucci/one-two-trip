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

    // SharedPreferences
    public static final String SHARED_PREFERENCES_FILE_NAME = "it.unimib.sal.one_two_trip.preferences";
    public static final String SHARED_PREFERENCES_NOTIFICATIONS_ON = "notifications_o";
    public static final String SHARED_PREFERENCES_TRIP_NOTIFICATIONS = "trip_notifications";
    public static final String SHARED_PREFERENCES_ACTIVITY_NOTIFICATIONS = "activity_notifications";
    public static final String TWELVE_HOURS = "720";    // Minutes
    public static final String ONE_DAY = "144";
    public static final String TWO_DAYS = "288";
    public static final String HALF_HOUR = "30";
    public static final String ONE_HOUR = "60";
    public static final String TWO_HOURS = "120";

    // Constants for refresh rate of trips
    public static final String LAST_UPDATE = "last_update";
    public static final int FRESH_TIMEOUT = 60 * 1000; // 1 minute (in milliseconds)

    public static final String RETROFIT_ERROR = "retrofit_error";
    public static final String API_KEY_ERROR = "api_key_error";
    public static final String UNEXPECTED_ERROR = "unexpected_error";

    // Constants for Firebase Realtime Database
    public static final String FIREBASE_REALTIME_DATABASE = "https://trip-b29d1-default-rtdb.europe-west1.firebasedatabase.app/";
    public static final String FIREBASE_TRIPS_COLLECTION = "trips";
    public static final String FIREBASE_USER_COLLECTION = "users";

    // Constants for Unsplash photos API
    public static final String PHOTOS_BASE_URL = "https://api.unsplash.com/";
    public static final String PHOTOS_ENDPOINT = "search/photos";
    public static final String PHOTOS_QUERY = "query";
    public static final String PHOTOS_PER_PAGE = "per_page";

    // Status
    public static final String STATUS_OK = "OK";
}
