package com.axel_stein.tasktracker.utils;

import java.util.List;

public class ListUtil {

    private ListUtil() {}

    public static boolean notEmpty(List list) {
        return list != null && list.size() > 0;
    }

    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    public static <T> void print(List<T> list) {
        StringBuilder builder = new StringBuilder();
        for (T item : list) {
            builder.append(item).append("; ");
        }
        LogUtil.debug(builder.toString());
    }

}
