package cn.longmaster.ihmonitor.core.app;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * SharePreference管理类
 */
public class AppPreference {
    public static final String TAG = AppPreference.class.getSimpleName();

    public static final String SHARED_PREFERENCE_FILE_NAME = AppApplication.getInstance().getPackageName() + ".sharedpreference";

    /**
     * 服务器最新版本号key
     */
    public static final String KEY_SERVER_LASTEST_VERSION = "key_server_lastest_version";
    /**
     * 服务器最新版本新版特性key
     */
    public static final String KEY_SERVER_LASTEST_VERSION_FEATURE = "key_server_lastest_version_feature";
    /**
     * 服务器最新版本apk文件大小key
     */
    public static final String KEY_SERVER_LASTEST_VERSION_APK_SIZE = "key_server_lastest_version_apk_size";
    /**
     * 服务器最新版本apk文件MD5key
     */
    public static final String KEY_SERVER_LASTEST_VERSION_MD5 = "key_server_lastest_version_md5";
    /**
     * 服务器提示版本key
     */
    public static final String KEY_SERVER_ALERT_VERSION = "key_server_alert_version";
    /**
     * 服务器限制最低版本key
     */
    public static final String KEY_SERVER_LIMIT_VERSION = "key_server_limit_version";

    public static final String KEY_CLIENT_VERSION = TAG + ".KEY_CLIENT_VERSION";
    /**
     * 首次进入软件标志
     */
    public static final String KEY_GUIDE_PAGE_CURRENT_VERSION = "key_guide_page_current_version";
    /**
     * 发送激活包时间
     */
    public static final String KEY_SEND_ACTION_DT = "key_send_action_dt";
    /**
     * 是否请求预约信息成功
     */
    public static final String KEY_FLAG_REQUEST_APPOINTMENT_SUCCESS = "key_flag_request_appointment_success";
    /**
     * 用户后台被踢下线
     */
    public static final String FLAG_BACKGROUND_KICKOFF = "flag_background_kickoff";
    /**
     * 适配xml文件下载token
     */
    public static final String KEY_AMX_TOKEN = "key_amx_token";
    /**
     * 启动页图片token
     */
    public static final String TOKEN_APP_START_PICTURE = "token_app_start_picture";
    /**
     * 用户正在发起的就诊类型
     */
    public static final String KEY_USER_APPOINT_GOING_TYPE = "key_user_appoint_going_type";
    /**
     * 首次添加排班
     */
    public static final String KEY_FIRST_ADD_SCHEDULE = "key_first_add_schedule";
    /**
     * 首次修改排班
     */
    public static final String KEY_FIRST_CHANGE_SCHEDULE = "key_first_change_schedule";
    /**
     * 是否首次出具医嘱
     */
    public static final String KEY_IS_FIRST_GIVE_ADVICE = "key_is_first_give_advice";
    /**
     * 是否首次我的账户
     */
    public static final String KEY_IS_FIRST_MY_ACCOUNT = "key_is_first_my_account";
    /**
     * 首次进入视频诊室
     */
    public static final String KEY_IS_FIRST_ENTER_VIDEO_ROOM = "key_is_first_enter_video_room";
    /**
     * 有新消息,"消息中心"红点提示
     */
    public static final String KEY_MESSAGE_CENTER_NEW_MESSAGE = "key_message_center_new_message";
    public static final String KEY_IS_SHOW_NEWBIE_GUIDE = "key_is_show_newbie_guide";

    /**
     * 服务器地址
     */
    public static final String KEY_SERVICE_ADDRESS = "key_service_address";
    /**
     *用户鉴权信息
     */
    public static final String KEY_AUTHENTICATION_AUTH = "key_authentication_auth";
    /**
     *用户鉴权信息有效时长
     */
    public static final String KEY_AUTHENTICATION_OUT_TIME = "key_authentication_out_time";

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
