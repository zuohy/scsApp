package cn.longmaster.ihmonitor.core.app;

/**
 * 常量定义类
 */

public class AppConstant {
    //duws服务器校验key
    public static final String DUWS_APP_KEY = "doctor_dtws_2015";

    /**
     * 账号类型
     */
    public final static class UserAccountType {
        // 0：未定义 1：邮箱 2：手机号 3：用户自定义  4：第三方账号：QQ
        // 5：第三方账号：新浪微博 6：来宾用户 7：39通行证（此时密码传空）
        public static final byte ACCOUNT_PHONE_NUMBER = 2;
        public static final byte ACCOUNT_3RD_QQ = 4;
        public static final byte ACCOUNT_3RD_WEIBO = 5;
        public static final byte ACCOUNT_39 = 7;

    }

    /**
     * 视频用户类型 0、患者 1、首诊医生 2、管理员 3、上级专家 4、医生助理 5、会议人员 6、MDT医生 7、MDT患者家属
     * 视频诊室专用
     */
    public static final class UserType {
        public static final int USER_TYPE_PATIENT = 0;//患者
        public static final int USER_TYPE_ATTENDING_DOCTOR = 1;//申请医师(首诊医生)
        public static final int USER_TYPE_ADMINISTRATOR = 2;//管理员
        public static final int USER_TYPE_SUPERIOR_DOCTOR = 3;//会诊医师(上级专家)
        public static final int USER_TYPE_ASSISTANT_DOCTOR = 4;//医生助理
        public static final int USER_TYPE_CONFERENCE_STAFF = 5;//会议人员
        public static final int USER_TYPE_MDT_DOCTOR = 6;//MDT医生
        public static final int USER_TYPE_MDT_PATIENT = 7;//MDT患者家属
    }

    //用户在线状态
    public final static class OnlineState {
        public static final byte ONLINE_STATE_OFFLINE = 0;
        public static final byte ONLINE_STATE_ONLINE = 1;
        public static final byte ONLINE_STATE_KICKOFF = 2;
        public static final byte ONLINE_STATE_UNREG = 3;
        public static final byte ONLINE_STATE_FORBIDDEN = 4;
    }

    /**
     * 排班服务类型
     */
    public static final class ServiceType {
        public static final int SERVICE_TYPE_NOT_SELECT = 0;//没选择
        public static final int SERVICE_TYPE_REMOTE_CONSULT = 101001;//远程会诊
        public static final int SERVICE_TYPE_MEDICAL_ADVICE = 101002;//医学咨询、远程咨询
        public static final int SERVICE_TYPE_RETURN_CONSULT = 101003;//远程会诊复诊
        public static final int SERVICE_TYPE_RETURN_ADVICE = 101004;//远程咨询复诊
        public static final int SERVICE_TYPE_REMOTE_OUTPATIENT = 101005;//远程门诊
        public static final int SERVICE_TYPE_REMOTE_WARDS = 101006;//远程查房
        public static final int SERVICE_TYPE_IMAGE_CONSULT = 102001;//远程影像会诊
    }

    public static final class SignalState {
        public static final int SIGNAL_BAD = 1;//信号 差
        public static final int SIGNAL_GENERAL = 2;//信号 一般
        public static final int SIGNAL_GOOD = 3;//信号 好
    }

    public static final class RoomState {
        public static final int STATE_DEFAULT = 0;//默认
        public static final int STATE_BEING_VIDEO = 2;//正在视频
        public static final int STATE_BEING_VOICE = 3;//正在音频
    }
}
