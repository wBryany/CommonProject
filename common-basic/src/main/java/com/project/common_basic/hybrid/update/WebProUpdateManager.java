package com.project.common_basic.hybrid.update;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.project.common_basic.hybrid.HybridUpdateValue;
import com.project.common_basic.reactive.CustomIoScheduler;
import com.project.common_basic.utils.CompressUtil;
import com.project.common_basic.utils.FileUtil;
import com.project.common_basic.widget.dialog.LoadingDialog;
import com.project.common_basic.widget.dialog.ProgressLineDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallback;

import java.io.File;

import okhttp3.Call;
import okhttp3.Request;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import static com.project.common_basic.hybrid.HybridUpdateValue.WEB_LOCAL_DIR_NAME;

/**
 * web工程更新管理
 * <p>
 * Created by joye on 2017/3/16.
 */

public class WebProUpdateManager {
    private static volatile WebProUpdateManager mInstance;
    private UpdateListener mListener;
    private ProgressLineDialog mProgressDialog;
    private ProgressLineDialog.Builder mProgressDialogBuilder;
    private LoadingDialog mLoadingDialog;
    private boolean mIsShowDialog;
    private Subscription unZipSub;

    public interface UpdateListener {
        void onStart();

        void onDownloading(float progress);

        void onUnzip();

        void onFinish(String dir);

        void onError(int errorCode, String errorMsg);
    }

    private WebProUpdateManager() {
    }

    public static WebProUpdateManager getInstance() {
        if (mInstance == null) {
            synchronized (WebProUpdateManager.class) {
                if (mInstance == null) {
                    mInstance = new WebProUpdateManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 更新Web组件
     *
     * @param context     上下文
     * @param downloadUrl 下载地址
     * @param listener    更新过程监听
     */
    public void update(Context context, String downloadUrl, UpdateListener listener) {
        updateInternal(context, downloadUrl, listener, false);
    }

    /**
     * 更新Web组件，同时显示进度对话框
     *
     * @param activity    Activity上下文
     * @param downloadUrl 下载地址
     * @param listener    更新过程监听
     */
    public void updateWithProgressDialog(Activity activity, String downloadUrl, UpdateListener listener) {
        updateInternal(activity, downloadUrl, listener, true);
    }

    /**
     * 更新web组件
     *
     * @param downloadUrl  下载地址
     * @param listener     过程监听
     * @param isShowDialog 是否显示对话框 注：这个对话框是一个通用对话框，用来显示更新过程中的进度信息。
     *                     使用者可以自定义其他方式或不显示。
     * @param context      如果显示通用对话框，需要提供Activity上下文
     */
    private void updateInternal(Context context, String downloadUrl, UpdateListener listener, boolean isShowDialog) {
        if (context == null || listener == null) {
            throw new IllegalArgumentException("Param Error, the context is " + context + ", the listener is " + listener);
        }
        this.mListener = listener;
        this.mIsShowDialog = isShowDialog;
        if (TextUtils.isEmpty(downloadUrl)) {
            mListener.onError(WebUpdateErrorCode.CODE_PARAM_ERROR, "download url is " + downloadUrl);
            return;
        }
        if (isShowDialog) {
            if (!(context instanceof Activity)) {
                listener.onError(WebUpdateErrorCode.CODE_PARAM_ERROR, "the Context must be an Activity context if you want to show a dialog!");
                return;
            } else {
                mProgressDialogBuilder = ProgressLineDialog.builder().setTouchOutDismiss(false).setMsg(context.getString(com.project.common_basic.R.string.cc_web_updating));
                mProgressDialog = mProgressDialogBuilder.build(context);
                mProgressDialog.setCancelable(false);
                mLoadingDialog = (LoadingDialog) LoadingDialog.builder().setMsg(context.getString(com.project.common_basic.R.string.cc_web_unziping)).setTouchOutDismiss(false).build(context);
                mLoadingDialog.setCancelable(false);
            }
        }
        startDownLoad(context, downloadUrl);
    }

    //开始下载
    private void startDownLoad(final Context context, String downloadUrl) {
        FileCallback callBack = new FileCallback(HybridUpdateValue.DOWNLOAD_FILE_PATH, HybridUpdateValue.DOWNLOAD_FILE_NAME) {
            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                if (mIsShowDialog && mProgressDialog != null) {
                    mProgressDialog.show();
                }
                if (mListener != null) {
                    mListener.onStart();
                }
            }

            @Override
            public void inProgress(float progress) {
                if (mIsShowDialog && mProgressDialogBuilder != null) {
                    mProgressDialogBuilder.setProgress(progress);
                }
                if (mListener != null) {
                    mListener.onDownloading(progress);
                }
            }

            @Override
            public void onError(Call call, Exception e) {
                if (mIsShowDialog && mProgressDialog != null) {
                    mProgressDialog.cancel();
                }
                if (mListener != null) {
                    mListener.onError(WebUpdateErrorCode.CODE_DOWN_LOAD_ERROR, e.getMessage());
                }
            }

            @Override
            public void onResponse(File file) {
                if (mIsShowDialog && mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                unTarGzFile(HybridUpdateValue.DOWNLOAD_FILE_PATH + HybridUpdateValue.DOWNLOAD_FILE_NAME,
                        HybridUpdateValue.getHybridRootPath(context));
            }
        };
        OkHttpUtils.get().url(downloadUrl).build().execute(callBack);
    }

    //开始解压
    private void unTarGzFile(final String srcFileName, final String destPath) {
        if (mListener != null) {
            mListener.onUnzip();
        }
        if (mIsShowDialog && mLoadingDialog != null) {
            mLoadingDialog.show();
        }
        unZipSub = Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                //先删除已存在的web文件
                boolean delDirRet = FileUtil.deleteDirAndItself(new File(destPath + "/" + WEB_LOCAL_DIR_NAME));
                if (!delDirRet) {
                    subscriber.onError(new Throwable("del old web file dir failed!"));
                    return;
                }
                boolean unTarGzSuccess = CompressUtil.unTarGzFile(srcFileName, destPath);
                if (unTarGzSuccess) {
                    subscriber.onNext(true);
                } else {
                    subscriber.onError(new Throwable("please check storage!"));
                }
                subscriber.onCompleted();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(CustomIoScheduler.getInstance()).subscribe(new UnTarGzSubscriber(destPath));
    }


    private class UnTarGzSubscriber extends Subscriber<Boolean> {
        private String mDestPath;

        UnTarGzSubscriber(String destPath) {
            this.mDestPath = destPath;
        }

        @Override
        public void onCompleted() {
            if (mIsShowDialog && mLoadingDialog != null) {
                mLoadingDialog.cancel();
            }
        }

        @Override
        public void onError(Throwable e) {
            if (mListener != null) {
                mListener.onError(WebUpdateErrorCode.CODE_UNZIP_FAIL, e.getMessage());
            }
        }

        @Override
        public void onNext(Boolean aBoolean) {
            //删除下载的zip包
            File zipFile = new File(HybridUpdateValue.DOWNLOAD_FILE_PATH + HybridUpdateValue.DOWNLOAD_FILE_NAME);
            if (zipFile.exists()) {
                final boolean delete = zipFile.delete();
                Timber.v("删除下载中产生的缓存文件 %b", delete);
            }
            if (mListener != null) {
                mListener.onFinish(mDestPath);
            }
        }
    }

    /**
     * 取消更新
     */
    public void cancel() {
        mListener = null;
        if (unZipSub != null) {
            unZipSub.unsubscribe();
            unZipSub = null;
        }
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
            mLoadingDialog = null;
        }
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
    }

}
