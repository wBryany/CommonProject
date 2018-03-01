package com.project.common_basic.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.project.common_basic.R;
import com.rey.material.widget.ProgressView;

/**
 * Created by chenfeiyue on 16/5/7.
 */
public class ProgressLineDialog extends Dialog {
    private View mContentView;

    public ProgressLineDialog(Context context, View view) {
        super(context);
        initView(context, view);
    }

    public ProgressLineDialog(Context context, int theme, View view) {
        super(context, theme);
        initView(context, view);
    }

    private void initView(Context context, View view) {
        mContentView = view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mContentView != null) {
            setContentView(mContentView);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String msg = "";
        private boolean canTouchOutDismiss = false;

        private ProgressView progressView;

        private ProgressLineDialog dialog;

        public ProgressLineDialog getDialog() {
            return dialog;
        }

        public void dismissDialog() {
            stop();
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        public Builder setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder setTouchOutDismiss(boolean canTouchOutDismiss) {
            this.canTouchOutDismiss = canTouchOutDismiss;
            return this;
        }

        public void setProgress(float progress) {
            progressView.setProgress(progress);
        }

        public void start() {
            progressView.post(new Runnable() {
                @Override
                public void run() {
                    progressView.start();
                }
            });
        }

        public void stop() {
            progressView.stop();
        }

        public ProgressLineDialog build(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_progress_line_dialog, null);
            TextView tvMsg = (TextView) view.findViewById(R.id.tv_loading_msg);
            tvMsg.setText(this.msg);
            progressView = (ProgressView) view.findViewById(R.id.progress_view);
            dialog = new ProgressLineDialog(context, R.style.Theme_Near_CustomProgressDialog, view);
            dialog.setCanceledOnTouchOutside(canTouchOutDismiss);
            return dialog;
        }
    }
}