package cn.longmaster.ihmonitor.core.manager.message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.longmaster.doctorlibrary.util.handler.AppHandlerProxy;
import cn.longmaster.ihmonitor.core.app.AppApplication;
import cn.longmaster.ihmonitor.core.app.DcpErrorcodeDef;
import cn.longmaster.ihmonitor.core.entity.common.BaseMessageInfo;
import cn.longmaster.ihmonitor.core.manager.BaseManager;
import cn.longmaster.ihmonitor.core.manager.dcp.DcpManager;


/**
 * 消息管理类
 */
public class MessageManager extends BaseManager {
    private final static String TAG = MessageManager.class.getSimpleName();
    private List<MessageStateChangeListener> msgStateChangeListeners = new ArrayList<>();

    /**
     * 是否在消息中心界面
     */
    private boolean mIsInMessageCenter = false;

    @Override
    public void onManagerCreate(AppApplication application) {
    }

    @Override
    public void onAllManagerCreated() {
        super.onAllManagerCreated();
    }

    public boolean isInMessageCenter() {
        return mIsInMessageCenter;
    }

    public void setIsInMessageCenter(boolean isInMessageCenter) {
        mIsInMessageCenter = isInMessageCenter;
    }

    public void onGetMessage(int result, String json) {
        if (result == DcpErrorcodeDef.RET_SUCCESS) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                int count = jsonObject.optInt("_count", 0);
                if (count > 0) {
                    JSONArray jsonArray = jsonObject.optJSONArray("_msgList");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject msgJsonObject = new JSONObject(jsonArray.get(i).toString());
                            final BaseMessageInfo messageInfo = new BaseMessageInfo();
                            messageInfo.setSenderID(msgJsonObject.optInt("_senderID", 0));
                            messageInfo.setRecverID(msgJsonObject.optInt("_recverID", 0));
                            messageInfo.setSeqId(msgJsonObject.optLong("_seqID", 0));
                            messageInfo.setMsgId(msgJsonObject.optLong("_msgID", 0));
                            messageInfo.setSendDt(msgJsonObject.optLong("_sendDT", 0));
                            messageInfo.setMsgType(msgJsonObject.optInt("_msgType", 0));
                            messageInfo.setMsgContent(msgJsonObject.optString("_msgContent", ""));
                            messageInfo.setMsgState(MessageProtocol.MSG_STATE_UNREAD);
                            if (messageInfo.getMsgType() == MessageProtocol.SMS_TYPE_DOCTOR_BALANCE_CHANGE ||
                                    messageInfo.getMsgType() == MessageProtocol.SMS_TYPE_DOCTOR_APPOINTMENT_TODAY ||
                                    messageInfo.getMsgType() == MessageProtocol.SMS_TYPE_DOCTOR_39_ACTIVITY) {
//                                saveMessageInfo(messageInfo);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

//    public void getMessageList(final int userId) {
//        MessageListRequester messageListRequester = new MessageListRequester(new OnResultListener<List<MessageListInfo>>() {
//            @Override
//            public void onResult(BaseResult baseResult, List<MessageListInfo> messageListInfos) {
//                if (baseResult.getCode() == RESULT_SUCCESS && !messageListInfos.isEmpty()) {
//                    try {
//                        for (int i = 0; i < messageListInfos.size(); i++) {
//                            BaseMessageInfo baseMessageInfo = new BaseMessageInfo();
//                            baseMessageInfo.setMsgContent(messageListInfos.get(i).getMsgContent());
//                            baseMessageInfo.setSenderID(messageListInfos.get(i).getSenderID());
//                            baseMessageInfo.setRecverID(userId);
//                            baseMessageInfo.setSendDt(messageListInfos.get(i).getSendDT());
//                            baseMessageInfo.setMsgId(System.currentTimeMillis() + i);
//                            baseMessageInfo.setMsgState(MessageProtocol.MSG_STATE_UNREAD);
//                            JSONObject contentJson = new JSONObject(baseMessageInfo.getMsgContent());
//                            baseMessageInfo.setMsgType(contentJson.optInt("st"));
//                            if (baseMessageInfo.getMsgType() == MessageProtocol.SMS_TYPE_DOCTOR_39_ACTIVITY) {
//                                saveMessageInfo(baseMessageInfo);
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        messageListRequester.doPost();
//    }

    public void sendMessage(int senderID, int recverID, byte msgType, String msgContent) {
        AppApplication.getInstance().getManager(DcpManager.class).sendMessage(senderID, recverID, msgType, msgContent);
    }

    public void onSendMessage(int reslut, String json) {

    }

    /**
     * 收到服务器推送消息
     *
     * @param result 返回码
     * @param json   消息json
     */
    public void onMessageNotification(int result, String json) {
        if (result == DcpErrorcodeDef.RET_SUCCESS) {
            JSONObject msgJsonObject = null;
            try {
                final BaseMessageInfo messageInfo = new BaseMessageInfo();
                //"_msgContent":"{\"senderID\":10003,\"recverID\":1001331,\"msgType\":41,\"appointment_id\":9967,\"ap_stat\":8,\"is_recure\":0,\"stat_reason\":0}","_msgID":4338429543900539,"_msgType":41,"_recverID":1001331,"_sendDT":1473832315,"_senderID":10003,"_seqID":8788}
                //{"_msgContent":"{\"st\":75,\"aid\":9749,\"un\":\"\\u6768\\u52c7\",\"c\":\"\",\"ap_stat\":\"2\",\"stat_reason\":\"0\"}","_msgID":4310495076441790,"_msgType":75,"_recverID":1001331,"_sendDT":1473666750,"_senderID":10003,"_seqID":2284}
                //{"_msgContent":"{\"uid\":1003770,\"ut\":1,\"aid\":13328,\"cd\":\"1495181729\",\"smsType\":103}","_msgID":6426067792744589310,"_msgType":103,"_recverID":1003771,"_sendDT":1495181310,"_senderID":1003770,"_seqID":1495181729}
                //解析基本
                msgJsonObject = new JSONObject(json);
                messageInfo.setSenderID(msgJsonObject.optInt("_senderID", 0));
                messageInfo.setRecverID(msgJsonObject.optInt("_recverID", 0));
                messageInfo.setSeqId(msgJsonObject.optLong("_seqID", 0));
                messageInfo.setMsgId(msgJsonObject.optLong("_msgID", 0));
                messageInfo.setSendDt(msgJsonObject.optLong("_sendDT", 0));
                messageInfo.setMsgType(msgJsonObject.optInt("_msgType", 0));
                messageInfo.setMsgContent(msgJsonObject.optString("_msgContent", ""));
                messageInfo.setMsgState(MessageProtocol.MSG_STATE_UNREAD);
//                JSONObject contentJson = new JSONObject(messageInfo.getMsgContent());
//                if (contentJson.has("aid")) {
//                    messageInfo.setAppointmentId(contentJson.optInt("aid"));
//                } else if (contentJson.has("appointment_id")) {
//                    messageInfo.setAppointmentId(contentJson.optInt("appointment_id"));
//                }

                AppHandlerProxy.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int j = 0; j < msgStateChangeListeners.size(); j++) {
                            msgStateChangeListeners.get(j).onNewMessage(messageInfo);
                        }
                    }
                });

//                if (messageInfo.getMsgType() == MessageProtocol.SMS_TYPE_DOCTOR_CALL || messageInfo.getMsgType() == MessageProtocol.SMS_TYPE_DOCTOR_ASSISTANT_CALL) {//医生呼叫
//                    AppHandlerProxy.runOnUIThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            for (int j = 0; j < msgStateChangeListeners.size(); j++) {
//                                msgStateChangeListeners.get(j).onNewMessage(messageInfo);
//                            }
//                        }
//                    });
//                    if (AppApplication.getInstance().isEnterVideoRoom()) {
//                        return;
//                    }
//                    if (AppApplication.getInstance().isCallsing()) {
//                        return;
//                    }
//                    long date = contentJson.optLong("cd", 0);
//                    int appId = contentJson.optInt("aid",0);
//                    int doctorId = contentJson.optInt("uid", 0);
//                    int userType = contentJson.optInt("ut",0);
//                    Intent intent = new Intent(AppApplication.getInstance(), VideoCallActivity.class);
//                    if (messageInfo.getMsgType() == MessageProtocol.SMS_TYPE_DOCTOR_CALL) {
//                        intent.putExtra(VideoCallActivity.EXTRA_DATA_PAGE_TYPE, VideoCallActivity.PAGE_TYPE_CALL);
//                        intent.putExtra(VideoCallActivity.EXTRA_DATA_CALL_DOCTOR_ID, doctorId);
//                    } else {
//                        intent.putExtra(VideoCallActivity.EXTRA_DATA_PAGE_TYPE, VideoCallActivity.DOCTOR_TYPE_DOCTOR_ASSISTANT);
//                        intent.putExtra(VideoCallActivity.EXTRA_DATA_CALL_DOCTOR_ID, messageInfo.getSenderID() + "");
//                    }
//                    intent.putExtra(VideoCallActivity.EXTRA_DATA_CALL_TIME, date);
//                    intent.putExtra(VideoCallActivity.EXTRA_DATA_APPOINTMENT_ID, appId);
//                    intent.putExtra(VideoCallActivity.EXTRA_DATA_USER_TYPE, userType);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    AppApplication.getInstance().startActivity(intent);
//                } else if (messageInfo.getMsgType() == MessageProtocol.SMS_TYPE_DOCTOR_BALANCE_CHANGE ||
//                        messageInfo.getMsgType() == MessageProtocol.SMS_TYPE_DOCTOR_APPOINTMENT_TODAY) {
////                    saveMessageInfo(messageInfo);
//                } else if (messageInfo.getMsgType() == MessageProtocol.SMS_TYPE_DOCTOR_39_ACTIVITY) {
//                    //活动咨询
//                    AppHandlerProxy.runOnUIThread(new Runnable() {
//                        @Override
//                        public void run() {
////                            getMessageList(AppApplication.getInstance().getManager(UserInfoManager.class).getCurrentUserInfo().getUserId());
//                        }
//                    });
//                } else {
//                    //预约状态变化
//                    AppHandlerProxy.runOnUIThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            for (int j = 0; j < msgStateChangeListeners.size(); j++) {
//                                msgStateChangeListeners.get(j).onNewMessage(messageInfo);
//                            }
//                        }
//                    });
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从数据库获取未读消息列表
     *
     * @param userId   用户id
     * @param listener 回调接口
     */
//    public void getUnReadMessages(final int userId, final GetMessageStateChangeListener listener) {
//        DatabaseTask<List<BaseMessageInfo>> task = new DatabaseTask<List<BaseMessageInfo>>() {
//            @Override
//            public AsyncResult<List<BaseMessageInfo>> runOnDBThread(AsyncResult<List<BaseMessageInfo>> asyncResult, DBHelper dbHelper) {
//                SQLiteDatabase database = dbHelper.getWritableDatabase();
//                database.beginTransaction();
//                Cursor cursor = null;
//                List<BaseMessageInfo> messageInfos = new ArrayList<>();
//                try {
//                    String sql = "SELECT * FROM " +
//                            MessageContract.MessageEntry.TABLE_NAME +
//                            " WHERE " +
//                            MessageContract.MessageEntry.COLUMN_NAME_RECVER_ID +
//                            " = ? AND " +
//                            MessageContract.MessageEntry.COLUMN_NAME_MSG_STATE +
//                            " = ?" +
//                            " ORDER BY " + MessageContract.MessageEntry._ID + " DESC";
//                    cursor = database.rawQuery(sql, new String[]{String.valueOf(userId), String.valueOf(MessageProtocol.MSG_STATE_UNREAD)});
//                    while (cursor.moveToNext()) {
//                        BaseMessageInfo baseMessageInfo = new BaseMessageInfo();
//                        baseMessageInfo.setSeqId(cursor.getLong(cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_SEQ_ID)));
//                        baseMessageInfo.setMsgId(cursor.getLong(cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_MSG_ID)));
//                        baseMessageInfo.setSenderID(cursor.getInt(cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_SENDER_ID)));
//                        baseMessageInfo.setRecverID(cursor.getInt(cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_RECVER_ID)));
//                        baseMessageInfo.setMsgType(cursor.getInt(cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_MSG_TYPE)));
//                        baseMessageInfo.setMsgContent(cursor.getString(cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_MSG_CONTENT)));
//                        baseMessageInfo.setSendDt(cursor.getLong(cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_SEND_DT)));
//                        baseMessageInfo.setAppointmentId(cursor.getInt(cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_APPOINTMENT_ID)));
//                        baseMessageInfo.setMsgState(cursor.getInt(cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_MSG_STATE)));
//                        messageInfos.add(baseMessageInfo);
//                    }
//                    database.setTransactionSuccessful();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (cursor != null) {
//                        cursor.close();
//                    }
//                    database.endTransaction();
//                }
//                asyncResult.setData(messageInfos);
//                return asyncResult;
//            }
//
//            @Override
//            public void runOnUIThread(AsyncResult<List<BaseMessageInfo>> asyncResult) {
//                listener.getMessageStateChanged(asyncResult.getData());
//            }
//        };
//        AppApplication.getInstance().getManager(DBManager.class).submitDatabaseTask(task);
//    }

    /**
     * 从数据库获取所有消息
     *
     * @param userId   用户id
     * @param listener 回调接口
     */
//    public void getAllMessage(final int userId, final GetMessageStateChangeListener listener) {
//        DatabaseTask<List<BaseMessageInfo>> task = new DatabaseTask<List<BaseMessageInfo>>() {
//            @Override
//            public AsyncResult<List<BaseMessageInfo>> runOnDBThread(AsyncResult<List<BaseMessageInfo>> asyncResult, DBHelper dbHelper) {
//                SQLiteDatabase database = dbHelper.getWritableDatabase();
//                database.beginTransaction();
//                Cursor cursor = null;
//                List<BaseMessageInfo> messageInfos = new ArrayList<>();
//                try {
//                    String sql = "SELECT * FROM " +
//                            MessageContract.MessageEntry.TABLE_NAME +
//                            " WHERE " +
//                            MessageContract.MessageEntry.COLUMN_NAME_RECVER_ID +
//                            " = ?" +
//                            " ORDER BY " + MessageContract.MessageEntry.COLUMN_NAME_SEND_DT + " DESC";
//                    cursor = database.rawQuery(sql, new String[]{String.valueOf(userId)});
//                    while (cursor.moveToNext()) {
//                        BaseMessageInfo baseMessageInfo = new BaseMessageInfo();
//                        baseMessageInfo.setSeqId(cursor.getLong(cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_SEQ_ID)));
//                        baseMessageInfo.setMsgId(cursor.getLong(cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_MSG_ID)));
//                        baseMessageInfo.setSenderID(cursor.getInt(cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_SENDER_ID)));
//                        baseMessageInfo.setRecverID(cursor.getInt(cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_RECVER_ID)));
//                        baseMessageInfo.setMsgType(cursor.getInt(cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_MSG_TYPE)));
//                        baseMessageInfo.setMsgContent(cursor.getString(cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_MSG_CONTENT)));
//                        baseMessageInfo.setSendDt(cursor.getLong(cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_SEND_DT)));
//                        baseMessageInfo.setAppointmentId(cursor.getInt(cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_APPOINTMENT_ID)));
//                        baseMessageInfo.setMsgState(cursor.getInt(cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_NAME_MSG_STATE)));
//                        messageInfos.add(baseMessageInfo);
//                    }
//                    database.setTransactionSuccessful();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (cursor != null) {
//                        cursor.close();
//                    }
//                    database.endTransaction();
//                }
//                asyncResult.setData(messageInfos);
//                return asyncResult;
//            }
//
//            @Override
//            public void runOnUIThread(AsyncResult<List<BaseMessageInfo>> asyncResult) {
//                listener.getMessageStateChanged(asyncResult.getData());
//            }
//        };
//        AppApplication.getInstance().getManager(DBManager.class).submitDatabaseTask(task);
//    }

    /**
     * 将所有消息置为已读
     *
     * @param userId 用户id
     */
//    public void updateAllMessageToRead(final int userId) {
//        DatabaseTask<List<BaseMessageInfo>> task = new DatabaseTask<List<BaseMessageInfo>>() {
//            @Override
//            public AsyncResult<List<BaseMessageInfo>> runOnDBThread(AsyncResult<List<BaseMessageInfo>> asyncResult, DBHelper dbHelper) {
//                SQLiteDatabase database = dbHelper.getWritableDatabase();
//                database.beginTransaction();
//                Cursor cursor = null;
//                List<BaseMessageInfo> messageInfos = new ArrayList<>();
//                try {
//                    ContentValues contentValues = new ContentValues();
//                    contentValues.put(MessageContract.MessageEntry.COLUMN_NAME_MSG_STATE, MessageProtocol.MSG_STATE_READED);
//                    database.update(MessageContract.MessageEntry.TABLE_NAME,
//                            contentValues,
//                            MessageContract.MessageEntry.COLUMN_NAME_RECVER_ID + " = ?",
//                            new String[]{String.valueOf(userId)});
//                    database.setTransactionSuccessful();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (cursor != null) {
//                        cursor.close();
//                    }
//                    database.endTransaction();
//                }
//                asyncResult.setData(messageInfos);
//                return asyncResult;
//            }
//
//            @Override
//            public void runOnUIThread(AsyncResult<List<BaseMessageInfo>> asyncResult) {
//            }
//        };
//        AppApplication.getInstance().getManager(DBManager.class).submitDatabaseTask(task);
//    }

    /**
     * 将单条消息设为已读
     *
     * @param userId
     * @param msgId
     */
//    public void updateMessageToRead(final int userId, final long msgId) {
//        DatabaseTask<List<BaseMessageInfo>> task = new DatabaseTask<List<BaseMessageInfo>>() {
//            @Override
//            public AsyncResult<List<BaseMessageInfo>> runOnDBThread(AsyncResult<List<BaseMessageInfo>> asyncResult, DBHelper dbHelper) {
//                SQLiteDatabase database = dbHelper.getWritableDatabase();
//                database.beginTransaction();
//                Cursor cursor = null;
//                List<BaseMessageInfo> messageInfos = new ArrayList<>();
//                try {
//                    ContentValues contentValues = new ContentValues();
//                    contentValues.put(MessageContract.MessageEntry.COLUMN_NAME_MSG_STATE, MessageProtocol.MSG_STATE_READED);
//                    database.update(MessageContract.MessageEntry.TABLE_NAME,
//                            contentValues,
//                            MessageContract.MessageEntry.COLUMN_NAME_RECVER_ID + " = ? AND " + MessageContract.MessageEntry.COLUMN_NAME_MSG_ID + " = ?",
//                            new String[]{String.valueOf(userId), String.valueOf(msgId)});
//                    database.setTransactionSuccessful();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (cursor != null) {
//                        cursor.close();
//                    }
//                    database.endTransaction();
//                }
//                asyncResult.setData(messageInfos);
//                return asyncResult;
//            }
//
//            @Override
//            public void runOnUIThread(AsyncResult<List<BaseMessageInfo>> asyncResult) {
//            }
//        };
//        AppApplication.getInstance().getManager(DBManager.class).submitDatabaseTask(task);
//    }

    /**
     * 删除指定的消息
     *
     * @param userId
     * @param msgId
     */
//    public void deleteMessage(final int userId, final long msgId) {
//        DatabaseTask<Void> task = new DatabaseTask<Void>() {
//            @Override
//            public AsyncResult<Void> runOnDBThread(AsyncResult<Void> asyncResult, DBHelper dbHelper) {
//                SQLiteDatabase database = dbHelper.getWritableDatabase();
//                database.beginTransaction();
//                try {
//                    database.delete(MessageContract.MessageEntry.TABLE_NAME,
//                            MessageContract.MessageEntry.COLUMN_NAME_RECVER_ID + " =? AND " +
//                                    MessageContract.MessageEntry.COLUMN_NAME_MSG_ID + " =?",
//                            new String[]{String.valueOf(userId), String.valueOf(msgId)});
//                    database.setTransactionSuccessful();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    database.endTransaction();
//                }
//                return asyncResult;
//            }
//
//            @Override
//            public void runOnUIThread(AsyncResult<Void> asyncResult) {
//            }
//        };
//        AppApplication.getInstance().getManager(DBManager.class).submitDatabaseTask(task);
//    }

    /**
     * 保存消息信息
     *
     * @param messageInfo 消息列表
     */

//    public void saveMessageInfo(final BaseMessageInfo messageInfo) {
//        if (messageInfo == null)
//            return;
//
//        DatabaseTask<Void> task = new DatabaseTask<Void>() {
//            @Override
//            public AsyncResult<Void> runOnDBThread(AsyncResult<Void> asyncResult, DBHelper dbHelper) {
//                SQLiteDatabase database = dbHelper.getWritableDatabase();
//                database.beginTransaction();
//                try {
//                    ContentValues values = new ContentValues();
//                    values.put(MessageContract.MessageEntry.COLUMN_NAME_SEQ_ID, messageInfo.getSeqId());
//                    values.put(MessageContract.MessageEntry.COLUMN_NAME_MSG_ID, messageInfo.getMsgId());
//                    values.put(MessageContract.MessageEntry.COLUMN_NAME_SENDER_ID, messageInfo.getSenderID());
//                    values.put(MessageContract.MessageEntry.COLUMN_NAME_RECVER_ID, messageInfo.getRecverID());
//                    values.put(MessageContract.MessageEntry.COLUMN_NAME_MSG_TYPE, messageInfo.getMsgType());
//                    values.put(MessageContract.MessageEntry.COLUMN_NAME_MSG_CONTENT, messageInfo.getMsgContent());
//                    values.put(MessageContract.MessageEntry.COLUMN_NAME_SEND_DT, messageInfo.getSendDt());
//                    values.put(MessageContract.MessageEntry.COLUMN_NAME_APPOINTMENT_ID, messageInfo.getAppointmentId());
//                    values.put(MessageContract.MessageEntry.COLUMN_NAME_MSG_STATE, messageInfo.getMsgState());
//
//                    // 8.2新加
//                    database.insert(MessageContract.MessageEntry.TABLE_NAME, null, values);
//                    database.setTransactionSuccessful();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    database.endTransaction();
//                }
//                return asyncResult;
//            }
//
//            @Override
//            public void runOnUIThread(AsyncResult<Void> asyncResult) {
//                if (!isInMessageCenter()) {
//                    AppPreference.setBooleanValue(AppPreference.KEY_MESSAGE_CENTER_NEW_MESSAGE
//                                    + AppApplication.getInstance().getManager(UserInfoManager.class).getCurrentUserInfo().getUserId()
//                            , true);
//                    //弹通知
//                    AppApplication.getInstance().getManager(LocalNotificationManager.class).sendNewMessageNotice();
//                }
//                if (msgStateChangeListeners == null || msgStateChangeListeners.size() == 0)
//                    return;
//                for (int j = 0; j < msgStateChangeListeners.size(); j++) {
//                    msgStateChangeListeners.get(j).onNewMessage(messageInfo);
//                }
//            }
//        };
//        AppApplication.getInstance().getManager(DBManager.class).submitDatabaseTask(task);
//    }

    /**
     * 注册消息状态监听,调用本方法后请在界面销毁时调用unRegMsgStateChangeListener(listener)方法
     *
     * @param listener 消息状态监听
     */
    public void regMsgStateChangeListener(MessageStateChangeListener listener) {
        msgStateChangeListeners.add(listener);
    }

    /**
     * 注销消息状态变化
     */
    public void unRegMsgStateChangeListener(MessageStateChangeListener listener) {
        msgStateChangeListeners.remove(listener);
    }

    /**
     * 获取消息状态变化回调接口
     */
    public interface GetMessageStateChangeListener {
        void getMessageStateChanged(List<BaseMessageInfo> baseMessageInfos);
    }
}
