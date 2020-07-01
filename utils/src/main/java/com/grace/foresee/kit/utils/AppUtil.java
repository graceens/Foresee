package com.grace.foresee.kit.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

public class AppUtil {
    public static ApplicationInfo getAppInfo(Context context) {
        return getAppInfo(context, context.getPackageName());
    }

    public static ApplicationInfo getAppInfo(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            return packageInfo.applicationInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PackageInfo getPackageInfo(Context context) {
        return getPackageInfo(context, context.getPackageName());
    }

    public static PackageInfo getPackageInfo(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }

        PackageManager pm = context.getPackageManager();
        try {
            return pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int getTargetSdkVersion(Context context) {
        ApplicationInfo applicationInfo = AppUtil.getAppInfo(context);
        if (applicationInfo != null) {
            return applicationInfo.targetSdkVersion;
        }
        return -1;
    }

    public static int getMinSdkVersion(Context context) {
        ApplicationInfo applicationInfo = AppUtil.getAppInfo(context);
        if (applicationInfo != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return applicationInfo.minSdkVersion;
            }
        }
        return -1;
    }

    public static long getVersionCode(Context context) {
        return getVersionCode(context, context.getPackageName());
    }

    public static long getVersionCode(Context context, String packageName) {
        PackageInfo packageInfo = getPackageInfo(context, packageName);
        if (packageInfo != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return packageInfo.getLongVersionCode();
            } else {
                return packageInfo.versionCode;
            }
        }
        return -1;
    }

    public static String getVersionName(Context context, String packageName) {
        PackageInfo packageInfo = getPackageInfo(context, packageName);
        if (packageInfo != null) {
            return packageInfo.versionName;
        }
        return "";
    }

    /**
     * 重启app
     * @param context 当前上下文对象
     */
    public static void restartApp(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("REBOOT", "reboot");

            context.startActivity(intent);
        }
    }
}
