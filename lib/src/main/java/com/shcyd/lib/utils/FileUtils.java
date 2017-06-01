package com.shcyd.lib.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by tiangongyipin on 16/2/25.
 */
public class FileUtils {
    public static File getAppDir(Context context) {
        String dirPath = "";
        //SD卡是否存在
        boolean isSdCardExists = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        // Environment.getExternalStorageDirectory()相当于File file=new File("/sdcard")
        boolean isRootDirExists = Environment.getExternalStorageDirectory().exists();
        if (isSdCardExists && isRootDirExists) {
            dirPath = String.format("%s/%s/", Environment.getExternalStorageDirectory().getAbsolutePath(), context.getPackageName());
        } else {
            dirPath = String.format("%s/%s/", context.getApplicationContext().getFilesDir().getAbsolutePath(), context.getPackageName());
        }

        File appDir = new File(dirPath);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        return appDir;
    }

    public static File getAppPhotoDir(Context context) {
        File appDir = getAppDir(context);
        File photoDir = new File(appDir.getAbsolutePath(), "photo");
        if (!photoDir.exists()) {
            photoDir.mkdir();
        }
        return photoDir;
    }
    public static File getTempDir(Context context) {
        File appDir = getAppDir(context);
        File photoDir = new File(appDir.getAbsolutePath(), "tmp");
        if (!photoDir.exists()) {
            photoDir.mkdir();
        }
        return photoDir;
    }

    public static String createPhotoPath(Context context) {
        File photoDir = getAppPhotoDir(context);
        return photoDir.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg";
    }
    public static String createTmpImgPath(Context context) {
        File photoDir = getTempDir(context);
        return photoDir.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg";
    }

    public static String createAppPath(Context context) {
        File photoDir = getAppDir(context);
        return photoDir.getAbsolutePath() + "/" + "file.apk";
    }

}
