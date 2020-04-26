package com.axel_stein.tasktracker.api.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.axel_stein.tasktracker.api.model.Folder;

@Dao
public interface FolderDao {

    @Insert
    void insert(Folder folder);

    @Query("UPDATE folders SET name = :name WHERE id = :id")
    void setName(String id, String name);

    @Query("SELECT name FROM folders WHERE id = :id")
    String getName(String id);

    @Query("SELECT * FROM folders WHERE id = :id")
    Folder get(String id);

    @Query("DELETE FROM folders WHERE id = :id")
    void delete(String id);

    @Query("DELETE FROM folders")
    void delete();

}
