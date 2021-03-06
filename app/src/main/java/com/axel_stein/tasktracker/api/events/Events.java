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

    public static void invalidateEditTask() {
        getBus().post(new InvalidateEditTask());
    }

    public static void invalidateMenu() {
        getBus().post(new InvalidateMenu());
    }

    private static EventBus getBus() {
        return EventBus.getDefault();
    }

    public static class InvalidateEditTask {}

    public static class InvalidateMenu {}

}
