package com.axel_stein.tasktracker.api.repository;

import com.axel_stein.tasktracker.api.exception.FolderNotFoundException;
import com.axel_stein.tasktracker.api.exception.ListClosedException;
import com.axel_stein.tasktracker.api.exception.ListNotFoundException;
import com.axel_stein.tasktracker.api.exception.ReminderNotFoundException;
import com.axel_stein.tasktracker.api.exception.TaskNotFoundException;
import com.axel_stein.tasktracker.api.room.dao.FolderDao;
import com.axel_stein.tasktracker.api.room.dao.ListDao;
import com.axel_stein.tasktracker.api.room.dao.ReminderDao;
import com.axel_stein.tasktracker.api.room.dao.TaskDao;
import com.axel_stein.tasktracker.utils.ArgsUtil.CheckRule;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

class RepositoryUtil {

    private RepositoryUtil() {}

    static CheckRule taskExists(final TaskDao dao, final String id) {
        return new TaskCheckRule() {
            @Override
            public boolean check() {
                return dao.get(id) != null;
            }
        };
    }

    static CheckRule listExists(final ListDao dao, final String id) {
        return new ListCheckRule() {
            @Override
            public boolean check() {
                return dao.get(id) != null;
            }
        };
    }

    static CheckRule listNotClosed(final ListDao dao, final String id) {
        return new ListNotClosedCheckRule() {
            @Override
            public boolean check() {
                return !dao.isClosed(id);
            }
        };
    }

    static CheckRule folderExists(final FolderDao dao, final String id) {
        return new FolderCheckRule() {
            @Override
            public boolean check() {
                return dao.get(id) != null;
            }
        };
    }

    static CheckRule reminderExists(final ReminderDao dao, final String id) {
        return new ReminderCheckRule() {
            @Override
            public boolean check() {
                return dao.get(id) != null;
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

    public static class TaskCheckRule implements CheckRule {

        @Override
        public boolean check() {
            return false;
        }

        @Override
        public void thr() {
            throw new TaskNotFoundException();
        }
    }

    public static class ListCheckRule implements CheckRule {

        @Override
        public boolean check() {
            return false;
        }

        @Override
        public void thr() {
            throw new ListNotFoundException();
        }
    }

    public static class ListNotClosedCheckRule implements CheckRule {

        @Override
        public boolean check() {
            return false;
        }

        @Override
        public void thr() {
            throw new ListClosedException();
        }
    }

    public static class FolderCheckRule implements CheckRule {

        @Override
        public boolean check() {
            return false;
        }

        @Override
        public void thr() {
            throw new FolderNotFoundException();
        }
    }

    public static class ReminderCheckRule implements CheckRule {

        @Override
        public boolean check() {
            return false;
        }

        @Override
        public void thr() {
            throw new ReminderNotFoundException();
        }
    }

}
