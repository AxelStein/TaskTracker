package com.axel_stein.tasktracker.api.reminder;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.api.model.Reminder;
import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.api.repository.ReminderRepository;
import com.axel_stein.tasktracker.api.repository.TaskRepository;
import com.axel_stein.tasktracker.ui.IntentActionFactory;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.MutableDateTime;

import java.util.List;

import javax.inject.Inject;

import static com.axel_stein.tasktracker.ui.IntentActionFactory.ACTION_REBOOT;
import static com.axel_stein.tasktracker.ui.IntentActionFactory.ACTION_START;

public class ReminderService extends JobIntentService {

    @Inject
    ReminderScheduler mReminderScheduler;

    @Inject
    ReminderRepository mReminderRepository;

    @Inject
    TaskRepository mTaskRepository;

    @Inject
    IntentActionFactory mIntentActionFactory;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (intent.getAction() == null) return;

        App.getAppComponent().inject(this);

        String action = intent.getAction();
        switch (action) {
            case ACTION_START: {
                MutableDateTime now = new MutableDateTime();
                now.setSecondOfMinute(0);
                now.setMillisOfSecond(0);

                List<Reminder> list = mReminderRepository.queryDateTime(new LocalDate(now), new LocalTime(now));
                for (Reminder r : list) {
                    Task task = mTaskRepository.getSync(r.getTaskId());
                    sendBroadcast(mIntentActionFactory.getShowNotificationIntent(task));
                    if (r.getRepeatPeriod() != Reminder.REPEAT_PERIOD_NONE) {
                        r.shiftDateTime();
                        mReminderRepository.update(r);
                        mReminderScheduler.schedule(r);
                    }
                }
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

