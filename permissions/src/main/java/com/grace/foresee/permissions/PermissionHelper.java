package com.grace.foresee.permissions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.grace.foresee.kit.utils.AppUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionHelper {

    /**
     * 检查一些权限是否已授权
     *
     * @param context     当前上下文对象
     * @param permissions 需要检查的权限数组
     * @return 是否已授权
     */
    @SuppressLint("WrongConstant")
    public static boolean checkPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (AppUtil.getTargetSdkVersion(context) >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context, permission) ==
                            PackageManager.PERMISSION_DENIED) {
                        return false;
                    }
                } else {
                    if (PermissionChecker.checkSelfPermission(context, permission) ==
                            PackageManager.PERMISSION_DENIED) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 获取已授权的权限
     *
     * @param context     当前上下文对象
     * @param permissions 需要检查的权限
     * @return 已授权的权限
     */
    public static String[] getGrantedPermissions(Context context, String[] permissions) {
        List<String> grantedList = new ArrayList<>();
        for (String permission : permissions) {
            if (checkPermissions(context, new String[]{permission})) {
                grantedList.add(permission);
            }
        }

        return grantedList.toArray(new String[0]);
    }

    /**
     * 获取未授权或被拒绝的权限
     *
     * @param context     当前上下文对象
     * @param permissions 需要检查的权限
     * @return 未授权或被拒绝的权限
     */
    public static String[] getDeniedPermissions(Context context, String[] permissions) {
        String[] grantedPermissions = getGrantedPermissions(context, permissions);
        List<String> permissionList = new ArrayList<>(Arrays.asList(permissions));
        permissionList.removeAll(Arrays.asList(grantedPermissions));

        return permissionList.toArray(new String[0]);
    }

    /**
     * 获取已授权的权限
     *
     * @param permissions  需要检查的权限
     * @param grantResults 授权结果
     * @return 已授权的权限
     */
    public static String[] getGrantedPermissions(String[] permissions, int[] grantResults) {
        List<String> grantedList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                grantedList.add(permissions[i]);
            }
        }
        return grantedList.toArray(new String[0]);
    }

    /**
     * 获取未授权或被拒绝的权限
     *
     * @param permissions  需要检查的权限
     * @param grantResults 授权结果
     * @return 未授权或被拒绝的权限
     */
    public static String[] getDeniedPermissions(String[] permissions, int[] grantResults) {
        String[] grantedPermissions = getGrantedPermissions(permissions, grantResults);
        List<String> permissionList = new ArrayList<>(Arrays.asList(permissions));
        permissionList.removeAll(Arrays.asList(grantedPermissions));

        return permissionList.toArray(new String[0]);
    }

    /**
     * 获取永久拒绝授权的权限
     *
     * @param deniedPermissions 已经被拒绝一次授权的权限
     * @return 永久拒绝授权的权限
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String[] getPermanentlyDeniedPermissions(Context context, String[] deniedPermissions) {
        List<String> permanentlyDeniedPermissions = new ArrayList<>();

        for (String deniedPermission : deniedPermissions) {
            if (!((Activity) context).shouldShowRequestPermissionRationale(deniedPermission)) {
                permanentlyDeniedPermissions.add(deniedPermission);
            }
        }

        return permanentlyDeniedPermissions.toArray(new String[0]);
    }

    /**
     * 获取需要向用户解释权限用途的权限
     *
     * @param context           当前上下文对象
     * @param deniedPermissions 已经被拒绝一次授权的权限
     * @return 需要向用户解释权限用途的权限
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String[] getRationalePermissions(Context context, String[] deniedPermissions) {
        String[] permanentlyDeniedPermissions = getPermanentlyDeniedPermissions(context, deniedPermissions);
        List<String> rationalePermissionList = new ArrayList<>(Arrays.asList(deniedPermissions));
        rationalePermissionList.removeAll(Arrays.asList(permanentlyDeniedPermissions));

        return rationalePermissionList.toArray(new String[0]);
    }
}
