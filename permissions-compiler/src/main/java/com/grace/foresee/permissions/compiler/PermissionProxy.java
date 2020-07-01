package com.grace.foresee.permissions.compiler;

public interface PermissionProxy<T> {
    /**
     * 授权成功
     *
     * @param source      被代理对象
     * @param requestCode 请求码
     * @param permissions 授权成功的权限
     */
    void onGranted(T source, int requestCode, String[] permissions);

    /**
     * 用户拒绝授权
     *
     * @param source      被代理对象
     * @param requestCode 请求码
     * @param permissions 拒绝授权的权限
     */
    void onDenied(T source, int requestCode, String[] permissions);

    /**
     * 向用户解释申请权限的理由
     *
     * @param source      被代理对象
     * @param requestCode 请求码
     * @param permissions 需要解释的权限
     * @return 是否处理
     */
    boolean onRationale(T source, int requestCode, String[] permissions);

    /**
     * 用户永久拒绝授权
     *
     * @param source      被代理对象
     * @param requestCode 请求码
     * @param permissions 永久拒绝授权的权限
     */
    void onPermanentlyDenied(T source, int requestCode, String[] permissions);
}
