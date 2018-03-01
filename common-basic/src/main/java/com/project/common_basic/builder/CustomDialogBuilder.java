package com.project.common_basic.builder;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.project.common_basic.R;
import com.project.common_basic.widget.CustomProgressDialog;

/**
 * Created by yamlee on 15/8/5.
 * 提示框管理类,包括loading
 */
@SuppressWarnings("HardCodedStringLiteral")
public class CustomDialogBuilder {

    private TextView tvTitle, tvMsg;
    private TextView tvConfirm, tvCancel;

    private static final int DIALOG_LOADING_TYPE = 1001;
    private static final int DIALOG_SINGLE_BTN_TYPE = 1002;
    private static final int DIALOG_DOUBLE_BTN_TYPE = 1003;
    private View mDialogView;
    private Context mContext;
    private Dialog mDialog;

    private CustomDialogBuilder(int dialogType, Context context) {

        mContext = context;

        LayoutInflater inflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (dialogType == DIALOG_SINGLE_BTN_TYPE) {
            mDialog = new Dialog(context, R.style.publish_dialog);
            mDialogView = inflater.inflate(
                    R.layout.view_dialog_layout_type1, null);

            tvMsg = (TextView) mDialogView
                    .findViewById(R.id.tv_alert_content);

            tvTitle = (TextView) mDialogView
                    .findViewById(R.id.tv_alert_title);
            tvConfirm = (TextView) mDialogView.findViewById(R.id.tv_confirm);
            mDialog.setContentView(mDialogView);
        }
        else if (dialogType == DIALOG_DOUBLE_BTN_TYPE) {
            mDialog = new Dialog(context, R.style.publish_dialog);
            mDialogView = inflater.inflate(
                    R.layout.view_dialog_layout_type2, null);
            tvMsg = (TextView) mDialogView
                    .findViewById(R.id.tv_alert_content);
            tvTitle = (TextView) mDialogView
                    .findViewById(R.id.tv_alert_title);
            tvConfirm = (TextView) mDialogView.findViewById(R.id.tv_confirm);
            tvCancel = (TextView) mDialogView.findViewById(R.id.tv_cancel);
            mDialog.setContentView(mDialogView);
        }
        else if (dialogType == DIALOG_LOADING_TYPE) {
            CustomProgressDialog customProgressDialog = new CustomProgressDialog(context, R.style.Theme_Near_CustomProgressDialog);
            customProgressDialog.show();
            tvMsg = customProgressDialog.getMsgTextView();
            mDialog = customProgressDialog;

        }
    }


    public CustomDialogBuilder setMsg(String msg) {
        if (null == tvMsg) throw new NullPointerException("Dialog Msg TextView is Null!");
        tvMsg.setText(msg);
        return this;
    }

    public CustomDialogBuilder setTitle(String title) {
        if (null == tvTitle)
            throw new NullPointerException("CustomDialogBuilder Title TextView is Null! ");
        tvTitle.setText(title);
        return this;
    }

    public CustomDialogBuilder setTouchOutsideCanDismiss(boolean isTouchOutSideDismiss) {
        mDialog.setCanceledOnTouchOutside(isTouchOutSideDismiss);
        return this;
    }

    public CustomDialogBuilder setOnConfrimClickListener(final OnConfirmClickListener confrimClickListener) {
        if (null == tvConfirm)
            throw new NullPointerException("CustomDialogBuilder Confirm Btn is Null!");
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog != null)
                    confrimClickListener.onClick(mDialog);
            }
        });
        return this;
    }


    public CustomDialogBuilder setConfirmBtnText(String confirmBtnText) {
        if (null == tvConfirm)
            throw new NullPointerException("CustomDialogBuilder Confirm Btn is Null!");
        tvConfirm.setText(confirmBtnText);
        return this;

    }

    public CustomDialogBuilder setCancelBtnText(String cancelBtnText) {
        if (null == tvCancel)
            throw new NullPointerException("CustomDialogBuilder Cancel Btn is Null!");
        tvCancel.setText(cancelBtnText);
        return this;
    }

    public CustomDialogBuilder setCancelClickListener(final OnCancelCLickListener cancelClickListener) {
        if (null == tvCancel)
            throw new NullPointerException("CustomDialogBuilder Cancel Btn is Null!");
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog != null)
                    cancelClickListener.onClick(mDialog);
            }
        });
        return this;
    }

    public Dialog getDialog() {
        return mDialog;
    }


    public static CustomDialogBuilder buildSingleBtnDialog(Context context) {
        return new CustomDialogBuilder(DIALOG_SINGLE_BTN_TYPE, context);
    }

    public static CustomDialogBuilder buildDoubleBtnDialog(Context context) {
        return new CustomDialogBuilder(DIALOG_DOUBLE_BTN_TYPE, context);
    }

    public static CustomDialogBuilder buildLoadingDialog(Context context) {
        return new CustomDialogBuilder(DIALOG_LOADING_TYPE, context);
    }

    /**
     * 对话框确认按钮点击事件
     */
    public interface OnConfirmClickListener {
        void onClick(DialogInterface dialog);
    }

    /**
     * 对话框取消按钮点击事件
     */
    public interface OnCancelCLickListener {
        void onClick(DialogInterface dialog);
    }

}
