package com.axel_stein.tasktracker.api.repository;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.axel_stein.tasktracker.api.exception.FolderNotFoundException;
import com.axel_stein.tasktracker.api.exception.ListClosedException;
import com.axel_stein.tasktracker.api.exception.ListNotFoundException;
import com.axel_stein.tasktracker.api.model.Folder;
import com.axel_stein.tasktracker.api.model.Book;
import com.axel_stein.tasktracker.api.model.Task;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.axel_stein.tasktracker.utils.TextUtil.isEmpty;
import static com.axel_stein.tasktracker.utils.TextUtil.notEmpty;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class BookRepositoryTest extends RepositoryTest {

    @Test
    public void testInsert() {
        Book list = new Book();
        list.setName("Test");
        mBookRepository.insert(list).test().assertComplete();
        assertNotNull(list.getId());
    }

    @Test
    public void testInsert_args() {
        mBookRepository.insert(null).test().assertError(IllegalArgumentException.class);

        Book list = new Book();
        list.setName("");
        mBookRepository.insert(list).test().assertError(IllegalArgumentException.class);
    }

    @Test
    public void testSetName() {
        Book list = insertTestList();
        mBookRepository.setName(list.getId(), "Test 2").test().assertComplete();
    }

    @Test
    public void testSetName_args() {
        Book list = insertTestList();
        mBookRepository.setName(null, "Test 2").test().assertError(IllegalArgumentException.class);
        mBookRepository.setName(list.getId(), null).test().assertError(IllegalArgumentException.class);
        mBookRepository.setName("test", "Test 2").test().assertError(ListNotFoundException.class);
    }

    @Test
    public void testSetName_closed() {
        Book list = insertTestList();
        mBookRepository.setClosed(list.getId(), true).subscribe();
        mBookRepository.setName(list.getId(), "Test 2").test().assertError(ListClosedException.class);
    }

    @Test
    public void testSetFolder() {
        Book list = insertTestList("some list");
        Folder folder = insertTestFolder("some folder");

        mBookRepository.setFolder(list.getId(), folder.getId()).test().assertComplete();
        mBookRepository.get(list.getId()).test().assertValue(listEntity -> notEmpty(listEntity.getFolderId()));

        mBookRepository.setFolder(list.getId(), null).test().assertComplete();
        mBookRepository.get(list.getId()).test().assertValue(listEntity -> isEmpty(listEntity.getFolderId()));
    }

    @Test
    public void testSetFolder_args() {
        Book list = insertTestList("some list");
        Folder folder = insertTestFolder("some folder");

        mBookRepository.setFolder(null, folder.getId()).test().assertError(IllegalArgumentException.class);
        mBookRepository.setFolder("test", folder.getId()).test().assertError(ListNotFoundException.class);
        mBookRepository.setFolder(list.getId(), "test").test().assertError(FolderNotFoundException.class);
    }

    @Test
    public void testSetFolder_closed() {
        Book list = insertTestList("some list");
        Folder folder = insertTestFolder("some folder");
        mBookRepository.setClosed(list.getId(), true).subscribe();
        mBookRepository.setFolder(list.getId(), folder.getId()).test().assertError(ListClosedException.class);
    }

    @Test
    public void testSetColor() {
        Book list = insertTestList();
        mBookRepository.setColor(list.getId(), 0).test().assertComplete();
    }

    @Test
    public void testSetColor_args() {
        mBookRepository.setColor(null, -1).test().assertError(IllegalArgumentException.class);
        mBookRepository.setColor("test", 123).test().assertError(ListNotFoundException.class);
    }

    @Test
    public void testSetColor_closed() {
        Book list = insertTestList();
        mBookRepository.setClosed(list.getId(), true).subscribe();
        mBookRepository.setColor(list.getId(), 123).test().assertError(ListClosedException.class);
    }

    @Test
    public void testGet() {
        Book list = insertTestList();
        mBookRepository.get(list.getId()).test().assertComplete();
        mBookRepository.get(list.getId()).test().assertValue(list1 -> notEmpty(list1.getId()) && notEmpty(list1.getName()));
    }

    @Test
    public void testGet_args() {
        mBookRepository.get(null).test().assertError(IllegalArgumentException.class);
        mBookRepository.get("test").test().assertError(ListNotFoundException.class);
    }

    @Test
    public void testDelete() {
        Book list = insertTestList();
        mBookRepository.delete(list.getId()).test().assertComplete();
        mBookRepository.get(list.getId()).test().assertError(ListNotFoundException.class);
    }

    @Test
    public void testDelete_tasks() {
        insertTestTask("task 2");
        final Task task = insertTestTask("task 1");
        Book list = insertTestList();

        mTaskRepository.setListId(task.getId(), list.getId()).test().assertComplete();

        mBookRepository.delete(list.getId()).test().assertComplete();

        mTaskRepository.query(list.getId()).test().assertError(ListNotFoundException.class);
        mTaskRepository.query().test().assertValue(tasks -> tasks.size() == 1);
    }

    @Test
    public void testDelete_args() {
        mBookRepository.delete(null).test().assertError(IllegalArgumentException.class);
        mBookRepository.delete("test").test().assertError(ListNotFoundException.class);
    }

    @Test
    public void testSetClosed() {
        Book list = insertTestList();
        mBookRepository.setClosed(list.getId(), true).test().assertComplete();
    }

    @Test
    public void testSetClosed_args() {
        mBookRepository.setClosed(null, true).test().assertError(IllegalArgumentException.class);
        mBookRepository.setClosed("test", true).test().assertError(ListNotFoundException.class);
    }

}
