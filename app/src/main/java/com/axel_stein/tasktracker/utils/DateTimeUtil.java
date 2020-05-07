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
    public static final int FLAG_DATE = 1;
    public static final int FLAG_TIME = 2;
    public static final int FLAG_YEAR = 4;
    public static final int FLAG_TIME_24H = 8;
    public static final int FLAG_ABBREV_MONTH = 16;

    private Locale mLocale;
    private boolean m24HourFormat;

    public DateTimeUtil(Context context) {
        mLocale = ConfigurationCompat.getLocales(context.getResources().getConfiguration()).get(0);
        m24HourFormat = DateFormat.is24HourFormat(requireNonNull(context));
    }

    public String formatDate(LocalDate date) {
        if (date == null) return "";
        LocalDate today = new LocalDate();
        int flags = FLAG_DATE | FLAG_ABBREV_MONTH;
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
        return formatDate(date).concat(formatTime(time));
    }

    public String format(Date date, int flags) {
        StringBuilder builder = new StringBuilder();
        if (isFlagSet(flags, FLAG_DATE)) {
            builder.append("d");
            if (isFlagSet(flags, FLAG_ABBREV_MONTH)) {
                builder.append(" MMM");
            } else {
                builder.append(" MMMM");
            }
            if (isFlagSet(flags, FLAG_YEAR)) {
                builder.append(" yyyy");
            }
        }
        if (isFlagSet(flags, FLAG_TIME)) {
            if (isFlagSet(flags, FLAG_TIME_24H)) {
                builder.append(" H:mm");
            } else {
                builder.append(" h:mm aa");
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(builder.toString(), mLocale);
        return simpleDateFormat.format(date).toLowerCase();
    }

}
