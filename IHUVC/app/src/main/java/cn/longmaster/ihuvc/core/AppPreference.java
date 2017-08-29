package cn.longmaster.ihuvc.core;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * SharePreference管理类
 * Created by yangyong on 2016/4/18.
 */
public class AppPreference {
    public static final String TAG = AppPreference.class.getSimpleName();

    public static final String SHARED_PREFERENCE_FILE_NAME = AppApplication.getInstance().getPackageName() + ".sharedpreference";
    /**
     * 服务器地址
     */
    public static final String KEY_SERVICE_ADDRESS = "key_service_address";

    /**
     * 服务器最新版本号key
     */
    public static final String KEY_SERVER_LASTEST_VERSION = "key_server_lastest_version";

    /**
     * 服务器限制最低版本key
     */
    public static final String KEY_SERVER_LIMIT_VERSION = "key_server_limit_version";
    /**
     * 服务器最新版本apk文件大小key
     */
    public static final String KEY_SERVER_LASTEST_VERSION_APK_SIZE = "key_server_lastest_version_apk_size";

    /**
     * 服务器最新版本新版特性key
     */
    public static final String KEY_SERVER_LASTEST_VERSION_FEATURE = "key_server_lastest_version_feature";

    /**
     * 服务器最新版本apk文件MD5key
     */
    public static final String KEY_SERVER_LASTEST_VERSION_MD5 = "key_server_lastest_version_md5";


    private static SharedPreferences getPreferences() {
        return AppApplication.getInstance().getSharedPreferences(SHARED_PREFERENCE_FILE_NAME, Activity.MODE_PRIVATE);
    }

    /**
     * 清除所有share配置
     */
    public static void clearAll() {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.clear();
        editor.commit();
    }

    public static void removeKey(String key) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * 设置整型值
     *
     * @param key   键
     * @param value 值
     */
    public static void setIntValue(String key, int value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 获取整型值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 整型值
     */
    public static int getIntValue(String key, int defaultValue) {
        return getPreferences().getInt(key, defaultValue);
    }

    /**
     * 设置字符型值
     *
     * @param key   键
     * @param value 值
     */
    public static void setStringValue(String key, String value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 获取字符型值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 字符型值
     */
    public static String getStringValue(String key, String defaultValue) {
        return getPreferences().getString(key, defaultValue);
    }

    /**
     * 设置布尔型值
     *
     * @param key   键
     * @param value 值
     */
    public static void setBooleanValue(String key, boolean value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 获取布尔型值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 布尔型值
     */
    public static boolean getBooleanValue(String key, boolean defaultValue) {
        return getPreferences().getBoolean(key, defaultValue);
    }

    /**
     * 设置布浮点值
     *
     * @param key   键
     * @param value 值
     */
    public static void setFloatValue(String key, float value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * 获取浮点型值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 浮点型值
     */
    public static float getFloatValue(String key, float defaultValue) {
        return getPreferences().getFloat(key, defaultValue);
    }

    /**
     * 设置长整型值
     *
     * @param key   键
     * @param value 值
     */
    public static void setLongValue(String key, long value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 获取长整型值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 长整型值
     */
    public static long getLongValue(String key, long defaultValue) {
        return getPreferences().getLong(key, defaultValue);
    }
}
