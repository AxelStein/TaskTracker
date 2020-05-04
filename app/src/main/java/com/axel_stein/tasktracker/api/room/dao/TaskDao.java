package com.axel_stein.tasktracker.api.room.dao;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.axel_stein.tasktracker.api.model.Task;

import org.joda.time.DateTime;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Query("UPDATE tasks SET title = :title WHERE id = :id")
    void setTitle(String id, String title);

    @Query("UPDATE tasks SET description = :description WHERE id = :id")
    void setDescription(String id, String description);

    @Query("UPDATE tasks SET list_id = :listId WHERE id = :id")
    void setListId(String id, String listId);

    @Query("UPDATE tasks SET completed = :completed WHERE id = :id")
    void setCompleted(String id, boolean completed);

    @Query("UPDATE tasks SET completed_date_time = :dateTime WHERE id = :id")
    void setCompletedDateTime(String id, DateTime dateTime);

    @Query("UPDATE tasks SET trashed = :trashed WHERE id = :id")
    void setTrashed(String id, boolean trashed);

    @Query("UPDATE tasks SET trashed_date_time = :dateTime WHERE id = :id")
    void setTrashedDateTime(String id, DateTime dateTime);

    @Query("UPDATE tasks SET priority = :priority WHERE id = :id")
    void setPriority(String id, int priority);

    @Query("UPDATE tasks SET reminder_id = :reminderId WHERE id = :taskId")
    void setReminderId(String taskId, String reminderId);

    @Query("DELETE FROM tasks WHERE id = :id")
    void delete(String id);

    @Query("DELETE FROM tasks")
    void delete();

    @Query("DELETE FROM tasks WHERE list_id = :listId")
    void deleteList(String listId);

    @Query("UPDATE tasks SET reminder_id = NULL WHERE reminder_id = :reminderId")
    void clearReminder(String reminderId);

    @Query("SELECT * FROM tasks WHERE id = :id")
    Task get(String id);

    @Query("SELECT * FROM tasks")
    List<Task> query();

    @Query("SELECT * FROM tasks WHERE (list_id IS NULL OR list_id = '') AND trashed = 0 AND completed = 0 " +
            "ORDER BY priority DESC, title ASC, reminder_id DESC")
    DataSource.Factory<Integer, Task> queryInbox();

    @Query("SELECT * FROM tasks WHERE list_id = :listId AND trashed = 0 AND completed = 0 ORDER BY priority DESC, title ASC, reminder_id DESC")
    DataSource.Factory<Integer, Task> queryList(String listId);

    @Query("SELECT * FROM tasks WHERE completed = 1 AND trashed = 0 ORDER BY completed_date_time DESC")
    DataSource.Factory<Integer, Task> queryCompleted();

    @Query("SELECT * FROM tasks WHERE trashed = 1 ORDER BY trashed_date_time DESC")
    DataSource.Factory<Integer, Task> queryTrashed();

    @Query("SELECT * FROM tasks WHERE trashed = 1 ORDER BY trashed_date_time DESC")
    List<Task> queryTrashedList();

    @Query("SELECT * FROM tasks WHERE title LIKE :query AND trashed = 0")
    DataSource.Factory<Integer, Task> search(String query);

    @Query("SELECT COUNT(*) FROM tasks")
    int count();

}
