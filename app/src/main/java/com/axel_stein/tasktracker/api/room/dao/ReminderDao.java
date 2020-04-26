package com.axel_stein.tasktracker.api.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.axel_stein.tasktracker.api.model.Reminder;

import org.joda.time.DateTime;

import java.util.List;

@Dao
public interface ReminderDao {

    @Insert
    void insert(Reminder reminder);

    @Update
    void update(Reminder reminder);

    @Query("SELECT * FROM reminders WHERE id = :id")
    Reminder get(String id);

    @Query("DELETE FROM reminders WHERE id = :reminderId")
    void delete(String reminderId);

    @Query("DELETE FROM reminders WHERE taskId = :taskId")
    void deleteTask(String taskId);

    @Query("SELECT * FROM reminders ORDER BY dateTime")
    List<Reminder> query();

    @Query("SELECT * FROM reminders WHERE dateTime = :dateTime ORDER BY dateTime")
    List<Reminder> queryDateTime(DateTime dateTime);

}
