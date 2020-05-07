package com.axel_stein.tasktracker.api.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import static com.axel_stein.tasktracker.utils.TextUtil.notEmpty;

@Entity(tableName = "reminders")
public class Reminder {
    public static final int REPEAT_MODE_NONE = 0;
    public static final int REPEAT_MODE_DAY = 1;
    public static final int REPEAT_MODE_WEEK = 2;
    public static final int REPEAT_MODE_MONTH = 3;
    public static final int REPEAT_MODE_YEAR = 4;

    @PrimaryKey
    @NonNull
    private String id = "";

    @ColumnInfo(name = "task_id")
    private String taskId;

    @ColumnInfo
    private LocalDate date;

    @ColumnInfo
    private LocalTime time;

    @ColumnInfo(name = "repeat_mode")
    private int repeatMode;

    @ColumnInfo(name = "repeat_count")
    private int repeatCount;

    @ColumnInfo(name = "repeat_end_date")
    private LocalDate repeatEndDate;

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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public int getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(int repeatMode) {
        this.repeatMode = repeatMode;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public LocalDate getRepeatEndDate() {
        return repeatEndDate;
    }

    public void setRepeatEndDate(LocalDate repeatEndDate) {
        this.repeatEndDate = repeatEndDate;
    }

    public void shiftDateTime() {
        LocalDate shifted = null;
        switch (repeatMode) {
            case REPEAT_MODE_DAY:
                shifted = date.plusDays(repeatCount);
                break;

            case REPEAT_MODE_WEEK:
                shifted = date.plusWeeks(repeatCount);
                break;

            case REPEAT_MODE_MONTH:
                shifted = date.plusMonths(repeatCount);
                break;

            case REPEAT_MODE_YEAR:
                shifted = date.plusYears(repeatCount);
                break;
        }

        if (shifted != null) {
            if (repeatEndDate != null) {
                if (shifted.isBefore(repeatEndDate)) {
                    date = shifted;
                }
            } else {
                date = shifted;
            }
        }
    }

}
