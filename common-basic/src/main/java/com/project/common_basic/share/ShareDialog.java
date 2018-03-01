package com.project.common_basic.share;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.project.common_basic.R;


/**
 * 分享对话框
 * <p>
 * Created by zcZhang on 15/4/21.
 *
 * @author zcZhang
 * @author yamlee
 */
public class ShareDialog {
    /**
     * 分享点击监听接口
     */
    public interface ShareItemClickListener {
        void clickWeixinFriend();

        void clickWeixinMoments();

        void clickSinaWeibo();

        void clickQQFriend();

        void clickQQZone();

        void clickCopyLink();
    }

    public static void showDialog(Activity context, final ShareItemClickListener clickListener) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View shareView = layoutInflater.inflate(R.layout.dialog_share, null);
        final int cFullFillWidth = 10000;
        shareView.setMinimumWidth(cFullFillWidth);
        final Dialog dialog = new Dialog(context, R.style.publish_dialog);
        Window w = dialog.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        w.setAttributes(lp);
        dialog.onWindowAttributesChanged(lp);
        dialog.setContentView(shareView);
        dialog.setCanceledOnTouchOutside(true);
        shareView.findViewById(R.id.tv_share_copy_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.clickCopyLink();
                dialog.cancel();
            }
        });
        shareView.findViewById(R.id.tv_share_weixin_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.clickWeixinFriend();
                dialog.cancel();
            }
        });
        shareView.findViewById(R.id.tv_share_weixin_moments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.clickWeixinMoments();
                dialog.cancel();
            }
        });
        shareView.findViewById(R.id.tv_share_sina_weibo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.clickSinaWeibo();
                dialog.cancel();
            }
        });
        shareView.findViewById(R.id.tv_share_qq_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.clickQQFriend();
                dialog.cancel();
            }
        });
        shareView.findViewById(R.id.tv_share_qq_zone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.clickQQZone();
                dialog.cancel();
            }
        });
        shareView.findViewById(R.id.tv_share_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
}
