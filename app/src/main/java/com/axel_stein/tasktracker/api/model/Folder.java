package com.axel_stein.tasktracker.api.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "folders", indices = {@Index(value = {"name"}, unique = true)})
public class Folder {

    @PrimaryKey
    @NonNull
    private String id = "";

    @ColumnInfo
    private String name;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
