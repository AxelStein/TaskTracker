package com.axel_stein.tasktracker.api.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.axel_stein.tasktracker.api.exception.ReminderNotFoundException;
import com.axel_stein.tasktracker.api.exception.TaskListNotFoundException;
import com.axel_stein.tasktracker.api.exception.TaskLockedException;
import com.axel_stein.tasktracker.api.exception.TaskNotFoundException;
import com.axel_stein.tasktracker.api.model.Reminder;
import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.api.model.TaskList;

import org.junit.Test;

import static com.axel_stein.tasktracker.utils.TextUtil.notEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TaskRepositoryTest extends RepositoryTest {

    @Test
    public void testInsert() {
        Task task = new Task();
        task.setTitle("Test");
        mTaskRepository.insert(task).test().assertComplete();
        assertNotNull(task.getId());
    }

    @Test
    public void testInsert_args() {
        mTaskRepository.insert(null).test().assertError(IllegalArgumentException.class);
    }

    @Test
    public void testSetTitle() {
        Task task = insertTestTask();
        mTaskRepository.setTitle(task.getId(), "text").test().assertComplete();
    }

    @Test
    public void testSetTitle_locked() {
        Task task = insertTestTask();
        mTaskRepository.setTrashed(task.getId(), true).subscribe();
        mTaskRepository.setTitle(task.getId(), "some text").test().assertError(TaskLockedException.class);
    }

    @Test
    public void testSetTitle_args() {
        mTaskRepository.setTitle(null, "text").test().assertError(IllegalArgumentException.class);
        mTaskRepository.setTitle("text", "text").test().assertError(TaskNotFoundException.class);
    }

    @Test
    public void testSetDescription() {
        Task task = insertTestTask();
        mTaskRepository.setDescription(task.getId(), "some text").test().assertComplete();
    }

    @Test
    public void testSetDescription_args() {
        mTaskRepository.setDescription(null, "text").test().assertError(IllegalArgumentException.class);
        mTaskRepository.setDescription("text", "text").test().assertError(TaskNotFoundException.class);
    }

    @Test
    public void testSetDescription_locked() {
        Task task = insertTestTask();
        mTaskRepository.setTrashed(task.getId(), true).subscribe();
        mTaskRepository.setDescription(task.getId(), "some text").test().assertError(TaskLockedException.class);
    }

    @Test
    public void testSetListId() {
        Task task = insertTestTask();
        TaskList list = insertTestList();
        mTaskRepository.setListId(task.getId(), list.getId()).test().assertComplete();
    }

    @Test
    public void testSetListId_args() {
        Task task = insertTestTask();
        TaskList list = insertTestList();
        mTaskRepository.setListId("", "listId").test().assertError(IllegalArgumentException.class);
        mTaskRepository.setListId("taskId", "").test().assertError(IllegalArgumentException.class);
        mTaskRepository.setListId("taskId", list.getId()).test().assertError(TaskNotFoundException.class);
        mTaskRepository.setListId(task.getId(), "listId").test().assertError(TaskListNotFoundException.class);
    }

    @Test
    public void testSetListId_locked() {
        Task task = insertTestTask();
        mTaskRepository.setTrashed(task.getId(), true).subscribe();

        TaskList list = insertTestList();
        mTaskRepository.setListId(task.getId(), list.getId()).test().assertError(TaskLockedException.class);
    }

    @Test
    public void testSetCompleted() {
        Task task = insertTestTask();
        mTaskRepository.setCompleted(task.getId(), true).test().assertComplete();
    }

    @Test
    public void testSetCompleted_date() {
        Task task = insertTestTask();
        mTaskRepository.setCompleted(task.getId(), true).test().assertComplete();
        mTaskRepository.get(task.getId()).test().assertValue(task1 -> task1.getCompletedDateTime() != null);

        mTaskRepository.setCompleted(task.getId(), false).test().assertComplete();
        mTaskRepository.get(task.getId()).test().assertValue(task12 -> task12.getCompletedDateTime() == null);
    }

    @Test
    public void testSetCompleted_args() {
        mTaskRepository.setCompleted("", true).test().assertError(IllegalArgumentException.class);
        mTaskRepository.setCompleted("id", true).test().assertError(TaskNotFoundException.class);
    }

    @Test
    public void testSetCompleted_locked() {
        Task task = insertTestTask();
        mTaskRepository.setTrashed(task.getId(), true).subscribe();
        mTaskRepository.setCompleted(task.getId(), true).test().assertError(TaskLockedException.class);
    }

    @Test
    public void testSetTrashed() {
        Task task = insertTestTask();
        mTaskRepository.setTrashed(task.getId(), true).test().assertComplete();
    }

    @Test
    public void testSetTrashed_date() {
        Task task = insertTestTask();
        mTaskRepository.setTrashed(task.getId(), true).test().assertComplete();
        mTaskRepository.get(task.getId()).test().assertValue(t -> t.getTrashedDateTime() != null);

        mTaskRepository.setTrashed(task.getId(), false).test().assertComplete();
        mTaskRepository.get(task.getId()).test().assertValue(t -> t.getTrashedDateTime() == null);
    }

    @Test
    public void testSetTrashed_args() {
        mTaskRepository.setTrashed("", true).test().assertError(IllegalArgumentException.class);
        mTaskRepository.setTrashed("id", true).test().assertError(TaskNotFoundException.class);
    }

    @Test
    public void testSetPriority() {
        Task task = insertTestTask();
        mTaskRepository.setPriority(task.getId(), Task.PRIORITY_MIDDLE).test().assertComplete();
    }

    @Test
    public void testSetPriority_args() {
        Task task = insertTestTask();
        mTaskRepository.setPriority("", Task.PRIORITY_MIDDLE).test().assertError(IllegalArgumentException.class);
        mTaskRepository.setPriority(task.getId(), Task.PRIORITY_NONE - 1).test().assertError(IllegalArgumentException.class);
        mTaskRepository.setPriority(task.getId(), Task.PRIORITY_HIGH + 1).test().assertError(IllegalArgumentException.class);
        mTaskRepository.setPriority("id", Task.PRIORITY_LOW).test().assertError(TaskNotFoundException.class);
    }

    @Test
    public void testSetPriority_locked() {
        Task task = insertTestTask();
        mTaskRepository.setTrashed(task.getId(), true).subscribe();
        mTaskRepository.setPriority(task.getId(), Task.PRIORITY_LOW).test().assertError(TaskLockedException.class);
    }

    @Test
    public void testDelete() {
        Task task = insertTestTask();
        mTaskRepository.delete(task.getId()).test().assertComplete();
        mTaskRepository.get(task.getId()).test().assertError(TaskNotFoundException.class);
    }

    @Test
    public void testDelete_reminder() {
        Reminder reminder = insertTestReminder();
        mReminderRepository.update(reminder).subscribe();

        mTaskRepository.delete(reminder.getTaskId()).subscribe();
        mReminderRepository.get(reminder.getId()).test().assertError(ReminderNotFoundException.class);
    }

    @Test
    public void testDelete_args() {
        mTaskRepository.delete("").test().assertError(IllegalArgumentException.class);
        mTaskRepository.delete("id").test().assertError(TaskNotFoundException.class);
    }

    @Test
    public void testGet() {
        Task task = insertTestTask();
        mTaskRepository.get(task.getId()).test().assertValue(task1 -> notEmpty(task1.getId()));
    }

    @Test
    public void testQueryInbox() {
        Task task = insertTestTask();
        loadPagedList(mTaskRepository.queryInboxDataSource(), tasks -> {
            assertNotNull(tasks);
            assertEquals(tasks.size(), 1);
            assertEquals(task, tasks.get(0));
        });
    }

    @Test
    public void testSearch() {
        insertTestTask("test 1");
        insertTestTask("test 2");

        Task task = insertTestTask();
        loadPagedList(mTaskRepository.search("test"), tasks -> {
            assertNotNull(tasks);
            assertEquals(tasks.size(), 1);
            assertEquals(task, tasks.get(0));
        });
    }

    @Test
    public void testQueryList() {
        insertTestTask("test 1");

        Task task = insertTestTask();
        TaskList taskList = insertTestList();
        mTaskRepository.setListId(task.getId(), taskList.getId()).subscribe();

        loadPagedList(mTaskRepository.queryTaskListDataSource(taskList.getId()), tasks -> {
            assertNotNull(tasks);
            assertEquals(tasks.size(), 1);
            assertEquals(task, tasks.get(0));
        });
    }

    @Test
    public void testQueryCompleted() {
        insertTestTask("test 1");
        insertTestTask("test 2");

        Task task = insertTestTask();
        mTaskRepository.setCompleted(task, true).subscribe();

        loadPagedList(mTaskRepository.queryCompletedDataSource(), tasks -> {
            assertNotNull(tasks);
            assertEquals(tasks.size(), 1);
            assertEquals(task, tasks.get(0));
        });
    }

    @Test
    public void testQueryTrashed() {
        insertTestTask("test 1");
        insertTestTask("test 2");

        Task task = insertTestTask();
        mTaskRepository.setTrashed(task, true).subscribe();

        loadPagedList(mTaskRepository.queryTrashedDataSource(), tasks -> {
            assertNotNull(tasks);
            assertEquals(tasks.size(), 1);
            assertEquals(task, tasks.get(0));
        });
    }

    private void loadPagedList(DataSource.Factory<Integer, Task> dataSource, Observer<? super PagedList<Task>> observer) {
        LiveData<PagedList<Task>> taskList = new LivePagedListBuilder<>(dataSource, 50).build();
        taskList.observeForever(observer);
    }

}