package com.project.common_basic.network;

import android.content.Context;

import com.project.common_basic.R;
import com.project.common_basic.exception.NearLogger;
import com.project.common_basic.exception.RequestException;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * 网络层错误拦截
 * <p>
 * Created by LiFZhe on 3/11/15.
 * @author yamlee
 */
public class HErrorHandler implements ErrorHandler {

    private Context context;
    private NetErrorListener netErrorListener;


    public HErrorHandler(Context context) {
        this.context = context;
    }

    /**
     * 添加网络回调接口
     */
    public void addNetErrorListener(NetErrorListener listener) {
        this.netErrorListener = listener;
    }

    /**
     * 移除回调接口
     */
    public void removeNetErrorListener(NetErrorListener listener) {
        this.netErrorListener = null;
    }

    @Override
    public Throwable handleError(RetrofitError cause) {
        NearLogger.log(cause);
        String shownMsg = context.getString(R.string.get_data_faile_please_try);
        switch (cause.getKind()) {
            case HTTP:
                shownMsg = context.getString(R.string.has_alittle_problem);
                //上报异常信息
                if (netErrorListener != null) {
                    if (!netErrorListener.onHttpError(cause)) {
                        return new RequestException(shownMsg);
                    }
                }
                break;
            case NETWORK:
                shownMsg = context.getString(R.string.network_err_please_check);
                if (netErrorListener != null) {
                    if (!netErrorListener.onNetworkError(cause)) {
                        return new RequestException(shownMsg);
                    }
                }
                break;
            case CONVERSION:
                //上报异常信息
                shownMsg = context.getString(R.string.please_refresh);
                if (netErrorListener != null) {
                    if (!netErrorListener.onDataConversionError(cause)) {
                        return new RequestException(shownMsg);
                    }
                }

                break;
            case UNEXPECTED:
                shownMsg = context.getString(R.string.unknow_exception);
                if (netErrorListener != null) {
                    if (!netErrorListener.onUnknowError(cause)) {
                        return new RequestException(shownMsg);
                    }
                }
                break;
        }
        return new RequestException(shownMsg);
    }
}
