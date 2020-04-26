package com.axel_stein.tasktracker.api.repository;

import com.axel_stein.tasktracker.api.model.ListEntity;
import com.axel_stein.tasktracker.api.room.dao.FolderDao;
import com.axel_stein.tasktracker.api.room.dao.ListDao;
import com.axel_stein.tasktracker.api.room.dao.TaskDao;
import com.axel_stein.tasktracker.utils.LogUtil;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.reactivex.Completable;
import io.reactivex.Flowable;

import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.completable;
import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.flowable;
import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.folderExists;
import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.listExists;
import static com.axel_stein.tasktracker.api.repository.RepositoryUtil.listNotClosed;
import static com.axel_stein.tasktracker.utils.ArgsUtil.checkRules;
import static com.axel_stein.tasktracker.utils.ArgsUtil.checkRulesSilent;
import static com.axel_stein.tasktracker.utils.ArgsUtil.notEmptyString;
import static com.axel_stein.tasktracker.utils.ArgsUtil.notNull;
import static com.axel_stein.tasktracker.utils.TextUtil.isEmpty;
import static com.axel_stein.tasktracker.utils.TextUtil.notEmpty;

public class ListRepository {
    private ListDao mDao;
    private FolderDao mFolderDao;
    private TaskDao mTaskDao;

    public ListRepository(ListDao dao, FolderDao folderDao, TaskDao taskDao) {
        mDao = Objects.requireNonNull(dao);
        mFolderDao = Objects.requireNonNull(folderDao);
        mTaskDao = Objects.requireNonNull(taskDao);
    }

    public Completable insert(final ListEntity list) {
        return completable(() -> {
            checkRules(notNull(list));
            checkRules(notEmptyString(list.getName()));
            if (isEmpty(list.getId())) list.setId(UUID.randomUUID().toString());
            mDao.insert(list);
        });
    }

    public Completable setName(final String id, final String name) {
        return completable(() -> {
            checkRules(
                    notEmptyString(id, name),
                    listExists(mDao, id),
                    listNotClosed(mDao, id)
            );
            mDao.setName(id, name);
        });
    }

    public Completable setFolder(final String id, final String folderId) {
        return completable(() -> {
            checkRules(
                    notEmptyString(id),
                    listExists(mDao, id),
                    listNotClosed(mDao, id)
            );
            if (notEmpty(folderId)) {
                checkRules(folderExists(mFolderDao, folderId));
            }
            mDao.setFolder(id, folderId);
        });
    }

    public Completable setColor(final String id, final int color) {
        return completable(() -> {
            checkRules(
                    notEmptyString(id),
                    listExists(mDao, id),
                    listNotClosed(mDao, id)
            );
            mDao.setColor(id, color);
        });
    }

    public Completable setClosed(final String id, final boolean closed) {
        return completable(() -> {
            checkRules(
                    notEmptyString(id),
                    listExists(mDao, id)
            );
            mDao.setClosed(id, closed);
        });
    }

    public Flowable<ListEntity> get(final String id) {
        return flowable(() -> {
            checkRules(
                    notEmptyString(id),
                    listExists(mDao, id)
            );
            ListEntity list = mDao.get(id);
            String folderId = list.getFolderId();
            if (notEmpty(folderId)) {
                if (checkRulesSilent(folderExists(mFolderDao, folderId))) {
                    list.setFolderName(mFolderDao.getName(folderId));
                } else {
                    LogUtil.error("Folder with id " + folderId + " not exists. Clear folderId");
                    mDao.setFolder(id, null);
                }
            }
            return mDao.get(id);
        });
    }

    public Flowable<List<ListEntity>> query() {
        return flowable(() -> {
            List<ListEntity> lists = mDao.query();
            for (ListEntity l : lists) {
                l.setFolderName(mFolderDao.getName(l.getFolderId()));
            }
            return lists;
        });
    }

    public Completable delete(final String id) {
        return completable(() -> {
            checkRules(
                    notEmptyString(id),
                    listExists(mDao, id)
            );
            mDao.delete(id);
            mTaskDao.deleteList(id);
        });
    }

}
