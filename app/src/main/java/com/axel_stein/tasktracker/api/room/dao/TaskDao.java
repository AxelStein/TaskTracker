package com.axel_stein.tasktracker.api.room.dao;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.axel_stein.tasktracker.api.model.Task;

import org.joda.time.DateTime;

import java.util.List;

@Dao
public abstract class TaskDao {

    @Insert
    public abstract void insert(Task task);

    @Update
    public abstract void update(Task task);

    @Query("UPDATE tasks SET title = :title WHERE id = :id")
    public abstract void setTitle(String id, String title);

    @Query("UPDATE tasks SET description = :description WHERE id = :id")
    public abstract void setDescription(String id, String description);

    @Query("UPDATE tasks SET list_id = :listId WHERE id = :id")
    public abstract void setListId(String id, String listId);

    @Query("UPDATE tasks SET completed = :completed WHERE id = :id")
    public abstract void setCompleted(String id, boolean completed);

    @Query("UPDATE tasks SET completed_date_time = :dateTime WHERE id = :id")
    public abstract void setCompletedDateTime(String id, DateTime dateTime);

    @Query("UPDATE tasks SET trashed = :trashed WHERE id = :id")
    public abstract void setTrashed(String id, boolean trashed);

    @Query("UPDATE tasks SET trashed_date_time = :dateTime WHERE id = :id")
    public abstract void setTrashedDateTime(String id, DateTime dateTime);

    @Query("UPDATE tasks SET priority = :priority WHERE id = :id")
    public abstract void setPriority(String id, int priority);

    @Query("UPDATE tasks SET reminder_id = :reminderId WHERE id = :taskId")
    public abstract void setReminderId(String taskId, String reminderId);

    @Query("DELETE FROM tasks WHERE id = :id")
    public abstract void delete(String id);

    @Query("DELETE FROM tasks")
    public abstract void delete();

    @Transaction
    public void deleteList(String listId) {
        moveToInboxWhereList(listId);
        deleteWhereList(listId);
    }

    @Query("UPDATE tasks SET list_id = NULL WHERE list_id = :listId AND (completed = 1 OR trashed = 1)")
    public abstract void moveToInboxWhereList(String listId);

    @Query("DELETE FROM tasks WHERE list_id = :listId")
    public abstract void deleteWhereList(String listId);

    @Query("UPDATE tasks SET reminder_id = NULL WHERE reminder_id = :reminderId")
    public abstract void clearReminder(String reminderId);

    @Query("SELECT * FROM tasks WHERE id = :id")
    public abstract Task get(String id);

    @Query("SELECT * FROM tasks")
    public abstract List<Task> query();

    @Query("SELECT * FROM tasks WHERE (list_id IS NULL OR list_id = '') AND trashed = 0 AND completed = 0 " +
            "ORDER BY priority DESC, title ASC")
    public abstract DataSource.Factory<Integer, Task> queryInbox();

    @Query("SELECT * FROM tasks WHERE list_id = :listId AND trashed = 0 AND completed = 0 ORDER BY priority DESC, title ASC")
    public abstract DataSource.Factory<Integer, Task> queryList(String listId);

    @Query("SELECT * FROM tasks WHERE completed = 1 AND trashed = 0 ORDER BY completed_date_time DESC")
    public abstract DataSource.Factory<Integer, Task> queryCompleted();

    @Query("SELECT * FROM tasks WHERE trashed = 1 ORDER BY trashed_date_time DESC")
    public abstract DataSource.Factory<Integer, Task> queryTrashed();

    @Query("SELECT * FROM tasks WHERE trashed = 1 ORDER BY trashed_date_time DESC")
    public abstract List<Task> queryTrashedList();

    @Query("SELECT * FROM tasks WHERE title LIKE :query AND trashed = 0")
    public abstract DataSource.Factory<Integer, Task> search(String query);

    @Query("SELECT COUNT(*) FROM tasks")
    public abstract int count();

    @Query("SELECT * FROM tasks INNER JOIN reminders ON reminders.task_id = tasks.id " +
            "WHERE reminders.date = date('now') " +
            "AND completed = 0 AND trashed = 0 " +
            "ORDER BY priority DESC, title ASC")
    public abstract DataSource.Factory<Integer, Task> queryToday();

    @Query("SELECT * FROM tasks INNER JOIN reminders ON reminders.task_id = tasks.id " +
            "WHERE reminders.date BETWEEN date('now') AND date('now', '+6 day') " +
            "AND completed = 0 AND trashed = 0 " +
            "ORDER BY priority DESC, title ASC")
    public abstract DataSource.Factory<Integer, Task> queryWeek();

    @Query("SELECT * FROM tasks WHERE trashed = 0 AND completed = 0 ORDER BY priority DESC, title ASC")
    public abstract DataSource.Factory<Integer, Task> queryAll();

    @Query("SELECT COUNT(*) FROM tasks WHERE list_id = :listId AND trashed = 0 AND completed = 0")
    public abstract int count(String listId);
}
