package com.axel_stein.tasktracker.ui.edit_list;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.api.model.TaskList;
import com.axel_stein.tasktracker.utils.KeyboardUtil;
import com.axel_stein.tasktracker.utils.MenuUtil;
import com.axel_stein.tasktracker.utils.SimpleTextWatcher;
import com.axel_stein.tasktracker.utils.TextUtil;
import com.axel_stein.tasktracker.utils.ViewUtil;
import com.axel_stein.tasktracker.views.IconTextView;
import com.google.android.material.snackbar.Snackbar;

import static android.text.TextUtils.isEmpty;
import static com.axel_stein.tasktracker.ui.IntentActionFactory.EXTRA_LIST_ID;

public class EditListActivity extends AppCompatActivity {
    private View mFocusView;
    private EditText mEditName;
    private TextView mTextError;
    private IconTextView mTextColor;
    private Switch mSwitchClose;
    private EditListViewModel mViewModel;
    private TextWatcher mTextWatcher;
    private boolean mEnableDoneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mTextWatcher = new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                boolean empty = setErrorText(isEmpty(s), R.string.error_name_required);
                boolean exists = setErrorText(!mViewModel.setName(s.toString()), R.string.error_name_exists);
                ViewUtil.setVisible(empty || exists, mTextError);

                mEnableDoneButton = !empty && !exists;
                invalidateOptionsMenu();
            }
        };
        mFocusView = findViewById(R.id.focus_view);
        mEditName = findViewById(R.id.edit_name);
        mTextError = findViewById(R.id.text_error);
        mTextColor = findViewById(R.id.text_color);
        mSwitchClose = findViewById(R.id.switch_close);
        mSwitchClose.setOnCheckedChangeListener((v, checked) -> mViewModel.setClosed(checked));

        Intent intent = getIntent();
        String id = intent.getStringExtra(EXTRA_LIST_ID);
        mViewModel = new ViewModelProvider(this).get(EditListViewModel.class);
        mViewModel.getData(id).observe(this, viewState -> {
            switch (viewState.getState()) {
                case EditListViewState.STATE_SUCCESS:
                    TaskList list = viewState.getData();
                    mEditName.setText(list.getName());
                    mEditName.setSelection(TextUtil.length(mEditName.getText()));
                    if (mEditName.getText().length() == 0) {
                        mEditName.requestFocus();
                        KeyboardUtil.show(mEditName);
                    } else {
                        mEditName.clearFocus();
                    }
                    mEditName.addTextChangedListener(mTextWatcher);
                    mTextColor.setIconRightTintColor(list.getColor());
                    mSwitchClose.setChecked(list.isClosed());
                    invalidateOptionsMenu();
                    break;

                case EditListViewState.STATE_ERROR:
                    Snackbar.make(mTextColor, R.string.error, Snackbar.LENGTH_SHORT).show();
                    break;
            }
        });

        final View viewMain = findViewById(R.id.coordinator);
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

    private boolean setErrorText(boolean set, int textResId) {
        if (set) {
            mTextError.setText(textResId);
        }
        return set;
    }

    @Override
    protected void onDestroy() {
        mEditName.removeTextChangedListener(mTextWatcher);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_list, menu);
        MenuUtil.tintMenuIconsAttr(this, menu, R.attr.menuItemTintColor);
        MenuUtil.show(menu, mViewModel.hasId(), R.id.menu_delete);
        MenuUtil.enable(menu, mEnableDoneButton, R.id.menu_done);
        return true;
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

            case R.id.menu_done:
                mViewModel.save();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
