package com.grace.foresee.permissions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class PermissionTranslator {
    @SuppressLint("InlinedApi")
    private static Map<String, Integer> sPermissionNames = new HashMap<String, Integer>() {
        {
            put(Manifest.permission.READ_CALENDAR, R.string.permission_read_calendar);
            put(Manifest.permission.WRITE_CALENDAR, R.string.permission_write_calendar);
            put(Manifest.permission.CAMERA, R.string.permission_camera);
            put(Manifest.permission.READ_CONTACTS, R.string.permission_read_contacts);
            put(Manifest.permission.WRITE_CONTACTS, R.string.permission_write_contacts);
            put(Manifest.permission.GET_ACCOUNTS, R.string.permission_get_accounts);
            put(Manifest.permission.ACCESS_FINE_LOCATION, R.string.permission_access_fine_location);
            put(Manifest.permission.ACCESS_COARSE_LOCATION, R.string.permission_access_coarse_location);
            put(Manifest.permission.RECORD_AUDIO, R.string.permission_record_audio);
            put(Manifest.permission.CALL_PHONE, R.string.permission_call_phone);
            put(Manifest.permission.READ_PHONE_STATE, R.string.permission_read_phone_state);
            put(Manifest.permission.READ_CALL_LOG, R.string.permission_read_call_log);
            put(Manifest.permission.WRITE_CALL_LOG, R.string.permission_write_call_log);
            put(Manifest.permission.ADD_VOICEMAIL, R.string.permission_add_voice_mail);
            put(Manifest.permission.USE_SIP, R.string.permission_use_sip);
            put(Manifest.permission.BODY_SENSORS, R.string.permission_body_sensors);
            put(Manifest.permission.SEND_SMS, R.string.permission_send_sms);
            put(Manifest.permission.RECEIVE_SMS, R.string.permission_receive_sms);
            put(Manifest.permission.READ_SMS, R.string.permission_read_sms);
            put(Manifest.permission.RECEIVE_MMS, R.string.permission_receive_mms);
            put(Manifest.permission.RECEIVE_WAP_PUSH, R.string.permission_receive_wap_push);
            put(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.permission_write_external_storage);
            put(Manifest.permission.READ_EXTERNAL_STORAGE, R.string.permission_read_external_storage);
        }
    };

    /**
     * 将权限名称翻译成用户能看懂的文字
     * @param context 当前上下文对象
     * @param permission 需要翻译的权限
     * @return 翻译后的权限名称
     */
    public static String translate(Context context, String permission) {
        @SuppressWarnings("ConstantConditions")
        int id = sPermissionNames.get(permission);
        if (id > 0) {
            return context.getString(id);
        }
        return "";
    }

    public static String[] translate(Context context, String[] permissions) {
        String[] result = new String[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            result[i] = translate(context, permissions[i]);
        }

        return result;
    }
}
