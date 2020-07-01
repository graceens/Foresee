package com.grace.foresee.permissions;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import java.util.Arrays;

public class DefaultRationalRequestor implements RationalRequestor {
    @Override
    public void request(Context context, String[] permissions, PermissionRequestor requestor) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.permission_rationale_title)
                .setMessage(context.getString(R.string.permission_rationale_message,
                        Arrays.toString(PermissionTranslator.translate(context, permissions))))
                .setPositiveButton(R.string.permission_rationale_ok, (dialog, which) -> requestor.request(permissions))
                .setNegativeButton(R.string.permission_rationale_cancel, null)
                .show()
                .setCanceledOnTouchOutside(false);
    }
}
