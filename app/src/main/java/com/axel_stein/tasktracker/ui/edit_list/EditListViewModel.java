package com.axel_stein.tasktracker.ui.edit_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.api.model.TaskList;
import com.axel_stein.tasktracker.api.repository.TaskListRepository;

import org.reactivestreams.Subscription;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.FlowableSubscriber;

import static com.axel_stein.tasktracker.ui.BaseViewState.STATE_SUCCESS;
import static com.axel_stein.tasktracker.ui.edit_list.EditListViewState.error;
import static com.axel_stein.tasktracker.ui.edit_list.EditListViewState.success;
import static com.axel_stein.tasktracker.utils.TextUtil.contentEquals;
import static com.axel_stein.tasktracker.utils.TextUtil.isEmpty;
import static java.util.Objects.requireNonNull;

public class EditListViewModel extends ViewModel implements FlowableSubscriber<TaskList> {
    private MutableLiveData<EditListViewState> mData;
    private HashMap<String, Boolean> mHashMap;
    // private String mSrcName; todo

    @Inject
    TaskListRepository mRepository;

    public EditListViewModel() {
        App.getAppComponent().inject(this);
    }

    public LiveData<EditListViewState> getData(String id) {
        if (mData == null) {
            mData = new MutableLiveData<>();
            loadHashMap();
            if (isEmpty(id)) {
                mData.postValue(success(new TaskList()));
            } else {
                mRepository.get(id).subscribe(this);
            }
        }
        return mData;
    }

    private void loadHashMap() {
        mHashMap = new HashMap<>();
        mRepository.query().subscribe(new FlowableSubscriber<List<TaskList>>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(List<TaskList> items) {
                for (TaskList item : items) {
                    mHashMap.put(item.getName(), true);
                }
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void onSubscribe(Subscription s) {
        s.request(1);
    }

    @Override
    public void onNext(TaskList list) {
        mData.postValue(success(list));
    }

    private TaskList getList() {
        EditListViewState state = mData.getValue();
        if (state != null && state.getState() == STATE_SUCCESS) {
            return state.getData();
        }
        return null;
    }

    @Override
    public void onComplete() {}

    public boolean setName(String name) {
        TaskList list = getList();
        if (list != null) {
            if (mHashMap != null && !mHashMap.containsKey(name)) {
                list.setName(name);
                return true;
            }
        }
        return false;
    }

    public void setClosed(boolean closed) {
        requireNonNull(mData.getValue()).getData().setClosed(closed);
        TaskList list = getList();
        if (list != null && list.isClosed() != closed) {
            list.setClosed(closed);
        }
    }

    public void save() {
        TaskList list = getList();
        if (list != null) {
            if (list.hasId()) {
                mRepository.update(list).subscribe();
            } else {
                mRepository.insert(list).subscribe();
            }
        }
    }

    public void delete() {
        // todo confirmation
        TaskList list = getList();
        if (list != null) {
            mRepository.delete(list.getId()).subscribe();
        }
    }

    public boolean hasId() {
        TaskList list = getList();
        return list != null && list.hasId();
    }

    private TaskList getList() {
        EditListViewState state = mData.getValue();
        if (state != null && state.getState() == STATE_SUCCESS) {
            return state.getData();
        }
        return null;
    }

}
