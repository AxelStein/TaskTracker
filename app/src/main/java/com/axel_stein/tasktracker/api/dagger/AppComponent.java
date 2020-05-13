package com.axel_stein.tasktracker.api.dagger;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.api.reminder.ReminderReceiver;
import com.axel_stein.tasktracker.api.reminder.ReminderScheduler;
import com.axel_stein.tasktracker.api.reminder.ReminderService;
import com.axel_stein.tasktracker.ui.MainActivity;
import com.axel_stein.tasktracker.ui.dialog.SelectListDialog;
import com.axel_stein.tasktracker.ui.edit_list.EditListViewModel;
import com.axel_stein.tasktracker.ui.edit_reminder.EditReminderActivity;
import com.axel_stein.tasktracker.ui.edit_reminder.EditReminderViewModel;
import com.axel_stein.tasktracker.ui.edit_task.EditTaskActivity;
import com.axel_stein.tasktracker.ui.edit_task.EditTaskViewModel;
import com.axel_stein.tasktracker.ui.task_list.view_model.TasksViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(App app);
    void inject(ReminderReceiver receiver);
    void inject(ReminderService service);
    void inject(TasksViewModel viewModel);
    void inject(MainActivity activity);
    void inject(EditTaskViewModel viewModel);
    void inject(EditListViewModel viewModel);
    void inject(SelectListDialog.ListViewModel viewModel);
    void inject(SelectListDialog dialog);
    void inject(EditReminderViewModel viewModel);
    void inject(EditTaskActivity activity);
    void inject(EditReminderActivity activity);
    void inject(ReminderScheduler scheduler);
}
