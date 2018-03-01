package com.project.common_basic.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.project.common_basic.R;


/**
 * Created by yamlee on 15/8/5.
 * 常用加载进度对话框
 */
public class CustomProgressDialog extends Dialog {
    private TextView tvMsg;

    public CustomProgressDialog(Context context) {
        super(context);
        initView(context);
    }


    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
        initView(context);
    }

    private void initView(Context context){
//        LayoutInflater inflater = (LayoutInflater)
//                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mContentView = inflater.inflate(R.layout.layout_progress_dialog,null);
//        tvMsg = (TextView) mContentView.findViewById(R.id.tv_loading_msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_progress_dialog);
        tvMsg = (TextView)findViewById(R.id.tv_loading_msg);
    }
    
    private void setMsg(String msg){
        if(null != tvMsg){
            tvMsg.setText(msg);
        }
    }

    public TextView getMsgTextView(){
        return tvMsg;
    }

}
