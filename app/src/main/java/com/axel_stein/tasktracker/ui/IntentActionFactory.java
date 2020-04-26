package com.axel_stein.tasktracker.ui;

import android.content.Context;
import android.content.Intent;

import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.ui.edit_task.EditTaskActivity;

public class IntentActionFactory {
    private Context mContext;

    public IntentActionFactory(Context context) {
        mContext = context.getApplicationContext();
    }

    public void addTask() {
        Intent intent = new Intent(mContext, EditTaskActivity.class);
        mContext.startActivity(intent);
    }

    public void editTask(Task task) {
        Intent intent = new Intent(mContext, EditTaskActivity.class);
        intent.setAction(task.getId());
        mContext.startActivity(intent);
    }

    public void addList() {

    }
}
