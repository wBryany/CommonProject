package com.project.common_basic.manager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.project.common_basic.R;
import com.project.common_basic.exception.NearNetWorkException;
import com.project.common_basic.utils.MathUtil;
import com.project.common_basic.utils.NetUtil;
import com.project.common_basic.utils.SecurityUtil;
import com.project.common_basic.widget.dialog.DoubleBtnConfirmDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.FileCallback;
import com.zhy.http.okhttp.exception.UserCancelException;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;

import okhttp3.Call;
import timber.log.Timber;

/**
 * 文件下载器
 * 运行在主线程
 * <p>
 * Created by zczhang on 16/5/5.
 */
public class FileDownLoader {
    @SuppressWarnings("HardCodedStringLiteral")
    private static final String TAG = "FileDownLoader";

    /**
     * 上传过程回调
     */
    public interface DownLoadProgressCallBack {

        void onProgress(int progress);

        void onSuccess(File file);

        void onFailure(Exception e);

        void onCancel();
    }

    /**
     * 默认下载模式为非WIFI下提示
     */
    private int mDownLoadModel = DownloadModel.MODEL_REMIND_NOT_WIFI;

    /**
     * 上下文
     */
    private Context mContext;

    /**
     * 文件下载地址
     */
    private String mFileUrl;

    /**
     * 存储文件路径
     */
    private String mStoreFilePath;

    /**
     * 存储文件名称
     */
    private String mStoreFileName;

    /**
     * 下载过程回调
     */
    private DownLoadProgressCallBack mCallBack;

    private RequestCall mRequestCall;//请求调用

    private FileDownLoader(Context context) {
        this.mContext = context;
    }

    public static FileDownLoader newInstance(Context context) {
        return new FileDownLoader(context);
    }

    public FileDownLoader setDownLoadModel(@DownloadModel.DownloadModelTemplate int mDownLoadModel) {
        this.mDownLoadModel = mDownLoadModel;
        return this;
    }

    public FileDownLoader url(String url) {
        this.mFileUrl = url;
        return this;
    }

    public FileDownLoader fileName(String fileName) {
        this.mStoreFileName = fileName;
        return this;
    }

    public FileDownLoader filePath(String filePath) {
        mStoreFilePath = filePath;
        return this;
    }

    public FileDownLoader setListener(DownLoadProgressCallBack downLoadProgressCallBack) {
        this.mCallBack = downLoadProgressCallBack;
        return this;
    }

    public void cancelDownload() {
        if (mRequestCall != null) {
            mRequestCall.cancel();
        }
    }

    public void downLoad() {
        //检查网路链接
        if (!NetUtil.isNetAvailable(mContext)) {
            if (mCallBack != null) {
                mCallBack.onFailure(new NearNetWorkException(mContext.getString(R.string.network_error_please_check)));
            }
            return;
        }
        //检查下载链接
        if (TextUtils.isEmpty(mFileUrl)) {
            if (mCallBack != null) {
                mCallBack.onFailure(new IllegalArgumentException(mContext.getString(R.string.please_set_file_download_link)));
            }
            return;
        }
        //取消之前的下载
        if (mRequestCall != null) {
            mRequestCall.cancel();
            mRequestCall = null;
        }

        mRequestCall = createRequestCall(mFileUrl);
        FileCallback fileCallBack = new FileCallback(getDownLoadPath(), getDownLoadFileName()) {
            @SuppressWarnings("HardCodedStringLiteral")
            @Override
            public void inProgress(float progress) {
                int downloadProgress = (int) (100 * progress);
                Timber.v("文件下载进度--->" + downloadProgress);
                if (mCallBack != null) {
                    mCallBack.onProgress(downloadProgress);
                }
            }

            @SuppressWarnings("HardCodedStringLiteral")
            @Override
            public void onError(Call call, Exception e) {
                Timber.v("文件下载失败--->" + e.toString());
                if (mCallBack != null) {
                    if (e instanceof UserCancelException) {
                        mCallBack.onCancel();
                    } else {
                        mCallBack.onFailure(e);
                    }
                }
            }

            @SuppressWarnings("HardCodedStringLiteral")
            @Override
            public void onResponse(File response) {
                Timber.v("文件下载成功--->" + response.getAbsolutePath());
                if (mCallBack != null) {
                    mCallBack.onSuccess(response);
                }
            }

            @Override
            public void getContentLength(long length) {
                super.getContentLength(length);
                if (mDownLoadModel == DownloadModel.MODEL_REMIND_NOT_WIFI) {
                    //非wifi链接情况下,提示下载
                    if (!NetUtil.isWifiConnect(mContext)) {
                        showFileLengthWarnDialog(length, this);
                    } else {
                        this.continueDownload();
                    }
                } else if (mDownLoadModel == DownloadModel.MODEL_REMIND_ALWAYS) {
                    showFileLengthWarnDialog(length, this);
                } else if (mDownLoadModel == DownloadModel.MODEL_DOWNLOAD_IF_WIFI_NOT_REMIND) {
                    //WIFI下不提醒直接下载
                    if (NetUtil.isWifiConnect(mContext)) {
                        this.continueDownload();
                    }
                }
            }
        };

        if (mDownLoadModel == DownloadModel.MODEL_REMIND_NOT_WIFI || mDownLoadModel == DownloadModel.MODEL_REMIND_ALWAYS) {
            fileCallBack.setNeedWait(true);
        }
        //执行下载
        mRequestCall.execute(fileCallBack);
    }

