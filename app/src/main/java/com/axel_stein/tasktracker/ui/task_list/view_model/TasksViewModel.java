package com.axel_stein.tasktracker.ui.task_list.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.api.repository.TaskRepository;
import com.axel_stein.tasktracker.ui.IntentActionFactory;

import javax.inject.Inject;

public abstract class TasksViewModel extends ViewModel {
    private LiveData<PagedList<Task>> mTaskList;

    @Inject
    TaskRepository mRepository;

    @Inject
    IntentActionFactory mIntentActionFactory;

    public TasksViewModel() {
        App.getAppComponent().inject(this);
    }

    public void onTaskClick(Task task) {
        mIntentActionFactory.editTask(task);
    }

    public void onTaskLongClick(Task task) {}

    public void setCompleted(Task task) {
        if (!task.isTrashed()) {
            mRepository.setCompleted(task, !task.isCompleted()).subscribe();
        }
    }

    public LiveData<PagedList<Task>> getTaskList() {
        if (mTaskList == null) {
            mTaskList = new LivePagedListBuilder<>(getDataSource(), 50).build();
        }
        return mTaskList;
    }

    protected void resetData() {
        mTaskList = null;
    }

    protected abstract DataSource.Factory<Integer, Task> getDataSource();

}
