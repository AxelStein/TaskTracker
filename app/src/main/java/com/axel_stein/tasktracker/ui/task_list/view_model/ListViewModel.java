package com.axel_stein.tasktracker.ui.task_list.view_model;

import androidx.paging.DataSource;

import com.axel_stein.tasktracker.api.model.Task;

import static com.axel_stein.tasktracker.utils.TextUtil.contentEquals;

public class ListViewModel extends TasksViewModel {
    private String mListId;

    public void setListId(String listId) {
        if (!contentEquals(mListId, listId)) {
            mListId = listId;
            resetData();
        }
    }

    public String getListId() {
        return mListId;
    }

    @Override
    protected DataSource.Factory<Integer, Task> getDataSource() {
        return mRepository.queryTaskListDataSource(mListId);
    }

}
