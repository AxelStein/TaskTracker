package com.axel_stein.tasktracker.api.reminder;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.ui.IntentActionFactory;
import com.axel_stein.tasktracker.ui.edit_task.EditTaskActivity;

import java.util.Objects;

public class PendingIntentFactory {
    private Context mContext;
    private IntentActionFactory mIntentFactory;

    public PendingIntentFactory(Context context, IntentActionFactory factory) {
        mContext = context;
        mIntentFactory = factory;
    }

    public PendingIntent getSetAlarmIntent(final String taskId) {
        Intent intent = mIntentFactory.launchReminderReceiverIntent();
        return PendingIntent.getBroadcast(mContext, Objects.hashCode(taskId), intent, PendingIntent.FLAG_ONE_SHOT);
    }

    public PendingIntent getEditTaskIntent(Task task) {
        Intent resultIntent = new Intent(mContext, EditTaskActivity.class);
        resultIntent.putExtra(IntentActionFactory.EXTRA_TASK_ID, task.getId());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        return stackBuilder.getPendingIntent(Objects.hashCode(task.getId()), PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
