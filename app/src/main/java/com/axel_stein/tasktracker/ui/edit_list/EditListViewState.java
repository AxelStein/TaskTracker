package com.axel_stein.tasktracker.ui.edit_list;

import com.axel_stein.tasktracker.api.model.TaskList;
import com.axel_stein.tasktracker.ui.BaseViewState;

public class EditListViewState extends BaseViewState<TaskList> {

    private EditListViewState(int state, TaskList data, Throwable error) {
        super(state, data, error);
    }

    public static EditListViewState loading() {
        return new EditListViewState(STATE_LOADING,null,null);
    }

    public static EditListViewState success(TaskList list) {
        return new EditListViewState(STATE_SUCCESS, list,null);
    }

    public static EditListViewState error(Throwable t) {
        return new EditListViewState(STATE_ERROR, null, t);
    }

}
