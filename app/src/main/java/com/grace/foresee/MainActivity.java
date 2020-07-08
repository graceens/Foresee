package com.grace.foresee;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;

import com.grace.foresee.storage.Storage;
import com.grace.foresee.storage.StorageWriter;
import com.grace.foresee.logger.AndroidLogAdapter;
import com.grace.foresee.logger.LogStorageStrategy;
import com.grace.foresee.logger.LogStrategy;
import com.grace.foresee.logger.Logger;
import com.grace.foresee.logger.PrettyFormatStrategy;
import com.grace.foresee.permissions.PermissionRequestor;
import com.grace.foresee.permissions.annotation.Denied;
import com.grace.foresee.permissions.annotation.Granted;
import com.grace.foresee.permissions.annotation.PermanentlyDenied;
import com.grace.foresee.permissions.annotation.Rationale;
import com.grace.foresee.permissions.annotation.RequestCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

@RequestCode(1111)
public class MainActivity extends AppCompatActivity {

    private PermissionRequestor mPermissionRequestor;

    private static final int CREATE_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Logger.v("test");
        Logger.i("test: %s", 111);
        Logger.d("test");
        Logger.e("test");
        Logger.w("test");

        //storage日志适配器
        String logPath = Storage.getAppCachePath(this, "log.txt");
        LogStrategy logStrategy = new PrettyFormatStrategy.Builder()
                .logStrategy(new LogStorageStrategy(logPath))
                .build();
        Logger.addAdapter(new AndroidLogAdapter(logStrategy));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("a", 1);
            jsonObject.put("b", 2);
            jsonObject.put("c", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.json(jsonObject.toString());

        String[] requestPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermission = new String[]{
                    "android.permission.READ_SMS"
            };
        } else {
            requestPermission = new String[]{"android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.READ_SMS",
                    "android.permission.READ_PHONE_STATE"};
        }

        mPermissionRequestor = PermissionRequestor.with(this).request(requestPermission);
    }

    @Granted
    private void granted(String[] grantedPermissions) {
        Logger.i(Arrays.toString(grantedPermissions));
    }

    @Denied
    private void denied(String[] deniedPermissions) {
        Logger.i(Arrays.toString(deniedPermissions));
    }

    @Rationale
    private void rationale(String[] permissions) {
        mPermissionRequestor.rationalRequest(permissions);
    }

    @PermanentlyDenied
    private void permanentlyDenied(String[] permissions) {
        Logger.i(Arrays.toString(permissions));
        mPermissionRequestor.manualRequest(permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        mPermissionRequestor.handleResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Logger.d("requestCode: %s, resultCode: %s", requestCode, resultCode);

        mPermissionRequestor.handleResult(requestCode);
    }
}