    private String getDownLoadPath() {
        if (TextUtils.isEmpty(mStoreFilePath)) {
            mStoreFilePath = mContext.getCacheDir().getAbsolutePath();
        }

        return mStoreFilePath;
    }

    private String getDownLoadFileName() {
        if (TextUtils.isEmpty(mStoreFileName)) {
            String urlFileName = getDownloadUrlFileName(mFileUrl);
            if (TextUtils.isEmpty(urlFileName)) {
                //noinspection HardCodedStringLiteral
                mStoreFileName = System.currentTimeMillis() + ".tmp";
            } else {
                mStoreFileName = urlFileName;
            }
        }

        return mStoreFileName;
    }

    /**
     * 显示文件长度警告对话框
     */
    private void showFileLengthWarnDialog(long fileLength, final FileCallback fileCallBack) {
        if (mContext instanceof Activity) {
            Dialog dialog = NearDialogFactory.getDoubleBtnDialogBuilder()
                    .setMsg(mContext.getString(R.string.not_wifi_if_continue_download, byte2M(fileLength)))
                    .setConfirmBtnText(mContext.getString(R.string.confirm_download))
                    .setDoubleBtnClickListener(new DoubleBtnConfirmDialog.DoubleBtnClickListener() {
                        @Override
                        public void onConfirm() {
                            fileCallBack.continueDownload();
                        }

                        @Override
                        public void onCancel() {
                            fileCallBack.cancelDownload();
                        }
                    })
                    .build(mContext);

            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        fileCallBack.cancelDownload();
                        return true;
                    }
                    return false;
                }
            });

            dialog.show();
        } else {
            //noinspection HardCodedStringLiteral
            Log.e(TAG, "context must be an activity instance , if you want show a warn dialog!");
        }
    }

    private String byte2M(long bytes) {
        if (bytes == -1) {
            return mContext.getString(R.string.unknown_size);
        }
        //noinspection HardCodedStringLiteral
        return MathUtil.divide(String.valueOf(bytes), String.valueOf(1024 * 1024), 2) + "M";
    }

    /**
     * 从url中截取文件名称
     *
     * @param url
     * @return
     */
    private String getDownloadUrlFileName(String url) {
        if (TextUtils.isEmpty(url)) return "";
        int lastBankSlantPos = url.lastIndexOf("/");
        String fileName = url.substring(lastBankSlantPos + 1);
        if (!TextUtils.isEmpty(fileName)) {
            if (fileName.contains(".")) {
                return fileName.replace(" ", "").trim().toLowerCase();
            }
        }
        return "";
    }

    /**
     * 由下载链接的MD5值作为临时文件名称
     *
     * @param url
     * @return
     */
    private String getTempFileName(String url) {
        return SecurityUtil.toMd5(url);
    }

    /**
     * 创建文件下载请求
     *
     * @param url
     * @return
     */
    @SuppressWarnings("HardCodedStringLiteral")
    private RequestCall createRequestCall(String url) {
        GetBuilder getBuilder = OkHttpUtils.get().url(mFileUrl);
        File tempFile = new File(getDownLoadPath(), getTempFileName(url));
        //如果存在缓存文件,则继续下载
        if (tempFile.exists()) {
            long tempFileSize = tempFile.length();
            Timber.v("存在未下载完成的文件---->" + tempFile.getAbsolutePath() + "; size= " + tempFileSize);
            String rangeProperty = "bytes=" + tempFileSize + "-";
            getBuilder.addHeader("RANGE", rangeProperty);
        }
        return getBuilder.build();
    }

}
