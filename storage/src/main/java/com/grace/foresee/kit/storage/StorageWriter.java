package com.grace.foresee.kit.storage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class StorageWriter {
    public static void write(String path, String content) {
        write(path, content, false);
    }

    public static void write(String path, String content, boolean append) {
        write(path, content.getBytes(), append);
    }

    public static void write(OutputStream outputStream, String content) {
        write(outputStream, content.getBytes());
    }

    public static void write(String path, byte[] content) {
        write(path, content, false);
    }

    public static void write(String path, byte[] content, boolean append) {
        File file = new File(path);
        if (canWrite(file)) {
            try {
                write(new FileOutputStream(path, append), content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void write(OutputStream outputStream, byte[] content) {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(outputStream);
            bos.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean canWrite(File file) {
        boolean result = file.exists() && file.canWrite();

        if (!result && !file.exists()) {
            //通过创建目录或文件再删除的方式，检测是否可写
            try {
                if (file.isDirectory()) {
                    result = file.mkdirs() && file.delete();
                } else {
                    File parentDir = file.getParentFile();
                    if (!parentDir.exists()) {
                        result = parentDir.mkdirs() && file.createNewFile() && file.delete();
                    } else {
                        result = file.createNewFile() && file.delete();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
