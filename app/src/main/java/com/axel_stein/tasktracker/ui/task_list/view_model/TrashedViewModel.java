package com.axel_stein.tasktracker.ui.task_list.view_model;

import androidx.paging.DataSource;

import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.api.model.Task;

public class TrashedViewModel extends TasksViewModel {

    @Override
    protected DataSource.Factory<Integer, Task> getDataSource() {
        return mRepository.queryTrashedDataSource();
    }

    @Override
    public int getCheckModeMenuResId() {
        return R.menu.menu_action_mode_trashed;
    }

    @Override
    public int getMenuResId() {
        return R.menu.menu_trash;
    }

    @Override
    public void onMenuItemClick(int itemId) {
        if (itemId == R.id.menu_empty_trash) {
            mRepository.emptyTrash();
        } else {
            super.onMenuItemClick(itemId);
        }
    }
}
