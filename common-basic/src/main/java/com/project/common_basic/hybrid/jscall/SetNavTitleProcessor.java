package com.project.common_basic.hybrid.jscall;

import android.graphics.Color;
import android.text.TextUtils;

import com.project.common_basic.hybrid.BaseJsCallProcessor;
import com.project.common_basic.hybrid.JsCallData;
import com.project.common_basic.hybrid.NativeComponentProvider;
import com.project.common_basic.mvp.NearWebLogicView;

/**
 * Js调用设置界面title
 *
 * @author yamlee
 */
public class SetNavTitleProcessor extends BaseJsCallProcessor {

    public static final String FUNC_NAME = "setNavTitle";

    public SetNavTitleProcessor(NativeComponentProvider componentProvider) {
        super(componentProvider);
    }

    @Override
    public boolean onHandleJsQuest(JsCallData callData) {
        if (FUNC_NAME.equals(callData.getFunc())) {
            TitleJsRequest request = convertJsonToObject(callData.getParams(), TitleJsRequest.class);
            int colorInt = 0, bgColorInt = 0;
            try {
                if (!TextUtils.isEmpty(request.color)) {
                    colorInt = Color.parseColor(request.color);
                }
            } catch (IllegalArgumentException e) {
                //设置color异常
            }
            try {
                if (!TextUtils.isEmpty(request.bgcolor)) {
                    bgColorInt = Color.parseColor(request.bgcolor);
                }
            } catch (IllegalArgumentException e) {
                //设置color异常
            }
            boolean titleClick = request.titleClick;
            NearWebLogicView view = componentProvider.provideWebLogicView();
            view.onChangeHeader(request.title, colorInt, bgColorInt, titleClick);
            return true;
        }
        return false;
    }

    @Override
    public String getFuncName() {
        return FUNC_NAME;
    }


    private class TitleJsRequest {
        public String title;
        public String color;
        public String bgcolor;
        public boolean titleClick;
    }
}
