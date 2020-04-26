package com.axel_stein.tasktracker.ui.edit_task;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.utils.MenuUtil;
import com.axel_stein.tasktracker.utils.SimpleTextWatcher;

public class EditTaskActivity extends AppCompatActivity {
    private CheckBox mCheckBoxCompleted;
    private EditText mEditTitle;
    private TextView mTextList;
    private Spinner mSpinnerPriority;
    private TextView mTextReminder;
    private EditTaskViewModel mViewModel;
    private SimpleTextWatcher mTextWatcher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mCheckBoxCompleted = findViewById(R.id.check_box_completed);
        mEditTitle = findViewById(R.id.edit_title);
        mTextWatcher = new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mViewModel.onTitleChanged(s.toString());
            }
        };
        mTextList = findViewById(R.id.text_list);
        mSpinnerPriority = findViewById(R.id.spinner_priority);

        mViewModel = new ViewModelProvider(this).get(EditTaskViewModel.class);

        Intent intent = getIntent();
        String action = intent.getAction();
        mViewModel.getData(action).observe(this, task -> {
            mCheckBoxCompleted.setChecked(task.isCompleted());
            mCheckBoxCompleted.setOnClickListener(v -> mViewModel.onCompletedChanged());

            mSpinnerPriority.setOnItemSelectedListener(null);
            mSpinnerPriority.setSelection(task.getPriority());
            mSpinnerPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mViewModel.onPriorityChanged(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            mEditTitle.removeTextChangedListener(mTextWatcher);
            mEditTitle.setText(task.getTitle());
            mEditTitle.addTextChangedListener(mTextWatcher);

            mTextList.setText(task.getListName());
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_task, menu);
        MenuUtil.tintMenuIconsAttr(this, menu, R.attr.menuItemTintColor);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.menu_delete:
                mViewModel.onDelete();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
