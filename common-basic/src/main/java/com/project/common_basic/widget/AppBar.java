package com.project.common_basic.widget;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project.common_basic.R;


/**
 * 通用头部导航栏
 *
 * @author yamlee
 */
public class AppBar extends FrameLayout {
    TextView tvNavigationHeaderLeft;
    ImageView ivNavigationHeaderLeft;
    TextView tvNavigationTitle;
    TextView tvNavigationHeaderRight;
    ImageView ivNavigationHeaderRight;
    RelativeLayout layoutNavigationHeader;
    ImageView ivTitleImage;
    RelativeLayout rlNavigationLeft;
    RelativeLayout rlNavigationRight;
    LinearLayout llTitle;
    ImageView ivBaseDivider;

    private Context mContext;

    public AppBar(Context mContext) {
        super(mContext);
        initView(mContext);
    }

    public AppBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AppBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.include_layout_header, this);
        tvNavigationHeaderLeft = (TextView) view.findViewById(R.id.tv_navigation_header_left);
        ivNavigationHeaderLeft = (ImageView) view.findViewById(R.id.iv_navigation_header_left);
        tvNavigationTitle = (TextView) view.findViewById(R.id.tv_naviation_title);
        tvNavigationHeaderRight = (TextView) view.findViewById(R.id.tv_navigation_header_right);
        ivNavigationHeaderRight = (ImageView) view.findViewById(R.id.iv_navigation_header_right);
        layoutNavigationHeader = (RelativeLayout) view.findViewById(R.id.layout_navigation_header);
        ivTitleImage = (ImageView) view.findViewById(R.id.iv_title_image);
        rlNavigationLeft = (RelativeLayout) view.findViewById(R.id.rl_navigation_left);
        rlNavigationRight = (RelativeLayout) view.findViewById(R.id.rl_navigation_right);
        llTitle = (LinearLayout) view.findViewById(R.id.ll_title);
        ivBaseDivider = (ImageView) view.findViewById(R.id.iv_base_divider);
    }

    public void setLeftNavigation(String leftTitle, final OnLeftClickListener onLeftClickListener) {
        tvNavigationHeaderLeft.setVisibility(View.VISIBLE);
        ivNavigationHeaderLeft.setVisibility(View.GONE);
        tvNavigationHeaderLeft.setText(leftTitle);
        rlNavigationLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onLeftClickListener.onClick(v);
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void setLeftNavigation(int imgResId, final OnLeftClickListener onLeftClickListener) throws
            NotFoundException {
        Drawable drawable = mContext.getResources().getDrawable(imgResId);
        if (null == drawable) //noinspection HardCodedStringLiteral
            throw new NotFoundException("the resource id not found in drawable");
        ivNavigationHeaderLeft.setVisibility(View.VISIBLE);
        tvNavigationHeaderLeft.setVisibility(View.GONE);
        ivNavigationHeaderLeft.setImageResource(imgResId);
        rlNavigationLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onLeftClickListener.onClick(v);
            }
        });
    }


    public void setRightNavigation(String rightTitle, final OnRightClickListener onRightClickListener) {
        tvNavigationHeaderRight.setVisibility(View.VISIBLE);
        tvNavigationHeaderRight.setText(rightTitle);
        rlNavigationRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onRightClickListener.onClick(v);
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void setRightNavigation(int imgResId, final OnRightClickListener onRightClickListener) throws
            NotFoundException {
        Drawable drawable = mContext.getResources().getDrawable(imgResId);
        if (null == drawable) //noinspection HardCodedStringLiteral
            throw new NotFoundException("the resource id not found in drawable");
        ivNavigationHeaderRight.setVisibility(View.VISIBLE);
        ivNavigationHeaderRight.setImageResource(imgResId);
        rlNavigationRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onRightClickListener.onClick(v);
            }
        });
    }

    public void setRightText(String text) {
        if (text != null) {
            tvNavigationHeaderRight.setVisibility(View.VISIBLE);
            tvNavigationHeaderRight.setText(text);
        }
    }

    public void setRightClickListener(final OnRightClickListener onRightClickListener) {
        rlNavigationRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onRightClickListener.onClick(v);
            }
        });
    }

    public void setShowLeft(boolean showLeft) {
        if (showLeft) {
            tvNavigationHeaderLeft.setVisibility(View.VISIBLE);
            ivNavigationHeaderLeft.setVisibility(View.VISIBLE);
        } else {
            tvNavigationHeaderLeft.setVisibility(View.GONE);
            ivNavigationHeaderLeft.setVisibility(View.GONE);
        }
    }

    public void setShowRight(boolean showRight) {
        if (showRight) {
            tvNavigationHeaderRight.setVisibility(View.VISIBLE);
            ivNavigationHeaderRight.setVisibility(View.VISIBLE);
        } else {
            tvNavigationHeaderRight.setVisibility(View.GONE);
            ivNavigationHeaderRight.setVisibility(View.GONE);
        }
    }

    public void setTitle(String title) {
        llTitle.setVisibility(View.VISIBLE);
        tvNavigationTitle.setText(title);
    }

    public void setTitle(int titleId) {
        llTitle.setVisibility(View.VISIBLE);
        tvNavigationTitle.setText(titleId);
        tvNavigationTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //default no implement
            }
        });

    }

    public void setTitleColor(int colorId) {
        tvNavigationTitle.setTextColor(colorId);
    }

    public void setTitle(String title, final OnTitleClickListener titleClickListener) {
        llTitle.setVisibility(View.VISIBLE);
        tvNavigationTitle.setText(title);
        llTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                titleClickListener.onClick(v);
            }
        });
    }

    public void setTitleHint(String msg) {
        tvNavigationTitle.setHint(msg);
    }

    public void setBaseDividerVisible(boolean visible) {
        ivBaseDivider.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setShowTitleImage(boolean isShow) {
        if (isShow) {
            ivTitleImage.setVisibility(View.VISIBLE);
        } else {
            ivTitleImage.setVisibility(View.GONE);
        }
    }

    public void setBackgroundResourceX(int resId) {
        layoutNavigationHeader.setBackgroundResource(resId);
    }

    public View getRightNavigationBtn() {
        return rlNavigationRight;
    }

    public interface OnLeftClickListener {
        void onClick(View v);
    }

    public interface OnRightClickListener {
        void onClick(View v);
    }

    public interface OnTitleClickListener {
        void onClick(View v);
    }

}
