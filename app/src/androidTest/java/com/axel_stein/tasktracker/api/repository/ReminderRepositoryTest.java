package com.axel_stein.tasktracker.api.repository;

import com.axel_stein.tasktracker.api.exception.ReminderNotFoundException;
import com.axel_stein.tasktracker.api.exception.TaskNotFoundException;
import com.axel_stein.tasktracker.api.model.Reminder;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;

import static com.axel_stein.tasktracker.utils.TextUtil.isEmpty;
import static org.junit.Assert.assertNotNull;

public class ReminderRepositoryTest extends RepositoryTest {

    @Test
    public void testInsert() {
        Reminder reminder = new Reminder();
        reminder.setDateTime(new DateTime());
        reminder.setTaskId(insertTestTask().getId());

        mReminderRepository.insert(reminder).test().assertComplete();
        assertNotNull(reminder.getId());
    }

    @Test
    public void testInsert_args() {
        mReminderRepository.insert(null).test().assertError(IllegalArgumentException.class);
        mReminderRepository.insert(new Reminder()).test().assertError(IllegalArgumentException.class);

        Reminder reminder = new Reminder();
        reminder.setDateTime(new DateTime());
        reminder.setTaskId("id");
        mReminderRepository.insert(reminder).test().assertError(TaskNotFoundException.class);

        reminder = new Reminder();
        reminder.setTaskId(insertTestTask().getId());
        reminder.setDateTime(null);
        mReminderRepository.insert(reminder).test().assertError(IllegalArgumentException.class);
    }

    @Test
    public void testUpdate() {
        Reminder reminder = insertTestReminder();
        reminder.setRepeatMode(Reminder.REPEAT_MODE_MONTH);
        reminder.setRepeatCount(2);
        reminder.setRepeatEndDate(new LocalDate());
        mReminderRepository.update(reminder).test().assertComplete();
    }

    @Test
    public void testUpdate_args() {
        mReminderRepository.update(null).test().assertError(IllegalArgumentException.class);

        // test id
        Reminder reminder = new Reminder();
        reminder.setDateTime(new DateTime());
        mReminderRepository.update(reminder).test().assertError(IllegalArgumentException.class);

        reminder = new Reminder();
        reminder.setDateTime(new DateTime());
        reminder.setId("id");
        reminder.setTaskId(insertTestTask().getId());
        mReminderRepository.update(reminder).test().assertError(ReminderNotFoundException.class);

        // test date time
        reminder = insertTestReminder();
        reminder.setDateTime(null);
        mReminderRepository.update(reminder).test().assertError(IllegalArgumentException.class);

        // test task
        reminder = insertTestReminder();
        reminder.setTaskId("taskId");
        mReminderRepository.update(reminder).test().assertError(TaskNotFoundException.class);

        // test repeat mode
        reminder = insertTestReminder();
        reminder.setRepeatMode(Reminder.REPEAT_MODE_NONE - 1);
        mReminderRepository.update(reminder).test().assertError(IllegalArgumentException.class);

        reminder = insertTestReminder();
        reminder.setRepeatMode(Reminder.REPEAT_MODE_YEAR + 1);
        mReminderRepository.update(reminder).test().assertError(IllegalArgumentException.class);

        // test repeat count
        reminder = insertTestReminder();
        reminder.setRepeatCount(-1);
        mReminderRepository.update(reminder).test().assertError(IllegalArgumentException.class);
    }

    @Test
    public void testDelete() {
        Reminder reminder = insertTestReminder();
        String taskId = reminder.getTaskId();
        mReminderRepository.delete(reminder).test().assertComplete();
        mTaskRepository.get(taskId).test().assertValue(task -> isEmpty(task.getReminderId()));
    }

    @Test
    public void testDelete_args() {
        mReminderRepository.delete("").test().assertError(IllegalArgumentException.class);
        mReminderRepository.delete("id").test().assertError(ReminderNotFoundException.class);
    }

}