package it.unimib.sal.one_two_trip.util;

import static it.unimib.sal.one_two_trip.util.Constants.NOTIFICATION_ENTITY_ID;
import static it.unimib.sal.one_two_trip.util.Constants.NOTIFICATION_ENTITY_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.NOTIFICATION_ENTITY_START_TIME;
import static it.unimib.sal.one_two_trip.util.Constants.NOTIFICATION_TYPE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.DateFormat;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.ui.main.HomeActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent incomingIntent) {
        String type = incomingIntent.getStringExtra(NOTIFICATION_TYPE);
        String name = incomingIntent.getStringExtra(NOTIFICATION_ENTITY_NAME);
        Long startTime = Long.parseLong(incomingIntent.getStringExtra(NOTIFICATION_ENTITY_START_TIME));
        Long id = Long.parseLong(incomingIntent.getStringExtra(NOTIFICATION_ENTITY_ID));

        Intent intent = new Intent(context, HomeActivity.class); // TO DO, CHANGE TO TRIP/ACTIVITY activity
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = System.currentTimeMillis() + "";
        int notificationImportance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelId, context.getString(R.string.app_name), notificationImportance);
            channel.setDescription(context.getString(R.string.app_name));

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24) // TO DO CHANGE ICON
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(String.format(context.getString(R.string.notification_text), type,
                        name, DateFormat.getTimeInstance(DateFormat.SHORT)
                                .format(startTime)))
                .setPriority(notificationImportance)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }
}
