package com.axel_stein.tasktracker;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;

import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

public class AndroidTest {
    protected Context mContext;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void start() {
        mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        RxJavaPlugins.reset();
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());

        RxAndroidPlugins.reset();
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @AfterClass
    public static void after(){
        RxAndroidPlugins.reset();
        RxJavaPlugins.reset();
    }

}
