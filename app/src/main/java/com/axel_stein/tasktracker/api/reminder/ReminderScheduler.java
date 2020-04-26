package com.axel_stein.tasktracker.api.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.AlarmManagerCompat;

import java.util.Objects;

import static android.app.AlarmManager.RTC_WAKEUP;
import static com.axel_stein.tasktracker.utils.TextUtil.isEmpty;

public class ReminderScheduler {
    private Context mContext;
    private AlarmManager mAlarmManager;

    public ReminderScheduler(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void schedule(String taskId, long timestamp) {
        if (isEmpty(taskId)) return;
        if (timestamp < System.currentTimeMillis()) return;

        Intent intent = ReminderReceiver.getLaunchIntent(mContext);
        PendingIntent pi = PendingIntent.getBroadcast(mContext, Objects.hashCode(taskId), intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManagerCompat.setExactAndAllowWhileIdle(mAlarmManager, RTC_WAKEUP, timestamp, pi);
    }

    public void cancel(String noteId) {
        Intent intent = ReminderReceiver.getLaunchIntent(mContext);
        PendingIntent pi = PendingIntent.getBroadcast(mContext, Objects.hashCode(noteId), intent, PendingIntent.FLAG_ONE_SHOT);
        mAlarmManager.cancel(pi);
    }
}
