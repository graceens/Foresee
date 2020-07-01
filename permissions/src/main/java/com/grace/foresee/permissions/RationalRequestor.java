package com.grace.foresee.permissions;

import android.content.Context;

public interface RationalRequestor {
    void request(Context context, String[] permissions, PermissionRequestor requestor);
}
