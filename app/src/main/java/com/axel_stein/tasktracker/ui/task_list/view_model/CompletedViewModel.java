package com.axel_stein.tasktracker.ui.task_list.view_model;

import androidx.paging.DataSource;

import com.axel_stein.tasktracker.api.model.Task;

public class CompletedViewModel extends TaskListViewModel {
    @Override
    protected DataSource.Factory<Integer, Task> getDataSource() {
        return mRepository.queryCompletedDataSource();
    }
}
