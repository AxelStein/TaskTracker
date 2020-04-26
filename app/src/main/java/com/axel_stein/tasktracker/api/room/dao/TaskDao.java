package com.axel_stein.tasktracker.api.room.dao;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.axel_stein.tasktracker.api.model.Task;

import org.joda.time.DateTime;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Query("UPDATE tasks SET title = :title WHERE id = :id")
    void setTitle(String id, String title);

    @Query("UPDATE tasks SET description = :description WHERE id = :id")
    void setDescription(String id, String description);

    @Query("UPDATE tasks SET listId = :listId WHERE id = :id")
    void setListId(String id, String listId);

    @Query("UPDATE tasks SET completed = :completed WHERE id = :id")
    void setCompleted(String id, boolean completed);

    @Query("UPDATE tasks SET completedDateTime = :dateTime WHERE id = :id")
    void setCompletedDateTime(String id, DateTime dateTime);

    @Query("UPDATE tasks SET trashed = :trashed WHERE id = :id")
    void setTrashed(String id, boolean trashed);

    @Query("UPDATE tasks SET trashedDateTime = :dateTime WHERE id = :id")
    void setTrashedDateTime(String id, DateTime dateTime);

    @Query("UPDATE tasks SET priority = :priority WHERE id = :id")
    void setPriority(String id, int priority);

    @Query("UPDATE tasks SET reminderId = :reminderId WHERE id = :taskId")
    void setReminderId(String taskId, String reminderId);

    @Query("DELETE FROM tasks WHERE id = :id")
    void delete(String id);

    @Query("DELETE FROM tasks")
    void delete();

    @Query("DELETE FROM tasks WHERE listId = :listId")
    void deleteList(String listId);

    @Query("UPDATE tasks SET reminderId = NULL WHERE reminderId = :reminderId")
    void clearReminder(String reminderId);

    @Query("SELECT * FROM tasks WHERE id = :id")
    Task get(String id);

    @Query("SELECT * FROM tasks")
    List<Task> query();

    @Query("SELECT * FROM tasks WHERE (listId IS NULL OR listId = '') AND trashed = 0 AND completed = 0 " +
            "ORDER BY priority DESC, title ASC, reminderId DESC")
    DataSource.Factory<Integer, Task> queryInbox();

    @Query("SELECT * FROM tasks WHERE listId = :listId AND trashed = 0 ORDER BY priority DESC, title ASC, reminderId DESC")
    List<Task> queryList(String listId);

    @Query("SELECT * FROM tasks WHERE completed = 1 AND trashed = 0 ORDER BY completedDateTime DESC")
    DataSource.Factory<Integer, Task> queryCompleted();

    @Query("SELECT * FROM tasks WHERE trashed = 1 ORDER BY trashedDateTime DESC")
    DataSource.Factory<Integer, Task> queryTrashed();

    @Query("SELECT * FROM tasks WHERE title LIKE :query AND trashed = 0")
    List<Task> search(String query);

    @Query("SELECT COUNT(*) FROM tasks")
    int count();

}
