package com.grace.foresee.kit.storage;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StorageReader {
    public static String read(String path) {
        byte[] bytes = readForBytes(path);
        if (bytes != null && bytes.length > 0) {
            return new String(bytes, StandardCharsets.UTF_8);
        }
        return null;
    }

    public static String read(InputStream inputStream) {
        byte[] bytes = readForBytes(inputStream);
        if (bytes != null && bytes.length > 0) {
            return new String(bytes, StandardCharsets.UTF_8);
        }
        return null;
    }

    public static byte[] readForBytes(String path) {
        File file = new File(path);
        if (file.canRead()) {
            try {
                return readForBytes(new FileInputStream(path));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static byte[] readForBytes(InputStream inputStream) {
        BufferedInputStream bis = null;
        @SuppressWarnings("SpellCheckingInspection")
        ByteArrayOutputStream baos = null;

        try {
            bis = new BufferedInputStream(inputStream);
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
