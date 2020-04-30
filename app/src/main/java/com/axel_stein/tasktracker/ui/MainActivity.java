package com.axel_stein.tasktracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.api.model.TaskList;
import com.axel_stein.tasktracker.api.repository.TaskListRepository;
import com.axel_stein.tasktracker.api.repository.TaskRepository;
import com.axel_stein.tasktracker.ui.task_list.view_model.ListViewModel;
import com.axel_stein.tasktracker.ui.task_list.view_model.SearchViewModel;
import com.axel_stein.tasktracker.utils.MenuUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static androidx.core.view.GravityCompat.START;
import static androidx.navigation.ui.NavigationUI.onNavDestinationSelected;
import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;
import static com.axel_stein.tasktracker.utils.MenuUtil.removeGroupItems;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private NavController mNavController;

    @Inject
    TaskRepository mRepository;

    @Inject
    TaskListRepository mListRepository;

    @Inject
    IntentActionFactory mIntentActionFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> mIntentActionFactory.addTask());

        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.fragment_list:
                    ListViewModel viewModel = new ViewModelProvider(this).get(ListViewModel.class);
                    viewModel.setListId(item.getIntent().getAction());
                    break;

                case R.id.menu_add_list:
                    mIntentActionFactory.addList();
                    return true;
            }
            mDrawerLayout.closeDrawer(START);
            return onNavDestinationSelected(item, mNavController);
        });

        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(mListRepository.query().subscribe(books -> {
            Menu menu = mNavigationView.getMenu();
            removeGroupItems(menu, R.id.group_list);
            for (TaskList taskList : books) {
                MenuItem item = menu.add(R.id.group_list, R.id.fragment_list, 0, taskList.getName());
                item.setIcon(R.drawable.ic_list_alt_24px);
                item.setCheckable(true);
                item.setIntent(new Intent().setAction(taskList.getId()));
            }
            menu.add(R.id.group_list, R.id.menu_add_list, 1, "Add list").setIcon(R.drawable.ic_add_box_24px);
        }, Throwable::printStackTrace));

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.fragment_inbox, R.id.fragment_completed,
                R.id.fragment_trash, R.id.fragment_list)
                .setDrawerLayout(mDrawerLayout)
                .build();
        setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
    }

    private void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(START)) {
            mDrawerLayout.closeDrawer(START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuUtil.tintMenuIconsAttr(this, menu, R.attr.menuItemTintColor);

        MenuItem searchItem = menu.findItem( R.id.fragment_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchViewModel viewModel = new ViewModelProvider(MainActivity.this).get(SearchViewModel.class);
                viewModel.setQuery(query);
                onNavDestinationSelected(searchItem, mNavController);

                searchView.clearFocus();
                //searchView.setQuery("", false);
                //searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

}
