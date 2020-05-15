package com.axel_stein.tasktracker.utils;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MenuItemBuilder {
    public static MenuItemBuilder from(int id) {
        return new MenuItemBuilder().setId(id);
    }

    private int id;
    private int group;
    private int order;
    private CharSequence title;
    private int titleRes;
    private int icon;
    private boolean checkable;
    private boolean checked;
    private Intent intent;
    private TextView counterView;
    private int counter;

    public MenuItemBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public MenuItemBuilder setGroup(int group) {
        this.group = group;
        return this;
    }

    public MenuItemBuilder setOrder(int order) {
        this.order = order;
        return this;
    }

    public MenuItemBuilder setTitle(CharSequence title) {
        this.title = title;
        return this;
    }

    public MenuItemBuilder setTitleRes(int titleRes) {
        this.titleRes = titleRes;
        return this;
    }

    public MenuItemBuilder setIcon(int icon) {
        this.icon = icon;
        return this;
    }

    public MenuItemBuilder setCheckable(boolean checkable) {
        this.checkable = checkable;
        return this;
    }

    public MenuItemBuilder setChecked(boolean checked) {
        this.checked = checked;
        return this;
    }

    public MenuItemBuilder setIntent(Intent intent) {
        this.intent = intent;
        return this;
    }

    public MenuItemBuilder setCounterView(Context context, int layoutRes) {
        this.counterView = (TextView) View.inflate(context, layoutRes, null);
        return this;
    }

    public MenuItemBuilder setCounter(int counter) {
        this.counter = counter;
        return this;
    }

    public void add(Menu menu) {
        MenuItem item;
        if (titleRes != 0) {
            item = menu.add(group, id, order, titleRes);
        } else {
            item = menu.add(group, id, order, title);
        }
        item.setCheckable(checkable);
        if (checkable) {
            item.setChecked(checked);
        }
        if (intent != null) {
            item.setIntent(intent);
        }
        if (icon != 0) {
            item.setIcon(icon);
        }
        if (counterView != null) {
            counterView.setText(counter != 0 ? String.valueOf(counter) : null);
            item.setActionView(counterView);
        }
    }
}
