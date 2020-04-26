package com.axel_stein.tasktracker.ui.task_list.view_model;

import androidx.paging.DataSource;

import com.axel_stein.tasktracker.api.model.Task;

public class BookViewModel extends TaskListViewModel {
    private String mBookId;

    public void setBookId(String bookId) {
        mBookId = bookId;
    }

    @Override
    protected DataSource.Factory<Integer, Task> getDataSource() {
        return mRepository.queryBookDataSource(mBookId);
    }

}
