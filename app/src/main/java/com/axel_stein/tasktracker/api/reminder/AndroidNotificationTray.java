package com.axel_stein.tasktracker.api.reminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.api.model.Task;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.N;

public class AndroidNotificationTray {
    private static final String APP_ID = "com.axel_stein.tasktracker:";
    private static final String CHANNEL_ID = APP_ID + "CHANNEL_ID";
    private static final String CHANNEL_NAME = "Reminders";
    private static final String CHANNEL_DESCRIPTION = "Task reminders";
    private static final String GROUP_ID = APP_ID + "GROUP_ID";

    private Context mContext;
    private PendingIntentFactory mIntentFactory;

    public AndroidNotificationTray(Context context, PendingIntentFactory factory) {
        mContext = context;
        mIntentFactory = factory;
    }

    public void showNotification(Task task) {
        wakeLock();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        Notification notification = buildNotification(task);
        createNotificationChannel();
        notificationManager.notify(task.hashCode(), notification);
        if (SDK_INT >= N) {
            notificationManager.notify(0, buildSummaryNotification());
        }
    }

    public void wakeLock() {
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, APP_ID + "reminder");
            wl.acquire(3000);
        }
    }

    private Notification buildNotification(Task task) {
        String title = task.getTitle();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_24px)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.InboxStyle())
                .setGroup(GROUP_ID)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(mIntentFactory.getEditTaskIntent(task))
                .setAutoCancel(true)
                .setVibrate(new long[]{500,500,500,500,500,500,500,500})
                .setPriority(NotificationCompat.PRIORITY_MAX);
        return builder.build();
    }

    private Notification buildSummaryNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_24px)
                .setGroup(GROUP_ID)
                .setGroupSummary(true);
        return builder.build();
    }

    private void createNotificationChannel() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        if (SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(mChannel);
        }
    }
}
