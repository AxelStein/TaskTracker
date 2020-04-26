package com.axel_stein.tasktracker.api.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

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

    @ColumnInfo
    private String taskId;

    @ColumnInfo
    private DateTime dateTime;

    @ColumnInfo
    private int repeatMode;

    @ColumnInfo
    private int repeatCount;

    @ColumnInfo
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

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
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
        DateTime shifted = null;
        switch (repeatMode) {
            case REPEAT_MODE_DAY:
                shifted = dateTime.plusDays(repeatCount);
                break;

            case REPEAT_MODE_WEEK:
                shifted = dateTime.plusWeeks(repeatCount);
                break;

            case REPEAT_MODE_MONTH:
                shifted = dateTime.plusMonths(repeatCount);
                break;

            case REPEAT_MODE_YEAR:
                shifted = dateTime.plusYears(repeatCount);
                break;
        }

        if (shifted != null) {
            if (repeatEndDate != null) {
                DateTime end = repeatEndDate.toDateTimeAtStartOfDay();
                if (shifted.isBefore(end)) {
                    dateTime = shifted;
                }
            } else {
                dateTime = shifted;
            }
        }
    }

}
