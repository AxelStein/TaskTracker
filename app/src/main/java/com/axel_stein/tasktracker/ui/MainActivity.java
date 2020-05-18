package com.axel_stein.tasktracker.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.axel_stein.tasktracker.App;
import com.axel_stein.tasktracker.R;
import com.axel_stein.tasktracker.api.events.Events;
import com.axel_stein.tasktracker.api.model.TaskList;
import com.axel_stein.tasktracker.api.repository.MainMenuRepository;
import com.axel_stein.tasktracker.api.repository.TaskListRepository;
import com.axel_stein.tasktracker.api.repository.TaskRepository;
import com.axel_stein.tasktracker.ui.task_list.TasksFragment;
import com.axel_stein.tasktracker.ui.task_list.view_model.SearchViewModel;
import com.axel_stein.tasktracker.utils.FragmentDestinationBuilder;
import com.axel_stein.tasktracker.utils.MenuUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static androidx.core.view.GravityCompat.START;
import static androidx.navigation.ui.NavigationUI.onNavDestinationSelected;
import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;
import static com.axel_stein.tasktracker.ui.task_list.TasksFragment.BUNDLE_LIST_ID;
import static com.axel_stein.tasktracker.ui.task_list.TasksFragment.BUNDLE_VIEW_MODEL;
import static com.axel_stein.tasktracker.ui.task_list.TasksFragment.VIEW_MODEL_LIST;

public class MainActivity extends AppCompatActivity {
    private static final String EXTRA_CHECKED_MENU_ITEM = "EXTRA_CHECKED_MENU_ITEM";

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private NavController mNavController;
    private int mCheckedMenuItem = R.id.fragment_inbox;
    private CompositeDisposable mDisposable;

    @Inject
    TaskRepository mRepository;

    @Inject
    TaskListRepository mListRepository;

    @Inject
    IntentActionFactory mIntentActionFactory;

    @Inject
    MainMenuRepository mMenuRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        Events.subscribe(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> mIntentActionFactory.addTask());

        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_add_list) {
                mIntentActionFactory.addList();
                return true;
            }
            item.setChecked(true);
            mCheckedMenuItem = item.getItemId();
            mDrawerLayout.closeDrawer(START);
            return onNavDestinationSelected(item, mNavController);
        });

        if (savedInstanceState != null) {
            mCheckedMenuItem = savedInstanceState.getInt(EXTRA_CHECKED_MENU_ITEM);
        }

        mDisposable = new CompositeDisposable();
        mDisposable.add(mListRepository.query().subscribe(lists -> {
            mMenuRepository.inflateMenu(mNavigationView.getMenu(), mCheckedMenuItem);

            NavGraph graph = mNavController.getNavInflater().inflate(R.navigation.nav_graph);

            Set<Integer> topLevelIds = new LinkedHashSet<>();
            topLevelIds.add(R.id.fragment_all);
            topLevelIds.add(R.id.fragment_today);
            topLevelIds.add(R.id.fragment_week);
            topLevelIds.add(R.id.fragment_inbox);
            topLevelIds.add(R.id.fragment_completed);
            topLevelIds.add(R.id.fragment_trash);

            for (TaskList list : lists) {
                FragmentDestinationBuilder.from(mNavController)
                        .setId(Objects.hash(list.getId()))
                        .setClass(TasksFragment.class)
                        .setLabel(list.getName())
                        .addArgument(BUNDLE_VIEW_MODEL, VIEW_MODEL_LIST)
                        .addArgument(BUNDLE_LIST_ID, list.getId())
                        .add(graph);
                topLevelIds.add(Objects.hash(list.getId()));
            }

            mNavController.setGraph(graph);
            mAppBarConfiguration = new AppBarConfiguration.Builder(topLevelIds)
                    .setDrawerLayout(mDrawerLayout)
                    .build();
            setupActionBarWithNavController(MainActivity.this, mNavController, mAppBarConfiguration);

            mNavigationView.setCheckedItem(mCheckedMenuItem);
        }, Throwable::printStackTrace));
    }

    @Subscribe
    public void invalidateMenu(Events.InvalidateMenu e) {

    }

    @Override
    protected void onDestroy() {
        Events.unsubscribe(this);
        mDisposable.clear();
        super.onDestroy();
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_CHECKED_MENU_ITEM, mCheckedMenuItem);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return onNavDestinationSelected(item, mNavController) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

}
