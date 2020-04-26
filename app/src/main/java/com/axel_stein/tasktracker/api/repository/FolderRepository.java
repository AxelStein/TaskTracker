package com.axel_stein.tasktracker.api.repository;

import com.axel_stein.tasktracker.api.model.Folder;
import com.axel_stein.tasktracker.api.room.dao.TaskListDao;
import com.axel_stein.tasktracker.api.room.dao.FolderDao;

import java.util.UUID;

import io.reactivex.Completable;
import io.reactivex.Flowable;

import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.completable;
import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.flowable;
import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.folderExists;
import static com.axel_stein.tasktracker.utils.ArgsUtil.checkRules;
import static com.axel_stein.tasktracker.utils.ArgsUtil.notEmptyString;
import static com.axel_stein.tasktracker.utils.ArgsUtil.notNull;
import static com.axel_stein.tasktracker.utils.TextUtil.isEmpty;

public class FolderRepository {
    private FolderDao mDao;
    private TaskListDao mListDao;

    public FolderRepository(FolderDao dao, TaskListDao listDao) {
        mDao = dao;
        mListDao = listDao;
    }

    public Completable insert(final Folder folder) {
        return completable(() -> {
            checkRules(notNull(folder));
            checkRules(notEmptyString(folder.getName()));
            if (isEmpty(folder.getId())) folder.setId(UUID.randomUUID().toString());
            mDao.insert(folder);
        });
    }

    public Completable setName(final String id, final String name) {
        return completable(() -> {
            checkRules(
                    notEmptyString(id, name),
                    folderExists(mDao, id)
            );
            mDao.setName(id, name);
        });
    }

    public Flowable<Folder> get(final String id) {
        return flowable(() -> {
            checkRules(
                    notEmptyString(id),
                    folderExists(mDao, id)
            );
            return mDao.get(id);
        });
    }

    public Completable delete(final String id) {
        return completable(() -> {
            checkRules(
                    notEmptyString(id),
                    folderExists(mDao, id)
            );
            mDao.delete(id);
            mListDao.clearFolder(id);
        });
    }

}
