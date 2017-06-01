package com.zzia.graduationproject.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import com.shcyd.lib.helper.MediaItem;
import com.shcyd.lib.utils.StringUtils;
import com.zzia.graduationproject.base.App;
import com.zzia.graduationproject.base.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import okhttp3.ResponseBody;

/**
 * Created by timor.fan on 2016/9/10.
 * *项目名：CZF
 * 类描述：
 */
public class CzfFileUtils {

    public static void splitFile(String sourceFilePath, int partFileLength) throws Exception {
        File sourceFile = null;
        File targetFile = null;
        InputStream ips = null;
        OutputStream ops = null;
        OutputStream configOps = null;//该文件流用于存储文件分割后的相关信息，包括分割后的每个子文件的编号和路径,以及未分割前文件名
        Properties partInfo = null;//properties用于存储文件分割的信息
        byte[] buffer = null;
        int partNumber = 1;
        sourceFile = new File(sourceFilePath);//待分割文件
        ips = new FileInputStream(sourceFile);//找到读取源文件并获取输入流
        File congFile = getTempDir(App.getInstance());
        if (!congFile.exists()) {
            congFile.mkdirs();
        }
        congFile = new File(congFile, "config.properties");
        configOps = new FileOutputStream(congFile);
        buffer = new byte[partFileLength];//开辟缓存空间
        int tempLength = 0;
        partInfo = new Properties();//key:1开始自动编号 value:文件路径

        while ((tempLength = ips.read(buffer, 0, partFileLength)) != -1) {
            String targetFilePath = getTempDir(App.getInstance()).getAbsolutePath() + File.separator + "part_" + (partNumber) + sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1);//分割后的文件路径+文件名
            Log.e("targetFilePath", targetFilePath);
            partInfo.setProperty((partNumber++) + "", targetFilePath);//将相关信息存储进properties
            targetFile = new File(targetFilePath);
            ops = new FileOutputStream(targetFile);//分割后文件
            ops.write(buffer, 0, tempLength);//将信息写入碎片文件

            ops.close();//关闭碎片文件
        }

