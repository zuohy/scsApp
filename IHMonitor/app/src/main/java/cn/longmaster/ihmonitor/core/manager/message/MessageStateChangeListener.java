package cn.longmaster.ihmonitor.core.manager.message;


import cn.longmaster.ihmonitor.core.entity.common.BaseMessageInfo;

/**
 * 消息状态变化监听
 */
public interface MessageStateChangeListener {
    /**
     * 收到新消息
     *
     * @param baseMessageInfo 消息实例
     */
    public void onNewMessage(BaseMessageInfo baseMessageInfo);
}
