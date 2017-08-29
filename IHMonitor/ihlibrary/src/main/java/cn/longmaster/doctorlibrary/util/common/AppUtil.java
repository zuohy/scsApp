package cn.longmaster.doctorlibrary.util.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;

import cn.longmaster.doctorlibrary.util.UtilStatus;

/**
 * 获取 应用App相关信息
 */
@SuppressWarnings("ALL")
public class AppUtil {

    /**
     * 获取应用versionCode
     *
     * @return
     */
    public static int getAppVersionCode() {
        int versionCode = 0;
        try {
            PackageInfo pInfo = UtilStatus.getApplication().getPackageManager().getPackageInfo(UtilStatus.getApplication().getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * versionName
     *
     * @return
     */
    public static String getAppVersionName() {
        String versionName = "";
        try {
            PackageInfo pInfo = UtilStatus.getApplication().getPackageManager().getPackageInfo(UtilStatus.getApplication().getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取渠道号
     *
     * @return 渠道号
     */
    public static int getChannelCode() {
        String code = getMetaData("channel");
        if (code != null) {
            try {
                return Integer.valueOf(code);
            } catch (NumberFormatException e) {
            }
        }
        return -1;
    }

    /**
     * 获取META_DATA值
     *
     * @param key 键
     * @return 当存在key时返回数值否则返回null
     */
    private static String getMetaData(String key) {
        try {
            ApplicationInfo ai = UtilStatus.getApplication().getPackageManager().getApplicationInfo(UtilStatus.getApplication().getPackageName(), PackageManager.GET_META_DATA);
            if (ai.metaData != null) {
                Object value = ai.metaData.get(key);
                if (value != null) {
                    return value.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 模拟Home键
     *
     * @param context 上下文
     */
    public static void imitateHome(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            context.startActivity(intent);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || TextUtils.isEmpty(packageName))
            return false;
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static boolean checkApkExist(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);
        if (list.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 通过文件路径安装程序
     *
     * @param context  上下文
     * @param filaPath 文件路径
     * @return
     */
    public static boolean install(Context context, String filaPath) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(new File(filaPath)), "application/vnd.android.package-archive");
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 返回屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getDisplayWidthPixels(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 返回屏幕高度
     *
     * @param context
     * @return
     */
    public static int getDisplayHeightPixels(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * Dip转成对应Px值
     *
     * @param context 上下文
     * @param dip
     * @return
     */
    public static int dipTopx(Context context, float dip) {
        float s = context.getResources().getDisplayMetrics().density;
        return (int) (dip * s + 0.5f);
    }

    /**
     * 隐藏软键盘
     *
     * @param activity
     * @return
     */
    public static final boolean hideSoftPad(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            return ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return false;
    }

    /**
     * 显示软键盘
     *
     * @param activity
     * @param view
     */
    public static final void showSoftPad(Activity activity, View view) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0); // 显示软键盘
    }

    /**
     * 判断程序是否前段运行
     *
     * @param context     上下文
     * @param packageName 待判断程序包名
     * @return
     */
    public static boolean isAppRunningForeground(Context context, String packageName) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        List<RunningTaskInfo> mRunningTaskInfoList = mActivityManager.getRunningTasks(1);
        if (mRunningTaskInfoList == null || mRunningTaskInfoList.isEmpty())
            return false;
        RunningTaskInfo mRunningTaskInfo = mRunningTaskInfoList.get(0);
        String strPackageName = mRunningTaskInfo.topActivity.getPackageName();
        if (strPackageName != null && strPackageName.equals(packageName)) {
            return true;
        } else {
            return false;
        }
    }


    private static ApplicationInfo getMetaDataApplicationInfo(Context context) throws NameNotFoundException {
        return context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
    }

    /**
     * 获取Application的String Meta-Data
     *
     * @param context
     * @param key
     * @param def
     * @return
     */
    public static String getApplicationStringMetaData(Context context, String key, String def) {
        try {
            String data = getMetaDataApplicationInfo(context).metaData.getString(key);

            if (!TextUtils.isEmpty(data)) {
                return data;
            } else {
                return def;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return def;
    }

    /**
     * 获取Application的Int Meta-Data
     *
     * @param context
     * @param key
     * @param def
     * @return
     */
    public static int getApplicationIntMetaData(Context context, String key, int def) {
        try {
            return getMetaDataApplicationInfo(context).metaData.getInt(key, def);

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return def;
    }

    /**
     * 获取Application的Boolean Meta-Data
     *
     * @param context
     * @param key
     * @param def
     * @return
     */
    public static boolean getApplicationBooleanMetaData(Context context, String key, boolean def) {
        try {
            return getMetaDataApplicationInfo(context).metaData.getBoolean(key, def);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return def;
    }

    /**
     * 获取版本名
     *
     * @param context
     * @return
     */
    public static String getApplicationVersionName(Context context) {
        try {
            PackageManager lPackageManager = context.getPackageManager();
            PackageInfo lPackageInfo = lPackageManager.getPackageInfo(context.getPackageName(), 0);
            return lPackageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0.0";
        }
    }

    /**
     * 根据域名获取ip地址
     *
     * @param name 域名
     * @return ip地址
     */
    public static String getIP(String name) {
        InetAddress address = null;
        try {
            address = InetAddress.getByName(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address == null ? "getIP_FAILED" : address.getHostAddress().toString();
    }

    /**
     * 输出日志文件
     *
     * @param logContent 日志内容
     */
    public static void outPutLogFile(final String logDir, final String userId, final String logContent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String logFilePath = logDir;
                if (!logDir.endsWith(File.separator))
                    logFilePath += File.separator;
                logFilePath += "net.log";
                StringBuffer content = new StringBuffer("------" + DateUtil.millisecondToStandardDate(System.currentTimeMillis()) + "------\n");
                content = content.append("userId:" + (TextUtils.isEmpty(userId) ? "-1" : userId)).append("\n")
                        .append("phoneType:" + OsUtil.getPhoneType()).append("\n")
                        .append("phoneOS:Android").append("\n")
                        .append("phoneOSVersion:" + OsUtil.getPhoneOSVersion()).append("\n")
                        .append("romVersion:" + OsUtil.getRomVersion()).append("\n")
                        .append("userFrom:" + AppUtil.getChannelCode()).append("\n")
                        .append("MAC:" + OsUtil.getMacAddress()).append("\n")
                        .append("IMEI:" + OsUtil.getIMEI()).append("\n")
                        .append("clientVersion:" + UtilStatus.getClientVersion()).append("\n")
                        .append("errorContent:" + logContent).append("\n")
                        .append("ping dws:" + ping("dws.doctor.langma.cn")).append("\n")
                        .append("ping baidu:" + ping("www.baidu.com")).append("\n");
                FileUtil.writeFile(logFilePath, Collections.singletonList(content.toString()), true);
            }
        }).start();
    }

    public static String ping(String str) {
        String result = "";
        Process p;
        try {
            //ping -c 3 -w 100  中  ，-c 是指ping的次数 3是指ping 3次 ，-w 100  以秒为单位指定超时间隔，是指超时时间为100秒
            p = Runtime.getRuntime().exec("ping -c 1 -w 10 " + str);
            int status = p.waitFor();

            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            if (status == 0) {
                result = "success:" + buffer.toString();
            } else {
                result = "failed!";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
