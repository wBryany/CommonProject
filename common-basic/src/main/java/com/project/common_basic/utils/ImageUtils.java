package com.project.common_basic.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import timber.log.Timber;

/**
 * 关于图片的工具类
 * <p>
 * Created by zcZhang on 15/5/2.
 */
public class ImageUtils {
    @SuppressWarnings("HardCodedStringLiteral")
    private static final String TAG = "ImageUtils";
    @SuppressWarnings("HardCodedStringLiteral")
    public static final String AVATAR = "_avatar";//100*100
    public static final String MICRO = "_140";
    public static final String MIDDLE = "_320";
    public static final String LARGE = "_640";
    public static final String HUGE = "_900";
    private static ImageUtils imageUtils;
//    private static HashMap<String, Integer> imageSizeTypeMap;
    private static int screenWidth;


    public static boolean urlIsImage(String url) {
        //noinspection HardCodedStringLiteral
        return url != null &&
                (url.endsWith("jpg") || url.endsWith("png") || url.endsWith("jpeg"));
    }

    /**
     * bitmap数据转字节数据
     *
     * @param bmp
     * @param needRecycle
     * @return
     */
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {

        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
//            if (needRecycle)
//                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                e.printStackTrace();
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }

    /**
     * 字节数组转Bitmap
     *
     * @param b
     * @return
     */
    public static Bitmap bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

//    /**
//     * 根据不同屏幕分辨率设定不同页面的图片请求尺寸
//     *
//     * @param screenWidth
//     */
//    private static void initHashMap(int screenWidth) {
//        imageSizeTypeMap = new HashMap<>();
//        if (screenWidth >= 1080) {
//            imageSizeTypeMap.put(MICRO, 100);
//            imageSizeTypeMap.put(AVATAR, 200);
//            imageSizeTypeMap.put(MIDDLE, 400);
//            imageSizeTypeMap.put(LARGE, 600);
//            imageSizeTypeMap.put(HUGE, 1200);
//        } else if (screenWidth >= 640) {
//            imageSizeTypeMap.put(MICRO, 100);
//            imageSizeTypeMap.put(AVATAR, 200);
//            imageSizeTypeMap.put(MIDDLE, 200);
//            imageSizeTypeMap.put(LARGE, 400);
//            imageSizeTypeMap.put(HUGE, 800);
//        } else if (screenWidth >= 480) {
//            imageSizeTypeMap.put(MICRO, 100);
//            imageSizeTypeMap.put(AVATAR, 200);
//            imageSizeTypeMap.put(MIDDLE, 200);
//            imageSizeTypeMap.put(LARGE, 400);
//            imageSizeTypeMap.put(HUGE, 600);
//        } else if (screenWidth >= 320) {
//            imageSizeTypeMap.put(MICRO, 100);
//            imageSizeTypeMap.put(AVATAR, 100);
//            imageSizeTypeMap.put(MIDDLE, 100);
//            imageSizeTypeMap.put(LARGE, 200);
//            imageSizeTypeMap.put(HUGE, 400);
//        } else {
//            imageSizeTypeMap.put(MICRO, 100);
//            imageSizeTypeMap.put(AVATAR, 100);
//            imageSizeTypeMap.put(MIDDLE, 100);
//            imageSizeTypeMap.put(LARGE, 200);
//            imageSizeTypeMap.put(HUGE, 400);
//        }
//    }

