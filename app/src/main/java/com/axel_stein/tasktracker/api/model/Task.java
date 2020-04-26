package com.axel_stein.tasktracker.api.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.axel_stein.tasktracker.utils.CompareBuilder;

import org.joda.time.DateTime;

import static com.axel_stein.tasktracker.utils.TextUtil.contentEquals;
import static com.axel_stein.tasktracker.utils.TextUtil.notEmpty;

@Entity(tableName = "tasks")
public class Task {
    public static final int PRIORITY_NONE = 0;
    public static final int PRIORITY_LOW = 1;
    public static final int PRIORITY_MIDDLE = 2;
    public static final int PRIORITY_HIGH = 3;

    @PrimaryKey
    @NonNull
    private String id = "";

    @ColumnInfo
    private String title;

    @ColumnInfo
    private String description;

    @ColumnInfo
    private boolean completed;

    @ColumnInfo(name = "completed_date_time")
    private DateTime completedDateTime;

    @ColumnInfo
    private int priority;

    @ColumnInfo
    private boolean trashed;

    @ColumnInfo(name = "trashed_date_time")
    private DateTime trashedDateTime;

    @ColumnInfo(name = "book_id")
    private String bookId;

    @ColumnInfo(name = "reminder_id")
    private String reminderId;

    @Ignore
    private String dateTimeFormatted;

    @Ignore
    private boolean reminderPassed;

    @Ignore
    private String bookName;

    @Ignore
    private int color;

    public boolean hasId() {
        return notEmpty(id);
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        this.completedDateTime = completed ? new DateTime() : null;
    }

    public DateTime getCompletedDateTime() {
        return completedDateTime;
    }

    public void setCompletedDateTime(DateTime completedDateTime) {
        this.completedDateTime = completedDateTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isTrashed() {
        return trashed;
    }

    public void setTrashed(boolean trashed) {
        this.trashed = trashed;
        this.trashedDateTime = trashed ? new DateTime() : null;
    }

    public DateTime getTrashedDateTime() {
        return trashedDateTime;
    }

    public void setTrashedDateTime(DateTime trashedDateTime) {
        this.trashedDateTime = trashedDateTime;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean hasReminder() {
        return notEmpty(reminderId);
    }

    public String getReminderId() {
        return reminderId;
    }

    public void setReminderId(String reminderId) {
        this.reminderId = reminderId;
    }

    public String getDateTimeFormatted() {
        return dateTimeFormatted;
    }

    public void setDateTimeFormatted(String dateTimeFormatted) {
        this.dateTimeFormatted = dateTimeFormatted;
    }

    public boolean isReminderPassed() {
        return reminderPassed;
    }

    public void setReminderPassed(boolean reminderPassed) {
        this.reminderPassed = reminderPassed;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Task) {
            Task t = (Task) obj;
            CompareBuilder builder = new CompareBuilder();
            builder.append(id, t.id);
            builder.append(title, t.title);
            builder.append(description, t.description);
            builder.append(completed, t.completed);
            builder.append(trashed, t.trashed);
            builder.append(priority, t.priority);
            builder.append(bookId, t.bookId);
            builder.append(reminderId, t.reminderId);
            return contentEquals(id, t.id);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
