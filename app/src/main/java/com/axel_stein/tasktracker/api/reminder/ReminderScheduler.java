package com.axel_stein.tasktracker.api.reminder;

import android.app.AlarmManager;
import android.content.Context;

import androidx.core.app.AlarmManagerCompat;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.api.model.Reminder;

import org.joda.time.DateTime;

import javax.inject.Inject;

import static android.app.AlarmManager.RTC_WAKEUP;
import static com.axel_stein.tasktracker.utils.TextUtil.isEmpty;

public class ReminderScheduler {
    private AlarmManager mAlarmManager;

    @Inject
    PendingIntentFactory mIntentFactory;

    public ReminderScheduler(Context context) {
        App.getAppComponent().inject(this);
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void schedule(Reminder reminder) {
        DateTime dateTime = new DateTime().withDate(reminder.getDate()).withTime(reminder.getTime());
        schedule(reminder.getId(), dateTime.getMillis());
    }

    public void schedule(String taskId, long timestamp) {
        if (isEmpty(taskId)) return;
        if (timestamp < System.currentTimeMillis()) return;
        AlarmManagerCompat.setExactAndAllowWhileIdle(mAlarmManager, RTC_WAKEUP, timestamp,
                mIntentFactory.getSetAlarmIntent(taskId));
    }

    public void cancel(String taskId) {
        mAlarmManager.cancel(mIntentFactory.getSetAlarmIntent(taskId));
    }
}
