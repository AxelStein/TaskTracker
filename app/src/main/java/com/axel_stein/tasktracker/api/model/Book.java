package com.axel_stein.tasktracker.api.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static com.axel_stein.tasktracker.utils.TextUtil.notEmpty;

@Entity(tableName = "books", indices = {@Index(value = {"name"}, unique = true)})
public class Book {

    @PrimaryKey
    @NonNull
    private String id = "";

    @ColumnInfo
    private String name;

    @ColumnInfo
    private int color;

    @ColumnInfo(name = "folder_id")
    private String folderId;

    private String folderName;

    @ColumnInfo
    private boolean closed;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public boolean hasId() {
        return notEmpty(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    @Override
    public String toString() {
        return "ListEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", folderId='" + folderId + '\'' +
                ", folderName='" + folderName + '\'' +
                ", closed=" + closed +
                '}';
    }
}
