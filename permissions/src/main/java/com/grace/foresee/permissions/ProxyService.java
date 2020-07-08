package com.grace.foresee.permissions;

import com.grace.foresee.reflect.Reflector;
import com.grace.foresee.permissions.annotation.Constant;

public class ProxyService {
    private Object proxy;

    public ProxyService(Class<?> sourceClass) {
        this.proxy = Reflector.with(sourceClass.getName() + Constant.PERMISSION_PROXY_SUFFIX)
                .create()
                .get();
    }

    public int getRequestCode() {
        return Reflector.with(proxy)
                .field("REQUEST_CODE")
                .get();
    }

    public void onGranted(Object source, int requestCode, String[] permissions) {
        Reflector.with(proxy)
                .call("onGranted", source, requestCode, permissions);
    }

    public void onDenied(Object source, int requestCode, String[] permissions) {
        Reflector.with(proxy)
                .call("onDenied", source, requestCode, permissions);
    }

    public boolean onRationale(Object source, int requestCode, String[] permissions) {
        return Reflector.with(proxy)
                .call("onRationale", source, requestCode, permissions)
                .get();
    }

    public void onPermanentlyDenied(Object source, int requestCode, String[] permissions) {
        Reflector.with(proxy)
                .call("onPermanentlyDenied", source, requestCode, permissions);
    }
}
