package com.grace.foresee.permissions;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.grace.foresee.utils.AppUtil;

public class PermissionRequestor {
    private Object source;
    private Context context;
    private ProxyService service;
    private RationalRequestor rationalRequestor;
    private ManualRequestor manualRequestor;
    private String[] requestPermissions;

    public static PermissionRequestor with(Activity activity) {
        return new PermissionRequestor(activity);
    }

    public static PermissionRequestor with(Fragment fragment) {
        return new PermissionRequestor(fragment);
    }

    public PermissionRequestor(Object source) {
        this.source = source;
        if (source instanceof Activity) {
            this.context = (Context) source;
        } else if (source instanceof Fragment) {
            this.context = ((Fragment) source).getContext();
        }
        this.service = new ProxyService(source.getClass());
        this.rationalRequestor = new DefaultRationalRequestor();
        this.manualRequestor = new DefaultManualRequestor();
    }

    public PermissionRequestor setRationalRequestor(RationalRequestor rationalRequestor) {
        this.rationalRequestor = rationalRequestor;
        return this;
    }

    public PermissionRequestor setManualRequestor(ManualRequestor manualRequestor) {
        this.manualRequestor = manualRequestor;
        return this;
    }

    public PermissionRequestor request(String permission) {
        return request(new String[]{permission});
    }

    public PermissionRequestor request(String[] permissions) {
        this.requestPermissions = permissions;
        doRequest();
        return this;
    }

    public void reRequest() {
        doRequest();
    }

    private void doRequest() {
        if (requestPermissions == null || requestPermissions.length < 1) {
            return;
        }

        // 检查权限是否已授权
        if (!PermissionHelper.checkPermissions(context, requestPermissions)) {
            // 检查目标sdk版本是否大于等于23（android 6.0）
            if (AppUtil.getTargetSdkVersion(context) >= Build.VERSION_CODES.M) {
                //真正的调用系统api请求权限
                realRequest();
            } else {
                // 手动开启权限
                service.onPermanentlyDenied(source, service.getRequestCode(), requestPermissions);
            }
        } else {
            // 所有权限都已授权，不需要重新授权
            service.onGranted(source, service.getRequestCode(), requestPermissions);
        }
    }


    /**
     * 真正的请求权限
     */
    private void realRequest() {
        // 获取未授权或被拒绝的权限
        String[] deniedPermissions = PermissionHelper.getDeniedPermissions(context, requestPermissions);
        if (deniedPermissions.length > 0) {
            ActivityCompat.requestPermissions((Activity) context, deniedPermissions,
                    service.getRequestCode());
        }
    }

    public PermissionRequestor rationalRequest(String permission) {
        return rationalRequest(new String[]{permission});
    }

    /**
     * 合理的授权，向用户解释权限的用途
     * @param permissions 需要授权的权限
     * @return PermissionRequestor
     */
    public PermissionRequestor rationalRequest(String[] permissions) {
        rationalRequestor.request(context, permissions, this);
        return this;
    }

    public void manualRequest(String permission) {
        manualRequest(new String[]{permission});
    }

    /**
     * 手动开启权限
     *
     * @param permissions 需要授权的权限
     */
    public void manualRequest(String[] permissions) {
        manualRequestor.request(context, permissions, service.getRequestCode());
    }

    /**
     * 处理授权结果，在onRequestPermissionsResult调用
     *
     * @param requestCode  请求码
     * @param permissions  请求授权的权限
     * @param grantResults 授权结果
     */
    public void handleResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == service.getRequestCode()) {
            String[] grantedPermissions = PermissionHelper.getGrantedPermissions(context, requestPermissions);
            // 通知授权成功的权限
            if (grantedPermissions.length > 0) {
                service.onGranted(source, requestCode, grantedPermissions);
            }

            String[] deniedPermissions = PermissionHelper.getDeniedPermissions(permissions,
                    grantResults);
            // 通知拒绝授权的权限
            if (deniedPermissions.length > 0) {
                service.onDenied(source, requestCode, deniedPermissions);

                // 检查永久拒绝授权的权限
                checkPermanentlyDeniedPermissions(deniedPermissions, requestCode);
            }
        }
    }

    /**
     * 处理授权结果，在onActivityResult调用
     *
     * @param requestCode 自定义请求码
     */
    public void handleResult(int requestCode) {
        if (requestCode == service.getRequestCode()) {
            String[] grantedPermissions = PermissionHelper.getGrantedPermissions(context, requestPermissions);
            if (grantedPermissions.length > 0) {
                service.onGranted(source, requestCode, grantedPermissions);
            }

            String[] deniedPermissions = PermissionHelper.getDeniedPermissions(context, requestPermissions);
            if (deniedPermissions.length > 0) {
                service.onDenied(source, requestCode, deniedPermissions);

                checkPermanentlyDeniedPermissions(deniedPermissions, requestCode);
            }
        }
    }

    /**
     * 检查永久拒绝授权的权限
     *
     * @param deniedPermissions 已经被拒绝一次授权的权限
     * @param requestCode       请求码
     */
    private void checkPermanentlyDeniedPermissions(String[] deniedPermissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 如果同时处理两个操作，界面可能重叠
            // 所以先判断是否处理解释权限，如果处理了则直接返回，不进行下一个操作
            String[] rationalePermissions = PermissionHelper.getRationalePermissions(context, deniedPermissions);
            if (rationalePermissions.length > 0 && service.onRationale(source, requestCode, rationalePermissions)) {
                return;
            }

            String[] permanentlyDeniedPermissions = PermissionHelper.getPermanentlyDeniedPermissions(
                    context, deniedPermissions);
            if (permanentlyDeniedPermissions.length > 0) {
                service.onPermanentlyDenied(source, requestCode, permanentlyDeniedPermissions);
            }
        }
    }
}
