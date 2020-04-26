package com.axel_stein.tasktracker.api.reminder;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;

import com.axel_stein.tasktracker.api.model.Task;

import java.util.Objects;

public class PendingIntentFactory {
    private Context mContext;

    public PendingIntentFactory(Context context) {
        mContext = context;
    }

    public PendingIntent showTask(Task task) {
        /*
        Intent resultIntent = new Intent(mContext, EditNoteActivity.class);
        resultIntent.putExtra(EditNoteActivity.EXTRA_NOTE_ID, task.getId());
        */
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        //stackBuilder.addNextIntentWithParentStack(resultIntent);
        return stackBuilder.getPendingIntent(Objects.hashCode(task.getId()), PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
