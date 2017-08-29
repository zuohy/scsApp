package cn.longmaster.doctorlibrary.util.log;

import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 日志打印类，大医生应用所有日志必须采用本类来进行日志打印，以方便正式打包时对日志的控制
 */
public class Logger {
    private static final String TAG_USER = "tag_user";//用户信息
    private static final String TAG_DOCTOR = "tag_doctor";//医生
    private static final String TAG_APPOINTMENT = "tag_appointment";//预约
    private static final String TAG_ROOM = "tag_room";//诊室
    private static final String TAG_COMMON = "tag_common";//通用
    private static final String TAG_HTTP = "tag_http";//web服务器请求

    public static final int USER = 0;
    public static final int DOCTOR = 1;
    public static final int APPOINTMENT = 2;
    public static final int ROOM = 3;
    public static final int COMMON = 4;
    public static final int HTTP = 5;

    @IntDef({USER, DOCTOR, APPOINTMENT, ROOM, COMMON, HTTP})
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
            case USER:
                tag = TAG_USER;
                break;

            case DOCTOR:
                tag = TAG_DOCTOR;
                break;

            case APPOINTMENT:
                tag = TAG_APPOINTMENT;
                break;

            case ROOM:
                tag = TAG_ROOM;
                break;

            case COMMON:
                tag = TAG_COMMON;
                break;

            case HTTP:
                tag = TAG_HTTP;
                break;
        }
        return tag;
    }
}
