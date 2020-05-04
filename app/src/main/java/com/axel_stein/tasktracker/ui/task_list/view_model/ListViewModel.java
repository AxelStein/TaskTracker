package com.axel_stein.tasktracker.ui.task_list.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.api.model.Task;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

import static com.axel_stein.tasktracker.utils.TextUtil.contentEquals;

public class ListViewModel extends TasksViewModel {
    private MutableLiveData<String> mListName = new MutableLiveData<>();
    private String mListId;

    public LiveData<String> getListName() {
        return mListName;
    }

    public void setListId(String listId) {
        if (!contentEquals(mListId, listId)) {
            mListId = listId;
            resetData();
            mListRepository.getName(mListId).subscribe(new SingleObserver<String>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onSuccess(String s) {
                    mListName.postValue(s);
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public String getListId() {
        return mListId;
    }

    @Override
    public int getMenuResId() {
        return R.menu.menu_task_list;
    }

    @Override
    public void onMenuItemClick(int itemId) {
        switch (itemId) {
            case R.id.menu_edit_list:
                mIntentActionFactory.editList(mListId);
                break;
        }
    }

    @Override
    protected DataSource.Factory<Integer, Task> getDataSource() {
        return mRepository.queryTaskListDataSource(mListId);
    }

}
