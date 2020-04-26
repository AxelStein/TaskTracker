package com.axel_stein.tasktracker.ui;

import android.content.Context;
import android.content.Intent;

import com.axel_stein.tasktracker.api.model.Task;

public class IntentActionFactory {
    private Context mContext;

    public IntentActionFactory(Context context) {
        mContext = context;
    }

    public void addTask() {
        Intent intent = new Intent();
        mContext.startActivity(intent);
    }

    public void editTask(Task task) {
        Intent intent = new Intent();
        mContext.startActivity(intent);
    }

}
