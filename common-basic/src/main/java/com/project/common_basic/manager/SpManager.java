package com.project.common_basic.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Shared Preference manager
 * <p>
 *
 * @author chenfeiyue
 * @author yamlee
 */
public class SpManager {
    private volatile static SpManager saver;
    private final static int MODE = Context.MODE_WORLD_READABLE
            | Context.MODE_WORLD_WRITEABLE | Context.MODE_MULTI_PROCESS;
    private SharedPreferences sp;
    private boolean isAsyncCommitMode = true;
    @SuppressWarnings("HardCodedStringLiteral")
    private static final String SHARED_PRE_NAME = EssentialSpKey.STRING_SP_NAME;

    public SharedPreferences getSharedPreferences() {
        return sp;
    }

    /**
     * This method is to get SpUtil instance,now is deprecated,
     * and advice  use dagger to inject spUtil instance
     *
     * @param context context
     * @return SpManager
     */
    public static SpManager getInstance(Context context) {
        if (saver == null) {
            synchronized (SpManager.class) {
                if (saver == null) {
                    saver = new SpManager(context);
                }
            }
        }
        return saver;
    }

    public SpManager(Context context) {
        this(context, SHARED_PRE_NAME);
    }

    public SpManager(Context context, String spName) {
        sp = context.getApplicationContext().getSharedPreferences(spName, MODE);
    }

    /**
     * 设置提交模式,如果参数传true即为异步模式,shared preference editor采用apply()方法提交数据
     * 否则editor采用commit()方法提交数据
     *
     * @param asyncCommit asyncCommit
     */
    public void setCommitMode(boolean asyncCommit) {
        isAsyncCommitMode = asyncCommit;
    }

    @SuppressLint("CommitPrefEdits")
    public void save(String key, String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        if (isAsyncCommitMode) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    @SuppressLint("CommitPrefEdits")
    public void save(String key, long value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        if (isAsyncCommitMode) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    @SuppressLint("CommitPrefEdits")
    public void save(String key, int value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        if (isAsyncCommitMode) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    @SuppressLint("CommitPrefEdits")
    public void save(String key, float value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(key, value);
        if (isAsyncCommitMode) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    @SuppressLint("CommitPrefEdits")
    public void save(String key, boolean value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        if (isAsyncCommitMode) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = sp.edit();
        if (sp.contains(key)) {
            editor.remove(key);
            if (isAsyncCommitMode) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public boolean hasValue(String key) {
        return sp.contains(key);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return sp.getLong(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return sp.getFloat(key, defValue);
    }

}
