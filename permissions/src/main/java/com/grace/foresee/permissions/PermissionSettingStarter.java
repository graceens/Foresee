package com.grace.foresee.permissions;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.grace.foresee.utils.AppUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class PermissionSettingStarter {
    /**
     * 启动手机权限设置界面
     * @param activity 当前activity
     * @param requestCode 请求码
     */
    public static void start(@NonNull Activity activity, int requestCode) {
        // 根据手机厂商做相应处理
        switch (Build.MANUFACTURER) {
            case "HUAWEI":
                startHuaweiPermissionSetting(activity, requestCode);
                break;
            case "vivo":
                startVivoPermissionSetting(activity, requestCode);
                break;
            case "OPPO":
                startOppoPermissionSetting(activity, requestCode);
                break;
            case "Coolpad":
                startCoolpadPermissionSetting(activity, requestCode);
                break;
            case "Meizu":
                startMeizuPermissionSetting(activity, requestCode);
                break;
            case "Xiaomi":
                startXiaomiPermissionSetting(activity, requestCode);
                break;
            case "samsung":
                startSamsungPermissionSetting(activity, requestCode);
                break;
            case "Sony":
                startSonyPermissionSetting(activity, requestCode);
                break;
            case "LG":
                startLgPermissionSetting(activity, requestCode);
                break;
            default:
                startSetting(activity, requestCode);
                break;
        }
    }

    /**
     * 启动华为权限设置界面
     * @param activity 当前activity
     * @param requestCode 请求码
     */
    private static void startHuaweiPermissionSetting(@NonNull Activity activity, int requestCode) {
        try {
            Intent intent = new Intent(activity.getPackageName());
            ComponentName componentName = new ComponentName("com.huawei.systemmanager",
                    "com.huawei.permissionmanager.ui.MainActivity");
            intent.setComponent(componentName);

            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            // 启动华为权限设置界面失败，直接启动应用设置界面
            startSetting(activity, requestCode);
        }
    }

    private static void startVivoPermissionSetting(@NonNull Activity activity, int requestCode) {
        startPermissionSettingWithPackageName(activity, requestCode,
                "com.bairenkeji.icaller");
    }

    private static void startOppoPermissionSetting(@NonNull Activity activity, int requestCode) {
        startPermissionSettingWithPackageName(activity, requestCode,
                "com.coloros.safecenter");
    }

    private static void startCoolpadPermissionSetting(@NonNull Activity activity, int requestCode) {
        startPermissionSettingWithPackageName(activity, requestCode,
                "com.yulong.android.security:remote");
    }

    private static void startMeizuPermissionSetting(@NonNull Activity activity, int requestCode) {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("sPackageName", activity.getPackageName());

            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            startSetting(activity, requestCode);
        }
    }

    private static void startXiaomiPermissionSetting(@NonNull Activity activity, int requestCode) {
        String version = getMiuiVersion();

        Intent intent = new Intent();
        intent.setAction("miui.intent.action.APP_PERM_EDITOR");
        if ("V6".equals(version) || "V7".equals(version)) {
            intent.setClassName("com.miui.securitycenter",
                    "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        } else if ("V8".equals(version) || "V9".equals(version) || "V10".equals(version)) {
            intent.setClassName("com.miui.securitycenter",
                    "com.miui.permcenter.permissions.PermissionsEditorActivity");
        } else {
            startSetting(activity, requestCode);
            return;
        }
        intent.putExtra("extra_pkgname", activity.getPackageName());

        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取小米手机miui版本
     * @return miui版本
     */
    private static String getMiuiVersion() {
        String propName = "ro.miui.ui.version.name";
        String version = null;
        BufferedReader reader = null;

        try {
            Process p = Runtime.getRuntime().exec("getprop" + propName);
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            version = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return version;
    }

    private static void startSamsungPermissionSetting(@NonNull Activity activity, int requestCode) {
        startSetting(activity, requestCode);
    }

    private static void startSonyPermissionSetting(@NonNull Activity activity, int requestCode) {
        try {
            Intent intent = new Intent(activity.getPackageName());
            ComponentName componentName = new ComponentName("com.sonymobile.cta",
                    "com.sonymobile.cta.SomcCTAMainActivity");
            intent.setComponent(componentName);

            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            startSetting(activity, requestCode);
        }
    }

    private static void startLgPermissionSetting(@NonNull Activity activity, int requestCode) {
        try {
            Intent intent = new Intent(activity.getPackageName());
            ComponentName componentName = new ComponentName("com.android.settings",
                    "com.android.settings.Settings$AccessLockSummaryActivity");
            intent.setComponent(componentName);

            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            startSetting(activity, requestCode);
        }
    }

    /**
     * 启动权限设置界面（使用包名查找该包内的启动Activity）
     * @param activity 当前activity
     * @param requestCode 请求码
     * @param packageName 包名
     */
    private static void startPermissionSettingWithPackageName(@NonNull Activity activity, int requestCode,
                                                              String packageName) {
        PackageInfo packageInfo = AppUtil.getPackageInfo(activity, packageName);
        if (packageInfo == null) {
            return;
        }

        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageName);

        // 查找包的所有Activity
        List<ResolveInfo> resolveInfos = activity.getPackageManager().queryIntentActivities(
                resolveIntent, 0);
        ResolveInfo resolveInfo = resolveInfos.iterator().next();
        if (resolveInfo != null) {
            String realPackageName = resolveInfo.activityInfo.packageName;
            String className = resolveInfo.activityInfo.name;

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName componentName = new ComponentName(realPackageName, className);
            intent.setComponent(componentName);
            try {
                activity.startActivityForResult(intent, requestCode);
            } catch (Exception e) {
                // 启动权限设置界面失败，直接启动手机设置界面
                startSetting(activity, requestCode);
            }
        }
    }

    /**
     * 启动手机设置界面
     * @param activity 当前activity
     * @param requestCode 请求码
     */
    private static void startSetting(@NonNull Activity activity, int requestCode) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);

        activity.startActivityForResult(intent, requestCode);
    }

}
