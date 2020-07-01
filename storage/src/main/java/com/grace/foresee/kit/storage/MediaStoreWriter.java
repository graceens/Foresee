package com.grace.foresee.kit.storage;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
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

public class MediaStoreWriter {
    /**
     * @see #write(Context, int, String, String, String, byte[])
     */
    public static void write(@NonNull Context context, @IntRange(from = 0, to = 10) int mediaType,
                             @NonNull String mimeType, @NonNull String fileName, @NonNull String content) {
        write(context, mediaType, mimeType, fileName, content.getBytes());
    }

    /**
     * @see #write(Context, int, String, String, String, byte[])
     */
    public static void write(@NonNull Context context, @IntRange(from = 0, to = 10) int mediaType,
                             @NonNull String mimeType, @NonNull String fileName, @NonNull byte[] content) {
        write(context, mediaType, mimeType, null, fileName, content);
    }

    /**
     * @see #write(Context, int, String, String, String, byte[])
     */
    public static void write(@NonNull Context context, @IntRange(from = 0, to = 10) int mediaType,
                             @NonNull String mimeType, @Nullable String dirName,
                             @NonNull String fileName, @NonNull String content) {
        write(context, mediaType, mimeType, dirName, fileName, content.getBytes());
    }

    /**
     * 将数据写入媒体库
     *
     * @param context   当前上下文对象
     * @param mediaType 媒体类型：
     *                  {@link MediaType#MUSIC}, {@link MediaType#PODCASTS},
     *                  {@link MediaType#ALARMS}, {@link MediaType#RINGTONES},
     *                  {@link MediaType#NOTIFICATIONS}, {@link MediaType#PICTURES},
     *                  {@link MediaType#MOVIES}, {@link MediaType#DOWNLOADS},
     *                  {@link MediaType#DCIM}, {@link MediaType#DOCUMENTS},
     *                  {@link MediaType#AUDIOBOOKS}
     * @param mimeType  文件mime类型
     * @param dirName   目录名称
     * @param fileName  文件名称
     * @param content   要写入的内容
     */
    @SuppressWarnings("deprecation")
    public static void write(@NonNull Context context, @IntRange(from = 0, to = 10) int mediaType,
                             @NonNull String mimeType, @Nullable String dirName,
                             @NonNull String fileName, @NonNull byte[] content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = context.getContentResolver();
            String[] projection = new String[]{MediaStore.MediaColumns._ID};
            String selection = MediaStore.MediaColumns.TITLE + "=?";
            String[] args = new String[]{fileName};

            Uri externalContentUri;
            String mediaDir;

            // 获取媒体库uri和媒体库目录
            switch (mediaType) {
                case MediaType.MUSIC:
                    externalContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    mediaDir = Environment.DIRECTORY_MUSIC;
                    break;
                case MediaType.PODCASTS:
                    externalContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    mediaDir = Environment.DIRECTORY_PODCASTS;
                    break;
                case MediaType.ALARMS:
                    externalContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    mediaDir = Environment.DIRECTORY_ALARMS;
                    break;
                case MediaType.RINGTONES:
                    externalContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    mediaDir = Environment.DIRECTORY_RINGTONES;
                    break;
                case MediaType.NOTIFICATIONS:
                    externalContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    mediaDir = Environment.DIRECTORY_NOTIFICATIONS;
                    break;
                case MediaType.PICTURES:
                    externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    mediaDir = Environment.DIRECTORY_PICTURES;
                    break;
                case MediaType.MOVIES:
                    externalContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    mediaDir = Environment.DIRECTORY_MOVIES;
                    break;
                case MediaType.DOWNLOADS:
                    externalContentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
                    mediaDir = Environment.DIRECTORY_DOWNLOADS;
                    break;
                case MediaType.DCIM:
                    externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    mediaDir = Environment.DIRECTORY_DCIM;
                    break;
                case MediaType.DOCUMENTS:
                    externalContentUri = MediaStore.Files.getContentUri("external");
                    mediaDir = Environment.DIRECTORY_DOCUMENTS;
                    break;
                case MediaType.AUDIOBOOKS:
                    externalContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    mediaDir = Environment.DIRECTORY_AUDIOBOOKS;
                    break;
                default:
                    throw new IllegalArgumentException("The parameter [mediaType] is typed incorrectly: " + mediaType);
            }

            Uri uri;
            // 查找文件
            Cursor cursor = resolver.query(externalContentUri, projection, selection, args, null);
            if (cursor != null && cursor.moveToFirst()) {
                // 获取文件uri
                uri = ContentUris.withAppendedId(externalContentUri, cursor.getLong(0));
                cursor.close();
            } else {
                // 文件没有找到，创建新文件，并返回文件uri
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.TITLE, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Path.join(mediaDir, dirName));

                uri = resolver.insert(externalContentUri, values);
            }

            if (uri != null) {
                try {
                    // 通过uri打开输出流写入数据
                    StorageWriter.write(resolver.openOutputStream(uri), content);
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
                // 写入数据
                String path = Path.join(mediaDir.getAbsolutePath(), dirName, fileName);
                StorageWriter.write(path, content);
            }
        }
    }
}
