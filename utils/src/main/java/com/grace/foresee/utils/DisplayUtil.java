package com.grace.foresee.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.Window;

import com.grace.foresee.reflect.Reflector;

public class DisplayUtil {
    /**
     * 获取屏幕密度
     */
    public static float getDisplayDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 获得屏幕按比例缩小的密度
     */
    public static float getDisplayScaledDensity(Context context) {
        return context.getResources().getDisplayMetrics().scaledDensity;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getDisplayWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getDisplayHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getStatusBarHeight(Activity activity) {
        int statusBarHeight = -1;
        try {
            final Resources res = activity.getResources();
            int resId = res.getIdentifier("status_bar_height", "dimen", "android");
            if (resId <= 0) {
                resId = Reflector.with("com.android.internal.R$dimen")
                        .field("status_bar_height")
                        .get();
            }
            if (resId > 0) {
                statusBarHeight = res.getDimensionPixelSize(resId);
            }
            if (statusBarHeight <= 0) {
                Rect rect = new Rect();
                Window window = activity.getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(rect);
                statusBarHeight = rect.top;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }
}
