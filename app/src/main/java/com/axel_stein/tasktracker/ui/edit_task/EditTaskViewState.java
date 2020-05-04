package com.axel_stein.tasktracker.ui.edit_task;

import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.ui.BaseViewState;

public class EditTaskViewState extends BaseViewState<Task> {

    private EditTaskViewState(int state, Task data, Throwable error) {
        super(state, data, error);
    }

    public static EditTaskViewState loading() {
        return new EditTaskViewState(STATE_LOADING, null, null);
    }

    public static EditTaskViewState success(Task task) {
        return new EditTaskViewState(STATE_SUCCESS, task,null);
    }

    public static EditTaskViewState error(Throwable t) {
        return new EditTaskViewState(STATE_ERROR,null, t);
    }

}
