package com.axel_stein.tasktracker.ui.task_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.ui.OnCheckBoxClickListener;
import com.axel_stein.tasktracker.ui.OnItemClickListener;
import com.axel_stein.tasktracker.ui.OnItemLongClickListener;

import static android.graphics.PorterDuff.Mode.SRC_IN;
import static com.axel_stein.tasktracker.utils.ColorUtil.getColorArray;
import static com.axel_stein.tasktracker.utils.TextUtil.contentEquals;

public class TasksAdapter extends PagedListAdapter<Task, TasksAdapter.ViewHolder> {
    private OnItemClickListener<Task> mOnItemClickListener;
    private OnItemLongClickListener<Task> mOnItemLongClickListener;
    private OnCheckBoxClickListener<Task> mOnCheckBoxClickListener;
    private int[] mPriorityColors;

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
                mOnItemClickListener.onItemClick(getItem(vh.getAdapterPosition()));
            }
        });
        vh.itemView.setOnLongClickListener(v -> {
            if (mOnItemLongClickListener != null) {
                mOnItemLongClickListener.onItemLongClick(getItem(vh.getAdapterPosition()));
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
            holder.bindTo(task, mPriorityColors);
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

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCheckBox = itemView.findViewById(R.id.check_box_completed);
            mTextTitle = itemView.findViewById(R.id.text_title);
        }

        void bindTo(Task task, int[] colors) {
            mTextTitle.setText(task.getTitle());
            mCheckBox.setImageResource(task.isCompleted() ? R.drawable.ic_check_box_24px : R.drawable.ic_check_box_outline_blank_24px);
            if (colors != null) {
                mCheckBox.setColorFilter(colors[task.getPriority()], SRC_IN);
            }
        }

        void clear() {
            mCheckBox.setImageResource(R.drawable.ic_check_box_outline_blank_24px);
            mTextTitle.setText(null);
        }

    }

}
