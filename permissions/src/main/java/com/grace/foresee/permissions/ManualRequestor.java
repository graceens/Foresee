package com.grace.foresee.permissions;

import android.content.Context;

public interface ManualRequestor {
    void request(Context context, String[] permissions, int requestCode);
}
