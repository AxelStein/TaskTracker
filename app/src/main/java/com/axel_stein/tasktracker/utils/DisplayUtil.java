package com.axel_stein.tasktracker.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayUtil {

    private DisplayUtil() {}

    public static int getStatusBarHeight(Context context) {
        if (context == null) return 0;

        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int dpToPx(Context context, int dp) {
        if (context == null) {
            return 0;
        } else if (dp < 0) {
            return 0;
        }
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxToDp(Context context, int px) {
        if (context == null) {
            return 0;
        } else if (px < 0) {
            return 0;
        }
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}
