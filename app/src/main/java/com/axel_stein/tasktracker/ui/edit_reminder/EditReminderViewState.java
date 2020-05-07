package com.axel_stein.tasktracker.ui.edit_reminder;

import com.axel_stein.tasktracker.api.model.Reminder;
import com.axel_stein.tasktracker.ui.BaseViewState;

public class EditReminderViewState extends BaseViewState<Reminder> {
    public EditReminderViewState(int state, Reminder data, Throwable error) {
        super(state, data, error);
    }

    public static EditReminderViewState success(Reminder reminder) {
        return new EditReminderViewState(STATE_SUCCESS, reminder,null);
    }

    public static EditReminderViewState error(Throwable t) {
        return new EditReminderViewState(STATE_ERROR, null, t);
    }

}
