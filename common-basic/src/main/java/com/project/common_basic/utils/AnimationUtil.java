package com.project.common_basic.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.project.common_basic.R;


/**
 * 动画工具类
 *
 * @author yamlee
 */
public class AnimationUtil {

    /**
     * 抖动效果
     *
     * @param context application context
     * @param view    进行动画的view
     */
    public static void shake(Context context, View view) {
        Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
        view.setAnimation(shake);
        view.startAnimation(shake);
    }

    /**
     * 淡入动画
     *
     * @param context application context
     * @param view    进行动画的view
     */
    public static void alphaIn(Context context, View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha_in);
        view.setAnimation(animation);
        view.startAnimation(animation);
    }

    /**
     * 顺时针旋转
     *
     * @param view  进行动画的view
     * @param angle 旋转的角度
     * @param time  一个动画的时间
     */
    public static void rotationClockWise(View view, float angle, long time) {
        view.animate()
                .rotation(angle)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(time)
                .start();
    }

    /**
     * 逆时针旋转
     *
     * @param view  进行动画的view
     * @param angle 逆时针旋转的角度
     * @param time  一个动画的时间
     */
    public static void rotationAnticlockwise(View view, float angle, long time) {
        view.animate()
                .rotation(angle)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(time)
                .start();
    }
}
