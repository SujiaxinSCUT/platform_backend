package com.trace.platform.utils;

import java.io.File;

public class FileUtil {

    public static boolean existsOrCreateDir(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return file.mkdirs();
        } else if (file.isDirectory()){
            return true;
        }
        return false;
    }
}
