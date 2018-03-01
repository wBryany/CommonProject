package com.project.common_basic.hybrid.jscall;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.project.common_basic.R;
import com.project.common_basic.hybrid.BaseJsCallProcessor;
import com.project.common_basic.hybrid.JsCallData;
import com.project.common_basic.hybrid.NativeComponentProvider;
import com.project.common_basic.mvp.NearWebLogicView;
import com.project.common_basic.widget.dialog.DoubleBtnConfirmDialog;

/**
 * 打电话
 * <p>
 * Created by joye on 2017/4/26.
 */

public class CallTelProcessor extends BaseJsCallProcessor {

    public static final String FUNC_NAME = "telephone";
    private Context mContext;
    private NearWebLogicView.WebLogicListener webLogicListener;
    private NearWebLogicView webLogicView;

    public CallTelProcessor(NativeComponentProvider componentProvider) {
        super(componentProvider);
        this.mContext = componentProvider.provideApplicationContext();
        this.webLogicListener = componentProvider.provideWebInteraction();
        this.webLogicView = componentProvider.provideWebLogicView();
    }

    @Override
    public boolean onHandleJsQuest(JsCallData callData) {
        if (FUNC_NAME.equalsIgnoreCase(callData.getFunc())) {
            final CallTelParam callTelParam = convertJsonToObject(callData.getParams(), CallTelParam.class);
            if (callTelParam != null) {
                webLogicView.showAlert(mContext.getString(R.string.text_tip), mContext.getString(R.string.cc_confirm_call_phone, callTelParam.phone),
                        mContext.getString(R.string.text_cancel), mContext.getString(R.string.text_confirm), new DoubleBtnConfirmDialog.DoubleBtnClickListener() {
                            @Override
                            public void onConfirm() {
                                String action = "tel:" + callTelParam.phone;
                                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(action));
                                webLogicListener.startOutsideActivity(callIntent);
                            }

                            @Override
                            public void onCancel() {

                            }
                        });

            }
            return true;
        }
        return false;
    }

    @Override
    public String getFuncName() {
        return FUNC_NAME;
    }

    private class CallTelParam {
        public String phone;
    }
}
