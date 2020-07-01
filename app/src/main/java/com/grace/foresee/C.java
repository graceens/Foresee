package com.grace.foresee;

import com.grace.foresee.permissions.annotation.Denied;
import com.grace.foresee.permissions.annotation.Granted;

public class C {
    private void onPermissionsRational(String[] permissions) {

    }

    @Granted
    private void onPermissionsGranted(String[] permissions) {

    }

    @Denied
    private void onPermissionsDenied(String[] permissions) {

    }
}
