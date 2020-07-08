package com.grace.foresee.storage;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

public class Storage {
    public static String getAppCachePath(Context context, String filename) {
        return Path.join(getAppCachePath(context), filename);
    }

    /**
     * 获取app缓存路径
     */
    public static String getAppCachePath(Context context) {
        String path = null;
        try {
            //判断是否装载外置存储
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                    !Environment.isExternalStorageRemovable()) {
                //获取外置存储的app缓存路径
                File cacheDir = context.getExternalCacheDir();
                if (cacheDir != null) {
                    path = cacheDir.getAbsolutePath();
                }
            }
            //获取手机存储的app缓存路径
            if (TextUtils.isEmpty(path)) {
                path = context.getCacheDir().getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public static String getAppFilesPath(Context context, String filename) {
        return Path.join(getAppFilesPath(context), filename);
    }

    /**
     * 获取app数据路径（相对缓存路径，保存时间更久一点）
     */
    public static String getAppFilesPath(Context context) {
        String path = null;
        try {
            //判断是否装载外置存储
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                    !Environment.isExternalStorageRemovable()) {
                //获取外置存储的app数据路径
                File filesDir = context.getExternalFilesDir("");
                if (filesDir != null) {
                    path = filesDir.getAbsolutePath();
                }
            }
            //获取手机存储的app数据路径
            if (TextUtils.isEmpty(path)) {
                path = context.getFilesDir().getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }
}
