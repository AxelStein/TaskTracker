package com.axel_stein.tasktracker.api.repository;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.axel_stein.tasktracker.api.exception.FolderNotFoundException;
import com.axel_stein.tasktracker.api.model.Folder;
import com.axel_stein.tasktracker.api.model.ListEntity;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.axel_stein.tasktracker.utils.TextUtil.notEmpty;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class FolderRepositoryTest extends RepositoryTest {

    @Test
    public void testInsert() {
        Folder folder = new Folder();
        folder.setName("Test");
        mFolderRepository.insert(folder).test().assertComplete();
        assertNotNull(folder.getId());
    }

    @Test
    public void testInsert_args() {
        mFolderRepository.insert(null).test().assertError(IllegalArgumentException.class);

        Folder folder = new Folder();
        folder.setName("");
        mFolderRepository.insert(folder).test().assertError(IllegalArgumentException.class);
    }

    @Test
    public void testSetName() {
        Folder folder = insertTestFolder();
        mFolderRepository.setName(folder.getId(), "Test 2").test().assertComplete();
    }

    @Test
    public void testSetName_args() {
        Folder folder = insertTestFolder();
        mFolderRepository.setName("", "Test 2").test().assertError(IllegalArgumentException.class);
        mFolderRepository.setName(folder.getId(), null).test().assertError(IllegalArgumentException.class);
        mFolderRepository.setName("test", "Test 2").test().assertError(FolderNotFoundException.class);
    }

    @Test
    public void testGet() {
        Folder folder = insertTestFolder();
        mFolderRepository.get(folder.getId()).test().assertComplete();
        mFolderRepository.get(folder.getId()).test().assertValue(f -> notEmpty(f.getId()) && notEmpty(f.getName()));
    }

    @Test
    public void testGet_args() {
        mFolderRepository.get(null).test().assertError(IllegalArgumentException.class);
        mFolderRepository.get("test").test().assertError(FolderNotFoundException.class);
    }

    @Test
    public void testDelete() {
        Folder folder = insertTestFolder("test 1");
        mFolderRepository.delete(folder.getId()).test().assertComplete();
        mFolderRepository.get(folder.getId()).test().assertError(FolderNotFoundException.class);
    }

    @Test
    public void testDelete_args() {
        mFolderRepository.delete("").test().assertError(IllegalArgumentException.class);
        mFolderRepository.delete("test").test().assertError(FolderNotFoundException.class);
    }

    @Test
    public void testDelete_list() {
        ListEntity list = insertTestList("list 2");
        Folder folder = insertTestFolder("test 2");
        mListRepository.setFolder(list.getId(), folder.getId()).test().assertComplete();
        mListRepository.get(list.getId()).test().assertValue(entity -> notEmpty(entity.getFolderId()));

        ListEntity listDelete = insertTestList("list 1");
        Folder folderDelete = insertTestFolder("test 1");
        mListRepository.setFolder(listDelete.getId(), folderDelete.getId()).test().assertComplete();
        mListRepository.get(listDelete.getId()).test().assertValue(entity -> notEmpty(entity.getFolderId()));
    }

}
