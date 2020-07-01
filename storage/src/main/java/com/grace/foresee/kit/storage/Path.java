package com.grace.foresee.kit.storage;

import android.text.TextUtils;

import java.io.File;

public class Path {
    /**
     * 拼接路径
     * @param args 路径片段
     * @return 完整的路径
     */
    public static String join(String... args) {
        StringBuilder path = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            String s = args[i];
            if (TextUtils.isEmpty(s)) {
                continue;
            }
            if (i > 0) {
                //去掉开始的分隔符
                if (s.startsWith(File.separator)) {
                    s = s.substring(1);
                }
            }
            //去掉结束的分隔符
            if (s.endsWith(File.separator)) {
                s = s.substring(0, s.length() - 1);
            }
            path.append(s);
            if (i != args.length - 1) {
                path.append(File.separator);
            }
        }
        return path.toString();
    }
}
