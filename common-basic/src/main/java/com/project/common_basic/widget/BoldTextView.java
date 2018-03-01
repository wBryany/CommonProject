package com.project.common_basic.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 字体加粗样式自定义
 *
 * strokeWidth = (float) (0.13 * density + 0.5f);
 * Created by fengruicong on 15-11-5.
 */
public class BoldTextView extends TextView {
    public BoldTextView(Context context) {
        super(context);
        init(true, context);
    }

    public BoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(true, context);
    }

    public BoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(true, context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BoldTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(true, context);
    }

    protected void init(boolean bold, Context context) {
        setNearTextBold(bold, context);
    }

    public void setNearTextBold(boolean bold, Context context) {
        //设置字体加粗
        if (bold) {
            getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
            float density =  context.getResources().getDisplayMetrics().density;
            float strokeWidth = (float) (0.13 * density + 0.5f);
            getPaint().setStrokeWidth(strokeWidth);
        } else {
            getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
            getPaint().setStrokeWidth(0);
        }
    }
}
