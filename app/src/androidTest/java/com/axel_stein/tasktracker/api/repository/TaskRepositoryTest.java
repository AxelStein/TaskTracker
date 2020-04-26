package com.axel_stein.tasktracker.api.repository;

import com.axel_stein.tasktracker.api.exception.ListNotFoundException;
import com.axel_stein.tasktracker.api.exception.ReminderNotFoundException;
import com.axel_stein.tasktracker.api.exception.TaskNotFoundException;
import com.axel_stein.tasktracker.api.model.ListEntity;
import com.axel_stein.tasktracker.api.model.Reminder;
import com.axel_stein.tasktracker.api.model.Task;

import org.junit.Test;

import java.util.ArrayList;

import io.reactivex.functions.Predicate;

import static com.axel_stein.tasktracker.utils.TextUtil.notEmpty;
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
    public void testSetListId() {
        Task task = insertTestTask();
        ListEntity list = insertTestList();
        mTaskRepository.setListId(task.getId(), list.getId()).test().assertComplete();
    }

    @Test
    public void testSetListId_args() {
        Task task = insertTestTask();
        ListEntity list = insertTestList();
        mTaskRepository.setListId("", "listId").test().assertError(IllegalArgumentException.class);
        mTaskRepository.setListId("taskId", "").test().assertError(IllegalArgumentException.class);
        mTaskRepository.setListId("taskId", list.getId()).test().assertError(TaskNotFoundException.class);
        mTaskRepository.setListId(task.getId(), "listId").test().assertError(ListNotFoundException.class);
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
    public void testSetTrashed() {
        Task task = insertTestTask();
        mTaskRepository.setTrashed(task.getId(), true).test().assertComplete();
    }

    @Test
    public void testSetTrashed_date() {
        Task task = insertTestTask();
        mTaskRepository.setTrashed(task.getId(), true).test().assertComplete();
        mTaskRepository.get(task.getId()).test().assertValue(new Predicate<Task>() {
            @Override
            public boolean test(Task task) {
                return task.getTrashedDateTime() != null;
            }
        });

        mTaskRepository.setTrashed(task.getId(), false).test().assertComplete();
        mTaskRepository.get(task.getId()).test().assertValue(new Predicate<Task>() {
            @Override
            public boolean test(Task task) {
                return task.getTrashedDateTime() == null;
            }
        });
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
        /*
        CompositeDisposable mDisposable = new CompositeDisposable();
        mDisposable.add(mDataSource.subscribe(input -> {
            ArrayList<Task> tasks = new ArrayList<>(input);
            assertNotSame(tasks, taskList.getTasks());
        }));
        */
        //mDataSource.test().assertValue(tasks -> tasks.size() > 0);
        /*
        TestTaskList taskList = new TestTaskList();
        mTaskRepository.queryInbox().test().assertValue(taskList.getTasks());
        */
    }

    @Test
    public void testQueryList() {
        TestTaskList taskList = new TestTaskList();
        mTaskRepository.query(taskList.getList()).test().assertValue(taskList.getInListTasks());
    }

    @Test
    public void testQueryCompleted() {
        /*
        fixme
        TestTaskList taskList = new TestTaskList();
        mTaskRepository.queryCompleted().test().assertValue(taskList.getCompletedTasks());
        */
    }

    @Test
    public void testQueryTrashed() {
        /*
        fixme
        TestTaskList taskList = new TestTaskList();
        mTaskRepository.queryTrashed().test().assertValue(taskList.getTrashedTasks());
        */
    }

    private class TestTaskList {
        ArrayList<Task> tasks;
        ArrayList<Task> trashedTasks;
        ArrayList<Task> completedTasks;
        ArrayList<Task> inListTasks;
        ListEntity list;

        TestTaskList() {
            trashedTasks = new ArrayList<>();
            completedTasks = new ArrayList<>();
            inListTasks = new ArrayList<>();

            tasks = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                tasks.add(insertTestTask("task " + i));
            }

            // do trash
            Task trashed = tasks.get(1);
            mTaskRepository.setTrashed(trashed.getId(), true).subscribe();
            tasks.remove(trashed);
            trashedTasks.add(trashed);

            // do completed
            Task completed = tasks.get(3);
            mTaskRepository.setCompleted(completed.getId(), true).subscribe();
            tasks.remove(completed);
            completedTasks.add(completed);

            // do list
            list = insertTestList();
            Task inList = tasks.get(2);
            mTaskRepository.setListId(inList.getId(), list.getId()).subscribe();
            tasks.remove(inList);
            inListTasks.add(inList);
        }

        ArrayList<Task> getTasks() {
            return tasks;
        }

        ArrayList<Task> getTrashedTasks() {
            return trashedTasks;
        }

        ArrayList<Task> getCompletedTasks() {
            return completedTasks;
        }

        ArrayList<Task> getInListTasks() {
            return inListTasks;
        }

        ListEntity getList() {
            return list;
        }
    }

}