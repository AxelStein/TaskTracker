package com.axel_stein.tasktracker.ui.edit_task;

import android.content.Intent;
import android.graphics.Rect;
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
import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.ui.IntentActionFactory;
import com.axel_stein.tasktracker.utils.MenuUtil;
import com.axel_stein.tasktracker.utils.SimpleTextWatcher;
import com.axel_stein.tasktracker.utils.ViewUtil;
import com.google.android.material.snackbar.Snackbar;

public class EditTaskActivity extends AppCompatActivity {
    private CheckBox mCheckBoxCompleted;
    private EditText mEditTitle;
    private TextView mTextList;
    private Spinner mSpinnerPriority;
    private TextView mTextReminder;
    private EditTaskViewModel mViewModel;
    private SimpleTextWatcher mTextWatcher;
    private View mFocusView;

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

        mFocusView = findViewById(R.id.focus_view);
        mCheckBoxCompleted = findViewById(R.id.check_box_completed);
        mCheckBoxCompleted.setOnCheckedChangeListener((view, checked) -> mViewModel.setCompleted(checked));

        mEditTitle = findViewById(R.id.edit_title);
        mTextWatcher = new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mViewModel.setTitle(s.toString());
            }
        };
        mEditTitle.addTextChangedListener(mTextWatcher);

        mTextList = findViewById(R.id.text_list);
        mSpinnerPriority = findViewById(R.id.spinner_priority);
        mSpinnerPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.setPriority(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        final View viewMain = findViewById(R.id.coordinator_edit);
        viewMain.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            viewMain.getWindowVisibleDisplayFrame(r);
            int screenHeight = viewMain.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;
            if (keypadHeight > screenHeight * 0.15) {
                // 0.15 ratio is perhaps enough to determine keypad height.
                // keyboard is opened
                if (!isKeyboardShowing) {
                    isKeyboardShowing = true;
                    onKeyboardVisibilityChanged(true);
                }
            } else {
                // keyboard is closed
                if (isKeyboardShowing) {
                    isKeyboardShowing = false;
                    onKeyboardVisibilityChanged(false);
                }
            }
        });

        mViewModel = new ViewModelProvider(this).get(EditTaskViewModel.class);

        Intent intent = getIntent();
        String id = intent.getStringExtra(IntentActionFactory.EXTRA_TASK_ID);
        String listId = intent.getStringExtra(IntentActionFactory.EXTRA_LIST_ID);
        mViewModel.getData(id, listId).observe(this, state -> {
            switch (state.getState()) {
                case EditTaskViewState.STATE_SUCCESS:
                    Task task = state.getData();
                    mCheckBoxCompleted.setChecked(task.isCompleted());
                    mSpinnerPriority.setSelection(task.getPriority());
                    mEditTitle.setText(task.getTitle());
                    mTextList.setText(task.getListName());
                    ViewUtil.enable(!task.isTrashed(), mCheckBoxCompleted, mSpinnerPriority, mEditTitle, mTextList);
                    invalidateOptionsMenu();
                    // todo reminder
                    break;

                case EditTaskViewState.STATE_ERROR: // todo
                    Snackbar.make(mFocusView, R.string.error, Snackbar.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private boolean isKeyboardShowing = false;
    private void onKeyboardVisibilityChanged(boolean opened) {
        if (!opened) {
            hideKeyboard();
        }
    }

    public void hideKeyboard() {
        if (!mFocusView.hasFocus()) {
            mFocusView.requestFocus();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_task, menu);
        MenuUtil.tintMenuIconsAttr(this, menu, R.attr.menuItemTintColor);

        boolean trashed = mViewModel.isTrashed();
        MenuUtil.showGroup(menu, R.id.group_trash, trashed);
        MenuUtil.showGroup(menu, R.id.group_main, !trashed);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.menu_delete:
                mViewModel.delete();
                finish();
                break;

            case R.id.menu_delete_forever:
                mViewModel.deleteForever(); // todo confirmation
                finish();
                break;

            case R.id.menu_restore:
                mViewModel.restore();
                finish();
                break;

            case R.id.menu_share:
                break;

            case R.id.menu_duplicate:
                mViewModel.duplicate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
