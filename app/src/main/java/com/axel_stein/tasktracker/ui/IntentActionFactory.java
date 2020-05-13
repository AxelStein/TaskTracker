package com.axel_stein.tasktracker.ui;

import android.content.Context;
import android.content.Intent;

import androidx.core.app.JobIntentService;

import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.api.reminder.ReminderReceiver;
import com.axel_stein.tasktracker.api.reminder.ReminderService;
import com.axel_stein.tasktracker.ui.edit_list.EditListActivity;
import com.axel_stein.tasktracker.ui.edit_reminder.EditReminderActivity;
import com.axel_stein.tasktracker.ui.edit_task.EditTaskActivity;

public class IntentActionFactory {
    private static final String APP_ID = "com.axel_stein.tasktracker:";
    public static final String EXTRA_TASK = APP_ID + "EXTRA_TASK";
    public static final String EXTRA_TASK_ID = APP_ID + "EXTRA_TASK_ID";
    public static final String EXTRA_LIST_ID = APP_ID + "EXTRA_LIST_ID";
    public static final String EXTRA_REMINDER_ID = APP_ID + "EXTRA_REMINDER_ID";
    public static final String ACTION_START = APP_ID + "ACTION_START";
    public static final String ACTION_REBOOT = APP_ID + "ACTION_REBOOT";
    public static final String ACTION_SHOW_NOTIFICATION = APP_ID + "ACTION_SHOW_NOTIFICATION";

    private Context mContext;

    public IntentActionFactory(Context context) {
        mContext = context.getApplicationContext();
    }

    public void addTask() {
        Intent intent = new Intent(mContext, EditTaskActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void addTask(String listId) {
        Intent intent = new Intent(mContext, EditTaskActivity.class);
        intent.putExtra(EXTRA_LIST_ID, listId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void editTask(String id) {
        Intent intent = new Intent(mContext, EditTaskActivity.class);
        intent.putExtra(EXTRA_TASK_ID, id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void addList() {
        Intent intent = new Intent(mContext, EditListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void editList(String id) {
        Intent intent = new Intent(mContext, EditListActivity.class);
        intent.putExtra(EXTRA_LIST_ID, id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void deleteList(String id) {

    }

    public void addReminder(String taskId) {
        Intent intent = new Intent(mContext, EditReminderActivity.class);
        intent.putExtra(EXTRA_TASK_ID, taskId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void editReminder(String id) {
        Intent intent = new Intent(mContext, EditReminderActivity.class);
        intent.putExtra(EXTRA_REMINDER_ID, id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void launchReminderService() {
        Intent intent = new Intent(mContext, ReminderService.class);
        intent.setAction(ACTION_START);
        JobIntentService.enqueueWork(mContext, ReminderService.class, -1, intent);
    }

    public Intent launchReminderReceiverIntent() {
        Intent intent = new Intent(mContext, ReminderReceiver.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public Intent getShowNotificationIntent(Task task) {
        Intent intent = new Intent(mContext, ReminderReceiver.class);
        intent.setAction(ACTION_SHOW_NOTIFICATION);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
    }

}
