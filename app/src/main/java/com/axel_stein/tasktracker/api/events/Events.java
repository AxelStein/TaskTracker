package com.axel_stein.tasktracker.api.events;

import org.greenrobot.eventbus.EventBus;

public class Events {

    public static void subscribe(Object o) {
        if (getBus().isRegistered(o)) {
            getBus().unregister(o);
        }
        getBus().register(o);
    }

    public static void unsubscribe(Object o) {
        getBus().unregister(o);
    }

    public static void invalidateTasks() {
        getBus().post(new InvalidateTasks());
    }

    private static EventBus getBus() {
        return EventBus.getDefault();
    }

    public static class InvalidateTasks {}

}
