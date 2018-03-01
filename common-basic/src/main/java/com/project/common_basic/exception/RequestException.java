package com.project.common_basic.exception;

/**
 * 网络请求过程中的异常信息, 包括连接失效, 超时等等
 *
 * Created by LiFZhe on 3/12/15.
 */
public class RequestException extends RuntimeException {
    private String mErrorMessage = "";
    private String mErrorCode = "";

    public RequestException(String msg) {
        this.mErrorMessage = msg;
    }

    @Override
    public String getMessage() {
        return mErrorMessage;
    }

    public RequestException(String errorCode, String errorMsg) {
        this.mErrorCode = errorCode;
        this.mErrorMessage = errorMsg;
    }

    public String getErrorMsg() {
        return mErrorMessage;
    }

    public String getErrorCode() {
        return mErrorCode;
    }
}
