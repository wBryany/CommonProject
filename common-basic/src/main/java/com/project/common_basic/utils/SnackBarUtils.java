package com.project.common_basic.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.project.common_basic.R;


/**
 * 底部提示
 * <p>
 *
 * @author zcZhang
 * @author yamlee
 */
public class SnackBarUtils {
    public static void showLongSnackBar(View view, String msg, String actionTitle) {

        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction(actionTitle, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
    }

    public static void showShortSnackBar(View view, String msg, String actionTitle) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
                .setAction(actionTitle, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
    }

    public static void showClickSnackBar(View view, String msg, String actionTitle,
                                         View.OnClickListener clickListener) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction(actionTitle, clickListener).show();
    }

    public static void showClickSnackBarAlways(View view, String msg, String actionTitle,
                                               View.OnClickListener clickListener) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction(actionTitle, clickListener).show();
    }

    public static void showShortSnackBar(View view, String msg) {
        showShortSnackBar(view, msg, view.getContext().getString(R.string.i_know_it));
    }
}
