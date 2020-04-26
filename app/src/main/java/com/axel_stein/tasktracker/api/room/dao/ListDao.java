package com.axel_stein.tasktracker.api.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.axel_stein.tasktracker.api.model.ListEntity;

import java.util.List;

@Dao
public interface ListDao {

    @Insert
    void insert(ListEntity list);

    @Query("UPDATE lists SET name = :name WHERE id = :id")
    void setName(String id, String name);

    @Query("SELECT name FROM lists WHERE id = :id")
    String getName(String id);

    @Query("UPDATE lists SET color = :color WHERE id = :id")
    void setColor(String id, int color);

    @Query("SELECT color FROM lists WHERE id = :id")
    int getColor(String id);

    @Query("UPDATE lists SET closed = :closed WHERE id = :id")
    void setClosed(String id, boolean closed);

    @Query("SELECT closed FROM lists WHERE id = :id")
    boolean isClosed(String id);

    @Query("UPDATE lists SET folderId = :folderId WHERE id = :id")
    void setFolder(String id, String folderId);

    @Query("SELECT * FROM lists WHERE id = :id")
    ListEntity get(String id);

    @Query("SELECT * FROM lists ORDER BY name")
    List<ListEntity> query();

    @Query("SELECT * FROM lists WHERE folderId = :folderId ORDER BY name")
    List<ListEntity> query(String folderId);

    @Query("DELETE FROM lists WHERE id = :id")
    void delete(String id);

    @Query("UPDATE lists SET folderId = NULL WHERE folderId = :folderId")
    void clearFolder(String folderId);

    @Query("DELETE FROM lists")
    void delete();

}
