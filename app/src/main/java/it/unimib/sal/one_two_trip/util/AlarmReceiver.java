package it.unimib.sal.one_two_trip.util;

import static it.unimib.sal.one_two_trip.util.Constants.MOVE_TO_ACTIVITY;
import static it.unimib.sal.one_two_trip.util.Constants.NOTIFICATION_ACTIVITY;
import static it.unimib.sal.one_two_trip.util.Constants.NOTIFICATION_CHANNEL_ID;
import static it.unimib.sal.one_two_trip.util.Constants.NOTIFICATION_ENTITY_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.NOTIFICATION_IMPORTANCE;
import static it.unimib.sal.one_two_trip.util.Constants.NOTIFICATION_TIME;
import static it.unimib.sal.one_two_trip.util.Constants.NOTIFICATION_TRIP;
import static it.unimib.sal.one_two_trip.util.Constants.NOTIFICATION_TYPE;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_ACTIVITY_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_ACTIVITY_NOTIFICATIONS;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_NOTIFICATIONS_ON;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_TRIP_NOTIFICATIONS;

import android.Manifest;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Set;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.ui.trip.TripActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent incomingIntent) {
        SharedPreferencesUtil sharedPreferencesUtil =
                new SharedPreferencesUtil((Application) context.getApplicationContext());
        boolean isNotificationEnabled = Boolean.parseBoolean(sharedPreferencesUtil
                .readStringData(SHARED_PREFERENCES_FILE_NAME, SHARED_PREFERENCES_NOTIFICATIONS_ON));

        if (!isNotificationEnabled) {
            return;
        }

        String type = incomingIntent.getStringExtra(NOTIFICATION_TYPE);
        long tripId = Long.parseLong(incomingIntent.getStringExtra(SELECTED_TRIP_ID));
        String name = incomingIntent.getStringExtra(NOTIFICATION_ENTITY_NAME);
        int notificationTime = Integer.parseInt(incomingIntent.getStringExtra(NOTIFICATION_TIME));

        long activityId = 0;

        if (type.equalsIgnoreCase(NOTIFICATION_ACTIVITY)) {
            activityId = Long.parseLong(incomingIntent.getStringExtra(SELECTED_ACTIVITY_ID));
        }

        int notificationId = type.equalsIgnoreCase(NOTIFICATION_ACTIVITY)
                ? (int) activityId : (int) tripId;

        String notificationTimeString;
        switch (notificationTime) {
            case 720:
                notificationTimeString = context.getString(R.string.twelve_hours_extended);
                break;
            case 1440:
                notificationTimeString = context.getString(R.string.one_day_extended);
                break;
            case 2880:
                notificationTimeString = context.getString(R.string.two_days_extended);
                break;
            case 30:
                notificationTimeString = context.getString(R.string.half_hour_extended);
                break;
            case 60:
                notificationTimeString = context.getString(R.string.one_hour_extended);
                break;
            case 120:
                notificationTimeString = context.getString(R.string.two_hours_extended);
                break;
            default:
                return;
        }

        Set<String> notificationsSet;
        if (type.equalsIgnoreCase(NOTIFICATION_TRIP)) {
            notificationsSet = sharedPreferencesUtil.readStringSetData(
                    SHARED_PREFERENCES_FILE_NAME, SHARED_PREFERENCES_TRIP_NOTIFICATIONS);
        } else {
            notificationsSet = sharedPreferencesUtil.readStringSetData(
                    SHARED_PREFERENCES_FILE_NAME, SHARED_PREFERENCES_ACTIVITY_NOTIFICATIONS);
        }
        String[] notificationsAr = new String[notificationsSet.size()];
        notificationsAr = notificationsSet.toArray(notificationsAr);

        if (notificationsAr.length == 0) {
            return;
        }

        boolean found = false;
        for (String s : notificationsAr) {
            if (s.equalsIgnoreCase(String.valueOf(notificationTime))) {
                found = true;
                break;
            }
        }

        if (!found) {
            // The notification is not enabled so we return
            return;
        }

        Intent intent = new Intent(context, TripActivity.class);

        if (type.equalsIgnoreCase(NOTIFICATION_TRIP)) {
            intent = new Intent(context, TripActivity.class);
            intent.putExtra(MOVE_TO_ACTIVITY, false);
            intent.putExtra(SELECTED_TRIP_ID, tripId);
        } else {
            intent.putExtra(MOVE_TO_ACTIVITY, true);
            intent.putExtra(SELECTED_TRIP_ID, tripId);
            intent.putExtra(SELECTED_ACTIVITY_ID, activityId);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String notificationText = type.equals(NOTIFICATION_TRIP) ?
                String.format(context.getString(R.string.notification_text_trip),
                        name,
                        notificationTimeString) :
                String.format(context.getString(R.string.notification_text_activity),
                        name,
                        notificationTimeString);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24) // TO DO CHANGE ICON
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(notificationText)
                .setPriority(NOTIFICATION_IMPORTANCE)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationText))
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            // TO DO: call permission request in the onboarding activity and check it when turning
            // on notifications
            notificationManager.notify(notificationId, builder.build());
        }
    }
}
