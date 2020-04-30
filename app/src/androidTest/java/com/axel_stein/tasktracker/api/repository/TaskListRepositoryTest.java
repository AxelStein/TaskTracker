package com.axel_stein.tasktracker.api.repository;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.axel_stein.tasktracker.api.exception.TaskListClosedException;
import com.axel_stein.tasktracker.api.exception.TaskListNotFoundException;
import com.axel_stein.tasktracker.api.exception.FolderNotFoundException;
import com.axel_stein.tasktracker.api.model.TaskList;
import com.axel_stein.tasktracker.api.model.Folder;
import com.axel_stein.tasktracker.api.model.Task;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.axel_stein.tasktracker.utils.TextUtil.isEmpty;
import static com.axel_stein.tasktracker.utils.TextUtil.notEmpty;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class TaskListRepositoryTest extends RepositoryTest {

    @Test
    public void testInsert() {
        TaskList list = new TaskList();
        list.setName("Test");
        mTaskListRepository.insert(list).test().assertComplete();
        assertNotNull(list.getId());
    }

    @Test
    public void testInsert_args() {
        mTaskListRepository.insert(null).test().assertError(IllegalArgumentException.class);

        TaskList taskList = new TaskList();
        taskList.setName("");
        mTaskListRepository.insert(taskList).test().assertError(IllegalArgumentException.class);
    }

    @Test
    public void testGetName() {
        TaskList taskList = insertTestList();
        mTaskListRepository.getName(taskList.getId()).test().assertValue(s -> s.contentEquals(taskList.getName()));
    }

    @Test
    public void testGetName_args() {
        mTaskListRepository.getName(null).test().assertError(IllegalArgumentException.class);
        mTaskListRepository.getName("test").test().assertError(TaskListNotFoundException.class);
    }

    @Test
    public void testSetName() {
        TaskList taskList = insertTestList();
        mTaskListRepository.setName(taskList.getId(), "Test 2").test().assertComplete();
    }

    @Test
    public void testSetName_args() {
        TaskList taskList = insertTestList();
        mTaskListRepository.setName(null, "Test 2").test().assertError(IllegalArgumentException.class);
        mTaskListRepository.setName(taskList.getId(), null).test().assertError(IllegalArgumentException.class);
        mTaskListRepository.setName("test", "Test 2").test().assertError(TaskListNotFoundException.class);
    }

    @Test
    public void testSetName_closed() {
        TaskList taskList = insertTestList();
        mTaskListRepository.setClosed(taskList.getId(), true).subscribe();
        mTaskListRepository.setName(taskList.getId(), "Test 2").test().assertError(TaskListClosedException.class);
    }

    @Test
    public void testSetFolder() {
        TaskList taskList = insertTestList("some book");
        Folder folder = insertTestFolder("some folder");

        mTaskListRepository.setFolder(taskList.getId(), folder.getId()).test().assertComplete();
        mTaskListRepository.get(taskList.getId()).test().assertValue(listEntity -> notEmpty(listEntity.getFolderId()));

        mTaskListRepository.setFolder(taskList.getId(), null).test().assertComplete();
        mTaskListRepository.get(taskList.getId()).test().assertValue(listEntity -> isEmpty(listEntity.getFolderId()));
    }

    @Test
    public void testSetFolder_args() {
        TaskList taskList = insertTestList("some book");
        Folder folder = insertTestFolder("some folder");

        mTaskListRepository.setFolder(null, folder.getId()).test().assertError(IllegalArgumentException.class);
        mTaskListRepository.setFolder("test", folder.getId()).test().assertError(TaskListNotFoundException.class);
        mTaskListRepository.setFolder(taskList.getId(), "test").test().assertError(FolderNotFoundException.class);
    }

    @Test
    public void testSetFolder_closed() {
        TaskList taskList = insertTestList("some book");
        Folder folder = insertTestFolder("some folder");
        mTaskListRepository.setClosed(taskList.getId(), true).subscribe();
        mTaskListRepository.setFolder(taskList.getId(), folder.getId()).test().assertError(TaskListClosedException.class);
    }

    @Test
    public void testSetColor() {
        TaskList taskList = insertTestList();
        mTaskListRepository.setColor(taskList.getId(), 0).test().assertComplete();
    }

    @Test
    public void testSetColor_args() {
        mTaskListRepository.setColor(null, -1).test().assertError(IllegalArgumentException.class);
        mTaskListRepository.setColor("test", 123).test().assertError(TaskListNotFoundException.class);
    }

    @Test
    public void testSetColor_closed() {
        TaskList taskList = insertTestList();
        mTaskListRepository.setClosed(taskList.getId(), true).subscribe();
        mTaskListRepository.setColor(taskList.getId(), 123).test().assertError(TaskListClosedException.class);
    }

    @Test
    public void testGet() {
        TaskList taskList = insertTestList();
        mTaskListRepository.get(taskList.getId()).test().assertComplete();
        mTaskListRepository.get(taskList.getId()).test().assertValue(list1 -> notEmpty(list1.getId()) && notEmpty(list1.getName()));
    }

    @Test
    public void testGet_args() {
        mTaskListRepository.get(null).test().assertError(IllegalArgumentException.class);
        mTaskListRepository.get("test").test().assertError(TaskListNotFoundException.class);
    }

    @Test
    public void testDelete() {
        TaskList taskList = insertTestList();
        mTaskListRepository.delete(taskList.getId()).test().assertComplete();
        mTaskListRepository.get(taskList.getId()).test().assertError(TaskListNotFoundException.class);
    }

    @Test
    public void testDelete_tasks() {
        insertTestTask("task 2");
        final Task task = insertTestTask("task 1");
        TaskList taskList = insertTestList();

        mTaskRepository.setListId(task.getId(), taskList.getId()).test().assertComplete();

        mTaskListRepository.delete(taskList.getId()).test().assertComplete();

        // mTaskRepository.query(book.getId()).test().assertError(BookNotFoundException.class); fixme
        mTaskRepository.query().test().assertValue(tasks -> tasks.size() == 1);
    }

    @Test
    public void testDelete_args() {
        mTaskListRepository.delete(null).test().assertError(IllegalArgumentException.class);
        mTaskListRepository.delete("test").test().assertError(TaskListNotFoundException.class);
    }

    @Test
    public void testSetClosed() {
        TaskList taskList = insertTestList();
        mTaskListRepository.setClosed(taskList.getId(), true).test().assertComplete();
    }

    @Test
    public void testSetClosed_args() {
        mTaskListRepository.setClosed(null, true).test().assertError(IllegalArgumentException.class);
        mTaskListRepository.setClosed("test", true).test().assertError(TaskListNotFoundException.class);
    }

}
