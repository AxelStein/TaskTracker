package com.axel_stein.tasktracker.ui;

import android.content.Context;
import android.content.Intent;

import com.axel_stein.tasktracker.ui.edit_list.EditListActivity;
import com.axel_stein.tasktracker.ui.edit_reminder.EditReminderActivity;
import com.axel_stein.tasktracker.ui.edit_task.EditTaskActivity;

public class IntentActionFactory {
    private static final String APP_ID = "com.axel_stein.tasktracker:";
    public static final String EXTRA_TASK_ID = APP_ID + "EXTRA_TASK_ID";
    public static final String EXTRA_LIST_ID = APP_ID + "EXTRA_LIST_ID";
    public static final String EXTRA_REMINDER_ID = APP_ID + "EXTRA_REMINDER_ID";

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

}
