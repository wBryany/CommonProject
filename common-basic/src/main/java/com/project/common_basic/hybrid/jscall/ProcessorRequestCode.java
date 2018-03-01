package com.project.common_basic.hybrid.jscall;

/**
 * JsCall Process 请求码统一管理类
 * Created by yamlee on 2/27/17.
 *
 * @author yamlee
 */
public class ProcessorRequestCode {
    public static final int SCAN_QRCODE_PROCESS = 1;
    public static final int REQUEST_CODE_MEMBER_PAY = SCAN_QRCODE_PROCESS + 1;
    public static final int REQUEST_SHOP_EDIT = REQUEST_CODE_MEMBER_PAY + 1;
}
