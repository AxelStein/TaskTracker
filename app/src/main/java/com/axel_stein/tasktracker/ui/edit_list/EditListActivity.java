package com.axel_stein.tasktracker.ui.edit_list;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.api.model.TaskList;
import com.axel_stein.tasktracker.ui.BaseViewAction;
import com.axel_stein.tasktracker.utils.MenuUtil;
import com.axel_stein.tasktracker.utils.SimpleTextWatcher;
import com.axel_stein.tasktracker.utils.TextUtil;
import com.axel_stein.tasktracker.views.IconTextView;
import com.google.android.material.snackbar.Snackbar;

import static com.axel_stein.tasktracker.ui.IntentActionFactory.EXTRA_LIST_ID;

public class EditListActivity extends AppCompatActivity {
    private EditText mEditName;
    private IconTextView mTextColor;
    private Switch mSwitchClose;
    private EditListViewModel mViewModel;

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

        mEditName = findViewById(R.id.edit_name);
        mEditName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mViewModel.onNameChanged(s.toString());
            }
        });
        mTextColor = findViewById(R.id.text_color);
        mSwitchClose = findViewById(R.id.switch_close);
        mSwitchClose.setOnCheckedChangeListener((v, checked) -> mViewModel.onCloseChanged(checked));

        Intent intent = getIntent();
        String id = intent.getStringExtra(EXTRA_LIST_ID);

        mViewModel = new ViewModelProvider(this).get(EditListViewModel.class);
        mViewModel.getData(id).observe(this, viewState -> {
            switch (viewState.getState()) {
                case EditListViewState.STATE_SUCCESS:
                    TaskList list = viewState.getData();
                    mEditName.setText(list.getName());
                    mEditName.setSelection(TextUtil.length(mEditName.getText()));
                    mTextColor.setIconRightTintColor(list.getColor());
                    mSwitchClose.setChecked(list.isClosed());
                    break;

                case EditListViewState.STATE_ERROR:
                    Snackbar.make(mTextColor, R.string.error, Snackbar.LENGTH_SHORT).show();
                    break;
            }
        });
        mViewModel.getActions().observe(this, action -> {
            switch (action.getId()) {
                case BaseViewAction.ACTION_FINISH:
                    finish();
                    break;

                case BaseViewAction.ACTION_SHOW_MESSAGE:
                    Snackbar.make(mTextColor, action.getIntExtra(), Snackbar.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_list, menu);
        MenuUtil.tintMenuIconsAttr(this, menu, R.attr.menuItemTintColor);
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
