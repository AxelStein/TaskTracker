package com.axel_stein.tasktracker.ui.edit_task;

import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.ui.BaseViewState;

public class EditTaskViewState extends BaseViewState<Task> {

    private EditTaskViewState(Task data, int currentState, Throwable error) {
        this.mData = data;
        this.mError = error;
        this.mState = currentState;
    }

    public static EditTaskViewState loading() {
        return new EditTaskViewState(null, STATE_LOADING, null);
    }

    public static EditTaskViewState success(Task task) {
        return new EditTaskViewState(task, STATE_SUCCESS, null);
    }

    public static EditTaskViewState error(Throwable t) {
        return new EditTaskViewState(null, STATE_ERROR, t);
    }
}
