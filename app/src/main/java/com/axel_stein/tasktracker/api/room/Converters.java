package com.axel_stein.tasktracker.api.room;

import androidx.room.TypeConverter;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class Converters {

    @TypeConverter
    public static DateTime toDateTime(String value) {
        return value == null ? null : new DateTime(value);
    }

    @TypeConverter
    public static String fromDateTime(DateTime date) {
        return date == null ? null : date.toString();
    }

    @TypeConverter
    public static LocalDate toLocalDate(String value) {
        return value == null ? null : new LocalDate(value);
    }

    @TypeConverter
    public static String fromLocalDate(LocalDate date) {
        return date == null ? null : date.toString();
    }

    @TypeConverter
    public static LocalTime toLocalTime(String value) {
        return value == null ? null : new LocalTime(value);
    }

    @TypeConverter
    public static String fromLocalTime(LocalTime date) {
        return date == null ? null : date.toString();
    }
}
