package it.unimib.sal.one_two_trip.util;

import static android.content.Context.ALARM_SERVICE;
import static it.unimib.sal.one_two_trip.util.Constants.HALF_HOUR;
import static it.unimib.sal.one_two_trip.util.Constants.MINUTE_IN_MILLIS;
import static it.unimib.sal.one_two_trip.util.Constants.MOVING_ACTIVITY_TYPE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.NOTIFICATION_ACTIVITY;
import static it.unimib.sal.one_two_trip.util.Constants.NOTIFICATION_DELETED;
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
import android.graphics.Color;
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
import java.util.Random;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.database.model.Trip;

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
        if (tripList == null || tripList.isEmpty()) return null;

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

        List<it.unimib.sal.one_two_trip.data.database.model.Activity> tmp =
                new ArrayList<>(tripList.get(tripPosition).getActivity().getActivityList());

        tmp.removeIf(activity -> activity == null
                || activity.getType().equalsIgnoreCase(MOVING_ACTIVITY_TYPE_NAME));

        if (tmp.isEmpty()) {
            Snackbar.make(view, application.getString(R.string.no_shareable_activities_2),
                    Snackbar.LENGTH_SHORT).show();
            return null;
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


    /**
     * This method should be called when an activity is added or its dates are updated.
     * It (re)schedules the notifications for the trip (aka the first activity of the trip)
     *
     * @param trip        the trip to schedule the notifications for (already updated)
     * @param application the application context
     */
    public static void scheduleNotifications(Trip trip, Application application) {
        scheduleTripNotifications(trip, application, false);
    }

    /**
     * This method should be called when an activity is added or its dates are updated.
     * It (re)schedules the notifications for the activity
     *
     * @param activity    the activity to schedule the notifications for (already updated)
     * @param application the application context
     */
    public static void scheduleNotifications(it.unimib.sal.one_two_trip.data.database.model.Activity activity,
                                             Application application, String tripId) {
        scheduleActivityNotifications(activity, application, tripId, false);
    }

    /**
     * This method should be called when the trip is deleted. It allows to delete the
     * notifications for the trip (i.e. the first activity of the trip)
     *
     * @param trip        the trip deleted
     * @param application the application context
     */
    public static void deleteNotifications(Trip trip, Application application) {
        scheduleTripNotifications(trip, application, true);
    }

    /**
     * This method should be called when an activity is deleted.
     * It doesn't reschedule the notifications for the trip (i.e. the first activity of the trip).
     * Useful to call when a trip is deleted.
     *
     * @param activity    the activity deleted
     * @param application the application context
     * @param tripId      the id of the trip the activity belongs to
     */
    public static void deleteNotifications(it.unimib.sal.one_two_trip.data.database.model.Activity activity,
                                           Application application, String tripId) {
        scheduleActivityNotifications(activity, application, tripId, true);
    }

    /**
     * This method should be called when an activity is either created or updated.
     * It (re)schedules the notifications for the trip (i.e. the first activity of the trip) and
     * the activity itself.
     *
     * @param trip        the trip to schedule the notifications for (already updated)
     * @param activity    the activity to schedule the notifications for (already updated)
     * @param application the application context
     */
    public static void onActivityCreate(Trip trip, it.unimib.sal.one_two_trip.data.database.model.Activity activity,
                                        Application application) {
        scheduleTripNotifications(trip, application, false);
        scheduleActivityNotifications(activity, application, trip.getId(), false);
    }

    /**
     * This method should be called when an activity is deleted.
     * It reschedules the notifications for the trip (i.e. the first activity of the trip)
     *
     * @param trip        the trip to schedule the notifications for (already updated)
     * @param activity    the activity deleted
     * @param application the application context
     */
    public static void onActivityDelete(Trip trip, it.unimib.sal.one_two_trip.data.database.model.Activity activity,
                                        Application application) {
        deleteNotifications(activity, application, trip.getId());
        scheduleNotifications(trip, application);
    }

    /**
     * This method generates a random color for a participant who doesn't have a picture
     *
     * @return the color
     */
    public static int getRandomColor() {
        Random random = new Random();
        return Color.argb(255, (random.nextInt(240) + 30),
                (random.nextInt(240) + 30), (random.nextInt(240) + 30));
    }

    /**
     * Schedule notifications for a trip (i.e. for the first activity of the trip).
     *
     * @param trip        trip to schedule notifications for
     * @param application application context
     * @param deleted     true if the trip is deleted (used to cancel notifications), false otherwise
     */
    private static void scheduleTripNotifications(Trip trip, Application application,
                                                  boolean deleted) {
        if (trip == null) {
            return;
        }

        if (trip.getActivity() == null || trip.getActivity().getActivityList() == null) {
            return;
        }


        String tripId = trip.getId();

        for (int i = 0; i < 3; i++) {
            int[] time = {Integer.parseInt(TWO_DAYS), Integer.parseInt(ONE_DAY),
                    Integer.parseInt(TWELVE_HOURS)};

            Calendar alarmTime = Calendar.getInstance();

            long timeInMillis = ((long) time[i] * MINUTE_IN_MILLIS);
            Date now = new Date();
            Date notificationTime = null;
            it.unimib.sal.one_two_trip.data.database.model.Activity firstComingActivity = null;

            for (it.unimib.sal.one_two_trip.data.database.model.Activity activity :
                    trip.getActivity().getActivityList()) {
                if (activity == null) {
                    continue;
                }

                long tripStartTime = activity.getStart_date();
                notificationTime = new Date(tripStartTime - timeInMillis);
                if (!now.after(notificationTime)) {
                    firstComingActivity = activity;
                    break;
                }
            }

            if (firstComingActivity == null) {
                continue;
            }
            alarmTime.setTime(notificationTime);

            Intent intent = new Intent(application, AlarmReceiver.class);
            intent.setData(Uri.parse("alarms://trip:" + tripId + ":" + i));

            intent.putExtra(NOTIFICATION_TYPE, NOTIFICATION_TRIP);
            intent.putExtra(SELECTED_TRIP_ID, tripId);
            intent.putExtra(NOTIFICATION_ENTITY_NAME, trip.getTitle());
            intent.putExtra(NOTIFICATION_TIME, String.valueOf(time[i]));
            intent.putExtra(NOTIFICATION_DELETED, deleted);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(application,
                    (int) System.currentTimeMillis(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) application.getSystemService(ALARM_SERVICE);

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    alarmTime.getTimeInMillis(), pendingIntent);
        }
    }

    /**
     * Schedules the notifications for the activities of a trip.
     *
     * @param activity    the activity to schedule the notifications for
     * @param application the application context
     * @param tripId      the id of the trip the activity belongs to
     * @param deleted     true if the activity is deleted (used to cancel the notifications), false otherwise
     */
    private static void scheduleActivityNotifications(it.unimib.sal.one_two_trip.data.database.model.Activity activity,
                                                      Application application, String tripId,
                                                      boolean deleted) {
        if (activity == null) {
            return;
        }

        if (activity.getStart_date() == 0) {
            return;
        }

        String activityId = activity.getId();

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
            intent.putExtra(SELECTED_ACTIVITY_ID, activityId);
            intent.putExtra(NOTIFICATION_ENTITY_NAME, activity.getTitle());
            intent.putExtra(NOTIFICATION_TIME, String.valueOf(time[i]));
            intent.putExtra(NOTIFICATION_DELETED, deleted);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(application,
                    (int) System.currentTimeMillis(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) application.getSystemService(ALARM_SERVICE);

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    alarmTime.getTimeInMillis(), pendingIntent);
        }
    }
}
