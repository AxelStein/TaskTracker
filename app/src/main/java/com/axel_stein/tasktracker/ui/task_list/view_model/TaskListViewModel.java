package com.axel_stein.tasktracker.ui.task_list.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.PagedList;
import androidx.paging.RxPagedListBuilder;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.api.repository.TaskRepository;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;

public abstract class TaskListViewModel extends ViewModel {
    protected Flowable<PagedList<Task>> mDataSource;
    protected MutableLiveData<PagedList<Task>> mTaskList;
    protected CompositeDisposable mDisposable;

    @Inject
    TaskRepository mRepository;

    public TaskListViewModel() {
        App.getAppComponent().inject(this);
        mDataSource = new RxPagedListBuilder<>(getDataSource(), 20).buildFlowable(BackpressureStrategy.LATEST);
        mDisposable = new CompositeDisposable();
    }

    public void onTaskClick(Task task) {

    }

    public void onTaskLongClick(Task task) {

    }

    public void setCompleted(Task task) {
        mRepository.setCompleted(task, !task.isCompleted()).subscribe();
    }

    public LiveData<PagedList<Task>> getTaskList() {
        if (mTaskList == null) {
            mTaskList = new MutableLiveData<>();
            loadData();
        }
        return mTaskList;
    }

    protected abstract DataSource.Factory<Integer, Task> getDataSource();

    private void loadData() {
        mDisposable.add(mDataSource.subscribe(tasks -> mTaskList.postValue(tasks), Throwable::printStackTrace));
    }

}
