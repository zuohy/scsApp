package cn.longmaster.doctorlibrary.utils.log;

import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 日志打印类，大医生应用所有日志必须采用本类来进行日志打印，以方便正式打包时对日志的控制
 * Created by YY on 2015/7/2.
 */
public class Logger {
    private static final String TAG_CAMERA = "tag_camera";//相机
    private static final String TAG_COMMON = "tag_common";//通用
    private static final String TAG_HTTP = "tag_http";//web服务器请求

    public static final int COMMON = 0;
    public static final int HTTP = 1;
    public static final int CAMERA = 2;

    @IntDef({COMMON, HTTP, CAMERA})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoggerTag {
    }

    public static void log(@LoggerTag int loggerTag, String msg) {
        Log.d(getTag(loggerTag), msg);
    }

    public static void logI(@LoggerTag int loggerTag, String msg) {
        Log.i(getTag(loggerTag), msg);
    }

    public static void logW(@LoggerTag int loggerTag, String msg) {
        Log.w(getTag(loggerTag), msg);
    }

    public static void logW(@LoggerTag int loggerTag, String msg, Throwable throwable) {
        Log.w(getTag(loggerTag), msg, throwable);
    }

    public static void logE(@LoggerTag int loggerTag, String msg) {
        Log.e(getTag(loggerTag), msg);
    }

    public static void logE(@LoggerTag int loggerTag, String msg, Throwable throwable) {
        Log.e(getTag(loggerTag), msg, throwable);
    }

    private static String getTag(@LoggerTag int loggerTag) {
        String tag = "";
        switch (loggerTag) {
            case COMMON:
                tag = TAG_COMMON;
                break;

            case HTTP:
                tag = TAG_HTTP;
                break;

            case CAMERA:
                tag = TAG_CAMERA;
                break;
        }
        return tag;
    }
}
