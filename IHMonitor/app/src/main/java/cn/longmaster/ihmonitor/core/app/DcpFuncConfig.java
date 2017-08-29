package cn.longmaster.ihmonitor.core.app;

/**
 * dcp方法声明类
 * Created by yangyong on 2015/7/17.
 */
public class DcpFuncConfig {
    public static final String FUN_NAME_SETGKDEMAIN = "setGKDomain";

    public static final String FUN_NAME_SETPESINFO = "setPesInfo";
    public static final String FUN_NAME_ON_SETPESINFO = "onPesSessionConnected";
    public static final int ACTION_TYPE_SETPESINFO = 1;

    public static final int ACTION_TYPE_REGACOUNT = 2;
    public static final int ACTION_TYPE_REG_CODE = 3;
    public static final int ACTION_TYPE_CHECK_VERIFY_CODE = 4;
    public static final int ACTION_TYPE_ACTIVEACOUNT = 5;

    public static final String FUN_NAME_LOGIN = "login";
    public static final String FUN_NAME_ON_LOGIN = "onLogin";
    public static final int ACTION_TYPE_LOGIN = 6;
    public static final int ACTION_TYPE_QUERYACCOUNT = 7;

    public static final String FUN_NAME_SEND_ACTION = "sendAction";
    public static final String FUN_NAME_ON_SEND_ACTION = "onSendAction";

    public static final String FUN_NAME_LOGOUT = "logout";
    public static final String FUN_NAME_DISCONNECT = "disconnect";
    public static final String FUN_NAME_ONKICKOFF = "onKickOff";
    public static final String FUN_NAME_KEEPALIVE = "keepAlive";
    public static final String FUN_NAME_ONOFFLINE = "onOffline";

    //聊天室相关方法定义
    public static final String FUN_NAME_SET_PCC_PARAM = "setPccParam";
    public static final String FUN_NAME_JOIN_VIDEO_ROOM = "joinVideoRoom";
    public static final String FUN_NAME_ON_SELF_JOIN_VIDEO_ROOM = "onSelfJoinVideoRoom";
    public static final String FUN_NAME_ON_MEMBER_JOIN_VIDEOA_ROOM = "onMemberJoinVideoRoom";

    public static final String FUN_NAME_VIDEO_CHANGE_SEAT = "changeSeatState";
    public static final String FUN_NAME_ON_SELF_SEAT_CHANGE = "onSelfChangeSeatState";

    public static final String FUN_NAME_EXIT_VIDEO_ROOM = "exitVideoRoom";
    public static final String FUN_NAME_ON_SELF_EXIT_VIDEO_ROOM = "onSelfExitVideoRoom";
    public static final String FUN_NAME_ON_MEMBER_EXIT_VIDEO_ROOM = "onMemberExitVideoRoom";

    public static final String FUN_NAME_CHANGE_AV_TYPE = "changeAVType";
    public static final String FUN_NAME_ON_AV_TYPE_CHANGED = "onAVTypeChanged";
    public static final String FUN_NAME_ON_SELF_CHANGE_AV_TYPE = "onSelfChangeAVType";
    public static final String FUN_NAME_ON_MEMBER_CHANGE_AV_TYPE = "onMemberChangeAVType";

    public static final String FUN_NAME_SEND_VIDEO_ROOM_MESSAGE = "sendVideoRoomMessage";
    public static final String FUN_NAME_ON_SELF_SEND_VIDEO_ROOM_MESSAGE = "onSelfSendVideoRoomMessage";
    public static final String FUN_NAME_ON_MEMBER_SEND_MESSAGE = "onMemberSendMessage";

    public static final String FUN_NAME_ON_VIDEO_ROOM_SHUT_DOWN = "onVideoRoomShutDown";
    public static final String FUN_NAME_ON_MEMBER_RESERVE_END = "onMemberReserveEnd";

    public static final String FUN_NAME_ON_RESERVE_INVATE = "onReserveInvate";

    public static final String FUN_NAME_VIDEO_SUBSCRIBE = "videoSubscribe";
    public static final String FUN_NAME_ON_SELF_VIDEO_SUBSCRIBE = "onSelfVideoSubscribe";

