package com.axel_stein.tasktracker.ui.edit_reminder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.utils.DateFormatter;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class EditReminderActivity extends AppCompatActivity {
    private TextView mTextDate;
    private TextView mTextTime;
    private EditReminderViewModel mViewModel;

    /*
    private TextView mTextRepeat;
    private TextView mTextRepeatEnd;
    private View mEditRepeatLayout;
    private EditText mEditRepeatCount;
    private TextView mTextRepeatPeriod;
    private int mRepeatMode;
    private int mRepeatCount = 1;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mViewModel = new ViewModelProvider(this).get(EditReminderViewModel.class);

        mTextDate = findViewById(R.id.text_date);
        mTextDate.setOnClickListener(v -> showDatePicker());

        mTextTime = findViewById(R.id.text_time);
        mTextTime.setOnClickListener(v -> showTimePicker());

        //mTextRepeat = findViewById(R.id.text_repeat);
        // mTextRepeat.setOnClickListener(v -> showSelectRepeatModeDialog());
        //mTextRepeatEnd = findViewById(R.id.text_repeat_end);
        //mTextRepeatEnd.setOnClickListener(v -> {});
        /*
        mEditRepeatLayout = findViewById(R.id.layout_edit_repeat);
        mEditRepeatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectRepeatModeDialog();
            }
        });
        mEditRepeatCount = findViewById(R.id.edit_repeat_count);
        mEditRepeatCount.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    mRepeatCount = Integer.parseInt(s.toString());
                } catch (Exception ignored) {
                }
            }
        });
        mTextRepeatPeriod = findViewById(R.id.text_repeat_period);
        */
    }

    private void showDatePicker() {
        LocalDate currentDate = getDate(mViewModel.getDate());
        int year = currentDate.getYear();
        int month = currentDate.getMonthOfYear()-1;
        int day = currentDate.getDayOfMonth();

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            LocalDate date = new LocalDate(year1, monthOfYear+1, dayOfMonth);
            mViewModel.setDate(date);

            mTextDate.setText(
                    DateFormatter.formatDate(getApplication(), date.toDateTimeAtStartOfDay().getMillis(), false)
            );
        }, year, month, day);
        dialog.show();
    }

    private void showTimePicker() {
        LocalTime currentTime = getTime(mViewModel.getTime());
        TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minutesOfHour) -> {
            LocalTime time = new LocalTime(hourOfDay, minutesOfHour);
            mViewModel.setTime(time);

            mTextTime.setText(
                    DateFormatter.formatTime(getApplication(), time.toDateTimeToday().getMillis())
            );
        }, currentTime.getHourOfDay(), currentTime.getMinuteOfHour(), DateFormat.is24HourFormat(this));
        dialog.show();
    }

    @NonNull
    private LocalDate getDate(LocalDate date) {
        if (date == null) {
            return new LocalDate();
        }
        return date;
    }

    @NonNull
    private LocalTime getTime(LocalTime time) {
        if (time == null) {
            return new LocalTime();
        }
        return time;
    }

    static class EditReminderViewModel extends ViewModel {
        LocalDate mDate;
        LocalTime mTime;

        public LocalDate getDate() {
            return mDate;
        }

        public void setDate(LocalDate date) {
            mDate = date;
        }

        public LocalTime getTime() {
            return mTime;
        }

        public void setTime(LocalTime time) {
            mTime = time;
        }
    }

}
