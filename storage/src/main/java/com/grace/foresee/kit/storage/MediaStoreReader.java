package com.grace.foresee.kit.storage;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

public class MediaStoreReader {

    /**
     * @see #readForBytes(Context, int, String, String)
     */
    public static String read(@NonNull Context context, @IntRange(from = 0, to = 10) int mediaType,
                              @NonNull String fileName) {
        return read(context, mediaType, null, fileName);
    }

    /**
     * @see #readForBytes(Context, int, String, String)
     */
    public static String read(@NonNull Context context, @IntRange(from = 0, to = 10) int mediaType,
                              @Nullable String dirName, @NonNull String fileName) {
        byte[] bytes = readForBytes(context, mediaType, dirName, fileName);
        if (bytes != null && bytes.length > 0) {
            return new String(bytes, StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * @see #readForBytes(Context, int, String, String)
     */
    public static byte[] readForBytes(@NonNull Context context, @IntRange(from = 0, to = 10) int mediaType,
                                      @NonNull String fileName) {
        return readForBytes(context, mediaType, null, fileName);
    }

    /**
     * 从媒体库读取数据
     *
     * @param context   当前上下文对象
     * @param mediaType 媒体类型：
     *                  {@link MediaType#MUSIC}, {@link MediaType#PODCASTS},
     *                  {@link MediaType#ALARMS}, {@link MediaType#RINGTONES},
     *                  {@link MediaType#NOTIFICATIONS}, {@link MediaType#PICTURES},
     *                  {@link MediaType#MOVIES}, {@link MediaType#DOWNLOADS},
     *                  {@link MediaType#DCIM}, {@link MediaType#DOCUMENTS},
     *                  {@link MediaType#AUDIOBOOKS}
     * @param dirName   目录名称
     * @param fileName  文件名称
     */
    @SuppressWarnings("deprecation")
    public static byte[] readForBytes(@NonNull Context context, @IntRange(from = 0, to = 10) int mediaType,
                                      @Nullable String dirName, @NonNull String fileName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = context.getContentResolver();
            String[] projection = new String[]{MediaStore.MediaColumns._ID};
            String selection = MediaStore.MediaColumns.TITLE + "=?";
            String[] args = new String[]{fileName};

            Uri externalContentUri;

            // 获取媒体库uri和媒体库目录
            switch (mediaType) {
                case MediaType.MUSIC:
                case MediaType.PODCASTS:
                case MediaType.ALARMS:
                case MediaType.RINGTONES:
                case MediaType.NOTIFICATIONS:
                case MediaType.AUDIOBOOKS:
                    externalContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    break;
                case MediaType.PICTURES:
                case MediaType.DCIM:
                    externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    break;
                case MediaType.MOVIES:
                    externalContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    break;
                case MediaType.DOWNLOADS:
                    externalContentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
                    break;
                case MediaType.DOCUMENTS:
                    externalContentUri = MediaStore.Files.getContentUri("external");
                    break;
                default:
                    throw new IllegalArgumentException("The parameter [mediaType] is typed incorrectly: " + mediaType);
            }

            Uri uri = null;
            // 查找文件
            Cursor cursor = resolver.query(externalContentUri, projection, selection, args, null);
            if (cursor != null && cursor.moveToFirst()) {
                // 获取文件uri
                uri = ContentUris.withAppendedId(externalContentUri, cursor.getLong(0));
                cursor.close();
            }

            if (uri != null) {
                try {
                    // 通过uri打开输入流读取数据
                    return StorageReader.readForBytes(resolver.openInputStream(uri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            File mediaDir;

            // 获取媒体库目录
            switch (mediaType) {
                case MediaType.MUSIC:
                    mediaDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                    break;
                case MediaType.PODCASTS:
                    mediaDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS);
                    break;
                case MediaType.ALARMS:
                    mediaDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);
                    break;
                case MediaType.RINGTONES:
                    mediaDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES);
                    break;
                case MediaType.NOTIFICATIONS:
                    mediaDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS);
                    break;
                case MediaType.PICTURES:
                    mediaDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    break;
                case MediaType.MOVIES:
                    mediaDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                    break;
                case MediaType.DOWNLOADS:
                    mediaDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    break;
                case MediaType.DCIM:
                    mediaDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    break;
                case MediaType.DOCUMENTS:
                    mediaDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                    break;
                case MediaType.AUDIOBOOKS:
                    mediaDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_AUDIOBOOKS);
                    break;
                default:
                    throw new IllegalArgumentException("The parameter [mediaType] is typed incorrectly: " + mediaType);
            }

            if (mediaDir != null) {
                String path = Path.join(mediaDir.getAbsolutePath(), dirName, fileName);
                return StorageReader.readForBytes(path);
            }
        }

        return null;
    }
}
