package com.axel_stein.tasktracker.ui.task_list.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.api.repository.TaskListRepository;
import com.axel_stein.tasktracker.api.repository.TaskRepository;
import com.axel_stein.tasktracker.ui.IntentActionFactory;

import java.util.HashMap;

import javax.inject.Inject;

public abstract class TasksViewModel extends ViewModel {
    private LiveData<PagedList<Task>> mTaskList;
    private MutableLiveData<Integer> mSelectionCount;
    private HashMap<String, Boolean> mHashMap;

    @Inject
    TaskRepository mRepository;

    @Inject
    TaskListRepository mListRepository;

    @Inject
    IntentActionFactory mIntentActionFactory;

    public TasksViewModel() {
        App.getAppComponent().inject(this);
        mHashMap = new HashMap<>();
        mSelectionCount = new MutableLiveData<>();
    }

    public LiveData<Integer> getSelectionCount() {
        return mSelectionCount;
    }

    public void onTaskClick(Task task) {
        if (mHashMap.size() > 0) {
            selectTask(task);
        } else {
            mIntentActionFactory.editTask(task.getId());
        }
    }

    public void onTaskLongClick(Task task) {
        selectTask(task);
    }

    public void selectTask(Task task) {
        String id = task.getId();
        boolean hasKey = mHashMap.containsKey(id);
        if (hasKey) {
            mHashMap.remove(id);
        } else {
            mHashMap.put(id, true);
        }
        mSelectionCount.postValue(mHashMap.size());
    }

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

    public int getMenuResId() {
        return 0;
    }

    public boolean hasMenu() {
        return getMenuResId() != 0;
    }

    public int getCheckModeMenuResId() {
        return 0;
    }

    public void onMenuItemClick(int itemId) {}

}
