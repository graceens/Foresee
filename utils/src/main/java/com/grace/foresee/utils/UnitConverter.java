package com.grace.foresee.utils;

import android.content.Context;

public class UnitConverter {
    /**
     * dp转px
     */
    public static int dp2px(Context context, float dp) {
        return (int) (dp * DisplayUtil.getDisplayDensity(context) + 0.5f);
    }

    /**
     * px转dp
     */
    public static int px2dp(Context context, float px) {
        return (int) (px / DisplayUtil.getDisplayDensity(context) + 0.5f);
    }

    /**
     * sp转px
     */
    public static int sp2px(Context context, float sp) {
        return (int) (sp * DisplayUtil.getDisplayScaledDensity(context) + 0.5f);
    }

    public static int px2sp(Context context, float px) {
        return (int) (px / DisplayUtil.getDisplayScaledDensity(context) + 0.5f);
    }
}
