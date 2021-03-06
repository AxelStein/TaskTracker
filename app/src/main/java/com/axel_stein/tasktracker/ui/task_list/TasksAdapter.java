package com.axel_stein.tasktracker.ui.task_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.ui.OnCheckBoxClickListener;
import com.axel_stein.tasktracker.ui.OnItemClickListener;
import com.axel_stein.tasktracker.ui.OnItemLongClickListener;
import com.axel_stein.tasktracker.utils.ViewUtil;

import java.util.HashMap;

import static android.graphics.PorterDuff.Mode.SRC_IN;
import static com.axel_stein.tasktracker.utils.ColorUtil.getColorArray;
import static com.axel_stein.tasktracker.utils.TextUtil.contentEquals;

public class TasksAdapter extends PagedListAdapter<Task, TasksAdapter.ViewHolder> {
    private OnItemClickListener<Task> mOnItemClickListener;
    private OnItemLongClickListener<Task> mOnItemLongClickListener;
    private OnCheckBoxClickListener<Task> mOnCheckBoxClickListener;
    private int[] mPriorityColors;
    private HashMap<String, Boolean> mHashMap;

    protected TasksAdapter() {
        super(new DiffUtil.ItemCallback<Task>() {
            @Override
            public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                return contentEquals(oldItem.getId(), newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    public void setHashMap(HashMap<String, Boolean> hashMap) {
        mHashMap = hashMap;
    }

    public void setOnItemClickListener(OnItemClickListener<Task> l) {
        mOnItemClickListener = l;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<Task> l) {
        mOnItemLongClickListener = l;
    }

    public void setOnCheckBoxClickListener(OnCheckBoxClickListener<Task> l) {
        mOnCheckBoxClickListener = l;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        ViewHolder vh = new ViewHolder(view);
        vh.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                int pos = vh.getAdapterPosition();
                mOnItemClickListener.onItemClick(pos, getItem(pos));
            }
        });
        vh.itemView.setOnLongClickListener(v -> {
            if (mOnItemLongClickListener != null) {
                int pos = vh.getAdapterPosition();
                mOnItemLongClickListener.onItemLongClick(pos, getItem(pos));
            }
            return true;
        });
        vh.mCheckBox.setOnClickListener(v -> {
            if (mOnCheckBoxClickListener != null) {
                mOnCheckBoxClickListener.onCheckBoxClick(getItem(vh.getAdapterPosition()));
            }
        });
        if (mPriorityColors == null) {
            mPriorityColors = getColorArray(parent.getContext(), R.array.priority_colors);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = getItem(position);
        if (task != null) {
            holder.bindTo(task, mPriorityColors, mHashMap);
        } else {
            // Null defines a placeholder item - PagedListAdapter automatically
            // invalidates this row when the actual object is loaded from the
            // database.
            holder.clear();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mCheckBox;
        TextView mTextTitle;
        TextView mTextReminder;
        View mForeground;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCheckBox = itemView.findViewById(R.id.check_box_completed);
            mTextTitle = itemView.findViewById(R.id.text_title);
            mTextReminder = itemView.findViewById(R.id.text_reminder);
            mForeground = itemView.findViewById(R.id.foreground);
        }

        void bindTo(Task task, int[] colors, HashMap<String, Boolean> hashMap) {
            mTextTitle.setText(task.getTitle());
            mCheckBox.setImageResource(task.isCompleted() ? R.drawable.ic_check_box_24px : R.drawable.ic_check_box_outline_blank_24px);
            if (colors != null) {
                mCheckBox.setColorFilter(colors[task.getPriority()], SRC_IN);
            }
            ViewUtil.setVisible(hashMap != null && checkBoolean(hashMap.get(task.getId())), mForeground);
            ViewUtil.setVisible(task.hasReminder(), mTextReminder);
            if (task.hasReminder()) {
                mTextReminder.setText(task.getReminderFormatted());
                mTextReminder.setSelected(task.isReminderPassed());
            }
        }

        boolean checkBoolean(Boolean val) {
            return val == null ? false : val;
        }

        void clear() {
            mCheckBox.setImageResource(R.drawable.ic_check_box_outline_blank_24px);
            mTextTitle.setText(null);
        }

    }

}
