package com.axel_stein.tasktracker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.AfterClass;
import org.junit.Before;

import java.util.concurrent.Callable;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

public class AndroidTest {
    protected Context mContext;

    @Before
    public void start() {
        mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        RxJavaPlugins.reset();
        RxJavaPlugins.setIoSchedulerHandler(new Function<Scheduler, Scheduler>() {
            @Override
            public Scheduler apply(@NonNull Scheduler scheduler) {
                return Schedulers.trampoline();
            }
        });

        RxAndroidPlugins.reset();
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
            @Override
            public Scheduler apply(@NonNull Callable<Scheduler> schedulerCallable) {
                return Schedulers.trampoline();
            }
        });
    }

    @AfterClass
    public static void after(){
        RxAndroidPlugins.reset();
        RxJavaPlugins.reset();
    }

}
