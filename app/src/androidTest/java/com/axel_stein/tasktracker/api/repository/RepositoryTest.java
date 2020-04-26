package com.axel_stein.tasktracker.api.repository;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.axel_stein.tasktracker.AndroidTest;
import com.axel_stein.tasktracker.api.model.Book;
import com.axel_stein.tasktracker.api.model.Folder;
import com.axel_stein.tasktracker.api.model.Reminder;
import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.api.reminder.ReminderScheduler;
import com.axel_stein.tasktracker.api.room.AppDatabase;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;

public class RepositoryTest extends AndroidTest {
    protected AppDatabase mDatabase;
    protected TaskRepository mTaskRepository;
    protected BookRepository mBookRepository;
    protected FolderRepository mFolderRepository;
    protected ReminderRepository mReminderRepository;

    @Before
    public void initDb() {
        mDatabase = Room.inMemoryDatabaseBuilder(mContext, AppDatabase.class).build();
        ReminderScheduler scheduler = new ReminderScheduler(mContext);

        mTaskRepository = new TaskRepository(mContext, mDatabase.getTaskDao(), mDatabase.getListDao(), mDatabase.getReminderDao());
        mBookRepository = new BookRepository(mDatabase.getListDao(), mDatabase.getFolderDao(), mDatabase.getTaskDao());
        mFolderRepository = new FolderRepository(mDatabase.getFolderDao(), mDatabase.getListDao());
        mReminderRepository = new ReminderRepository(mDatabase.getReminderDao(), mDatabase.getTaskDao(), scheduler);
    }

    @After
    public void closeDb() {
        mDatabase.close();
    }

    protected Reminder insertTestReminder() {
        return insertTestReminder(new DateTime());
    }

    protected Reminder insertTestReminder(@NonNull DateTime dateTime) {
        Reminder reminder = new Reminder();
        reminder.setDateTime(dateTime);
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

    protected Book insertTestBook() {
        return insertTestBook("test");
    }

    protected Book insertTestBook(@NonNull String name) {
        Book list = new Book();
        list.setName(name);
        mBookRepository.insert(list).subscribe();
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
