package it.unimib.sal.one_two_trip.util;

import static it.unimib.sal.one_two_trip.util.Constants.MOVING_ACTIVITY_TYPE_NAME;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Trip;

/**
 * Utility class to provide useful methods among the application.
 */
public class Utility {

    /**
     * Utility method used to make an user able to share a coming/past trip
     *
     * @param trip        trip to share
     * @param tripList    list of trips in which the trip to share is contained
     * @param application the application
     * @param view        a view, used to display snackbar in case of errors
     */
    public static String getRandomTripLocation(Trip trip, List<Trip> tripList, Application application,
                                               View view) {
        // PRELIMINARY CHECKS
        if (tripList == null) return null;

        int tripPosition = -1;

        for (Trip mTrip : tripList) {
            if (mTrip.equals(trip)) {
                tripPosition = tripList.indexOf(mTrip);
                break;
            }
        }

        if (tripPosition == -1 || tripList.get(tripPosition) == null ||
                tripList.get(tripPosition).getActivity() == null ||
                tripList.get(tripPosition).getActivity().getActivityList() == null ||
                tripList.get(tripPosition).getActivity().getActivityList().isEmpty()) {
            Snackbar.make(view, application.getString(R.string.no_shareable_activities),
                    Snackbar.LENGTH_SHORT).show();
            return null;
        }

        List<it.unimib.sal.one_two_trip.model.Activity> tmp =
                new ArrayList<>(tripList.get(tripPosition).getActivity().getActivityList());

        for (Iterator<it.unimib.sal.one_two_trip.model.Activity> i = tmp.iterator(); i.hasNext(); ) {
            it.unimib.sal.one_two_trip.model.Activity activity = i.next();
            if (activity == null || activity.getType().equalsIgnoreCase(MOVING_ACTIVITY_TYPE_NAME)) {
                i.remove();
            }
        }

        // RANDOM ACTIVITY TO SHARE
        int r;

        do {
            r = (int) (Math.random() * tmp.size());
        } while (tmp.get(r) == null);

        return tmp.get(r).getLocation();
    }

    /**
     * It checks if the device is connected to Internet.
     * See: https://developer.android.com/training/monitoring-device-state/connectivity-status-type#DetermineConnection
     *
     * @return true if the device is connected to Internet; false otherwise.
     */
    public static boolean isConnected(@NonNull Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = cm.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = cm.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            NetworkInfo nwInfo = cm.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }

    /**
     * Utility method to verify if two dates are in the same day (ignoring time).
     *
     * @param date1 first date
     * @param date2 second date
     * @return true if the dates are in the same day (ignoring time), false otherwise
     */
    public static boolean compareDate(long date1, long date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(new Date(date1));
        cal2.setTime(new Date(date2));

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
}
