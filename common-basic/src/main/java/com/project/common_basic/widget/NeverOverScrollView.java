package com.project.common_basic.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by chenfeiyue on 16/11/1.
 * NeverOverScrollView
 */
public class NeverOverScrollView extends ScrollView {
    public NeverOverScrollView(Context context) {
        super(context);
        initView();
    }

    public NeverOverScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public NeverOverScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NeverOverScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        setOverScrollMode(OVER_SCROLL_NEVER);
//        try {
//            Reflect reflect = Reflect.on(this);
//            reflect.set("mOverflingDistance", 0);
//            reflect.set("mOverscrollDistance", 0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}