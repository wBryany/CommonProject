package com.project.common_basic.network;

/**
 * 网络错误回调
 * Created by yamlee on 12/12/16.
 *
 * @author yamlee
 */
public class NetErrorListener {
    /**
     * http非200的错误,子类如果针对处理了需返回true，否则essential模块会抛出RequestException
     */
    public boolean onHttpError(Throwable e) {
        return false;
    }

    /**
     * 网络连接错误，子类如果针对处理了需返回true，否则essential模块会抛出RequestException
     */
    public boolean onNetworkError(Throwable e) {
        return false;
    }

    /**
     * 数据解析异常，子类如果针对处理了需返回true，否则essential模块会抛出RequestException
     */
    public boolean onDataConversionError(Throwable e) {
        return false;
    }

    /**
     * 未知错误，子类如果针对处理了需返回true，否则essential模块会抛出RequestException
     */
    public boolean onUnknowError(Throwable e) {
        return false;
    }
}
