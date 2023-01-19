package it.unimib.sal.one_two_trip.util;

import static android.content.Context.ALARM_SERVICE;
import static it.unimib.sal.one_two_trip.util.Constants.HALF_HOUR;
import static it.unimib.sal.one_two_trip.util.Constants.MINUTE_IN_MILLIS;
import static it.unimib.sal.one_two_trip.util.Constants.MOVING_ACTIVITY_TYPE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.NOTIFICATION_ACTIVITY;
import static it.unimib.sal.one_two_trip.util.Constants.NOTIFICATION_ENTITY_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.NOTIFICATION_TIME;
import static it.unimib.sal.one_two_trip.util.Constants.NOTIFICATION_TRIP;
import static it.unimib.sal.one_two_trip.util.Constants.NOTIFICATION_TYPE;
import static it.unimib.sal.one_two_trip.util.Constants.ONE_DAY;
import static it.unimib.sal.one_two_trip.util.Constants.ONE_HOUR;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_ACTIVITY_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_ID;
import static it.unimib.sal.one_two_trip.util.Constants.TWELVE_HOURS;
import static it.unimib.sal.one_two_trip.util.Constants.TWO_DAYS;
import static it.unimib.sal.one_two_trip.util.Constants.TWO_HOURS;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    public static String getRandomTripLocation(Trip trip, List<Trip> tripList,
                                               Application application, View view) {
        // PRELIMINARY CHECKS
        if (tripList == null) return null;

        int tripPosition = -1;

        for (Trip mTrip : tripList) {
            if (mTrip.equals(trip)) {
                tripPosition = tripList.indexOf(mTrip);
                break;
            }
        }

        if (tripPosition == -1 || tripList.get(tripPosition) == null
                || tripList.get(tripPosition).getActivity() == null
                || tripList.get(tripPosition).getActivity().getActivityList() == null
                || tripList.get(tripPosition).getActivity().getActivityList().isEmpty()) {
            Snackbar.make(view, application.getString(R.string.no_shareable_activities), Snackbar.LENGTH_SHORT).show();
            return null;
        }

        List<it.unimib.sal.one_two_trip.model.Activity> tmp =
                new ArrayList<>(tripList.get(tripPosition).getActivity().getActivityList());

        tmp.removeIf(activity -> activity == null
                || activity.getType().equalsIgnoreCase(MOVING_ACTIVITY_TYPE_NAME));

        // RANDOM ACTIVITY TO SHARE
        int r;

        do {
            r = (int) (Math.random() * tmp.size());
        } while (tmp.get(r) == null);

        return tmp.get(r).getLocation();
    }

    /**
     * It checks if the device is connected to Internet.
     * See <a href="https://developer.android.com/training/monitoring-device-state/connectivity-status-type#DetermineConnection">this.</a>
     *
     * @return true if the device is connected to Internet; false otherwise.
     */
    public static boolean isConnected(@NonNull Activity activity) {
        ConnectivityManager cm = (ConnectivityManager)
                activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        Network nw = cm.getActiveNetwork();
        if (nw == null) return false;
        NetworkCapabilities actNw = cm.getNetworkCapabilities(nw);
        return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));

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

    public static void scheduleTripNotifications(Trip trip, Application application) {
        if (trip == null) {
            return;
        }

        if (trip.getActivity() == null || trip.getActivity().getActivityList() == null
                || trip.getActivity().getActivityList().get(0) == null) {
            return;
        }

        long tripStartTime = trip.getActivity().getActivityList().get(0).getStart_date();
        long tripId = trip.getId();

        for (int i = 0; i < 3; i++) {
            int[] time = {Integer.parseInt(TWO_DAYS), Integer.parseInt(ONE_DAY),
                    Integer.parseInt(TWELVE_HOURS)};

            Calendar alarmTime = Calendar.getInstance();

            long timeInMillis = ((long) time[i] * MINUTE_IN_MILLIS);
            Date notificationTime = new Date(tripStartTime - timeInMillis);
            Date now = new Date();

            if (now.after(notificationTime)) {
                continue;
            }

            alarmTime.setTime(notificationTime);

            Intent intent = new Intent(application, AlarmReceiver.class);
            intent.setData(Uri.parse("alarms://trip:" + tripId + ":" + i));

            intent.putExtra(NOTIFICATION_TYPE, NOTIFICATION_TRIP);
            intent.putExtra(SELECTED_TRIP_ID, String.valueOf(tripId));
            intent.putExtra(NOTIFICATION_ENTITY_NAME, trip.getTitle());
            intent.putExtra(NOTIFICATION_TIME, String.valueOf(time[i]));

            PendingIntent pendingIntent = PendingIntent.getBroadcast(application,
                    (int) tripId * -1, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) application.getSystemService(ALARM_SERVICE);

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    alarmTime.getTimeInMillis(), pendingIntent);
        }
    }

    public static void scheduleActivityNotifications(it.unimib.sal.one_two_trip.model.Activity activity,
                                                     Application application, long tripId) {
        if (activity == null) {
            return;
        }

        if (activity.getStart_date() == 0) {
            return;
        }

        long activityId = activity.getId();

        for (int i = 0; i < 3; i++) {
            int[] time = {Integer.parseInt(TWO_HOURS), Integer.parseInt(ONE_HOUR),
                    Integer.parseInt(HALF_HOUR)};

            Calendar alarmTime = Calendar.getInstance();

            long timeInMillis = ((long) time[i] * MINUTE_IN_MILLIS);
            Date notificationTime = new Date(activity.getStart_date() - timeInMillis);
            Date now = new Date();

            if (now.after(notificationTime)) {
                continue;
            }

            alarmTime.setTime(notificationTime);

            Intent intent = new Intent(application, AlarmReceiver.class);
            intent.setData(Uri.parse("alarms://trip:" + tripId + "/activity:"
                    + activityId + ":" + i));
            intent.putExtra(NOTIFICATION_TYPE, NOTIFICATION_ACTIVITY);
            intent.putExtra(SELECTED_TRIP_ID, String.valueOf(tripId));
            intent.putExtra(SELECTED_ACTIVITY_ID, String.valueOf(activityId));
            intent.putExtra(NOTIFICATION_ENTITY_NAME, activity.getTitle());
            intent.putExtra(NOTIFICATION_TIME, String.valueOf(time[i]));

            PendingIntent pendingIntent = PendingIntent.getBroadcast(application,
                    (int) activityId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) application.getSystemService(ALARM_SERVICE);

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    alarmTime.getTimeInMillis(), pendingIntent);
        }
    }
}
