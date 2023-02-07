package it.unimib.sal.one_two_trip.util;

import androidx.core.app.NotificationManagerCompat;

/**
 * Utility class containing constants used throughout the application.
 */
public class Constants {
    // DATABASE CONSTANTS
    public static final String TRIPS_DATABASE_NAME = "TripsDatabase";
    public static final int TRIPS_DATABASE_VERSION = 1;

    // ACTIVITY CONSTANTS
    public static final int MAX_ACTIVITIES_PER_TRIP_HOME = 3;
    public static final String MOVING_ACTIVITY_TYPE_NAME = "moving";
    public static final String ENCRYPTED_DATA_FILE_NAME = "it.unimib.sal.one_two_trip.encrypted_preferences";
    public static final String ENCRYPTED_SHARED_PREFERENCES_FILE_NAME = "it.unimib.sal.one_two_trip.encrypted_preferences";
    // SharedPreferences
    public static final String STATIC_ACTIVITY_TYPE_NAME = "static";

    // SHARED PREFERENCES CONSTANTS
    public static final String SHARED_PREFERENCES_FILE_NAME = "it.unimib.sal.one_two_trip.preferences";
    public static final String SHARED_PREFERENCES_NOTIFICATIONS_ON = "notifications_on";
    public static final String SHARED_PREFERENCES_TRIP_NOTIFICATIONS = "trip_notifications";
    public static final String SHARED_PREFERENCES_ACTIVITY_NOTIFICATIONS = "activity_notifications";
    public static final String SHARED_PREFERENCES_THEME = "theme";
    public static final String LIGHT_THEME = "light";
    public static final String DARK_THEME = "dark";
    public static final String SYSTEM_THEME = "system";
    public static final String TWELVE_HOURS = "720";    // Minutes
    public static final String ONE_DAY = "1440";
    public static final String TWO_DAYS = "2880";
    public static final String HALF_HOUR = "30";
    public static final String ONE_HOUR = "60";
    public static final String TWO_HOURS = "120";
    public static final int MINUTE_IN_MILLIS = 60000;

    // TRIPS REFRESH RATE
    public static final String LAST_UPDATE = "last_update";
    public static final int FRESH_TIMEOUT = 60 * 1000; // 1 minute (in milliseconds)

    public static final String RETROFIT_ERROR = "retrofit_error";
    public static final String API_KEY_ERROR = "api_key_error";
    public static final String UNEXPECTED_ERROR = "unexpected_error";
    public static final String INVALID_USER_ERROR = "invalidUserError";
    public static final String INVALID_CREDENTIALS_ERROR = "invalidCredentials";
    public static final String USER_COLLISION_ERROR = "userCollisionError";
    public static final String WEAK_PASSWORD_ERROR = "passwordIsWeak";
    public static final String EMAIL_ADDRESS = "email_address";
    public static final String PASSWORD = "password";
    public static final String ID_TOKEN = "google_token";

    // FIREBASE REALTIME DATABASE CONSTANTS
    public static final String FIREBASE_REALTIME_DATABASE = "https://trip-b29d1-default-rtdb.europe-west1.firebasedatabase.app/";
    public static final String FIREBASE_TRIPS_COLLECTION = "trips";
    public static final String FIREBASE_USER_COLLECTION = "users";
    public static final String TRIP_LOGO_NAME = "logo.jpg";

    // UNSPLASH PHOTOS API CONSTANTS
    public static final String PHOTOS_BASE_URL = "https://api.unsplash.com/";
    public static final String PHOTOS_ENDPOINT = "search/photos";
    public static final String PHOTOS_VERSION = "v1";
    public static final String PHOTOS_QUERY = "query";
    public static final String PHOTOS_PER_PAGE = "per_page";
    public static final String AUTHORIZATION = "Authorization";
    public static final String ACCEPT_VERSION = "Accept-Version";
    public static final int PHOTOS_PER_PAGE_VALUE = 1;

    // GEOCODING API CONSTANTS
    public static final String GEOCODING_BASE_URL = "https://nominatim.openstreetmap.org/";
    public static final String GEOCODING_ENDPOINT = "search";
    public static final String GEOCODING_FORMAT = "format";
    public static final String GEOCODING_FORMAT_VALUE = "json";
    public static final String GEOCODING_QUERY = "q";
    public static final String GEOCODING_LIMIT = "limit";
    public static final String GEOCODING_LANGUAGE = "accept-language";

    // SHARE PHOTO CONSTANTS
    public static final String IMAGE_MIME = "image/*";
    public static final String FONT_NAME = "sans-serif-condensed-medium";
    public static final String SHARED_IMAGE = "shared_img_";

    // STATUS
    public static final String STATUS_OK = "OK";

    // WORKER DATA KEYS
    public static final String KEY_LOCATION = "location";
    public static final String KEY_COMPLETED = "completed";


    // ACTIVITY BUNDLE NAMES
    public static final String SELECTED_TRIP_ID = "tripId";
    public static final String SELECTED_TRIP_NAME = "tripName";
    public static final String SELECTED_ACTIVITY_ID = "activityId";
    public static final String MOVE_TO_ACTIVITY = "moveToActivity";
    public static final String ACTIVITY_TITLE = "activityTitle";

    // NOTIFICATIONS
    public static final String NOTIFICATION_TYPE = "type";
    public static final String NOTIFICATION_TRIP = "trip";
    public static final String NOTIFICATION_ACTIVITY = "activity";
    public static final String NOTIFICATION_ENTITY_NAME = "name";
    public static final String NOTIFICATION_CHANNEL_ID = "one_two_trip_channel";
    public static final String NOTIFICATION_DELETED = "deleted";
    public static final String NOTIFICATION_ID = "notId";
    public static final String INTENT_ID = "intentId";
    public static final int NOTIFICATION_IMPORTANCE = NotificationManagerCompat.IMPORTANCE_HIGH;
    public static final String NOTIFICATION_TIME = "time";

    // TRIP LOGO
    public static final String LAST_LOGO_UPDATE = "lastLogoUpdate";

    // USER COLOR
    public static final String USER_COLOR = "userColor";

    public static final int MINIMUM_PASSWORD_LENGTH = 6;
    public static final String FIREBASE_USERS_COLLECTION = "users";
}
