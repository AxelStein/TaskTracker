package com.axel_stein.tasktracker.api.repository;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.api.model.TaskList;
import com.axel_stein.tasktracker.api.room.dao.TaskDao;
import com.axel_stein.tasktracker.api.room.dao.TaskListDao;
import com.axel_stein.tasktracker.utils.MenuItemBuilder;
import com.axel_stein.tasktracker.utils.MenuUtil;

import java.util.List;
import java.util.Objects;

import io.reactivex.Completable;

import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.completable;
import static com.axel_stein.tasktracker.utils.MenuUtil.removeGroupItems;

public class MainMenuRepository {
    private Context mContext;
    private TaskDao mTaskDao;
    private TaskListDao mTaskListDao;

    public MainMenuRepository(Context context, TaskDao taskDao, TaskListDao listDao) {
        this.mContext = context;
        this.mTaskDao = taskDao;
        this.mTaskListDao = listDao;
    }

    public Completable inflateMenu(Menu menu) {
        return completable(() -> {
            List<MenuItem> items = MenuUtil.getVisibleMenuItems(menu);
            for (MenuItem item : items) {
                switch (item.getItemId()) {
                    case R.id.fragment_all:
                        MenuUtil.setCounter(item, mTaskDao.countAll());
                        break;

                    case R.id.fragment_today:
                        MenuUtil.setCounter(item, mTaskDao.countToday());
                        break;

                    case R.id.fragment_week:
                        MenuUtil.setCounter(item, mTaskDao.countWeek());
                        break;

                    case R.id.fragment_inbox:
                        MenuUtil.setCounter(item, mTaskDao.countInbox());
                        break;

                    case R.id.fragment_completed:
                        MenuUtil.setCounter(item, mTaskDao.countCompleted());
                        break;

                    case R.id.fragment_trash:
                        MenuUtil.setCounter(item, mTaskDao.countTrashed());
                        break;
                }
            }

            removeGroupItems(menu, R.id.group_list);

            List<TaskList> taskLists = mTaskListDao.querySync();
            for (TaskList list : taskLists) {
                MenuItemBuilder.from(Objects.hash(list.getId()))
                        .setGroup(R.id.group_list)
                        .setTitle(list.getName())
                        .setCheckable(true)
                        .setIcon(R.drawable.ic_list_alt_24px)
                        .setIntent(new Intent().setAction(list.getId()))
                        .setCounterView(mContext, R.layout.action_view_counter)
                        .setCounter(mTaskDao.count(list.getId()))
                        .add(menu);
            }

            MenuItemBuilder.from(R.id.menu_add_list)
                    .setGroup(R.id.group_list)
                    .setOrder(1)
                    .setTitleRes(R.string.action_add_list)
                    .setIcon(R.drawable.ic_add_box_24px)
                    .add(menu);
        });
    }

}
