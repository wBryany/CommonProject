package com.project.common_basic.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.project.common_basic.exception.NearLogger;

/**
 * 文本输入工具类
 *
 * @author yamlee
 */
public class TextEditUtil {

    /**
     * 设置EditText能输入小数的金额
     */
    public static void setPricePoint(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                try {
                    isDoubleDecimal(s, editText);
                } catch (IndexOutOfBoundsException e) {
                    NearLogger.log(e);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

    }

    /**
     * 判断当前EditText输入是否是两位小数
     */
    public static void isDoubleDecimal(CharSequence s, EditText editText) {
        String input = s.toString();
        if (input.contains(".")) {
            if (s.length() - 1 - input.indexOf(".") > 2) {
                s = input.subSequence(0,
                        input.indexOf(".") + 3);
                editText.setText(s);
                editText.setSelection(s.length());
            }
        }
        if (input.trim().equals(".")) {
            s = "0" + s;
            editText.setText(s);
            editText.setSelection(2);
        }

        if (input.startsWith("0")
                && input.trim().length() > 1) {
            if (!input.substring(1, 2).equals(".")) {
                editText.setText(s.subSequence(0, 1));
                editText.setSelection(1);
                return;
            }
        }
    }

}
