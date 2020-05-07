package com.axel_stein.tasktracker.ui.task_list.view_model;

import androidx.paging.DataSource;

import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.api.model.Task;

public class CompletedViewModel extends TasksViewModel {

    @Override
    protected DataSource.Factory<Integer, Task> getDataSource() {
        return mRepository.queryCompletedDataSource();
    }

    @Override
    public int getCheckModeMenuResId() {
        return R.menu.menu_action_mode_completed;
    }

    @Override
    public void onActionItemClick(int itemId) {
        if (itemId == R.id.menu_undo_complete) {
            completeCheckedTasks(false);
        } else {
            super.onActionItemClick(itemId);
        }
    }
}