    public static final String FUN_NAME_MAV_SUBSCRIBE = "mavSubscribe";
    public static final String FUN_NAME_ON_SELF_MAV_SUBSCRIBE = "onSelfMavSubscribe";
    public static final String FUN_NAME_ON_MEMBER_SUBSCRIBE = "onMemberSubscribe";

    public static final String FUN_NAME_MAV_DIS_SUBSCRIBE = "mavDisSubscribe";
    public static final String FUN_NAME_ON_SELF_MAV_DIS_SUBSCRIBE = "onSelfMavDisSubscribe";
    public static final String FUN_NAME_ON_MEMBER_DIS_SUBSCRIBE = "onMemberDisSubscribe";

    public static final String FUN_NAME_CHANGE_SELF_AV_TYPE = "changeSelfAVType";
    public static final String FUN_NAME_ON_SELF_CHANGE_AV_SELF_TYPE = "onSelfChangeSelfAVType";

    public static final String FUN_NAME_GET_ROOM_MEMBER = "getRoomMember";
    public static final String FUN_NAME_ON_SELF_GET_ROOM_MEMBER = "onSelfGetRoomMember";

    public static final String FUN_NAME_FORBID_SPEAK = "forbidSpeak";
    public static final String FUN_NAME_ON_SELF_FORBID_SPEAK = "onSelfForbidSpeak";
    public static final String FUN_NAME_ON_MEMBER_FORBID_SPEAK = "onMemberForbidSpeak";

    public static final String FUN_NAME_SEPARATE_MEMBER = "separateMember";
    public static final String FUN_NAME_ON_SELF_SEPARATE_MEMBER = "onSelfSeparateMember";
    public static final String FUN_NAME_ON_MEMBER_SEPARATED = "onMemberSeparated";

    public static final String FUN_NAME_PAUSE_MIC = "pauseMic";
    public static final String FUN_NAME_PAUSE_PLAY = "pausePlay";

    public static final String FUN_NAME_SELF_INTERRUPTED = "selfInterrupted";
    public static final String FUN_NAME_ON_USER_INTERRUPTED = "onUserInterrupted";

    public static final String FUN_NAME_ON_MEMBER_SPEAKING = "onMemberSpeaking";
    public static final String FUN_NAME_ON_MEMBER_SEATSTATE_CHANGED = "onMemberSeatStateChanged";

    //消息相关方法定义
    public static final String FUN_NAME_GET_MESSAGE = "getMessage";
    public static final String FUN_NAME_ON_GET_MESSAGE = "onGetMessage";

    public static final String FUN_NAME_SEND_MESSAGE = "sendMessage";
    public static final String FUN_NAME_ON_SEND_MESSAGE = "onSendMessage";

    public static final String FUN_NAME_ON_MESSAGE_NOTIFICATION = "onMessageNotification";

    // 账户相关方法定义
    public static final String FUN_NAME_INQUIRE_BALANCE_NEW = "inquireBalanceNew";
    public static final String FUN_NAME_ON_INQUIRE_BALANCE_NEW = "onInquireBalanceNew";
    public static final String FUN_NAME_ON_BALANCE_CHANGE_NOTIFICATION = "onBalanceChangeNotification";

    //上传定位及地址
    public static final String FUN_NAME_UPLOAD_POSITION_INFO = "uploadPositionInfo";
    public static final String FUN_NAME_UPLOAD_LOCATION_INFO = "uploadLocationInfo";

    //管理员
    public static final String FUN_NAME_CHECK_ADMIN_INFO = "checkAdminInfo";
    public static final String FUN_NAME_ON_CHECK_ADMIN_INFO = "onCheckAdminInfo";

    //获取鉴权信息
    public static final String FUN_NAME_GET_AUTHENTICATION_INFO = "getAuthenticationInfo";
    public static final String FUN_NAME_ON_GET_AUTHENTICATION_INFO = "onGetAuthenticationInfo";

    public static final String FUN_NAME_GET_ROOM_LIST_INFO = "getRoomListInfo";
    public static final String FUN_NAME_ON_GET_ROOM_LIST_INFO = "onGetRoomListInfo";

}
