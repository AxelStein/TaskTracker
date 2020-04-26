package com.axel_stein.tasktracker.api.repository;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.axel_stein.tasktracker.api.exception.FolderNotFoundException;
import com.axel_stein.tasktracker.api.exception.ListClosedException;
import com.axel_stein.tasktracker.api.exception.ListNotFoundException;
import com.axel_stein.tasktracker.api.model.Folder;
import com.axel_stein.tasktracker.api.model.ListEntity;
import com.axel_stein.tasktracker.api.model.Task;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.axel_stein.tasktracker.utils.TextUtil.isEmpty;
import static com.axel_stein.tasktracker.utils.TextUtil.notEmpty;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class ListRepositoryTest extends RepositoryTest {

    @Test
    public void testInsert() {
        ListEntity list = new ListEntity();
        list.setName("Test");
        mListRepository.insert(list).test().assertComplete();
        assertNotNull(list.getId());
    }

    @Test
    public void testInsert_args() {
        mListRepository.insert(null).test().assertError(IllegalArgumentException.class);

        ListEntity list = new ListEntity();
        list.setName("");
        mListRepository.insert(list).test().assertError(IllegalArgumentException.class);
    }

    @Test
    public void testSetName() {
        ListEntity list = insertTestList();
        mListRepository.setName(list.getId(), "Test 2").test().assertComplete();
    }

    @Test
    public void testSetName_args() {
        ListEntity list = insertTestList();
        mListRepository.setName(null, "Test 2").test().assertError(IllegalArgumentException.class);
        mListRepository.setName(list.getId(), null).test().assertError(IllegalArgumentException.class);
        mListRepository.setName("test", "Test 2").test().assertError(ListNotFoundException.class);
    }

    @Test
    public void testSetName_closed() {
        ListEntity list = insertTestList();
        mListRepository.setClosed(list.getId(), true).subscribe();
        mListRepository.setName(list.getId(), "Test 2").test().assertError(ListClosedException.class);
    }

    @Test
    public void testSetFolder() {
        ListEntity list = insertTestList("some list");
        Folder folder = insertTestFolder("some folder");

        mListRepository.setFolder(list.getId(), folder.getId()).test().assertComplete();
        mListRepository.get(list.getId()).test().assertValue(listEntity -> notEmpty(listEntity.getFolderId()));

        mListRepository.setFolder(list.getId(), null).test().assertComplete();
        mListRepository.get(list.getId()).test().assertValue(listEntity -> isEmpty(listEntity.getFolderId()));
    }

    @Test
    public void testSetFolder_args() {
        ListEntity list = insertTestList("some list");
        Folder folder = insertTestFolder("some folder");

        mListRepository.setFolder(null, folder.getId()).test().assertError(IllegalArgumentException.class);
        mListRepository.setFolder("test", folder.getId()).test().assertError(ListNotFoundException.class);
        mListRepository.setFolder(list.getId(), "test").test().assertError(FolderNotFoundException.class);
    }

    @Test
    public void testSetFolder_closed() {
        ListEntity list = insertTestList("some list");
        Folder folder = insertTestFolder("some folder");
        mListRepository.setClosed(list.getId(), true).subscribe();
        mListRepository.setFolder(list.getId(), folder.getId()).test().assertError(ListClosedException.class);
    }

    @Test
    public void testSetColor() {
        ListEntity list = insertTestList();
        mListRepository.setColor(list.getId(), 0).test().assertComplete();
    }

    @Test
    public void testSetColor_args() {
        mListRepository.setColor(null, -1).test().assertError(IllegalArgumentException.class);
        mListRepository.setColor("test", 123).test().assertError(ListNotFoundException.class);
    }

    @Test
    public void testSetColor_closed() {
        ListEntity list = insertTestList();
        mListRepository.setClosed(list.getId(), true).subscribe();
        mListRepository.setColor(list.getId(), 123).test().assertError(ListClosedException.class);
    }

    @Test
    public void testGet() {
        ListEntity list = insertTestList();
        mListRepository.get(list.getId()).test().assertComplete();
        mListRepository.get(list.getId()).test().assertValue(list1 -> notEmpty(list1.getId()) && notEmpty(list1.getName()));
    }

    @Test
    public void testGet_args() {
        mListRepository.get(null).test().assertError(IllegalArgumentException.class);
        mListRepository.get("test").test().assertError(ListNotFoundException.class);
    }

    @Test
    public void testDelete() {
        ListEntity list = insertTestList();
        mListRepository.delete(list.getId()).test().assertComplete();
        mListRepository.get(list.getId()).test().assertError(ListNotFoundException.class);
    }

    @Test
    public void testDelete_tasks() {
        insertTestTask("task 2");
        final Task task = insertTestTask("task 1");
        ListEntity list = insertTestList();

        mTaskRepository.setListId(task.getId(), list.getId()).test().assertComplete();

        mListRepository.delete(list.getId()).test().assertComplete();

        mTaskRepository.query(list.getId()).test().assertError(ListNotFoundException.class);
        mTaskRepository.query().test().assertValue(tasks -> tasks.size() == 1);
    }

    @Test
    public void testDelete_args() {
        mListRepository.delete(null).test().assertError(IllegalArgumentException.class);
        mListRepository.delete("test").test().assertError(ListNotFoundException.class);
    }

    @Test
    public void testSetClosed() {
        ListEntity list = insertTestList();
        mListRepository.setClosed(list.getId(), true).test().assertComplete();
    }

    @Test
    public void testSetClosed_args() {
        mListRepository.setClosed(null, true).test().assertError(IllegalArgumentException.class);
        mListRepository.setClosed("test", true).test().assertError(ListNotFoundException.class);
    }

}
