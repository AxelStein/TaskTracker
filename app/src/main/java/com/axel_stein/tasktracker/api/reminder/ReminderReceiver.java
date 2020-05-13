package com.axel_stein.tasktracker.api.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.ui.IntentActionFactory;

import javax.inject.Inject;

import static com.axel_stein.tasktracker.ui.IntentActionFactory.ACTION_SHOW_NOTIFICATION;
import static com.axel_stein.tasktracker.ui.IntentActionFactory.ACTION_START;
import static com.axel_stein.tasktracker.ui.IntentActionFactory.EXTRA_TASK;

public class ReminderReceiver extends BroadcastReceiver {

    @Inject
    AndroidNotificationTray mAndroidNotificationTray;

    @Inject
    IntentActionFactory mIntentActionFactory;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (context == null || intent == null) return;
        if (intent.getAction() == null) return;

        App.getAppComponent().inject(this);

        String action = intent.getAction();
        switch (action) {
            case ACTION_START:
                mIntentActionFactory.launchReminderService();
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

