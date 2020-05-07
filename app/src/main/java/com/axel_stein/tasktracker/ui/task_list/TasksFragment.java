package com.axel_stein.tasktracker.ui.task_list;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.api.events.Events;
import com.axel_stein.tasktracker.api.model.TaskList;
import com.axel_stein.tasktracker.ui.dialog.SelectListDialog;
import com.axel_stein.tasktracker.ui.task_list.view_model.CompletedViewModel;
import com.axel_stein.tasktracker.ui.task_list.view_model.InboxViewModel;
import com.axel_stein.tasktracker.ui.task_list.view_model.ListViewModel;
import com.axel_stein.tasktracker.ui.task_list.view_model.SearchViewModel;
import com.axel_stein.tasktracker.ui.task_list.view_model.TasksViewModel;
import com.axel_stein.tasktracker.ui.task_list.view_model.TrashedViewModel;
import com.axel_stein.tasktracker.utils.MenuUtil;
import com.axel_stein.tasktracker.utils.ViewUtil;

import org.greenrobot.eventbus.Subscribe;

public class TasksFragment extends Fragment implements SelectListDialog.OnListSelectedListener {
    private static final String BUNDLE_VIEW_MODEL = "BUNDLE_VIEW_MODEL";
    private static final int VIEW_MODEL_INBOX = 0;
    private static final int VIEW_MODEL_COMPLETED = 1;
    private static final int VIEW_MODEL_TRASHED = 2;
    private static final int VIEW_MODEL_LIST = 3;
    private static final int VIEW_MODEL_SEARCH = 4;

    private TasksViewModel mViewModel;
    private TasksAdapter mListAdapter;
    private TextView mTextEmpty;

    @Nullable
    private ActionMode mActionMode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Events.subscribe(this);

        ViewModelProvider provider = new ViewModelProvider(this);
        Bundle args = requireArguments();
        int viewModel = args.getInt(BUNDLE_VIEW_MODEL);
        switch (viewModel) {
            case VIEW_MODEL_INBOX:
                mViewModel = provider.get(InboxViewModel.class);
                break;

            case VIEW_MODEL_COMPLETED:
                mViewModel = provider.get(CompletedViewModel.class);
                break;

            case VIEW_MODEL_TRASHED:
                mViewModel = provider.get(TrashedViewModel.class);
                break;

            case VIEW_MODEL_LIST:
                mViewModel = new ViewModelProvider(requireActivity()).get(ListViewModel.class);
                break;

            case VIEW_MODEL_SEARCH:
                mViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
                break;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mListAdapter = new TasksAdapter();
        mListAdapter.setOnItemClickListener((pos, task) -> mViewModel.onTaskClick(pos, task));
        mListAdapter.setOnItemLongClickListener((pos, task) -> mViewModel.onTaskLongClick(pos, task));
        mListAdapter.setOnCheckBoxClickListener(task -> mViewModel.setCompleted(task));

        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        mTextEmpty = view.findViewById(R.id.text_empty);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(mListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Subscribe
    public void invalidate(Events.InvalidateTasks e) {
        if (mListAdapter != null) {
            mListAdapter.submitList(null);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel.getTaskList().observe(getViewLifecycleOwner(), tasks -> {
            mListAdapter.submitList(tasks);
            ViewUtil.setVisible(tasks == null || tasks.size() == 0, mTextEmpty);
        });
        mViewModel.getOnTaskCheckListener().observe(getViewLifecycleOwner(), l -> {
            if (l.getCount() != 0) {
                startCheckMode();
                mListAdapter.setHashMap(l.getHashMap());
            }
            if (mActionMode != null) {
                mActionMode.setTitle(String.valueOf(l.getCount()));
                mListAdapter.notifyItemChanged(l.getPos());
            }
            if (l.getCount() == 0) {
                stopCheckMode();
            }
        });
    }

    public void startCheckMode() {
        if (mActionMode != null) {
            return;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity == null) {
            return;
        }
        mActionMode = activity.startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(mViewModel.getCheckModeMenuResId(), menu);
                MenuUtil.tintMenuIconsAttr(getContext(), menu, R.attr.menuItemTintColor);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.menu_move_to_list) {
                    SelectListDialog.launch(TasksFragment.this);
                } else {
                    mViewModel.onActionItemClick(item.getItemId());
                    stopCheckMode();
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                stopCheckMode();
                mActionMode = null;
            }
        });
    }

    public void stopCheckMode() {
        mViewModel.clearCheckedTasks();
        if (mActionMode != null) {
            mActionMode.finish();
        }
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (mViewModel.hasMenu()) {
            inflater.inflate(mViewModel.getMenuResId(), menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mViewModel.hasMenu()) {
            mViewModel.onMenuItemClick(item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListSelected(TaskList list) {
        mViewModel.setListForCheckedTasks(list);
        stopCheckMode();
    }
}
