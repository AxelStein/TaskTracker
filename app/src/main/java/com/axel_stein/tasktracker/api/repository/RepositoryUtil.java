package com.axel_stein.tasktracker.api.repository;

import com.axel_stein.tasktracker.api.exception.FolderNotFoundException;
import com.axel_stein.tasktracker.api.exception.ReminderNotFoundException;
import com.axel_stein.tasktracker.api.exception.TaskListClosedException;
import com.axel_stein.tasktracker.api.exception.TaskListNotFoundException;
import com.axel_stein.tasktracker.api.exception.TaskNotFoundException;
import com.axel_stein.tasktracker.api.exception.TaskLockedException;
import com.axel_stein.tasktracker.api.room.dao.FolderDao;
import com.axel_stein.tasktracker.api.room.dao.ReminderDao;
import com.axel_stein.tasktracker.api.room.dao.TaskDao;
import com.axel_stein.tasktracker.api.room.dao.TaskListDao;
import com.axel_stein.tasktracker.utils.ArgsUtil.CheckRule;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

class RepositoryUtil {

    private RepositoryUtil() {}

    static CheckRule taskExists(final TaskDao dao, final String id) {
        return new CheckRule() {
            @Override
            public boolean check() {
                return dao.get(id) != null;
            }

            @Override
            public void thr() {
                throw new TaskNotFoundException();
            }
        };
    }

    static CheckRule taskNotTrashed(final TaskDao dao, final String id) {
        return new CheckRule() {
            @Override
            public boolean check() {
                return !dao.get(id).isTrashed();
            }

            @Override
            public void thr() {
                throw new TaskLockedException();
            }
        };
    }

    static CheckRule taskListExists(final TaskListDao dao, final String id) {
        return new CheckRule() {
            @Override
            public boolean check() {
                return dao.get(id) != null;
            }

            @Override
            public void thr() {
                throw new TaskListNotFoundException();
            }
        };
    }

    static CheckRule taskListNotClosed(final TaskListDao dao, final String id) {
        return new CheckRule() {
            @Override
            public boolean check() {
                return !dao.isClosed(id);
            }

            @Override
            public void thr() {
                throw new TaskListClosedException();
            }
        };
    }

    static CheckRule folderExists(final FolderDao dao, final String id) {
        return new CheckRule() {
            @Override
            public boolean check() {
                return dao.get(id) != null;
            }

            @Override
            public void thr() {
                throw new FolderNotFoundException();
            }
        };
    }

    static CheckRule reminderExists(final ReminderDao dao, final String id) {
        return new CheckRule() {
            @Override
            public boolean check() {
                return dao.get(id) != null;
            }

            @Override
            public void thr() {
                throw new ReminderNotFoundException();
            }
        };
    }

    static Completable completable(Action action) {
        return Completable.fromAction(action)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static <T> Flowable<T> flowable(Callable<T> callable) {
        return Flowable.fromCallable(callable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static <T> Single<T> single(Callable<T> callable) {
        return Single.fromCallable(callable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
