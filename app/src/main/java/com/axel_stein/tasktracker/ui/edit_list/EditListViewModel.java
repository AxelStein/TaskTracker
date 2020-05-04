package com.axel_stein.tasktracker.ui.edit_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.api.model.TaskList;
import com.axel_stein.tasktracker.api.repository.TaskListRepository;
import com.axel_stein.tasktracker.ui.BaseViewAction;

import org.reactivestreams.Subscription;

import javax.inject.Inject;

import io.reactivex.CompletableObserver;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.Disposable;

import static com.axel_stein.tasktracker.ui.BaseViewAction.finish;
import static com.axel_stein.tasktracker.ui.BaseViewAction.showMessage;
import static com.axel_stein.tasktracker.ui.edit_list.EditListViewState.error;
import static com.axel_stein.tasktracker.ui.edit_list.EditListViewState.success;
import static com.axel_stein.tasktracker.utils.TextUtil.isEmpty;
import static java.util.Objects.requireNonNull;

public class EditListViewModel extends ViewModel implements FlowableSubscriber<TaskList> {
    private MutableLiveData<EditListViewState> mData;
    private MutableLiveData<BaseViewAction> mActions;

    @Inject
    TaskListRepository mRepository;

    public EditListViewModel() {
        App.getAppComponent().inject(this);
    }

    public LiveData<EditListViewState> getData(String id) {
        if (mData == null) {
            mData = new MutableLiveData<>();
            if (isEmpty(id)) {
                mData.postValue(success(new TaskList()));
            } else {
                mRepository.get(id).subscribe(this);
            }
        }
        return mData;
    }

    public LiveData<BaseViewAction> getActions() {
        if (mActions == null) {
            mActions = new MutableLiveData<>();
        }
        return mActions;
    }

    public void onNameChanged(String name) {

        requireNonNull(mData.getValue()).getData().setName(name);
    }

    public void onCloseChanged(boolean closed) {
        requireNonNull(mData.getValue()).getData().setClosed(closed);
    }

    public void save() {
        CompletableObserver observer = new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                mActions.postValue(finish());
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                mActions.postValue(showMessage(R.string.error));
            }
        };

        TaskList list = requireNonNull(mData.getValue()).getData();
        if (list.hasId()) {
            mRepository.update(list).subscribe(observer);
        } else {
            mRepository.insert(list).subscribe(observer);
        }
    }

    public void delete() {
        // todo confirmation
        TaskList list = requireNonNull(mData.getValue()).getData();
        mRepository.delete(list.getId()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                mActions.postValue(finish());
            }

            @Override
            public void onError(Throwable e) {
                mData.postValue(error(e));
            }
        });
    }

    @Override
    public void onSubscribe(Subscription s) {}

    @Override
    public void onNext(TaskList list) {
        mData.postValue(success(list));
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
        mData.postValue(error(t));
    }

    @Override
    public void onComplete() {

    }

}
