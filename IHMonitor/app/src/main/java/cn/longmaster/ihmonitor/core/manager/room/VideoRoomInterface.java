package cn.longmaster.ihmonitor.core.manager.room;

import java.util.List;

import cn.longmaster.ihmonitor.core.entity.video.VideoRoomMember;
import cn.longmaster.ihmonitor.core.entity.video.VideoRoomResultInfo;

/**
 * 视频聊天室接口类，用于声明视频就诊相关接口
 * Created by Yang² on 2016/12/28.
 */

public interface VideoRoomInterface {

    /**
     * 加入聊天室
     *
     * @param reserveID 预约ID
     * @param roomID  房间ID
     * @param userType  用户类型
     * @param userName  用户姓名
     * @param listener  回调接口
     */
    void joinVideoRoom(int reserveID, int roomID, int userType, String userName, OnRoomSelfStateChangeListener listener);

    /**
     * 退出聊天室
     *
     * @param roomID   房间ID(传医生ID)
     * @param listener 回调接口
     */
    void exitVideoRoom(int roomID, OnRoomSelfStateChangeListener listener);

    /**
     * 订阅视频列表
     *
     * @param memberList
     * @param listener
     */
    void videoSubscribe(List<VideoRoomMember> memberList, OnRoomSelfStateChangeListener listener);

    /**
     * 关闭MIC
     *
     * @param isPause 是否关闭Mic
     */
    void pauseMic(boolean isPause);

    /**
     * 静音
     * @param isPause 是否静音
     */
    void pausePlay(boolean isPause);

    /**
     * 设置Pcc参数
     *
     * @param eq
     * @param level
     * @param sourceType
     * @param streamType
     */
    void setPccParam(String eq, String level, int sourceType, int streamType);

    /**
     * 接收到自己发起的请求回调
     *
     * @param result  返回嘛
     * @param reqType 请求类型
     * @param json    服务器返回json
     */
    void onRecvSelfReqData(int result, int reqType, String json);

    /**
     * 收到其它成员变化回调
     *
     * @param result     返回码
     * @param messageKey 消息key
     * @param json       服务器返回json
     */
    void onRecvMemberData(int result, int messageKey, String json);

    /**
     * 视频聊天室状态变化回调接口
     */
    interface OnRoomSelfStateChangeListener {
        //进入聊天室
        void onSelfJoinRoom(int result, VideoRoomResultInfo resultInfo);

        //退出聊天室
        void onSelfExitRoom(int result, VideoRoomResultInfo resultInfo);

        void onSelfVideoSubscribe(int result, VideoRoomResultInfo resultInfo);
    }

    /**
     * 聊天室其他成员变化回调
     */
    interface OnRoomMemberStateChangeListener {
        //成员加入房间
        void onMemberJoinRoom(VideoRoomResultInfo roomResultInfo);

        //成员退出房间
        void onMemberExitRoom(VideoRoomResultInfo roomResultInfo);

        //成员切换自身音视频
        void onMemberChangeAvType(VideoRoomResultInfo roomResultInfo);

        //音视频切换（特殊权限者发起）
        void onSpecialMemberChangeAVType(VideoRoomResultInfo roomResultInfo);

        //聊天室关闭
        void onShutDown(VideoRoomResultInfo roomResultInfo);

        //讲话身份成员是否在说话
        void onMemberSpeaking(VideoRoomResultInfo roomResultInfo);

        //成员改变席位状态
        void onMemberSeatStateChanged(VideoRoomResultInfo roomResultInfo);
    }
}
