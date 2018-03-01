package com.project.common_basic.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.project.common_basic.R;


/**
 * 先判断是否设定了mMaxHeight，如果设定了mMaxHeight，则直接使用mMaxHeight的值，
 * 如果没有设定mMaxHeight，则判断是否设定了mMaxRatio，如果设定了mMaxRatio的值
 * 则使用此值与屏幕高度的乘积作为最高高度
 *
 * @author Rick.Wang
 */
public class MaxHeightView extends RelativeLayout {

    private static final float DEFAULT_MAX_RATIO = 0.4f;
    private static final float DEFAULT_MAX_HEIGHT = 0f;

    private Context mContext;
    private float mMaxHeight = DEFAULT_MAX_HEIGHT;//优先级高
    private float mMaxRatio = DEFAULT_MAX_RATIO;//优先级低

    public MaxHeightView(Context context) {
        this(context, null);
    }

    public MaxHeightView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaxHeightView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.maxHeightView);

        final int count = a.getIndexCount();
        for (int i = 0; i < count; ++i) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.maxHeightView_maxHeightRatio) {
                mMaxRatio = a.getFloat(attr, DEFAULT_MAX_RATIO);

            } else if (attr == R.styleable.maxHeightView_maxHeightDimen) {
                mMaxHeight = a.getDimension(attr, DEFAULT_MAX_HEIGHT);

            }
        }
        a.recycle();

        if (mMaxHeight <= 0) {
            mMaxHeight = mMaxRatio * (float) getScreenHeight(mContext);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.EXACTLY) {
            heightSize = heightSize <= mMaxHeight ? heightSize : (int) mMaxHeight;
        }

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = heightSize <= mMaxHeight ? heightSize : (int) mMaxHeight;
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = heightSize <= mMaxHeight ? heightSize : (int) mMaxHeight;
        }

        int maxHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode);

        super.onMeasure(widthMeasureSpec, maxHeightMeasureSpec);
    }


    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }
}