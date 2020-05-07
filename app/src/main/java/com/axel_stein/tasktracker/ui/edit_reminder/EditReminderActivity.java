package com.axel_stein.tasktracker.ui.edit_reminder;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.ui.IntentActionFactory;
import com.axel_stein.tasktracker.utils.DateFormatter;
import com.axel_stein.tasktracker.utils.MenuUtil;
import com.axel_stein.tasktracker.utils.ViewUtil;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class EditReminderActivity extends AppCompatActivity {
    private TextView mTextDate;
    private View mBtnClearDate;
    private TextView mTextTime;
    private View mBtnClearTime;
    private EditReminderViewModel mViewModel;
    private boolean mEnableDoneButton;

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
        setContentView(R.layout.activity_edit_reminder);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mTextDate = findViewById(R.id.text_date);
        mTextDate.setOnClickListener(v -> showDatePicker());

        mBtnClearDate = findViewById(R.id.btn_clear_date);
        mBtnClearDate.setOnClickListener(v -> mViewModel.setDate(null));

        mTextTime = findViewById(R.id.text_time);
        mTextTime.setOnClickListener(v -> showTimePicker());

        mBtnClearTime = findViewById(R.id.btn_clear_time);
        mBtnClearTime.setOnClickListener(v -> mViewModel.setTime(null));

        mViewModel = new ViewModelProvider(this).get(EditReminderViewModel.class);

        String taskId = getIntent().getStringExtra(IntentActionFactory.EXTRA_TASK_ID);
        String reminderId = getIntent().getStringExtra(IntentActionFactory.EXTRA_REMINDER_ID);
        mViewModel.getReminderObserver(taskId, reminderId).observe(this, viewState -> {
            // todo
        });

        mViewModel.getDateObserver().observe(this, date -> {
            String dateFormatted = null;
            if (date != null) {
                dateFormatted = DateFormatter.formatDate(getApplication(), date.toDateTimeAtStartOfDay().getMillis(), false);
            }
            mTextDate.setText(dateFormatted);
            ViewUtil.setVisible(date != null, mBtnClearDate);
            mEnableDoneButton = date != null;
            invalidateOptionsMenu();
        });

        mViewModel.getTimeObserver().observe(this, time -> {
            String timeFormatted = null;
            if (time != null) {
                timeFormatted = DateFormatter.formatTime(getApplication(), time.toDateTimeToday().getMillis());
            }
            mTextTime.setText(timeFormatted);
            ViewUtil.setVisible(time != null, mBtnClearTime);
        });

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
        OnDateSetListener listener = (view, year, month, dayOfMonth) ->
                mViewModel.setDate(new LocalDate(year, month +1, dayOfMonth));

        LocalDate currentDate = mViewModel.getCurrentDate();
        if (currentDate == null) {
            currentDate = new LocalDate();
        }
        int year = currentDate.getYear();
        int month = currentDate.getMonthOfYear()-1;
        int day = currentDate.getDayOfMonth();

        DatePickerDialog dialog = new DatePickerDialog(this, listener, year, month, day);
        dialog.show();
    }

    private void showTimePicker() {
        OnTimeSetListener listener = (view, hourOfDay, minutesOfHour) ->
                mViewModel.setTime(new LocalTime(hourOfDay, minutesOfHour));

        LocalTime currentTime = mViewModel.getCurrentTime();
        if (currentTime == null) {
            currentTime = new LocalTime();
        }
        int hours = currentTime.getHourOfDay();
        int minutes = currentTime.getMinuteOfHour();
        boolean is24H = DateFormat.is24HourFormat(this);

        TimePickerDialog dialog = new TimePickerDialog(this, listener, hours, minutes, is24H);
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_reminder, menu);
        MenuUtil.tintMenuIconsAttr(this, menu, R.attr.menuItemTintColor);
        MenuUtil.show(menu, mViewModel.hasId(), R.id.menu_delete);
        MenuUtil.enable(menu, mEnableDoneButton, R.id.menu_done);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.menu_delete:
                mViewModel.delete();
                finish();
                break;

            case R.id.menu_done:
                mViewModel.save();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
