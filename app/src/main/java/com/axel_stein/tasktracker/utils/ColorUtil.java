package com.axel_stein.tasktracker.utils;

import android.content.Context;
import android.util.TypedValue;

import androidx.annotation.ArrayRes;
import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

public class ColorUtil {

    private ColorUtil() {}

    public static int[] getColorArray(@Nullable Context context, @ArrayRes int arrayRes) {
        if (context != null) {
            return context.getResources().getIntArray(arrayRes);
            /*
            int[] colors = new int[res.length];
            for (int i = 0; i < res.length; i++) {
                colors[i] = ContextCompat.getColor(context, res[i]);
            }
            return colors;
            */
        }
        return null;
    }

    @ColorInt
    public static int getColorAttr(@Nullable Context context, @AttrRes int colorAttr) {
        int color = 0;
        if (context != null) {
            try {
                TypedValue value = new TypedValue();
                context.getTheme().resolveAttribute(colorAttr, value, true);

                color = value.data;
            } catch (Exception ex) {
                ex.printStackTrace();
                color = 0;
            }
        }
        return color;
    }

}
