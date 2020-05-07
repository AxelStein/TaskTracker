package com.axel_stein.tasktracker.api.repository;

import android.content.Context;
import android.text.format.DateFormat;

import androidx.arch.core.util.Function;
import androidx.core.os.ConfigurationCompat;
import androidx.paging.DataSource;

import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.api.model.Reminder;
import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.api.room.dao.ReminderDao;
import com.axel_stein.tasktracker.api.room.dao.TaskDao;
import com.axel_stein.tasktracker.api.room.dao.TaskListDao;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.completable;
import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.flowable;
import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.reminderExists;
import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.single;
import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.taskExists;
import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.taskListExists;
import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.taskNotTrashed;
import static com.axel_stein.tasktracker.utils.ArgsUtil.checkRules;
import static com.axel_stein.tasktracker.utils.ArgsUtil.inRange;
import static com.axel_stein.tasktracker.utils.ArgsUtil.notEmptyString;
import static com.axel_stein.tasktracker.utils.ArgsUtil.notNull;
import static com.axel_stein.tasktracker.utils.TextUtil.isEmpty;
import static com.axel_stein.tasktracker.utils.TextUtil.notEmpty;
import static java.util.Objects.requireNonNull;

public class TaskRepository {
    private final String ACTION_INBOX;

    private TaskDao mDao;
    private TaskListDao mListDao;
    private ReminderDao mReminderDao;
    private Locale mLocale;
    private boolean m24HFormat;

    public TaskRepository(Context context, TaskDao dao, TaskListDao listDao, ReminderDao reminderDao) {
        ACTION_INBOX = context.getString(R.string.action_inbox);
        mDao = requireNonNull(dao);
        mListDao = requireNonNull(listDao);
        mReminderDao = requireNonNull(reminderDao);
        m24HFormat = DateFormat.is24HourFormat(requireNonNull(context));
        mLocale = ConfigurationCompat.getLocales(context.getResources().getConfiguration()).get(0);
    }

    public Single<Task> insert(final Task task) {
        return single(() -> {
            checkRules(notNull(task));
            if (notEmpty(task.getListId())) {
                checkRules(taskListExists(mListDao, task.getListId()));
            }
            if (notEmpty(task.getReminderId())) {
                checkRules(reminderExists(mReminderDao, task.getReminderId()));
            }
            if (isEmpty(task.getId())) task.setId(UUID.randomUUID().toString());
            mDao.insert(task);
            return task;
        });
    }

    public Completable update(final Task task) {
        return completable(() -> {
            checkRules(
                    notNull(task),
                    notEmptyString(task.getId())
            );
            if (notEmpty(task.getListId())) {
                checkRules(taskListExists(mListDao, task.getListId()));
            }
            if (notEmpty(task.getReminderId())) {
                checkRules(reminderExists(mReminderDao, task.getReminderId()));
            }
            mDao.update(task);
        });
    }

    public Completable setTitle(final String id, final String title) {
        return completable(() -> {
            checkRules(
                    notEmptyString(id),
                    taskExists(mDao, id),
                    taskNotTrashed(mDao, id)
            );
            mDao.setTitle(id, title);
        });
    }

    public Completable setDescription(final String id, final String description) {
        return completable(() -> {
            checkRules(
                    notEmptyString(id),
                    taskExists(mDao, id),
                    taskNotTrashed(mDao, id)
            );
            mDao.setDescription(id, description);
        });
    }

    public Completable setListId(final String id, final String listId) {
        return completable(() -> {
            checkRules(
                    notEmptyString(id),
                    taskExists(mDao, id),
                    taskNotTrashed(mDao, id)
            );
            if (notEmpty(listId)) {
                checkRules(taskListExists(mListDao, listId));
            }
            mDao.setListId(id, listId);
        });
    }

    public Completable toggleCompleted(Task task) {
        checkRules(notNull(task));
        return setCompleted(task, !task.isCompleted());
    }

    public Completable setCompleted(Task task, boolean completed) {
        checkRules(notNull(task));
        task.setCompleted(completed);
        return setCompleted(task.getId(), completed);
    }

    public Completable setCompleted(final String id, final boolean completed) {
        return completable(() -> {
            checkRules(
                    notEmptyString(id),
                    taskExists(mDao, id),
                    taskNotTrashed(mDao, id)
            );
            mDao.setCompleted(id, completed);
            mDao.setCompletedDateTime(id, completed ? new DateTime() : null);
        });
    }

