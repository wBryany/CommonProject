package com.project.common_basic.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ListView;


/**
 * Created by chenfeiyue on 16/11/1.
 * NeverOverScrollListView
 */
public class NeverOverScrollListView extends ListView {
    public NeverOverScrollListView(Context context) {
        super(context);
        initView();
    }

    public NeverOverScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public NeverOverScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NeverOverScrollListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        setOverScrollMode(OVER_SCROLL_NEVER);
    }
}