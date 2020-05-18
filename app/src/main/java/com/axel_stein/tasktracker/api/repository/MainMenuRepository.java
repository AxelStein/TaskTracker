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

import io.reactivex.disposables.CompositeDisposable;

import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.single;
import static com.axel_stein.tasktracker.utils.MenuUtil.removeGroupItems;

public class MainMenuRepository {
    private Context mContext;
    private TaskDao mTaskDao;
    private TaskListDao mTaskListDao;
    private CompositeDisposable mDisposable;

    public MainMenuRepository(Context context, TaskDao taskDao, TaskListDao listDao) {
        this.mContext = context;
        this.mTaskDao = taskDao;
        this.mTaskListDao = listDao;
        mDisposable = new CompositeDisposable();
    }

    private void setCounter(MenuItem item) {
        mDisposable.add(
                single(() -> {
                    switch (item.getItemId()) {
                        case R.id.fragment_all:
                            return mTaskDao.countAll();

                        case R.id.fragment_today:
                            return mTaskDao.countToday();

                        case R.id.fragment_week:
                            return mTaskDao.countWeek();

                        case R.id.fragment_inbox:
                            return mTaskDao.countInbox();

                        case R.id.fragment_completed:
                            return mTaskDao.countCompleted();

                        case R.id.fragment_trash:
                            return mTaskDao.countTrashed();
                    }
                    return 0;
                }).subscribe(counter -> MenuUtil.setCounter(item, counter))
        );
    }

    public void inflateMenu(Menu menu, int checkedItemId) {
        mDisposable.add(
                single(() -> mTaskListDao.querySync()).subscribe(taskLists -> {
                    List<MenuItem> items = MenuUtil.getVisibleMenuItems(menu);
                    for (MenuItem item : items) {
                        setCounter(item);
                        item.setChecked(item.getItemId() == checkedItemId);
                    }

                    removeGroupItems(menu, R.id.group_list);

                    MenuItemBuilder.from(R.id.menu_add_list)
                            .setGroup(R.id.group_list)
                            .setOrder(1)
                            .setTitleRes(R.string.action_add_list)
                            .setIcon(R.drawable.ic_add_box_24px)
                            .add(menu);

                    for (TaskList list : taskLists) {
                        mDisposable.add(single(() -> mTaskDao.count(list.getId()))
                                .subscribe(counter -> {
                                    int id = Objects.hash(list.getId());
                                    MenuItemBuilder.from(id)
                                            .setGroup(R.id.group_list)
                                            .setTitle(list.getName())
                                            .setCheckable(true)
                                            .setChecked(id == checkedItemId)
                                            .setIcon(R.drawable.ic_list_alt_24px)
                                            .setIntent(new Intent().setAction(list.getId()))
                                            .setCounterView(mContext, R.layout.action_view_counter)
                                            .setCounter(counter)
                                            .add(menu);
                                }));
                    }

                })
        );
    }

}
