package com.project.common_basic.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.project.common_basic.exception.NearLogger;
import com.project.common_basic.manager.SpManager;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import timber.log.Timber;

/**
 * 此工具类包含获取apk版本号和build号以及渠道号
 * <p>
 * Created by yamlee on 15/8/6.
 *
 * @author yamlee
 */
@SuppressWarnings("HardCodedStringLiteral")
public class ApkUtil {

    public static final String IS_FIRST_LAUNCH = "is_first_launch";

    /**
     * 判断当前渠道包是否显示首发字样
     *
     * @param context application
     * @return
     */
    public static boolean isFirstPublish(Context context) {
        AssertUtil.checkNotNull(context, "context can not be null");
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getBoolean("FIRST_LAUNCHER");
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 设置程序非第一次安装启动
     *
     * @param context
     */
    public static void setAlreadyInstalled(Context context) {
        AssertUtil.checkNotNull(context, "context can not be null");
        SpManager spManager = new SpManager(context);
        spManager.save(IS_FIRST_LAUNCH, false);
    }

    /**
     * 获取程序包所属渠道信息
     */
    public static String getChannel(Context context) {
        String channel = null;
        String sourceDir = context.getApplicationInfo().sourceDir;
        final String start_flag = "META-INF/channel_";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.contains(start_flag)) {
                    channel = entryName.replace(start_flag, "");
                    break;
                }
            }
        } catch (IOException e) {
            NearLogger.log(e);
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (TextUtils.isEmpty(channel)) {
            channel = getManifestChannel(context);
        }
        if (TextUtils.isEmpty(channel)) {
            //默认好近渠道
            channel = "haojin";
        }
        return channel;
    }

    private static String getManifestChannel(Context context) {
        AssertUtil.checkNotNull(context, "context can not be null");
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            if (bundle != null) {
                return bundle.getString("UMENG_CHANNEL", "");
            } else {
                return "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    /**
     * 获取版本名称
     *
     * @return 当前应用的版本号
     */
    public static String getVersionName(Context context) {
        AssertUtil.checkNotNull(context, "context can not be null");
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1.0";
    }

    /**
     * 获取app版本号也可称为build号
     */
    public static int getVersionCode(Context context) {
        AssertUtil.checkNotNull(context, "context can not be null");
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取应用名称
     *
     * @param context 上下文
     * @return
     */
    public static String getApplicationName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            return packageManager.getApplicationLabel(applicationInfo).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isAppStarted(Context context) {
        boolean isRunning = false;
        if(context == null) {
            Timber.e("context is %s, please conform.", context);
            return isRunning;
        }
        String pkgName = context.getPackageName();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if(Build.VERSION.SDK_INT >= 23) {
            List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();
            for(ActivityManager.AppTask appTask : appTasks) {
                ActivityManager.RecentTaskInfo recentTaskInfo = appTask.getTaskInfo();
                ComponentName topActivity = recentTaskInfo.topActivity;
                if(topActivity != null && topActivity.getPackageName().equals(pkgName)) {
                    isRunning = true;
                    break;
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(100);
            for(ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfos) {
                ComponentName topActivity = runningTaskInfo.topActivity;
                if(topActivity != null && topActivity.getPackageName().equals(pkgName)) {
                    isRunning = true;
                    break;
                }
            }
        }
        return isRunning;
    }
    public static String getMetaValue(Context context, String metaKey) {
        AssertUtil.checkNotNull(context, "context can not be null");
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            if (bundle != null) {
                return bundle.getString(metaKey, "");
            } else {
                return "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }
}
