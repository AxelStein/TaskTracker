package com.axel_stein.tasktracker.api.dagger;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.api.reminder.ReminderReceiver;
import com.axel_stein.tasktracker.api.reminder.ReminderService;
import com.axel_stein.tasktracker.ui.MainActivity;
import com.axel_stein.tasktracker.ui.task_list.view_model.TaskListViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(App app);
    void inject(ReminderReceiver receiver);
    void inject(ReminderService service);
    void inject(TaskListViewModel viewModel);
    void inject(MainActivity activity);
}
