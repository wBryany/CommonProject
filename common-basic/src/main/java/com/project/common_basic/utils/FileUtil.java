package com.project.common_basic.utils;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import com.project.common_basic.exception.NearLogger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * 文件操作相关工具类
 * <p>
 * Created by zczhang on 16/2/24.
 */
public class FileUtil {
    @SuppressWarnings("HardCodedStringLiteral")
    private static final String TAG = "FileUtil";

    /**
     * 创建文件目录如果目录路径不存在
     *
     * @param dirPath 目录路径
     */
    public static boolean createDirIfNotExist(String dirPath) {
        File file = new File(dirPath);
        //文件不存在
        if (!file.exists()) {
            return file.mkdirs();
        }
        //文件存在
        return true;
    }

    /**
     * 创建新文件
     *
     * @param dir      所在目录，若不存在，则自动创建
     * @param fileName 文件名称
     * @return 是否创建成功
     */
    public static boolean createFile(String dir, String fileName) throws IOException {
        boolean result;
        result = createDirIfNotExist(dir);
        File file = new File(dir, fileName);
        if (!file.exists()) {
            result = file.createNewFile();
        }

        return result;
    }

    public static boolean isFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 获取当前设备中存储空间剩余存储大小 单位
     */
    public static long getFreeDiskSpace() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs statFs = new StatFs(sdcardDir.getPath());
            long blockSize;
            long availCount;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = statFs.getBlockSizeLong();
                availCount = statFs.getAvailableBlocksLong();
            } else {
                blockSize = statFs.getBlockSize();
                availCount = statFs.getAvailableBlocks();
            }
            long availSpace = availCount * blockSize;
            //noinspection HardCodedStringLiteral
            NearLogger.i(TAG, "磁盘剩余空间------>" + availSpace + "B");
            return availSpace;
        }
        return 0;
    }

    /**
     * 将bitmap写入sdcard中
     *
     * @param bitmap   需要写入的bitmap
     * @param filePath 写入的目标目录
     * @param fileName 写入的文件名称
     */
    public static boolean writeBitmap2Disk(Bitmap bitmap, String filePath, String fileName) throws IOException {
        File file = new File(filePath, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return true;
    }

    /**
     * 向指定文件中写入字符串列表
     *
     * @param data 内容
     * @param file 目的文件
     * @return 是否写入成功
     * @throws IOException
     */
    public static boolean writeList2Disk(List<String> data, File file) throws IOException {
        boolean result = true;
        if (data == null || !file.exists()) {
            result = false;
            Timber.d("writeList2Disk method error, data is null or file is not exists.");
            return result;
        }
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(file, false);
            bufferedWriter = new BufferedWriter(fileWriter);
            for (String str : data) {
                bufferedWriter.write(str, 0, str.length());
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
        } finally {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (fileWriter != null) {
                fileWriter.close();
            }
        }


        return result;
    }

    /**
     * 读取文件内容到列表中
     *
     * @param dir      文件目录
     * @param fileName 文件名称
     * @return 文件内容
     * @throws IOException
     */
    public static List<String> readListFromFile(String dir, String fileName) throws IOException {
        List<String> result = new ArrayList<>();
        File file = new File(dir, fileName);
        if (!file.exists()) {
            Timber.v("readListFromFile error, the file %s not exist.", file.getAbsoluteFile());
            return result;
        }
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String oneLine = null;
            while ((oneLine = bufferedReader.readLine()) != null) {
                result.add(oneLine);
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (fileReader != null) {
                fileReader.close();
            }
        }

        return result;
    }


    /**
     * 删除指定文件
     */
    public static boolean deleteFile(File file) {
        boolean result = false;
        if (file.exists()) {
            result = file.delete();
        }
        return result;
    }

    /**
     * 删除目录
     *
     * @param file 目录文件
     * @return
     */
    public static boolean deleteDir(File file) {
        boolean ret = false;
        if (file == null || !file.exists()) {
            return ret;
        }
        try {
            for (File childFile : file.listFiles()) {
                if (childFile.isFile()) {
                    deleteFile(childFile);
                } else {
                    deleteDir(childFile);
                }
            }
            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        }
        return ret;
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 目录文件
     * @return
     */
    public static boolean deleteDirAndItself(File dir) {
        boolean ret = false;
        if (dir == null) {
            return ret;
        } else {
            if (!dir.exists()){
                dir.mkdirs();
            }
        }
        try {
            for (File childFile : dir.listFiles()) {
                if (childFile.isFile()) {
                    deleteFile(childFile);
                } else {
                    deleteDirAndItself(childFile);
                }
            }
            ret = dir.delete();
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        }
        return ret;
    }

    /**
     * 写内容到文件末尾
     *
     * @param f       文件
     * @param content 内容
     * @throws IOException
     */
    public static void writeMsgToFile(File f, String content) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            if (f == null) {
                return;
            }
            if (!f.exists()) {
                f.createNewFile();
            }
            byte[] buff = content.getBytes();
            // 追加文件
            fileOutputStream = new FileOutputStream(f, true);
            fileOutputStream.write(buff, 0, buff.length);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null)
                fileOutputStream.close();
        }

    }
}
