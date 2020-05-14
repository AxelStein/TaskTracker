package com.axel_stein.tasktracker.utils;

import android.content.Context;
import android.text.format.DateFormat;

import androidx.core.os.ConfigurationCompat;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.axel_stein.tasktracker.utils.FlagUtils.isFlagSet;
import static com.axel_stein.tasktracker.utils.FlagUtils.setFlag;
import static java.util.Objects.requireNonNull;

public class DateTimeUtil {
    public static final int FLAG_DAY = 1;
    public static final int FLAG_MONTH = 2;
    public static final int FLAG_ABBREV_MONTH = 4;
    public static final int FLAG_YEAR = 8;
    public static final int FLAG_TIME = 16;
    public static final int FLAG_TIME_24H = 32;

    private Locale mLocale;
    private boolean m24HourFormat;

    public DateTimeUtil(Context context) {
        mLocale = ConfigurationCompat.getLocales(context.getResources().getConfiguration()).get(0);
        m24HourFormat = DateFormat.is24HourFormat(requireNonNull(context));
    }

    public String formatMonth(LocalDate date) {
        if (date == null) return "";
        LocalDate today = new LocalDate();
        int flags = FLAG_MONTH;
        if (!today.year().equals(date.year())) {
            flags = setFlag(flags, FLAG_YEAR);
        }
        return format(date.toDate(), flags);
    }

    public String formatDate(LocalDate date) {
        if (date == null) return "";
        LocalDate today = new LocalDate();
        int flags = FLAG_DAY | FLAG_ABBREV_MONTH;
        if (!today.year().equals(date.year())) {
            flags = setFlag(flags, FLAG_YEAR);
        }
        return format(date.toDate(), flags);
    }

    public String formatTime(LocalTime time) {
        if (time == null) return "";
        int flags = FLAG_TIME;
        if (m24HourFormat) {
            flags = setFlag(flags, FLAG_TIME_24H);
        }
        return format(time.toDateTimeToday().toDate(), flags);
    }

    public String formatDateTime(LocalDate date, LocalTime time) {
        return formatDate(date).concat(" ").concat(formatTime(time));
    }

    public String format(LocalDate date, int flags) {
        return format(date.toDate(), flags);
    }

    public String format(Date date, int flags) {
        StringBuilder builder = new StringBuilder();
        boolean space = false;
        if (isFlagSet(flags, FLAG_DAY)) {
            builder.append("d");
            space = true;
        }
        if (isFlagSet(flags, FLAG_ABBREV_MONTH)) {
            if (space) {
                builder.append(' ');
            }
            builder.append("MMM");
            space = true;
        }
        if (isFlagSet(flags, FLAG_MONTH)) {
            if (space) {
                builder.append(' ');
            }
            builder.append("MMMM");
            space = true;
        }
        if (isFlagSet(flags, FLAG_YEAR)) {
            if (space) {
                builder.append(' ');
            }
            builder.append("yyyy");
            space = true;
        }
        if (isFlagSet(flags, FLAG_TIME)) {
            if (space) {
                builder.append(' ');
            }
            if (isFlagSet(flags, FLAG_TIME_24H)) {
                builder.append("H:mm");
            } else {
                builder.append("h:mm aa");
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(builder.toString(), mLocale);
        return simpleDateFormat.format(date).toLowerCase();
    }

}
