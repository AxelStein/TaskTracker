package com.axel_stein.tasktracker.ui.edit_task;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.api.events.Events;
import com.axel_stein.tasktracker.api.model.Task;
import com.axel_stein.tasktracker.api.model.TaskList;
import com.axel_stein.tasktracker.ui.IntentActionFactory;
import com.axel_stein.tasktracker.ui.dialog.SelectListDialog;
import com.axel_stein.tasktracker.utils.DisplayUtil;
import com.axel_stein.tasktracker.utils.KeyboardUtil;
import com.axel_stein.tasktracker.utils.MenuUtil;
import com.axel_stein.tasktracker.utils.SimpleTextWatcher;
import com.axel_stein.tasktracker.utils.ViewUtil;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

public class EditTaskActivity extends AppCompatActivity implements SelectListDialog.OnListSelectedListener {
    private View mScrollView;
    private CheckBox mCheckBoxCompleted;
    private EditText mEditTitle;
    private TextView mTextList;
    private Spinner mSpinnerPriority;
    private TextView mTextReminder;
    private EditTaskViewModel mViewModel;
    private SimpleTextWatcher mTextWatcher;
    private View mFocusView;
    private TextView mTextError;
    private boolean mShowMenu = true;

    @Inject
    IntentActionFactory mIntentActionFactory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        Events.subscribe(this);
        setContentView(R.layout.activity_edit_task);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mScrollView = findViewById(R.id.scroll_view);
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
        mTextList.setOnClickListener(v -> new SelectListDialog().show(getSupportFragmentManager(), null));

        mTextReminder = findViewById(R.id.text_reminder);
        mTextError = findViewById(R.id.text_error);

        mSpinnerPriority = findViewById(R.id.spinner_priority);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority, R.layout.item_spinner_priority);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerPriority.setAdapter(adapter);
        mSpinnerPriority.setDropDownHorizontalOffset(DisplayUtil.dpToPx(this, 72));
        mSpinnerPriority.setOnItemSelectedListener(new OnItemSelectedListener() {
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
                    ViewUtil.hide(mTextError);
                    ViewUtil.show(mScrollView);

                    Task task = state.getData();
                    mCheckBoxCompleted.setChecked(task.isCompleted());
                    mSpinnerPriority.setSelection(task.getPriority());
                    mEditTitle.setText(task.getTitle());
                    if (!task.isTrashed() && mEditTitle.getText().length() == 0) {
                        mEditTitle.requestFocus();
                        KeyboardUtil.show(mEditTitle);
                    }
                    mTextList.setText(task.getListName());
                    ViewUtil.enable(!task.isCompleted(), mTextReminder);
                    if (task.isTrashed()) {
                        ViewUtil.enable(false, mCheckBoxCompleted, mSpinnerPriority,
                                mEditTitle, mTextList, mTextReminder);
                    }
                    invalidateOptionsMenu();

                    mTextReminder.setText(task.getReminderFormatted());
                    mTextReminder.setOnClickListener(v -> {
                        if (task.hasReminder()) {
                            mIntentActionFactory.editReminder(task.getReminderId());
                        } else {
                            mIntentActionFactory.addReminder(task.getId());
                        }
                    });
                    break;

                case EditTaskViewState.STATE_ERROR:
                    ViewUtil.show(mTextError);
                    ViewUtil.hide(mScrollView);

                    mShowMenu = false;
                    invalidateOptionsMenu();
                    break;
            }
        });
    }

    @Subscribe
    public void invalidateEditTask(Events.InvalidateEditTask e) {
        mViewModel.invalidate();
    }

    @Override
    protected void onDestroy() {
        mEditTitle.removeTextChangedListener(mTextWatcher);
        Events.unsubscribe(this);
        super.onDestroy();
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
        if (mShowMenu) {
            getMenuInflater().inflate(R.menu.menu_edit_task, menu);
            MenuUtil.tintMenuIconsAttr(this, menu, R.attr.menuItemTintColor);

            boolean trashed = mViewModel.isTrashed();
            MenuUtil.showGroup(menu, R.id.group_trash, trashed);
            MenuUtil.showGroup(menu, R.id.group_main, !trashed);
        }
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
                actionShare();
                break;

            case R.id.menu_duplicate:
                mViewModel.duplicate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void actionShare() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, mEditTitle.getText().toString());

        PackageManager pm = getPackageManager();
        if (pm != null) {
            if (intent.resolveActivity(pm) != null) {
                startActivity(intent);
            } else {
                Log.e("TAG", "share note: no activity found");
                Snackbar.make(mFocusView, R.string.error, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onListSelected(TaskList list) {
        mViewModel.setList(list);
    }

}
