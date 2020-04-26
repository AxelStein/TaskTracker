package com.axel_stein.tasktracker.ui.edit_task;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.api.repository.TaskRepository;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import javax.inject.Inject;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;

import static com.axel_stein.tasktracker.utils.TextUtil.isEmpty;

public class EditTaskViewModel extends ViewModel implements Subscriber<Task> {
    private MutableLiveData<Task> mData;

    @Inject
    TaskRepository mRepository;

    public EditTaskViewModel() {
        App.getAppComponent().inject(this);
    }

    public LiveData<Task> getData(String id) {
        if (mData == null) {
            mData = new MutableLiveData<>();
            loadData(id);
        }
        return mData;
    }

    private void loadData(String id) {
        if (isEmpty(id)) {
            Task task = new Task();
            mRepository.insert(task).subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onComplete() {
                    mData.postValue(task);
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }
            });
        } else {
            mRepository.get(id).subscribe(this);
        }
    }

    @Override
    public void onSubscribe(Subscription s) {
        s.request(1);
    }

    @Override
    public void onNext(Task task) {
        if (mData != null) {
            mData.postValue(task);
        }
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
        // todo
    }

    @Override
    public void onComplete() {

    }

    public void onTitleChanged(String title) {
        Task task = mData.getValue();
        if (task != null && !task.isTrashed()) {
            task.setTitle(title);
            mRepository.setTitle(task.getId(), title).subscribe();
        }
    }

    public void onCompletedChanged() {
        Task task = mData.getValue();
        if (task != null && !task.isTrashed()) {
            mRepository.toggleCompleted(task).subscribe();
        }
    }

    public void onDelete() {
        Task task = mData.getValue();
        if (task != null) {
            mRepository.toggleTrashed(task).subscribe();
        }
    }

    public void onPriorityChanged(int priority) {
        Task task = mData.getValue();
        if (task != null && !task.isTrashed()) {
            mRepository.setPriority(task, priority).subscribe();
        }
    }
}
