package it.unimib.sal.one_two_trip.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

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
     */
    public static void onTripShare(Trip trip, List<Trip> tripList, Application application,
                                   View view) {
        int tripPosition = -1;

        if (tripList == null) return;

        for (Trip mTrip : tripList) {
            if (mTrip.equals(trip)) {
                tripPosition = tripList.indexOf(mTrip);
                break;
            }
        }

        if (tripPosition == -1 || tripList.get(tripPosition) == null ||
                tripList.get(tripPosition).getActivity() == null ||
                tripList.get(tripPosition).getActivity().activityList == null ||
                tripList.get(tripPosition).getActivity().activityList.isEmpty()) {
            Snackbar.make(view, application.getString(R.string.no_shareable_activities),
                    Snackbar.LENGTH_SHORT).show();
            return;
        }

        List<it.unimib.sal.one_two_trip.model.Activity> tmp =
                new ArrayList<>(tripList.get(tripPosition).getActivity().activityList);

        for (Iterator<it.unimib.sal.one_two_trip.model.Activity> i = tmp.iterator(); i.hasNext(); ) {
            it.unimib.sal.one_two_trip.model.Activity activity = i.next();
            if (activity == null || activity.getType().equals("moving")) i.remove();
        }

        int r = -1;

        do {
            r = (int) (Math.random() * tmp.size());
        } while (r == -1 || tmp.get(r) == null);

        SharePhotoGenerator sharePhotoGenerator = new SharePhotoGenerator(application,
                trip.isCompleted());
        sharePhotoGenerator.execute(tmp.get(r).getLocation());
    }

    /**
     * It checks if the device is connected to Internet.
     * See: https://developer.android.com/training/monitoring-device-state/connectivity-status-type#DetermineConnection
     *
     * @return true if the device is connected to Internet; false otherwise.
     */
    public static boolean isConnected(Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
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
