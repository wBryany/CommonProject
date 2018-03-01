package com.project.common_basic.network;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.project.common_basic.manager.EssentialSpKey;
import com.project.common_basic.manager.SpManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Https证书缓存
 * <p>
 * 在安装包assets中保存一份最新的证书文件,文件名必须为_.qfpay.com.cer
 * 根据服务器接口中证书链接字段,判断是否需要下载更新证书
 * <p>
 * Created by zczhang on 16/6/21.
 */
public class HttpsCertificateCache {
    @SuppressWarnings("HardCodedStringLiteral")
    private static final String CER_FILE_NAME = "_.qfpay.com.cer";
    public static final String CER_FILE_PATH = getCertFilePath();
    private Context mContext;
    private SpManager mSpManager;

    public HttpsCertificateCache(Context context) {
        this.mContext = context;
        this.mSpManager = new SpManager(context);
    }

    @SuppressWarnings("HardCodedStringLiteral")
    private static String getCertFilePath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "cert" + File.separator + CER_FILE_NAME;
        return path;
    }

    /**
     * 本地是否有缓存证书
     *
     * @return
     */
    public boolean isCerCached() {
        File file = new File(CER_FILE_PATH);
        return file.exists();
    }

    /**
     * 证书是否过期
     *
     * @return
     */
    public boolean isCerExpired(String url) {
        if (isCerCached()) {
            String cerCacheUrl = mSpManager.getString(EssentialSpKey.STRING_HTTPS_CER_URL, "");
            if (!TextUtils.isEmpty(url) && url.equals(cerCacheUrl)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取证书流
     *
     * @return
     */
    public InputStream getCerInputStream() {
        InputStream inputStream = null;
        if (isCerCached()) {
            try {
                inputStream = new FileInputStream(new File(CER_FILE_PATH));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                inputStream = mContext.getAssets().open(CER_FILE_NAME);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return inputStream;
    }
}
