package com.grace.foresee.permissions;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import java.util.Arrays;

public class DefaultManualRequestor implements ManualRequestor {

    @Override
    public void request(Context context, String[] permissions, int requestCode) {
        String[] permissionNames = PermissionTranslator.translate(context, permissions);

        new AlertDialog.Builder(context)
                .setTitle(R.string.permission_manual_title)
                .setMessage(context.getString(R.string.permission_manual_message, Arrays.toString(permissionNames)))
                .setPositiveButton(R.string.permission_manual_ok, (dialog, which) ->
                        PermissionSettingStarter.start((Activity) context, requestCode))
                .setNegativeButton(R.string.permission_manual_cancel, null)
                .show()
                .setCanceledOnTouchOutside(false);
    }
}
