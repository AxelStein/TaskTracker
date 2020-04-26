package com.axel_stein.tasktracker.ui;

import android.content.Context;
import android.content.Intent;

import com.axel_stein.tasktracker.ui.edit_list.EditListActivity;
import com.axel_stein.tasktracker.ui.edit_task.EditTaskActivity;

public class IntentActionFactory {
    private static final String APP_ID = "com.axel_stein.tasktracker:";
    public static final String EXTRA_TASK_ID = APP_ID + "EXTRA_TASK_ID";
    public static final String EXTRA_LIST_ID = APP_ID + "EXTRA_LIST_ID";

    private Context mContext;

    public IntentActionFactory(Context context) {
        mContext = context.getApplicationContext();
    }

    public void addTask() {
        Intent intent = new Intent(mContext, EditTaskActivity.class);
        mContext.startActivity(intent);
    }

    public void addTask(String listId) {
        Intent intent = new Intent(mContext, EditTaskActivity.class);
        intent.putExtra(EXTRA_LIST_ID, listId);
        mContext.startActivity(intent);
    }

    public void editTask(String id) {
        Intent intent = new Intent(mContext, EditTaskActivity.class);
        intent.putExtra(EXTRA_TASK_ID, id);
        mContext.startActivity(intent);
    }

    public void addList() {
        Intent intent = new Intent(mContext, EditListActivity.class);
        mContext.startActivity(intent);
    }

    public void editList(String id) {
        Intent intent = new Intent(mContext, EditListActivity.class);
        intent.putExtra(EXTRA_LIST_ID, id);
        mContext.startActivity(intent);
    }

    public void deleteList(String id) {

    }
}
