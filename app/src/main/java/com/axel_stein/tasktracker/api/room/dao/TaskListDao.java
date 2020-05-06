package com.axel_stein.tasktracker.api.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.axel_stein.tasktracker.api.model.TaskList;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface TaskListDao {

    @Insert
    void insert(TaskList list);

    @Update
    void update(TaskList list);

    @Query("UPDATE task_lists SET name = :name WHERE id = :id")
    void setName(String id, String name);

    @Query("SELECT name FROM task_lists WHERE id = :id")
    String getName(String id);

    @Query("UPDATE task_lists SET color = :color WHERE id = :id")
    void setColor(String id, int color);

    @Query("SELECT color FROM task_lists WHERE id = :id")
    int getColor(String id);

    @Query("UPDATE task_lists SET closed = :closed WHERE id = :id")
    void setClosed(String id, boolean closed);

    @Query("SELECT closed FROM task_lists WHERE id = :id")
    boolean isClosed(String id);

    @Query("UPDATE task_lists SET folder_id = :folderId WHERE id = :id")
    void setFolder(String id, String folderId);

    @Query("SELECT * FROM task_lists WHERE id = :id")
    TaskList get(String id);

    @Query("SELECT * FROM task_lists ORDER BY name")
    Flowable<List<TaskList>> query();

    @Query("SELECT * FROM task_lists WHERE folder_id = :folderId ORDER BY name")
    List<TaskList> query(String folderId);

    @Query("DELETE FROM task_lists WHERE id = :id")
    void delete(String id);

    @Query("UPDATE task_lists SET folder_id = NULL WHERE folder_id = :folderId")
    void clearFolder(String folderId);

    @Query("DELETE FROM task_lists")
    void delete();

}
