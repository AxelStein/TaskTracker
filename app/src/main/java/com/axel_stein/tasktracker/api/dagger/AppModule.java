package com.axel_stein.tasktracker.api.dagger;

import androidx.room.Room;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.api.reminder.AndroidNotificationTray;
import com.axel_stein.tasktracker.api.reminder.ReminderScheduler;
import com.axel_stein.tasktracker.api.repository.TaskListRepository;
import com.axel_stein.tasktracker.api.repository.FolderRepository;
import com.axel_stein.tasktracker.api.repository.ReminderRepository;
import com.axel_stein.tasktracker.api.repository.TaskRepository;
import com.axel_stein.tasktracker.api.room.AppDatabase;
import com.axel_stein.tasktracker.ui.IntentActionFactory;
import com.axel_stein.tasktracker.utils.DateTimeUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final App mApp;

    public AppModule(App app) {
        mApp = app;
    }

    @Provides
    @Singleton
    App provideApp() {
        return mApp;
    }

    @Provides
    @Singleton
    AppDatabase provideDatabase(App app) {
        return Room.databaseBuilder(app, AppDatabase.class, app.getPackageName()).build();
    }

    @Provides
    @Singleton
    TaskRepository provideTaskRepository(App app, AppDatabase db, DateTimeUtil dateTimeUtil) {
        return new TaskRepository(app, db.getTaskDao(), db.getListDao(), db.getReminderDao(), dateTimeUtil);
    }

    @Provides
    @Singleton
    TaskListRepository provideListRepository(AppDatabase db) {
        return new TaskListRepository(db.getListDao(), db.getFolderDao(), db.getTaskDao());
    }

    @Provides
    @Singleton
    FolderRepository provideFolderRepository(AppDatabase db) {
        return new FolderRepository(db.getFolderDao(), db.getListDao());
    }

    @Provides
    @Singleton
    ReminderRepository provideReminderRepository(AppDatabase db, ReminderScheduler scheduler) {
        return new ReminderRepository(db.getReminderDao(), db.getTaskDao(), scheduler);
    }

    @Provides
    ReminderScheduler provideReminderScheduler(App app) {
        return new ReminderScheduler(app);
    }

    @Provides
    AndroidNotificationTray provideAndroidNotificationTray(App app) {
        return new AndroidNotificationTray(app);
    }

    @Provides
    IntentActionFactory provideIntentActionFactory(App app) {
        return new IntentActionFactory(app);
    }

    @Provides
    @Singleton
    DateTimeUtil provideDateTimeUtil(App app) {
        return new DateTimeUtil(app);
    }

}
