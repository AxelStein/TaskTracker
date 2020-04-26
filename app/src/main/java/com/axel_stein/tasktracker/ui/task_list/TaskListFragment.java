package com.axel_stein.tasktracker.ui.task_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.ui.task_list.view_model.CompletedViewModel;
import com.axel_stein.tasktracker.ui.task_list.view_model.InboxViewModel;
import com.axel_stein.tasktracker.ui.task_list.view_model.TaskListViewModel;
import com.axel_stein.tasktracker.ui.task_list.view_model.TrashedViewModel;
import com.axel_stein.tasktracker.utils.ViewUtil;

public class TaskListFragment extends Fragment {
    public static final String BUNDLE_VIEW_MODEL = "BUNDLE_VIEW_MODEL";
    public static final int VIEW_MODEL_INBOX = 0;
    public static final int VIEW_MODEL_COMPLETED = 1;
    public static final int VIEW_MODEL_TRASHED = 2;

    private TaskListViewModel mViewModel;
    private TaskListAdapter mListAdapter;
    private TextView mTextEmpty;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            int viewModel = args.getInt(BUNDLE_VIEW_MODEL);
            ViewModelProvider provider = new ViewModelProvider(this);

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
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mListAdapter = new TaskListAdapter();
        mListAdapter.setOnItemClickListener(task -> {

        });
        mListAdapter.setOnItemLongClickListener(task -> {

        });
        mListAdapter.setOnCheckBoxClickListener(task -> mViewModel.setCompleted(task));

        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        mTextEmpty = view.findViewById(R.id.text_empty);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mViewModel != null) {
            mViewModel.getTaskList().observe(getViewLifecycleOwner(), tasks -> {
                mListAdapter.submitList(tasks);
                ViewUtil.setVisible(tasks == null || tasks.size() == 0, mTextEmpty);
            });
        }
    }

}
