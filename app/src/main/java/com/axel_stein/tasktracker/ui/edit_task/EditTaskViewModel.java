package com.axel_stein.tasktracker.ui.edit_task;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.api.repository.TaskRepository;

import org.reactivestreams.Subscription;

import javax.inject.Inject;

import io.reactivex.CompletableObserver;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.Disposable;

import static com.axel_stein.tasktracker.ui.edit_task.EditTaskViewState.error;
import static com.axel_stein.tasktracker.ui.edit_task.EditTaskViewState.success;
import static com.axel_stein.tasktracker.utils.TextUtil.contentEquals;
import static com.axel_stein.tasktracker.utils.TextUtil.isEmpty;

public class EditTaskViewModel extends ViewModel implements FlowableSubscriber<Task> {
    private MutableLiveData<EditTaskViewState> mData;

    @Inject
    TaskRepository mRepository;

    public EditTaskViewModel() {
        App.getAppComponent().inject(this);
    }

    public LiveData<EditTaskViewState> getData(String id, String listId) {
        if (mData == null) {
            mData = new MutableLiveData<>();
            loadData(id, listId);
        }
        return mData;
    }

    private void loadData(String id, String listId) {
        if (isEmpty(id)) {
            Task task = new Task();
            task.setListId(listId);
            mRepository.insert(task).subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(Disposable d) {}

                @Override
                public void onComplete() {
                    mData.postValue(success(task));
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    mData.postValue(error(e));
                }
            });
        } else {
            mRepository.get(id).subscribe(this);
        }
    }

    @Override
    public void onSubscribe(Subscription s) {}

    @Override
    public void onNext(Task task) {
        if (mData != null) {
            mData.postValue(success(task));
        }
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
        mData.postValue(error(t));
    }

    @Override
    public void onComplete() {}

    @Nullable
    private Task getTask() {
        EditTaskViewState viewState = mData.getValue();
        if (viewState != null && viewState.getState() == EditTaskViewState.STATE_SUCCESS) {
            return viewState.getData();
        }
        return null;
    }

    public void setTitle(String title) {
        Task task = getTask();
        if (task != null && !task.isTrashed() && !contentEquals(title, task.getTitle())) {
            task.setTitle(title);
            mRepository.setTitle(task.getId(), title).subscribe();
        }
    }

    public void setCompleted(boolean completed) {
        Task task = getTask();
        if (task != null && !task.isTrashed() && task.isCompleted() != completed) {
            mRepository.toggleCompleted(task).subscribe();
        }
    }

    public void delete() {
        Task task = getTask();
        if (task != null && !task.isTrashed()) {
            mRepository.setTrashed(task, true).subscribe();
        }
    }

    public void restore() {
        Task task = getTask();
        if (task != null && task.isTrashed()) {
            mRepository.setTrashed(task, false).subscribe();
        }
    }

    public void setPriority(int priority) {
        Task task = getTask();
        if (task != null && !task.isTrashed() && task.getPriority() != priority) {
            mRepository.setPriority(task, priority).subscribe();
        }
    }
}
