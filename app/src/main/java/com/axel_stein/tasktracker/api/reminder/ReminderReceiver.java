package com.axel_stein.tasktracker.api.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.api.model.Task;

import javax.inject.Inject;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String ACTION_START = "com.axel_stein.tasktracker:ACTION_START";
    public static final String ACTION_SHOW_NOTIFICATION = "com.axel_stein.tasktracker:ACTION_SHOW_NOTIFICATION";
    public static final String EXTRA_TASK = "com.axel_stein.tasktracker:EXTRA_TASK";

    public static Intent getLaunchIntent(Context context) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    @Inject
    AndroidNotificationTray mAndroidNotificationTray;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (context == null || intent == null) return;
        if (intent.getAction() == null) return;

        App.getAppComponent().inject(this);

        String action = intent.getAction();
        switch (action) {
            case ACTION_START:
                ReminderService.launch(context);
                break;

            case ACTION_SHOW_NOTIFICATION:
                Task task = (Task) intent.getSerializableExtra(EXTRA_TASK);
                if (task != null && task.hasId() && !task.isTrashed() && task.hasReminder()) {
                    mAndroidNotificationTray.showNotification(task);
                }
                break;
        }
    }
}

