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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.utils.DateTimeUtil;
import com.axel_stein.tasktracker.utils.DisplayUtil;
import com.axel_stein.tasktracker.utils.LogUtil;
import com.axel_stein.tasktracker.utils.MenuUtil;
import com.axel_stein.tasktracker.utils.ViewUtil;
import com.google.android.material.snackbar.Snackbar;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.axel_stein.tasktracker.ui.IntentActionFactory.EXTRA_REMINDER_ID;
import static com.axel_stein.tasktracker.ui.IntentActionFactory.EXTRA_TASK_ID;
import static com.axel_stein.tasktracker.ui.edit_reminder.EditReminderViewModel.REPEAT_PERIOD_PERSONAL;

public class EditReminderActivity extends AppCompatActivity {
    private CalendarView mCalendarView;
    private TextView mTextTime;
    private View mBtnClearTime;
    private Spinner mSpinnerRepeatMode;
    private Spinner mSpinnerRepeatCount;
    private Spinner mSpinnerRepeatPeriod;
    private View mEditRepeatLayout;
    private EditReminderViewModel mViewModel;
    private TextView mTextRepeatEndDate;
    private View mBtnClearRepeatEndDate;

    @Inject
    DateTimeUtil mDateTimeUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        setContentView(R.layout.activity_edit_reminder);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mCalendarView = findViewById(R.id.calendar_view);
        mCalendarView.setOnDateChangeListener((view, year, month, dayOfMonth) ->
                mViewModel.setDate(new LocalDate(year, month +1, dayOfMonth)));

        mTextTime = findViewById(R.id.text_time);
        mTextTime.setOnClickListener(v -> showTimePicker());

        mBtnClearTime = findViewById(R.id.btn_clear_time);
        mBtnClearTime.setOnClickListener(v -> mViewModel.setTime(null));

        mTextRepeatEndDate = findViewById(R.id.text_repeat_end_date);
        mTextRepeatEndDate.setOnClickListener(v ->
                showDatePicker(mViewModel.getRepeatEndDate(),
                        (view, year, month, dayOfMonth) ->
                                mViewModel.setRepeatEndDate(
                                        new LocalDate(year, month +1, dayOfMonth)
                                )
                )
        );

        mBtnClearRepeatEndDate = findViewById(R.id.btn_clear_repeat_end_date);
        mBtnClearRepeatEndDate.setOnClickListener(v -> mViewModel.setRepeatEndDate(null));

        mSpinnerRepeatMode = findViewById(R.id.spinner_repeat_mode);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.repeat_mode, R.layout.item_spinner_repeat_mode);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerRepeatMode.setAdapter(adapter);
        mSpinnerRepeatMode.setDropDownHorizontalOffset(DisplayUtil.dpToPx(this, 72));
        mSpinnerRepeatMode.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ViewUtil.setVisible(position == REPEAT_PERIOD_PERSONAL, mEditRepeatLayout,
                        findViewById(R.id.divider_edit_repeat));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        mEditRepeatLayout = findViewById(R.id.layout_edit_repeat);
        mSpinnerRepeatCount = findViewById(R.id.spinner_repeat_count);
        setupRepeatCountSpinner();

        mSpinnerRepeatPeriod = findViewById(R.id.spinner_repeat_period);

        String taskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        String reminderId = getIntent().getStringExtra(EXTRA_REMINDER_ID);

        mViewModel = new ViewModelProvider(this).get(EditReminderViewModel.class);
        mViewModel.init(taskId, reminderId);
        mViewModel.getDateObserver().observe(this, date ->
                mCalendarView.setDate(date.toDate().getTime()));

        mViewModel.getTimeObserver().observe(this, time -> {
            mTextTime.setText(mDateTimeUtil.formatTime(time));
            ViewUtil.setVisible(time != null, mBtnClearTime);
        });

        mViewModel.getRepeatEndDateObserver().observe(this, date -> {
            mTextRepeatEndDate.setText(mDateTimeUtil.formatDate(date));
            ViewUtil.setVisible(date != null, mBtnClearRepeatEndDate);
        });

        mViewModel.getMessageObserver().observe(this, msg -> {
            if (msg != 0) {
                Snackbar.make(toolbar, msg, Snackbar.LENGTH_SHORT).show();
            }
        });

        mViewModel.getRepeatModeObserver().observe(this, repeatMode -> {
            LogUtil.debug("observe: " + repeatMode);
            boolean showEditRepeat = repeatMode.getCount() > 1;
            mSpinnerRepeatMode.setSelection(showEditRepeat ? REPEAT_PERIOD_PERSONAL : repeatMode.getPeriod());
            ViewUtil.setVisible(showEditRepeat, mEditRepeatLayout, findViewById(R.id.divider_edit_repeat));
            if (showEditRepeat) {
                mSpinnerRepeatCount.setSelection(repeatMode.getCount() - 1);
                mSpinnerRepeatPeriod.setSelection(repeatMode.getPeriod() - 1);
            }
            invalidateOptionsMenu();
        });
    }

    private void setupRepeatCountSpinner() {
        List<CharSequence> items = new ArrayList<>();
        for (int i = 0; i < 365; i++) {
            items.add("" + (i + 1));
        }

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(EditReminderActivity.this,
                android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.addAll(items);
        mSpinnerRepeatCount.setAdapter(adapter);
        mSpinnerRepeatCount.setSelection(0);
    }

    private void showDatePicker(LocalDate currentDate, OnDateSetListener listener) {
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
                int mode = mSpinnerRepeatMode.getSelectedItemPosition();
                if (mode == REPEAT_PERIOD_PERSONAL) {
                    mViewModel.setRepeatPeriod(mSpinnerRepeatPeriod.getSelectedItemPosition() + 1);
                    mViewModel.setRepeatCount(mSpinnerRepeatCount.getSelectedItemPosition() + 1);
                } else {
                    mViewModel.setRepeatPeriod(mode);
                    mViewModel.setRepeatCount(1);
                }
                mViewModel.save();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
