package com.axel_stein.tasktracker.api.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.axel_stein.tasktracker.api.model.Folder;
import com.axel_stein.tasktracker.api.model.Book;
import com.axel_stein.tasktracker.api.model.Reminder;
import com.axel_stein.tasktracker.api.room.dao.FolderDao;
import com.axel_stein.tasktracker.api.room.dao.BookDao;
import com.axel_stein.tasktracker.api.room.dao.ReminderDao;
import com.axel_stein.tasktracker.api.room.dao.TaskDao;
import com.axel_stein.tasktracker.api.model.Task;

@Database(entities = {Task.class, Book.class, Folder.class, Reminder.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract TaskDao getTaskDao();

    public abstract BookDao getListDao();

    public abstract FolderDao getFolderDao();

    public abstract ReminderDao getReminderDao();

}