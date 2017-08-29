package cn.longmaster.ihmonitor.core.entity.common;

import java.io.Serializable;

/**
 * 消息基类
 */
public class BaseMessageInfo implements Serializable, Comparable<BaseMessageInfo> {
    private int senderID;// 发送者id
    private int recverID;// 接收者id
    private long seqId;//序列号
    private long msgId;//消息id
    private long sendDt;//发送时间
    private int msgType;// 消息类型
    private String msgContent;// 消息内容
    private int appointmentId;// 预约id
    private int msgState;// 消息状态

    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public int getRecverID() {
        return recverID;
    }

    public void setRecverID(int recverID) {
        this.recverID = recverID;
    }

    public long getSeqId() {
        return seqId;
    }

    public void setSeqId(long seqId) {
        this.seqId = seqId;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public long getSendDt() {
        return sendDt;
    }

    public void setSendDt(long sendDt) {
        this.sendDt = sendDt;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getMsgState() {
        return msgState;
    }

    public void setMsgState(int msgState) {
        this.msgState = msgState;
    }

    @Override
    public String toString() {
        return "BaseMessageInfo{" +
                "senderID=" + senderID +
                ", recverID=" + recverID +
                ", seqId=" + seqId +
                ", msgId=" + msgId +
                ", sendDt=" + sendDt +
                ", msgType=" + msgType +
                ", msgContent='" + msgContent + '\'' +
                ", appointmentId=" + appointmentId +
                ", msgState=" + msgState +
                '}';
    }

    @Override
    public int compareTo(BaseMessageInfo baseMessageInfo) {
        return (int) (baseMessageInfo.getSendDt() - this.getSendDt());
    }
}
