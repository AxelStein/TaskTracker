package com.axel_stein.tasktracker.api.repository;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.axel_stein.tasktracker.AndroidTest;
import com.axel_stein.tasktracker.api.model.Folder;
import com.axel_stein.tasktracker.api.model.Reminder;
import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.api.model.TaskList;
import com.axel_stein.tasktracker.api.reminder.ReminderScheduler;
import com.axel_stein.tasktracker.api.room.AppDatabase;
import com.axel_stein.tasktracker.utils.DateTimeUtil;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.After;
import org.junit.Before;

public class RepositoryTest extends AndroidTest {
    protected AppDatabase mDatabase;
    protected TaskRepository mTaskRepository;
    protected TaskListRepository mTaskListRepository;
    protected FolderRepository mFolderRepository;
    protected ReminderRepository mReminderRepository;

    @Before
    public void initDb() {
        mDatabase = Room.inMemoryDatabaseBuilder(mContext, AppDatabase.class).build();
        ReminderScheduler scheduler = new ReminderScheduler(mContext);

        mTaskRepository = new TaskRepository(mContext, mDatabase.getTaskDao(), mDatabase.getListDao(), mDatabase.getReminderDao(), new DateTimeUtil(mContext));
        mTaskListRepository = new TaskListRepository(mDatabase.getListDao(), mDatabase.getFolderDao(), mDatabase.getTaskDao());
        mFolderRepository = new FolderRepository(mDatabase.getFolderDao(), mDatabase.getListDao());
        mReminderRepository = new ReminderRepository(mDatabase.getReminderDao(), mDatabase.getTaskDao(), scheduler);
    }

    @After
    public void closeDb() {
        mDatabase.close();
    }

    protected Reminder insertTestReminder() {
        return insertTestReminder(new LocalDate(), new LocalTime());
    }

    protected Reminder insertTestReminder(@NonNull LocalDate date, @NonNull LocalTime time) {
        Reminder reminder = new Reminder();
        reminder.setDate(date);
        reminder.setTime(time);
        reminder.setTaskId(insertTestTask().getId());
        mReminderRepository.insert(reminder).subscribe();
        return reminder;
    }

    protected Task insertTestTask() {
        return insertTestTask("test");
    }

    protected Task insertTestTask(String name) {
        Task task = new Task();
        task.setTitle(name);
        mTaskRepository.insert(task).subscribe();
        return task;
    }

    protected TaskList insertTestList() {
        return insertTestList("test");
    }

    protected TaskList insertTestList(@NonNull String name) {
        TaskList list = new TaskList();
        list.setName(name);
        mTaskListRepository.insert(list).subscribe();
        return list;
    }

    protected Folder insertTestFolder() {
        return insertTestFolder("test");
    }

    protected Folder insertTestFolder(@NonNull String name) {
        Folder folder = new Folder();
        folder.setName(name);
        mFolderRepository.insert(folder).subscribe();
        return folder;
    }

}
