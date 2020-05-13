package com.axel_stein.tasktracker.api.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.axel_stein.tasktracker.api.model.Reminder;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.List;

@Dao
public interface ReminderDao {

    @Insert
    void insert(Reminder reminder);

    @Update
    void update(Reminder reminder);

    @Query("SELECT * FROM reminders WHERE id = :id")
    Reminder get(String id);

    @Query("DELETE FROM reminders WHERE id = :id")
    void delete(String id);

    @Query("DELETE FROM reminders WHERE task_id = :taskId")
    void deleteTask(String taskId);

    @Query("SELECT * FROM reminders ORDER BY date")
    List<Reminder> query();

    @Query("SELECT * FROM reminders WHERE date = :date AND time = :time ORDER BY date")
    List<Reminder> queryDateTime(LocalDate date, LocalTime time);

}
