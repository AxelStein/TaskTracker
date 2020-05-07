package com.axel_stein.tasktracker.ui.edit_reminder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.api.model.Reminder;
import com.axel_stein.tasktracker.api.repository.ReminderRepository;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.reactivestreams.Subscription;

import javax.inject.Inject;

import io.reactivex.FlowableSubscriber;

import static com.axel_stein.tasktracker.ui.BaseViewState.STATE_SUCCESS;
import static com.axel_stein.tasktracker.ui.edit_reminder.EditReminderViewState.error;
import static com.axel_stein.tasktracker.ui.edit_reminder.EditReminderViewState.success;
import static com.axel_stein.tasktracker.utils.TextUtil.contentEquals;
import static com.axel_stein.tasktracker.utils.TextUtil.isEmpty;

public class EditReminderViewModel extends ViewModel implements FlowableSubscriber<Reminder> {
    private String mReminderId;
    private MutableLiveData<EditReminderViewState> mReminderViewState;
    private MutableLiveData<LocalDate> mDate;
    private MutableLiveData<LocalTime> mTime;

    @Inject
    ReminderRepository mRepository;

    public EditReminderViewModel() {
        App.getAppComponent().inject(this);
        mDate = new MutableLiveData<>();
        mTime = new MediatorLiveData<>();
    }

    public LiveData<EditReminderViewState> getReminderObserver(String taskId, String reminderId) {
        if (mReminderViewState == null) {
            mReminderViewState = new MutableLiveData<>();
        }
        if (isEmpty(reminderId)) {
            Reminder reminder = new Reminder();
            reminder.setTaskId(taskId);
            onNext(reminder);
        } else if (!contentEquals(mReminderId, reminderId)) {
            mReminderId = reminderId;
            mRepository.get(mReminderId).subscribe(this);
        }
        return mReminderViewState;
    }

    @Override
    public void onSubscribe(Subscription s) {
        s.request(1);
    }

    @Override
    public void onNext(Reminder reminder) {
        mReminderViewState.postValue(success(reminder));
        mDate.postValue(reminder.getDate());
        mTime.postValue(reminder.getTime());
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
        mReminderViewState.postValue(error(t));
    }

    @Override
    public void onComplete() {}

    public LiveData<LocalDate> getDateObserver() {
        return mDate;
    }

    public void setDate(LocalDate date) {
        Reminder reminder = getReminder();
        if (reminder != null) {
            reminder.setDate(date);
            mDate.postValue(date);
        }
    }

    public LocalDate getCurrentDate() {
        return mDate.getValue();
    }

    public LiveData<LocalTime> getTimeObserver() {
        return mTime;
    }

    public void setTime(LocalTime time) {
        Reminder reminder = getReminder();
        if (reminder != null) {
            reminder.setTime(time);
            mTime.setValue(time);
        }
    }

    public LocalTime getCurrentTime() {
        return mTime.getValue();
    }

    public void save() {
        Reminder reminder = getReminder();
        if (reminder != null) {
            if (reminder.hasId()) {
                mRepository.update(reminder).subscribe();
            } else {
                mRepository.insert(reminder).subscribe();
            }
        }
    }

    public void delete() {
        Reminder reminder = getReminder();
        if (reminder != null) {
            mRepository.delete(reminder).subscribe();
        }
    }

    private Reminder getReminder() {
        EditReminderViewState state = mReminderViewState.getValue();
        if (state != null && state.getState() == STATE_SUCCESS) {
            return state.getData();
        }
        return null;
    }

    public boolean hasId() {
        return false;
    }
}
