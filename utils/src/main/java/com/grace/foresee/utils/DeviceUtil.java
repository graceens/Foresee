package com.grace.foresee.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.grace.foresee.storage.MediaStoreReader;
import com.grace.foresee.storage.MediaStoreWriter;
import com.grace.foresee.storage.MediaType;

import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class DeviceUtil {
    /**
     * 获得设备管理器
     *
     * @param context 当前上下文对象
     * @return 设备管理器
     */
    public static TelephonyManager getTelephonyManager(Context context) {
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * 获取设备唯一标识
     *
     * @param context 当前上下文对象
     * @return 设备唯一标识
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getImei(Context context) {
        String dirName = "foresee";
        String fileName = "#device_imei";

        SharedPreferences preferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        // 从缓存中读取设备唯一标识
        String imei = preferences.getString("imei", null);
        if (TextUtils.isEmpty(imei)) {
            imei = MediaStoreReader.read(context, MediaType.PICTURES, dirName, fileName);
        }

        if (TextUtils.isEmpty(imei)) {
            try {
                TelephonyManager telephonyManager = getTelephonyManager(context);
                imei = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? telephonyManager.getImei() :
                        telephonyManager.getDeviceId();
            } catch (Exception ignore) {

            }

            if (TextUtils.isEmpty(imei)) {
                // 无法获取设备唯一标识，随机生成一个UUID作为设备唯一标识
                imei = UUID.randomUUID().toString();
            }

            // 写入缓存
            // 将文件伪装成图片（文件存放在除Images、Video、Audio以外的数据表中时，应用重装后将不可访问）
            //noinspection ConstantConditions
            MediaStoreWriter.write(context, MediaType.PICTURES,
                    "image/*", dirName, fileName, imei);

            preferences.edit().putString("imei", imei).apply();
        }

        return imei;
    }

    /**
     * 获取当前设备的手机号码
     *
     * @param context 当前上下文对象
     * @return 当前设备的手机号码
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getPhoneNumber(Context context) {
        TelephonyManager telephonyManager = getTelephonyManager(context);
        String phoneNumber = null;

        try {
            phoneNumber = telephonyManager.getLine1Number();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumber = "+0000";
        }

        return phoneNumber;
    }

    /**
     * 获取系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getPhoneModel() {
        try {
            return URLEncoder.encode(Build.MODEL, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static WifiManager getWifiManager(Context context) {
        return (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 检查网络是否连接
     *
     * @param context 当前上下文对象
     * @return 网络是否连接
     */
    @SuppressLint("MissingPermission")
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = getConnectivityManager(context);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        } else {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
    }

    /**
     * 检查网络是否是数据流量
     *
     * @param context 当前上下文对象
     * @return 网络是否是数据流量
     */
    @SuppressLint("MissingPermission")
    public static boolean isMobile(Context context) {
        ConnectivityManager connectivityManager = getConnectivityManager(context);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
        }
    }

    /**
     * 检查网络是否是Wifi
     *
     * @param context 当前上下文对象
     * @return 网络是否是Wifi
     */
    @SuppressLint("MissingPermission")
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = getConnectivityManager(context);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        } else {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
        }
    }

    /**
     * 获取Wifi的Mac地址
     *
     * @param context 当前上下文对象
     * @return Wifi的Mac地址
     */
    @SuppressLint("HardwareIds")
    public static String getMac(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                WifiManager wifiManager = getWifiManager(context);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    return wifiInfo.getMacAddress();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface networkInterface : interfaces) {
                    if (networkInterface != null && !TextUtils.isEmpty(networkInterface.getName())) {
                        if ("wlan0".equalsIgnoreCase(networkInterface.getName())) {
                            byte[] macBytes = networkInterface.getHardwareAddress();
                            if (macBytes != null && macBytes.length > 0) {
                                StringBuilder builder = new StringBuilder();
                                for (byte b : macBytes) {
                                    builder.append(String.format("%02x:", b));
                                }
                                if (builder.length() > 0) {
                                    builder.deleteCharAt(builder.length() - 1);
                                }
                                return builder.toString();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "02:00:00:00:00:00";
    }
}
