package com.project.common_basic.exception;

/**
 * Created by yamlee on 15/9/18.
 */
public class NearNetWorkException extends RuntimeException {
    public static final String DEFAULT_ERROR_CODE = "-1";
    private String errorCode = DEFAULT_ERROR_CODE;

    public NearNetWorkException(String detailMessage, String errorCode) {
        super(detailMessage);
        this.errorCode = errorCode;
    }

    public NearNetWorkException(String detailMessage) {
        super(detailMessage);
    }

    public String getErrorCode() {
        return errorCode;
    }
}
