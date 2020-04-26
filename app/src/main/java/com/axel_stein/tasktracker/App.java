package com.axel_stein.tasktracker;

import android.app.Application;

import com.axel_stein.tasktracker.api.dagger.AppComponent;
import com.axel_stein.tasktracker.api.dagger.AppModule;
import com.axel_stein.tasktracker.api.dagger.DaggerAppComponent;

public class App extends Application {
    private static AppComponent sAppComponent;

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        sAppComponent.inject(this);
    }
}
