package com.axel_stein.tasktracker.ui.edit_reminder;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.api.events.Events;
import com.axel_stein.tasktracker.api.model.Reminder;
import com.axel_stein.tasktracker.api.repository.ReminderRepository;
import com.axel_stein.tasktracker.utils.LogUtil;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.inject.Inject;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

import static com.axel_stein.tasktracker.api.model.Reminder.REPEAT_PERIOD_NONE;
import static com.axel_stein.tasktracker.utils.TextUtil.contentEquals;
import static com.axel_stein.tasktracker.utils.TextUtil.isEmpty;
import static com.axel_stein.tasktracker.utils.TextUtil.notEmpty;

public class EditReminderViewModel extends ViewModel implements SingleObserver<Reminder> {
    public static final int REPEAT_PERIOD_PERSONAL = 5;

    private Reminder mReminder;
    private String mReminderId;
    private MutableLiveData<LocalDate> mDate;
    private MutableLiveData<LocalTime> mTime;
    private MutableLiveData<RepeatMode> mRepeatMode;

    public static class RepeatMode {
        private int period;
        private int count;

        public RepeatMode(int period, int count) {
            this.period = period;
            this.count = count;
        }

        public int getPeriod() {
            return period;
        }

        public int getCount() {
            return count;
        }

        @Override
        public String toString() {
            return "RepeatMode{" +
                    "period=" + period +
                    ", count=" + count +
                    '}';
        }
    }

    @Inject
    ReminderRepository mRepository;

    public EditReminderViewModel() {
        App.getAppComponent().inject(this);
    }

    public void init(String taskId, String reminderId) {
        if (mDate == null) {
            mDate = new MutableLiveData<>();
            mTime = new MutableLiveData<>();
            mRepeatMode = new MutableLiveData<>();

            LogUtil.debug("init: taskId = " + taskId + ", reminderId = " + reminderId);

            if (isEmpty(reminderId)) {
                mReminder = new Reminder();
                mReminder.setTaskId(taskId);
                mDate.setValue(new LocalDate());
                mRepeatMode.setValue(new RepeatMode(REPEAT_PERIOD_NONE, 1));
            } else if (!contentEquals(mReminderId, reminderId)) {
                mReminderId = reminderId;
                mRepository.get(mReminderId).subscribe(this);
            }
        }
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onSuccess(Reminder reminder) {
        LogUtil.debug("onSuccess: " + reminder);
        mReminder = reminder;
        mDate.postValue(reminder.getDate());
        mTime.postValue(reminder.getTime());
        mRepeatMode.setValue(new RepeatMode(reminder.getRepeatPeriod(), reminder.getRepeatCount()));
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    public LiveData<LocalDate> getDateObserver() {
        return mDate;
    }

    public void setDate(LocalDate date) {
        mDate.setValue(date);
    }

    public LocalDate getCurrentDate() {
        return mDate.getValue();
    }

    public LiveData<LocalTime> getTimeObserver() {
        return mTime;
    }

    public void setTime(@Nullable LocalTime time) {
        if (time != null) {
            time = time.withSecondOfMinute(0).withMillisOfSecond(0);
        }
        mTime.setValue(time);
    }

    public LocalTime getCurrentTime() {
        return mTime.getValue();
    }

    public LiveData<RepeatMode> getRepeatModeObserver() {
        return mRepeatMode;
    }

    public void save() {
        mReminder.setTime(mTime.getValue());
        mReminder.setDate(mDate.getValue());

        RepeatMode repeatMode = mRepeatMode.getValue();
        if (repeatMode != null) {
            mReminder.setRepeatPeriod(repeatMode.period);
            mReminder.setRepeatCount(repeatMode.count);
        }

        LogUtil.debug("save: " + mReminder);

        if (mReminder.hasId()) {
            mRepository.update(mReminder).subscribe(invalidateTasks());
        } else {
            mRepository.insert(mReminder).subscribe(invalidateTasks());
        }
    }

    public void delete() {
        if (hasId()) {
            mRepository.delete(mReminderId).subscribe(invalidateTasks());
        }
    }

    private static CompletableObserver invalidateTasks() {
        return new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {}

            @Override
            public void onComplete() {
                Events.invalidateEditTask();
                Events.invalidateTasks();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        };
    }

    public boolean hasId() {
        return notEmpty(mReminderId);
    }

    public void setRepeatCount(int count) {
        RepeatMode mode = mRepeatMode.getValue();
        if (mode != null) {
            mode.count = count;
        }
    }

    public void setRepeatPeriod(int period) {
        RepeatMode mode = mRepeatMode.getValue();
        if (mode != null) {
            mode.period = period;
        }
    }

}