    @SuppressWarnings("HardCodedStringLiteral")
    public static boolean saveImage2Gallery(Context context, String filePath, String fileName) throws IOException {
        if (context == null || fileName == null || filePath == null) {
            Log.e(TAG, "saveImage2Gallery: context: " + context + ",filePath: " + filePath + ",fileName: " + fileName);
            return false;
        }
        Timber.v("filePath: " + filePath + ",fileName: " + fileName);
        File file = new File(filePath, fileName);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        if (isPicMedia(fileName)) {
            ContentResolver contentResolver = context.getContentResolver();
            //向多媒体数据库中插入一条记录
            ContentValues values = new ContentValues(3);
            values.put(MediaStore.Images.Media.TITLE, "");
            values.put(MediaStore.Images.Media.DESCRIPTION, "");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            Uri url = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            //查询新创建记录的id
            if (url != null) {
                Timber.v("url--->" + url.toString());
                long id = ContentUris.parseId(url);
                OutputStream outputStream = null;
                FileInputStream fileInputStream = null;
                try {
                    outputStream = contentResolver.openOutputStream(url);
                    //向uri中写图片数据
                    fileInputStream = new FileInputStream(file);
                    byte[] buffer = new byte[1024 * 5];
                    int len;
                    while ((len = fileInputStream.read(buffer)) != -1) {
                        if (outputStream != null) {
                            outputStream.write(buffer, 0, len);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new IOException();
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //生成缩略图
                Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(contentResolver, id,
                        MediaStore.Images.Thumbnails.MINI_KIND, null);
                //生成缩略图失败
                if (miniThumb == null) {
                    return false;
                }
                //将缩略图插入到数据库
                Bitmap microThumb = StoreThumbnail(contentResolver, miniThumb, id, 50F, 50F,
                        MediaStore.Images.Thumbnails.MICRO_KIND);
                FileUtil.deleteFile(new File(filePath, fileName));
                String realPath = getFilePathFromContentUri(url, contentResolver);
                if (!TextUtils.isEmpty(realPath)) {
                    //通知系统更新此文件
                    Timber.v("插入相册后的路径------>" + realPath);
                    File insertFile = new File(realPath);
                    notifyMediaFileScan(context, insertFile.getParent(), insertFile.getName());
                }
                return true;
            }
            return false;
        } else {
            Log.e(TAG, "saveImage2Gallery: file type error. fileName : " + fileName);
            return false;
        }
    }

    /**
     * Gets the corresponding path to a file from the given content:// URI
     *
     * @param selectedVideoUri The content:// URI to find the file path from
     * @param contentResolver  The content resolver to use to perform the query.
     * @return the file path as a string
     */
    public static String getFilePathFromContentUri(Uri selectedVideoUri,
                                                   ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
//      也可用下面的方法拿到cursor
//      Cursor cursor = this.context.managedQuery(selectedVideoUri, filePathColumn, null, null, null);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    /**
     * 检查文件是否是图片类型
     *
     * @param fileName
     * @return
     */
    @SuppressWarnings("HardCodedStringLiteral")
    public static boolean isPicMedia(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }
        String fileNameLowercase = fileName.toLowerCase();
        return fileNameLowercase.endsWith(".jpg") || fileNameLowercase.endsWith(".jpeg") || fileNameLowercase.endsWith(".png") || fileNameLowercase.endsWith(".bmp");
    }

    /**
     * 存数图片数据到文件,文件名称可选
     *
     * @param bmp  图片源数据
     * @param path 存储目录
     * @param name 文件名称,可选
     * @return
     */
    @SuppressWarnings("HardCodedStringLiteral")
    public static File saveBitmap2File(Bitmap bmp, String path, String name) throws IOException {
        float bitmapSize = getBitmapSize(bmp);
        if (bitmapSize > FileUtil.getFreeDiskSpace()) {
            Log.e(TAG, "saveBitmap2File: save error, space insufficient");
            return null;
        }
        FileUtil.createDirIfNotExist(path);
        if (TextUtils.isEmpty(name)) {
            name = System.currentTimeMillis() + ".jpg";
        }
        if (!FileUtil.writeBitmap2Disk(bmp, path, name)) {
            Log.e(TAG, "saveBitmap2File: save error, io exception");
            return null;
        }
        return new File(path, name);
    }

    //通知系统文件改动
    public static void notifyMediaFileScan(Context context, String path, String fileName) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path, fileName))));
    }

    private static float getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    private static Bitmap StoreThumbnail(
            ContentResolver cr,
            Bitmap source,
            long id,
            float width, float height,
            int kind) {
        // create the matrix to scale it
        Matrix matrix = new Matrix();

        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                source.getWidth(),
                source.getHeight(), matrix,
                true);

        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Thumbnails.KIND, kind);
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID, (int) id);
        values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.getHeight());
        values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.getWidth());

        Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try {
            if (url != null) {
                OutputStream thumbOut = cr.openOutputStream(url);

                thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
                thumbOut.close();
                return thumb;
            }
            return null;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }
}