    public Completable toggleTrashed(Task task) {
        checkRules(notNull(task));
        return setTrashed(task, !task.isTrashed());
    }

    public Completable setTrashed(Task task, boolean trashed) {
        checkRules(notNull(task));
        task.setTrashed(trashed);
        return setTrashed(task.getId(), trashed);
    }

    public Completable setTrashed(final String id, final boolean trashed) {
        return completable(() -> {
            checkRules(
                    notEmptyString(id),
                    taskExists(mDao, id)
            );
            mDao.setTrashed(id, trashed);
            mDao.setTrashedDateTime(id, trashed ? new DateTime() : null);
        });
    }

    public Completable setPriority(Task task, int priority) {
        checkRules(notNull(task));
        task.setPriority(priority);
        return setPriority(task.getId(), priority);
    }

    public Completable setPriority(final String id, final int priority) {
        return completable(() -> {
            checkRules(
                    notEmptyString(id),
                    inRange(priority, Task.PRIORITY_NONE, Task.PRIORITY_HIGH),
                    taskExists(mDao, id),
                    taskNotTrashed(mDao, id)
            );
            mDao.setPriority(id, priority);
        });
    }

    public Completable delete(final String id) {
        return completable(() -> {
            checkRules(
                    notEmptyString(id),
                    taskExists(mDao, id)
            );
            mDao.delete(id);
            mReminderDao.deleteTask(id);
        });
    }

    public void emptyTrash() {
        Single.fromCallable(() -> mDao.queryTrashedList())
                .subscribeOn(Schedulers.io())
                .toObservable()
                .flatMapIterable(items -> items)
                .flatMapCompletable(task -> delete(task.getId()))
                .subscribe();
    }

    public Single<Task> getOrInsert(final String id, final String listId) { // fixme not tested
        Single<Task> single;
        if (isEmpty(id)) {
            Task task = new Task();
            task.setListId(listId);
            single = insert(task);
        } else {
            single = get(id);
        }
        return single.flatMap(task -> Single.fromCallable(() -> new TaskFunction().apply(task)).subscribeOn(Schedulers.io()));
    }

    public Single<Task> get(final String id) {
        return single(() -> {
            checkRules(
                    notEmptyString(id),
                    taskExists(mDao, id)
            );
            return mDao.get(id);
        });
    }

    public Completable duplicate(Task task) {
        return completable(() -> {
            Task copy = new Task(task);
            copy.setId("");
            insert(copy).subscribe();
        });
    }

    public Flowable<List<Task>> query() {
        return flowable(() -> mDao.query());
    }

    public DataSource.Factory<Integer, Task> queryInboxDataSource() {
        return mDao.queryInbox().map(new TaskFunction());
    }

    public DataSource.Factory<Integer, Task> queryCompletedDataSource() {
        return mDao.queryCompleted().map(new TaskFunction());
    }

    public DataSource.Factory<Integer, Task> queryTrashedDataSource() {
        return mDao.queryTrashed().map(new TaskFunction());
    }

    public DataSource.Factory<Integer, Task> queryTaskListDataSource(String listId) {
        checkRules(notEmptyString(listId)); // todo listExists(mBookDao, bookId)
        return mDao.queryList(listId).map(new TaskFunction());
    }

    public DataSource.Factory<Integer, Task> search(String query) {
        checkRules(notEmptyString(query));
        return mDao.search("%" + query + "%").map(new TaskFunction());
    }

    private class TaskFunction implements Function<Task, Task> {
        @Override
        public Task apply(Task task) {
            LocalDate today = new LocalDate();
            if (task.hasReminder()) {
                Reminder reminder = mReminderDao.get(task.getReminderId());
                LocalDate localDate = reminder.getDate();
                DateTime dateTime = localDate.toDateTime(reminder.getTime());
                task.setReminderPassed(localDate.isBefore(today));

                String pattern;
                if (localDate.equals(today)) {
                    if (m24HFormat) {
                        pattern = "H:mm";
                    } else {
                        pattern = "h:mm aa";
                    }
                } else if (localDate.year().equals(today.year())) {
                    pattern = "d MMM";
                } else {
                    pattern = "d MMM yyyy";
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, mLocale);
                task.setDateTimeFormatted(simpleDateFormat.format(dateTime.toDate()).toLowerCase());
            }

            String listId = task.getListId();
            task.setListName(isEmpty(listId) ? ACTION_INBOX : mListDao.getName(listId));
            task.setColor(isEmpty(listId) ? 0 : mListDao.getColor(listId));
            return task;
        }
    }

}
