package com.project.common_basic.hybrid.jscall;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.project.common_basic.hybrid.BaseJsCallProcessor;
import com.project.common_basic.hybrid.JsCallData;
import com.project.common_basic.hybrid.NativeComponentProvider;

/**
 * 复制内容到剪切板
 * <p>
 * Created by joye on 2017/4/26.
 */

public class CopyProcessor extends BaseJsCallProcessor {
    public static final String FUNC_NAME = "copy";
    private Context mContext;

    public CopyProcessor(NativeComponentProvider componentProvider) {
        super(componentProvider);
        this.mContext = componentProvider.provideApplicationContext();
    }

    @Override
    public boolean onHandleJsQuest(JsCallData callData) {
        if (FUNC_NAME.equalsIgnoreCase(callData.getFunc())) {
            CopyParam copyParam = convertJsonToObject(callData.getParams(), CopyParam.class);
            if (copyParam != null) {
                ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText("Text", copyParam.content));
            }
            return true;
        }
        return false;
    }

    @Override
    public String getFuncName() {
        return FUNC_NAME;
    }

    private class CopyParam {
        public String content;
    }
}