        partInfo.setProperty("name", sourceFile.getName());//存储源文件名
        partInfo.store(configOps, "ConfigFile");//将properties存储进实体文件中
        ips.close();//关闭源文件流
    }

    public static List<File> getFileList(String directoryPath) throws Exception {
        List<File> files = new ArrayList<>();
        Properties config = new Properties();
        InputStream ips = null;
        ips = new FileInputStream(new File(directoryPath + File.separator + "config.properties"));
        config.load(ips);

        Set keySet = config.keySet();//需要将keySet转换为int型


        //将keySet迭代出来,转换成int类型的set,排序后存储进去
        Set<Integer> intSet = new TreeSet<Integer>();
        Iterator iterString = keySet.iterator();
        while (iterString.hasNext()) {
            String tempKey = (String) iterString.next();
            if ("name".equals(tempKey)) {

            } else {
                int tempInt;
                tempInt = Integer.parseInt(tempKey);
                intSet.add(tempInt);
            }
        }

        Set<Integer> sortedKeySet = new TreeSet<Integer>();
        sortedKeySet.addAll(intSet);
        Iterator iter = sortedKeySet.iterator();
        while (iter.hasNext()) {
            String key = new String("" + iter.next());
            if (key.equals("name")) {
            } else {
                File file = new File(config.getProperty(key));
                files.add(file);
            }
        }
        return files;
    }

    public static String GetFileSize(File file) {
        String size = "";
        if (file.exists() && file.isFile()) {
            long fileS = file.length();
            DecimalFormat df = new DecimalFormat("#.00");
            if (fileS < 1024) {
                size = df.format((double) fileS) + "BT";
            } else if (fileS < 1048576) {
                size = df.format((double) fileS / 1024) + "KB";
            } else if (fileS < 1073741824) {
                size = df.format((double) fileS / 1048576) + "MB";
            } else {
                size = df.format((double) fileS / 1073741824) + "GB";
            }
        } else if (file.exists() && file.isDirectory()) {
            size = "";
        } else {
            size = "0BT";
        }
        return size;
    }

    public static long getFileSizeAll(List<String> files) {
        long allSize = 0;
        if (files != null) {
            for (int i = 0; i < files.size(); i++) {
                allSize += new File(files.get(i)).length();
            }
        }
        return allSize;
    }

    public static String getSubImgUrl(String str) {
        if (str.indexOf(Constants.Urls.PIC_URL) == -1) return str;
        return str.replace(Constants.Urls.PIC_URL, "");
    }

    public static boolean isServiceFiles(String str) {
        if (str.contains("/files/")) return true;
        return false;
    }

    public static boolean isServiceFilesStartWithFiles(String str) {
        if (str == null) return false;
        if (str.startsWith("/files/")) return true;
        return false;
    }

    public static List<String> getNoServceFile(List<String> allFiles) {
        if (allFiles == null || allFiles.isEmpty()) {
            return null;
        }
        for (int i = 0; i < allFiles.size(); i++) {
            if (CzfFileUtils.isServiceFilesStartWithFiles(allFiles.get(i))) {
                allFiles.remove(i);
                i--;
            }
        }
        return allFiles;
    }

    public static File save(ResponseBody body, Context context) {
        try {
            // todo change the file location/name according to your needs
            File saveFile = new File(com.shcyd.lib.utils.FileUtils.createAppPath(context));
            if (!saveFile.exists()) {
                if (!saveFile.createNewFile()) {
                    return null;
                }
            }
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] buffer = new byte[2048];
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(saveFile);
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
                outputStream.close();
                return saveFile;
            } catch (IOException e) {
                return null;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return null;
        }
    }


    public static File save(InputStream body, Context context) {
        try {
            // todo change the file location/name according to your needs
            File saveFile = new File(com.shcyd.lib.utils.FileUtils.createAppPath(context));
            if (!saveFile.exists()) {
                if (!saveFile.createNewFile()) {
                    return null;
                }
            }
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] buffer = new byte[2048];
                inputStream = body;
                outputStream = new FileOutputStream(saveFile);
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
                outputStream.close();
                return saveFile;
            } catch (IOException e) {
                return null;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return null;
        }
    }

    //该方法是视频相册压缩，压缩率较大。不建议大图使用。
    public static String bitmap2File(Bitmap bm, Context context) {
        if (bm == null) return null;
        File f;
        try {
            f = File.createTempFile(System.currentTimeMillis() + ".png", null, context.getCacheDir());
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 10, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return f.getAbsolutePath();
    }

    //************************************************************************add by wgy,处理文件******************
    //获取CZF根文件夹
    public static File getCZFDir(Context context) {
        String dirPath = "";
        //SD卡是否存在
        boolean isSdCardExists = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        // Environment.getExternalStorageDirectory()相当于File file=new File("/sdcard")
        boolean isRootDirExists = Environment.getExternalStorageDirectory().exists();
        if (isSdCardExists && isRootDirExists) {
            dirPath = String.format("%s/%s/", Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.Urls.CZF_ROOT_URL);
        } else {
            dirPath = String.format("%s/%s/", context.getApplicationContext().getFilesDir().getAbsolutePath(), Constants.Urls.CZF_ROOT_URL);
        }

        File appDir = new File(dirPath);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        return appDir;
    }

    //获取CZF缓存文件夹
    public static File getTempDir(Context context) {
        String dirPath = "";
        //SD卡是否存在
        boolean isSdCardExists = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        // Environment.getExternalStorageDirectory()相当于File file=new File("/sdcard")
        boolean isRootDirExists = Environment.getExternalStorageDirectory().exists();
        if (isSdCardExists && isRootDirExists) {
            dirPath = String.format("%s/%s/", Environment.getExternalStorageDirectory().getAbsolutePath(), Constants.Urls.CZF_TEMP_URL);
        } else {
            dirPath = String.format("%s/%s/", context.getApplicationContext().getFilesDir().getAbsolutePath(), Constants.Urls.CZF_TEMP_URL);
        }

        File appDir = new File(dirPath);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        //创建一个文件“.nomedia”，标记不允许媒体访问到。
        File nomedia = new File(appDir, ".nomedia");
        if (!nomedia.exists()) {
            try {
                nomedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return appDir;
    }

    //清空缓存文件夹
    public static boolean delTempDir(Context context) {
        deleteAll(getTempDir(context));
        return true;
    }

    //创建临时图片
    public static String createTempPhotoPath(Context context) {
        File photoDir = getTempPhotoDir(context);
        return photoDir.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg";
    }

    //创建图片,长时间保存。
    public static String createPhotoPath(Context context) {
        File photoDir = getPhotoDir(context);
        return photoDir.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg";
    }

    //获取缓存图片文件夹
    public static File getTempPhotoDir(Context context) {
        File appDir = getTempDir(context);
        File photoDir = new File(appDir.getAbsolutePath(), "photo");
        if (!photoDir.exists()) {
            photoDir.mkdir();
        }
        return photoDir;
    }

    //获取图片文件夹
    public static File getPhotoDir(Context context) {
        File appDir = getCZFDir(context);
        File photoDir = new File(appDir.getAbsolutePath(), "photo");
        if (!photoDir.exists()) {
            photoDir.mkdir();
        }
        return photoDir;
    }

    //获取视频文件夹
    public static File getVideoDir(Context context) {
        File appDir = getCZFDir(context);
        File photoDir = new File(appDir.getAbsolutePath(), "video");
        if (!photoDir.exists()) {
            photoDir.mkdir();
        }
        return photoDir;
    }

    //递归删除指定路径下的所有文件
    public static void deleteAll(File file) {
        if (file == null) return;
        if (file.list() == null) return;
        if (file.isFile() || file.list().length == 0) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteAll(f);//递归删除每一个文件
                f.delete();//删除该文件夹
            }
        }
    }

    //获取压缩图片集合
    public static List<String> compressPhotos(List<String> stringList, Context context) {
        List<String> compressImgs = new ArrayList<>();
        if (StringUtils.noEmptyList(stringList)) {
            for (String s : stringList) {
                compressImgs.add(ImageUtils.getCompressImg(s, context));
            }
            return compressImgs;
        }
        return null;
    }

    //获取压缩图片集合
    public static List<MediaItem> compressPhotosMediaItem(List<MediaItem> mediaItems, Context context) {
        List<MediaItem> compressImgs = new ArrayList<>();
        if (StringUtils.noEmptyList(mediaItems)) {
            for (MediaItem s : mediaItems) {
                s.setImagePath(ImageUtils.getCompressImg(s.getImagePath(), context));
                compressImgs.add(s);
            }
            return compressImgs;
        }
        return null;
    }

    /**
     * 获得指定文件的byte数组
     */
    public static String getBseString(File file){
        String buffer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = Base64.encodeToString(bos.toByteArray(),Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

}




