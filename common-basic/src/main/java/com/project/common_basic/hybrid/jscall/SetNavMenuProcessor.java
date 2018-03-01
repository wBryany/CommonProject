package com.project.common_basic.hybrid.jscall;

import android.net.Uri;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.project.common_basic.hybrid.BaseJsCallProcessor;
import com.project.common_basic.hybrid.JsCallData;
import com.project.common_basic.hybrid.NativeComponentProvider;
import com.project.common_basic.model.ListIconTextModel;
import com.project.common_basic.mvp.NearWebLogicView;

import java.util.ArrayList;
import java.util.List;

/**
 * Js调用设置返回跳转uri
 */
public class SetNavMenuProcessor extends BaseJsCallProcessor {

    public static final String FUNC_NAME = "setNavMenu";
    private NearWebLogicView view;

    public SetNavMenuProcessor(NativeComponentProvider componentProvider) {
        super(componentProvider);
        view = componentProvider.provideWebLogicView();
    }

    @Override
    public boolean onHandleJsQuest(JsCallData callData) {
        if (FUNC_NAME.equals(callData.getFunc())) {
            NavMenuJsRequest request = convertJsonToObject(callData.getParams(),
                    NavMenuJsRequest.class);
            if (request != null && request.buttons != null
                    && request.buttons.size() > 0) {
                NavMenuJsRequest.ButtonsEntity buttonsEntity = request.buttons.get(0);
                if (!TextUtils.isEmpty(buttonsEntity.icon)) {
                    view.onChangeHeaderRightAsIcon(buttonsEntity.icon, buttonsEntity.uri, buttonsEntity.isJsUriType());
                } else {
                    view.onChangeHeaderRightAsTitle(buttonsEntity.title, buttonsEntity.uri, buttonsEntity.isJsUriType());
                }
            }
            if (request != null && request.menus != null
                    && request.menus.size() > 0) {
                List<ListIconTextModel> menus = new ArrayList<>();
                for (int i = 0; i < request.menus.size(); i++) {
                    NavMenuJsRequest.MenusEntity entity = request.menus.get(i);
                    ListIconTextModel menu = new ListIconTextModel();
                    if (!TextUtils.isEmpty(entity.icon)) {
                        menu.setIconUri(Uri.parse(entity.icon));
                    }
                    if (TextUtils.isEmpty(entity.uri)) {
                        menu.setClickUri("http://haojin.in");
                    } else {
                        menu.setClickUri(entity.uri);
                    }
                    menu.setText(entity.title);
                    menus.add(menu);
                }
                view.showHeaderMoreMenus(menus);
            }
            return true;
        }
        return false;
    }

    @Override
    public String getFuncName() {
        return FUNC_NAME;
    }

    @VisibleForTesting
    class NavMenuJsRequest {
        public List<MenusEntity> menus;
        public List<ButtonsEntity> buttons;


        class MenusEntity {
            /**
             * type : uri
             * uri : hjsh://xx
             * icon : https://1.png
             * title : 测试菜单
             */
            public String type;
            public String uri;
            public String icon;
            public String title;
        }

        class ButtonsEntity {
            /**
             * type : uri or nativeCallJS
             * uri : hjsh://xxx or navClickRBtn
             * icon : https://1.png
             * title :
             */
            public String type;
            public String uri;
            public String icon;
            public String title;

            public boolean isJsUriType() {
                return type.equals("nativeCallJS");
            }
        }
    }
}
