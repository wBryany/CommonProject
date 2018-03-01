package com.project.common_basic.utils;

import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

/**
 * 给TextView设置不同的spannable属性
 *
 * @author yamlee
 */
public class SpannableStrUtil {

    /**
     * ForegroundColorSpan 文本颜色
     *
     * @param tv
     * @param str
     * @param color
     */
    public static void setForegroundColorSpan(TextView tv, String str, int color) {
        SpannableString spanString = new SpannableString(str);
        ForegroundColorSpan span = new ForegroundColorSpan(color);
        spanString.setSpan(span, 0, str.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(spanString);
    }

    /**
     * BackgroundColorSpan 文本背景色
     *
     * @param tv
     * @param str
     * @param color
     */
    public static void setBackgroundColorSpan(TextView tv, String str, int color) {
        SpannableString spanString = new SpannableString(str);
        BackgroundColorSpan span = new BackgroundColorSpan(color);
        spanString.setSpan(span, 0, str.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(spanString);
    }


    /**
     * 设置字体样式：粗体、斜体等
     *
     * @param tv       需要设置的TextView
     * @param str      文本内容
     * @param typeface 字体样式,如Typeface.BOLD,Typeface.ITALIC等
     */
    public static void setStyleSpan(TextView tv, String str, int typeface) {
        SpannableString spanString = new SpannableString(str);
        StyleSpan span = new StyleSpan(typeface);
        spanString.setSpan(span, 0, str.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(spanString);
    }

    /**
     * RelativeSizeSpan  相对大小
     *
     * @param tv
     * @param str
     */
    public static void setRelativeFontSpan(TextView tv, String str) {
        SpannableString spanString = new SpannableString(str);
        spanString.setSpan(new RelativeSizeSpan(2.5f), 0, str.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.append(spanString);
    }

    /**
     * TypefaceSpan   文本字体
     *
     * @param tv
     * @param str
     * @param typefaceSpan
     */
    public static void setTypefaceSpan(TextView tv, String str, String typefaceSpan) {
        SpannableString spanString = new SpannableString(str);
        spanString.setSpan(new TypefaceSpan(typefaceSpan), 0, str.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(spanString);
    }


    /**
     * URLSpan 文本超链接
     *
     * @param tv
     * @param str
     * @param url
     */
    public static void addUrlSpan(TextView tv, String str, String url) {
        SpannableString spanString = new SpannableString(str);
        URLSpan span = new URLSpan(url);
        spanString.setSpan(span, 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(spanString);
    }


    /**
     * ImageSpan  图片
     *
     * @param tv
     * @param str
     * @param drawable
     */
    public static void addImageSpan(TextView tv, String str, Drawable drawable) {
        SpannableString spanString = new SpannableString(str);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        spanString.setSpan(span, 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(spanString);
    }


    /**
     * UnderlineSpan  下划线
     *
     * @param tv
     * @param str
     */
    public static void addUnderLineSpan(TextView tv, String str) {
        SpannableString spanString = new SpannableString(str);
        UnderlineSpan span = new UnderlineSpan();
        spanString.setSpan(span, 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(spanString);
    }


    /**
     * StrikethroughSpan 删除线
     *
     * @param tv
     * @param str
     */
    public static void addStrikeSpan(TextView tv, String str) {
        SpannableString spanString = new SpannableString(str);
        StrikethroughSpan span = new StrikethroughSpan();
        spanString.setSpan(span, 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(spanString);
    }

    /**
     * TextAppearanceSpan 文本外貌（包括字体、大小、样式和颜色）
     *
     * @param tv
     * @param str
     * @param textAppearanceSpan
     */
    public static void setTextAppearanceSpan(TextView tv, String str,
                                             TextAppearanceSpan textAppearanceSpan) {
        SpannableString spanString = new SpannableString(str);
        spanString.setSpan(textAppearanceSpan, 0, str.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(spanString);
    }

}