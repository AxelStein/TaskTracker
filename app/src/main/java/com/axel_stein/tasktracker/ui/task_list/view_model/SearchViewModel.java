package com.axel_stein.tasktracker.ui.task_list.view_model;

import androidx.paging.DataSource;

import com.axel_stein.tasktracker.api.model.Task;

import static com.axel_stein.tasktracker.utils.TextUtil.contentEquals;

public class SearchViewModel extends TasksViewModel {
    private String mQuery;

    public void setQuery(String query) {
        if (!contentEquals(mQuery, query)) {
            mQuery = query;
            resetData();
        }
    }

    public String getQuery() {
        return mQuery;
    }

    @Override
    protected DataSource.Factory<Integer, Task> getDataSource() {
        return mRepository.search(mQuery);
    }
}
