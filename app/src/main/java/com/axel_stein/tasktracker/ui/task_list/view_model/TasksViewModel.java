package com.axel_stein.tasktracker.ui.task_list.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.api.repository.TaskListRepository;
import com.axel_stein.tasktracker.api.repository.TaskRepository;
import com.axel_stein.tasktracker.ui.IntentActionFactory;

import java.util.HashMap;
import java.util.Set;

import javax.inject.Inject;

public abstract class TasksViewModel extends ViewModel {
    private LiveData<PagedList<Task>> mTaskList;
    private MutableLiveData<OnTaskCheckListener> mOnTaskCheckListener;
    private HashMap<String, Boolean> mHashMap; // fixme change name

    @Inject
    TaskRepository mRepository;

    @Inject
    TaskListRepository mListRepository;

    @Inject
    IntentActionFactory mIntentActionFactory;

    public TasksViewModel() {
        App.getAppComponent().inject(this);
        mHashMap = new HashMap<>();
        mOnTaskCheckListener = new MutableLiveData<>();
    }

    public LiveData<OnTaskCheckListener> getOnTaskCheckListener() {
        return mOnTaskCheckListener;
    }

    public void onTaskClick(int pos, Task task) {
        if (mHashMap.size() > 0) {
            checkTask(pos, task);
        } else {
            mIntentActionFactory.editTask(task.getId());
        }
    }

    public void onTaskLongClick(int pos, Task task) {
        checkTask(pos, task);
    }

    public void checkTask(int pos, Task task) {
        String id = task.getId();
        boolean hasKey = mHashMap.containsKey(id);
        if (hasKey) {
            mHashMap.remove(id);
        } else {
            mHashMap.put(id, true);
        }
        mOnTaskCheckListener.postValue(new OnTaskCheckListener() {
            @Override
            public HashMap<String, Boolean> getHashMap() {
                return mHashMap;
            }

            @Override
            public int getPos() {
                return pos;
            }

            @Override
            public int getCount() {
                return mHashMap.size();
            }
        });
    }

    public void clearCheckedTasks() {
        mHashMap.clear();
    }

    private void checkAllTasks() { // fixme
        PagedList<Task> list = mTaskList.getValue();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Task task = list.get(i);
                if (task != null) {
                    checkTask(i, task);
                }
            }
        }
    }

    public void completeCheckedTasks(boolean completed) {
        Set<String> ids = mHashMap.keySet();
        for (String id : ids) {
            mRepository.setCompleted(id, completed).subscribe();
        }
    }

    public void deleteCheckedTasks() {
        Set<String> ids = mHashMap.keySet();
        for (String id : ids) {
            mRepository.setTrashed(id, true).subscribe();
        }
    }

    public void restoreCheckedTasks() {
        Set<String> ids = mHashMap.keySet();
        for (String id : ids) {
            mRepository.setTrashed(id, false).subscribe();
        }
    }

    public void deleteForeverCheckedTasks() { // todo confirmation
        Set<String> ids = mHashMap.keySet();
        for (String id : ids) {
            mRepository.delete(id).subscribe();
        }
    }

    public void setCompleted(Task task) {
        if (mHashMap.size() == 0 && !task.isTrashed()) {
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
        return R.menu.menu_action_mode_tasks;
    }

    public void onMenuItemClick(int itemId) {}

    public void onActionItemClick(int itemId) {
        switch (itemId) {
            case R.id.menu_check_all:
                checkAllTasks();
                break;

            case R.id.menu_complete:
                completeCheckedTasks(true);
                break;

            case R.id.menu_delete:
                deleteCheckedTasks();
                break;

            case R.id.menu_restore:
                restoreCheckedTasks();
                break;

            case R.id.menu_delete_forever:
                deleteForeverCheckedTasks();
                break;
        }
    }

    public interface OnTaskCheckListener {
        HashMap<String, Boolean> getHashMap();
        int getPos();
        int getCount();
    }

}
