package com.axel_stein.tasktracker.api.repository;

import com.axel_stein.tasktracker.api.model.Reminder;
import com.axel_stein.tasktracker.api.reminder.ReminderScheduler;
import com.axel_stein.tasktracker.api.room.dao.ReminderDao;
import com.axel_stein.tasktracker.api.room.dao.TaskDao;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.List;
import java.util.UUID;

import io.reactivex.Completable;
import io.reactivex.Single;

import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.completable;
import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.reminderExists;
import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.single;
import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.taskExists;
import static com.axel_stein.tasktracker.utils.ArgsUtil.checkRules;
import static com.axel_stein.tasktracker.utils.ArgsUtil.greaterOrEqualsZero;
import static com.axel_stein.tasktracker.utils.ArgsUtil.inRange;
import static com.axel_stein.tasktracker.utils.ArgsUtil.notEmptyString;
import static com.axel_stein.tasktracker.utils.ArgsUtil.notNull;
import static com.axel_stein.tasktracker.utils.TextUtil.isEmpty;
import static java.util.Objects.requireNonNull;

public class ReminderRepository {
    private ReminderDao mDao;
    private ReminderScheduler mReminderScheduler;
    private TaskDao mTaskDao;

    public ReminderRepository(ReminderDao dao, TaskDao taskDao, ReminderScheduler scheduler) {
        mDao = requireNonNull(dao);
        mReminderScheduler = requireNonNull(scheduler);
        mTaskDao = requireNonNull(taskDao);
    }

    public Completable insert(final Reminder reminder) {
        return completable(() -> {
            checkRules(notNull(reminder));

            String taskId = reminder.getTaskId();
            checkRules(
                    notNull(reminder.getDate()),
                    notEmptyString(taskId),
                    taskExists(mTaskDao, taskId)
            );

            if (isEmpty(reminder.getId())) reminder.setId(UUID.randomUUID().toString());
            mDao.insert(reminder);
            mTaskDao.setReminderId(taskId, reminder.getId());

            LocalTime time = reminder.getTime();
            if (time != null) {
                mReminderScheduler.schedule(reminder);
            }
        });
    }

    public Completable update(final Reminder reminder) {
        return completable(() -> {
            checkRules(notNull(reminder));

            String id = reminder.getId();
            String taskId = reminder.getTaskId();
            checkRules(
                    notEmptyString(id, taskId),
                    notNull(reminder.getDate()),
                    inRange(reminder.getRepeatPeriod(), Reminder.REPEAT_PERIOD_NONE, Reminder.REPEAT_PERIOD_YEAR),
                    greaterOrEqualsZero(reminder.getRepeatCount()),
                    reminderExists(mDao, id),
                    taskExists(mTaskDao, taskId)
            );

            mDao.update(reminder);
            mTaskDao.setReminderId(taskId, id);

            LocalTime time = reminder.getTime();
            if (time != null) {
                mReminderScheduler.schedule(reminder);
            }
        });
    }

    public Single<Reminder> get(final String id) {
        return single(() -> {
            checkRules(
                    notEmptyString(id),
                    reminderExists(mDao, id)
            );
            return mDao.get(id);
        });
    }

    public List<Reminder> queryDateTime(LocalDate date, LocalTime time) {
        return mDao.queryDateTime(date, time);
    }

    public Completable delete(Reminder reminder) {
        checkRules(notNull(reminder));
        return delete(reminder.getId());
    }

    public Completable delete(final String id) {
        return completable(() -> {
            checkRules(
                    notEmptyString(id),
                    reminderExists(mDao, id)
            );
            mDao.delete(id);
            mTaskDao.clearReminder(id);
            mReminderScheduler.cancel(id);
        });
    }

}
