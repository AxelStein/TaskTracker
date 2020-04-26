package com.axel_stein.tasktracker.api.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.axel_stein.tasktracker.api.model.Book;

import java.util.List;

@Dao
public interface BookDao {

    @Insert
    void insert(Book list);

    @Query("UPDATE books SET name = :name WHERE id = :id")
    void setName(String id, String name);

    @Query("SELECT name FROM books WHERE id = :id")
    String getName(String id);

    @Query("UPDATE books SET color = :color WHERE id = :id")
    void setColor(String id, int color);

    @Query("SELECT color FROM books WHERE id = :id")
    int getColor(String id);

    @Query("UPDATE books SET closed = :closed WHERE id = :id")
    void setClosed(String id, boolean closed);

    @Query("SELECT closed FROM books WHERE id = :id")
    boolean isClosed(String id);

    @Query("UPDATE books SET folder_id = :folderId WHERE id = :id")
    void setFolder(String id, String folderId);

    @Query("SELECT * FROM books WHERE id = :id")
    Book get(String id);

    @Query("SELECT * FROM books ORDER BY name")
    List<Book> query();

    @Query("SELECT * FROM books WHERE folder_id = :folderId ORDER BY name")
    List<Book> query(String folderId);

    @Query("DELETE FROM books WHERE id = :id")
    void delete(String id);

    @Query("UPDATE books SET folder_id = NULL WHERE folder_id = :folderId")
    void clearFolder(String folderId);

    @Query("DELETE FROM books")
    void delete();

}
