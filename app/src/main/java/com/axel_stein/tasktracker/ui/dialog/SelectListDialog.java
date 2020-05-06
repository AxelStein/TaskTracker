package com.axel_stein.tasktracker.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.api.model.TaskList;
import com.axel_stein.tasktracker.api.repository.TaskListRepository;
import com.axel_stein.tasktracker.ui.IntentActionFactory;
import com.axel_stein.tasktracker.views.IconTextView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import javax.inject.Inject;

import static com.axel_stein.tasktracker.utils.TextUtil.notEmpty;

public class SelectListDialog extends BottomSheetDialogFragment {
    private OnListSelectedListener mListener;

    @Inject
    IntentActionFactory mIntentActionFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Fragment fragment = getTargetFragment();
        if (fragment != null) {
            setListeners(fragment);
        } else {
            FragmentActivity activity = getActivity();
            if (activity != null) {
                setListeners(activity);
            }
        }
    }

    private void setListeners(Object o) {
        if (o instanceof OnListSelectedListener) {
            mListener = (OnListSelectedListener) o;
        }
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        mListener = null;
        super.onDestroyView();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = View.inflate(requireContext(), R.layout.dialog_select_list, null);
        view.findViewById(R.id.btn_action).setOnClickListener(v -> {
            mIntentActionFactory.addList();
            //dismiss(); // fixme
        });

        Adapter adapter = new Adapter(requireContext());
        adapter.setOnItemClickListener(list -> {
            if (mListener != null) {
                mListener.onListSelected(list);
            }
            dismiss();
        });

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        ListViewModel viewModel = new ViewModelProvider(this).get(ListViewModel.class);
        viewModel.getData().observe(this, adapter::setItems);

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), getTheme());
        dialog.setContentView(view);
        return dialog;
    }

    public static class ListViewModel extends ViewModel {
        MutableLiveData<List<TaskList>> mData;

        @Inject
        TaskListRepository mRepository;

        public ListViewModel() {
            App.getAppComponent().inject(this);
        }

        LiveData<List<TaskList>> getData() {
            if (mData == null) {
                mData = new MutableLiveData<>();
                loadData();
            }
            return mData;
        }

        @SuppressLint("CheckResult")
        private void loadData() {
            mRepository.query().subscribe(items -> {
                TaskList list = new TaskList();
                list.setName("Inbox"); // fixme
                items.add(0, list);
                mData.postValue(items);
            });
        }
    }

    public interface OnListSelectedListener {
        void onListSelected(TaskList list);
    }

    private static class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private final LayoutInflater mInflater;
        private List<TaskList> mItems;
        private OnItemClickListener mOnItemClickListener;
        private String mSelectedListId;

        Adapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        void setOnItemClickListener(OnItemClickListener l) {
            mOnItemClickListener = l;
        }

        void setItems(List<TaskList> items) {
            mItems = items;
            notifyDataSetChanged();
        }

        void setSelectedListId(String selectedListId) {
            mSelectedListId = selectedListId;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final ViewHolder holder = new ViewHolder(mInflater.inflate(R.layout.item_bottom_menu, parent, false));
            holder.itemView.setOnClickListener(view -> {
                int pos = holder.getAdapterPosition();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mItems.get(pos));
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TaskList list = mItems.get(position);
            holder.mTextView.setText(list.getName());

            if (notEmpty(mSelectedListId)) {
                int icon = R.drawable.ic_radio_button_unchecked_24px;
                if (TextUtils.equals(list.getId(), mSelectedListId)) {
                    icon = R.drawable.ic_radio_button_checked_24px;
                }
                holder.mTextView.setIconLeft(icon);
            }
        }

        @Override
        public int getItemCount() {
            return mItems == null ? 0 : mItems.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private final IconTextView mTextView;

        ViewHolder(View view) {
            super(view);
            mTextView = (IconTextView) view;
        }
    }

    interface OnItemClickListener {
        void onItemClick(TaskList list);
    }

}
