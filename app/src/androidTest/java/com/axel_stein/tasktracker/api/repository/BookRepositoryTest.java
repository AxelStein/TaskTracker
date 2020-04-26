package com.axel_stein.tasktracker.api.repository;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.axel_stein.tasktracker.api.exception.FolderNotFoundException;
import com.axel_stein.tasktracker.api.exception.BookClosedException;
import com.axel_stein.tasktracker.api.exception.BookNotFoundException;
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

        Book book = new Book();
        book.setName("");
        mBookRepository.insert(book).test().assertError(IllegalArgumentException.class);
    }

    @Test
    public void testSetName() {
        Book book = insertTestBook();
        mBookRepository.setName(book.getId(), "Test 2").test().assertComplete();
    }

    @Test
    public void testSetName_args() {
        Book book = insertTestBook();
        mBookRepository.setName(null, "Test 2").test().assertError(IllegalArgumentException.class);
        mBookRepository.setName(book.getId(), null).test().assertError(IllegalArgumentException.class);
        mBookRepository.setName("test", "Test 2").test().assertError(BookNotFoundException.class);
    }

    @Test
    public void testSetName_closed() {
        Book book = insertTestBook();
        mBookRepository.setClosed(book.getId(), true).subscribe();
        mBookRepository.setName(book.getId(), "Test 2").test().assertError(BookClosedException.class);
    }

    @Test
    public void testSetFolder() {
        Book book = insertTestBook("some book");
        Folder folder = insertTestFolder("some folder");

        mBookRepository.setFolder(book.getId(), folder.getId()).test().assertComplete();
        mBookRepository.get(book.getId()).test().assertValue(listEntity -> notEmpty(listEntity.getFolderId()));

        mBookRepository.setFolder(book.getId(), null).test().assertComplete();
        mBookRepository.get(book.getId()).test().assertValue(listEntity -> isEmpty(listEntity.getFolderId()));
    }

    @Test
    public void testSetFolder_args() {
        Book book = insertTestBook("some book");
        Folder folder = insertTestFolder("some folder");

        mBookRepository.setFolder(null, folder.getId()).test().assertError(IllegalArgumentException.class);
        mBookRepository.setFolder("test", folder.getId()).test().assertError(BookNotFoundException.class);
        mBookRepository.setFolder(book.getId(), "test").test().assertError(FolderNotFoundException.class);
    }

    @Test
    public void testSetFolder_closed() {
        Book book = insertTestBook("some book");
        Folder folder = insertTestFolder("some folder");
        mBookRepository.setClosed(book.getId(), true).subscribe();
        mBookRepository.setFolder(book.getId(), folder.getId()).test().assertError(BookClosedException.class);
    }

    @Test
    public void testSetColor() {
        Book book = insertTestBook();
        mBookRepository.setColor(book.getId(), 0).test().assertComplete();
    }

    @Test
    public void testSetColor_args() {
        mBookRepository.setColor(null, -1).test().assertError(IllegalArgumentException.class);
        mBookRepository.setColor("test", 123).test().assertError(BookNotFoundException.class);
    }

    @Test
    public void testSetColor_closed() {
        Book book = insertTestBook();
        mBookRepository.setClosed(book.getId(), true).subscribe();
        mBookRepository.setColor(book.getId(), 123).test().assertError(BookClosedException.class);
    }

    @Test
    public void testGet() {
        Book book = insertTestBook();
        mBookRepository.get(book.getId()).test().assertComplete();
        mBookRepository.get(book.getId()).test().assertValue(list1 -> notEmpty(list1.getId()) && notEmpty(list1.getName()));
    }

    @Test
    public void testGet_args() {
        mBookRepository.get(null).test().assertError(IllegalArgumentException.class);
        mBookRepository.get("test").test().assertError(BookNotFoundException.class);
    }

    @Test
    public void testDelete() {
        Book book = insertTestBook();
        mBookRepository.delete(book.getId()).test().assertComplete();
        mBookRepository.get(book.getId()).test().assertError(BookNotFoundException.class);
    }

    @Test
    public void testDelete_tasks() {
        insertTestTask("task 2");
        final Task task = insertTestTask("task 1");
        Book book = insertTestBook();

        mTaskRepository.setListId(task.getId(), book.getId()).test().assertComplete();

        mBookRepository.delete(book.getId()).test().assertComplete();

        // mTaskRepository.query(book.getId()).test().assertError(BookNotFoundException.class); fixme
        mTaskRepository.query().test().assertValue(tasks -> tasks.size() == 1);
    }

    @Test
    public void testDelete_args() {
        mBookRepository.delete(null).test().assertError(IllegalArgumentException.class);
        mBookRepository.delete("test").test().assertError(BookNotFoundException.class);
    }

    @Test
    public void testSetClosed() {
        Book book = insertTestBook();
        mBookRepository.setClosed(book.getId(), true).test().assertComplete();
    }

    @Test
    public void testSetClosed_args() {
        mBookRepository.setClosed(null, true).test().assertError(IllegalArgumentException.class);
        mBookRepository.setClosed("test", true).test().assertError(BookNotFoundException.class);
    }

}
