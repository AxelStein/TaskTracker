package com.axel_stein.tasktracker.ui.edit_list;

import com.axel_stein.tasktracker.api.model.TaskList;
import com.axel_stein.tasktracker.ui.BaseViewState;

public class EditListViewState extends BaseViewState<TaskList> {

    private EditListViewState(TaskList data, int currentState, Throwable error) {
        this.mData = data;
        this.mError = error;
        this.mState = currentState;
    }

    public static EditListViewState loading() {
        return new EditListViewState(null, STATE_LOADING, null);
    }

    public static EditListViewState success(TaskList list) {
        return new EditListViewState(list, STATE_SUCCESS, null);
    }

    public static EditListViewState error(Throwable t) {
        return new EditListViewState(null, STATE_ERROR, t);
    }

}
