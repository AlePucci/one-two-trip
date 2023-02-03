package it.unimib.sal.one_two_trip.util;

import com.google.gson.Gson;

/**
 * Utility class to get the singleton instance of the Gson object.
 */
public class GsonSingleton {

    private static volatile Gson INSTANCE = null;

    private GsonSingleton() {
    }

    public static Gson getInstance() {
        if (INSTANCE == null) {
            synchronized (GsonSingleton.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Gson();
                }
            }
        }
        return INSTANCE;
    }
}
