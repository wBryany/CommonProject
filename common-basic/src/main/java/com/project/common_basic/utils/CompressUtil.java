package com.project.common_basic.utils;

import com.project.common_basic.exception.NearLogger;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;


/**
 * 压缩,解压工具类
 */
public class CompressUtil {
    @SuppressWarnings("HardCodedStringLiteral")
    private static final String TAG = "CompressUtil";

    /**
     * 解压.tar.gz格式文件
     *
     * @param srcFileName 需要解压的文件路径(具体到文件)
     * @param destDir     解压目标路径
     * @return
     */
    public static boolean unTarGzFile(String srcFileName, String destDir) {
        File destFile = new File(destDir);
        if (!destFile.exists()) {
            boolean mkdirSuccess = destFile.mkdirs();
            if (!mkdirSuccess) {
                return false;
            }
        }
        return unzipOarFile(destDir, srcFileName);
    }

    @SuppressWarnings("HardCodedStringLiteral")
    private static boolean unzipOarFile(String destDir, String srcFileName) {
        BufferedOutputStream bufferedOutputStream;
        FileInputStream fis;
        ArchiveInputStream in;
        BufferedInputStream bufferedInputStream = null;
        try {
            fis = new FileInputStream(srcFileName);
            GZIPInputStream is = new GZIPInputStream(new BufferedInputStream(fis));
            in = new ArchiveStreamFactory().createArchiveInputStream("tar", is);
            bufferedInputStream = new BufferedInputStream(in);
            TarArchiveEntry entry = (TarArchiveEntry) in.getNextEntry();
            while (entry != null) {
                String name = entry.getName();
                String[] names = name.split("/");
                String destFileName = destDir;
                for (String str : names) {
                    destFileName = destFileName + File.separator + str;
                }
                if (name.endsWith("/")) {
                    mkFolder(destFileName);
                } else {
                    File file = mkFile(destFileName);
                    bufferedOutputStream = new BufferedOutputStream(
                            new FileOutputStream(file));
                    int b;
                    while ((b = bufferedInputStream.read()) != -1) {
                        bufferedOutputStream.write(b);
                    }
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                }
                entry = (TarArchiveEntry) in.getNextEntry();
            }
        } catch (FileNotFoundException e) {
            NearLogger.e(TAG, "FileNotFoundException   " + e.getMessage());
            return false;
        } catch (IOException e) {
            NearLogger.e(TAG, "IOException   " + e.getMessage());
            return false;
        } catch (ArchiveException e) {
            NearLogger.e(TAG, "ArchiveException   " + e.getMessage());
            return false;
        } finally {
            try {
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
            } catch (IOException e) {
                NearLogger.e(TAG, "IOException   " + e.getMessage());
            }
        }
        return true;
    }

    private static void mkFolder(String fileName) {
        File f = new File(fileName);
        if (!f.exists()) {
            f.mkdir();
        }
    }

    private static File mkFile(String fileName) throws IOException {
        File f = new File(fileName);
        f.createNewFile();
        return f;
    }
}