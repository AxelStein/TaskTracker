package com.axel_stein.tasktracker.api.reminder;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.axel_stein.tasktracker.App;

import org.joda.time.MutableDateTime;

import javax.inject.Inject;

public class ReminderService extends JobIntentService { // todo
    public static final String ACTION_START = "com.axel_stein.tasktracker:ACTION_START";
    public static final String ACTION_REBOOT = "com.axel_stein.tasktracker:ACTION_REBOOT";

    public static void launch(Context context) {
        Intent intent = new Intent(context, ReminderService.class);
        intent.setAction(ACTION_START);
        JobIntentService.enqueueWork(context, ReminderService.class, -1, intent);
    }

    public static void launch(Context context, Intent intent) {
        JobIntentService.enqueueWork(context, ReminderService.class, -1, intent);
    }

    @Inject
    ReminderScheduler mReminderScheduler;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (intent.getAction() == null) return;

        App.getAppComponent().inject(this);

        String action = intent.getAction();
        switch (action) {
            case ACTION_START: {
                MutableDateTime now = new MutableDateTime();
                now.setMillisOfSecond(0);
                /*
                List<Reminder> list = mGetReminderInteractor.queryDateTime(now.toDateTime());
                for (Reminder r : list) {
                    Note note = mGetNoteInteractor.get(r.getNoteId(), false);
                    sendBroadcast(ReminderReceiver.getShowNotificationIntent(this, note));
                    if (r.getRepeatMode() != Reminder.REPEAT_MODE_NONE) {
                        r.shiftDateTime();
                        mUpdateReminderInteractor.executeSync(r);
                        mReminderScheduler.schedule(r);
                    }
                }
                */
                break;
            }

            case ACTION_REBOOT:
                /*
                List<Reminder> list = mGetReminderInteractor.query();
                for (Reminder reminder : list) {
                    DateTime dateTime = reminder.getDateTime();
                    if (dateTime.isAfterNow()) {
                        mReminderScheduler.schedule(reminder);
                    }
                }
                */
                break;
        }
    }

}

