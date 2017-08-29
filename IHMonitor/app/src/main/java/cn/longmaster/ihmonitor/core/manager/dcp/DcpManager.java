package cn.longmaster.ihmonitor.core.manager.dcp;

import com.ppcp.manger.CallbackInterface;
import com.ppcp.manger.PpcpInterface;

import org.json.JSONException;
import org.json.JSONObject;

import cn.longmaster.doctorlibrary.util.common.AppUtil;
import cn.longmaster.doctorlibrary.util.log.Logger;
import cn.longmaster.ihmonitor.core.app.AppApplication;
import cn.longmaster.ihmonitor.core.app.AppConfig;
import cn.longmaster.ihmonitor.core.app.DcpFuncConfig;
import cn.longmaster.ihmonitor.core.manager.BaseManager;

/**
 * dcp操作管理类
 */
public class DcpManager extends BaseManager {
    private static final String TAG = DcpManager.class.getSimpleName();

    private PpcpInterface mDcpInterface;//  dcp接口
    private CallbackInterface mDcpJniCallback;// dcp jin回调
    private boolean mIsOpenClientSuccess;

    @Override
    public void onManagerCreate(AppApplication application) {
        mDcpInterface = new PpcpInterface();
        mDcpJniCallback = new CallbackInterface();
        mDcpJniCallback.setCallBackListener(new DcpListener());
    }

    @Override
    public void onAllManagerCreated() {
        super.onAllManagerCreated();
    }

    public PpcpInterface getDcpInterface() {
        return mDcpInterface;
    }

    public CallbackInterface getDcpJniCallback() {
        return mDcpJniCallback;
    }

    public boolean isOpenClientSuccess() {
        return mIsOpenClientSuccess;
    }

    public void openClient() {
        mIsOpenClientSuccess = mDcpInterface.openClient(mDcpJniCallback, AppApplication.getInstance().getFilesDir().getParent() + "/lib/", AppConfig.IS_DEBUG_MODE ? 6 : -1);
        Logger.log(Logger.COMMON, TAG + "->openClient()->mIsOpenClientSuccess:" + mIsOpenClientSuccess);
    }

    /**
     * 设置GK
     *
     * @return 接口调用结果
     */
    public boolean setGKDomain() {
        try {
            JSONObject json = new JSONObject();
            json.put("_gkPort", AppConfig.getServerPort());
            json.put("_gkDomain", AppUtil.getIP(AppConfig.getServerAddress()));
            String result = mDcpInterface.Request(DcpFuncConfig.FUN_NAME_SETGKDEMAIN, json.toString());
            Logger.log(Logger.COMMON, TAG + "->setGKDomain()->result:" + result);
            if (result.equals("true")) {
                AppApplication.getInstance().setIsSetGKDomain(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void keepAlive(int userId) {
        try {
            JSONObject json = new JSONObject();
            json.put("_userID", userId);
            getDcpInterface().Request(DcpFuncConfig.FUN_NAME_KEEPALIVE, json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置pes
     *
     * @param pesIp   ip地址
     * @param pesPort 端口
     */
    public void setPesInfo(long pesIp, int pesPort) {
        Logger.log(Logger.COMMON, TAG + "->setPesInfo()->pesIp:" + pesIp + ", pesPort:" + pesPort);
        try {
            JSONObject json = new JSONObject();
            json.put("_pesIP", pesIp);
            json.put("_pesPort", pesPort);
            Logger.log(Logger.COMMON, TAG + "->setPesInfo()->json:" + json.toString());
            mDcpInterface.Request(DcpFuncConfig.FUN_NAME_SETPESINFO, json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录
     *
     * @param userId       用户id
     * @param loginAuthKey 鉴权
     */
    public void login(int userId, String loginAuthKey) {
        Logger.log(Logger.COMMON, TAG + "->login()->userId:" + userId + ", loginAuthKey:" + loginAuthKey);
        try {
            JSONObject json = new JSONObject();
            json.put("_userID", userId);
            json.put("_loginAuthKey", loginAuthKey);
            json.put("_clientVersion", AppConfig.CLIENT_VERSION);
            json.put("_netType", 0);
            json.put("_reserved", 6);
            Logger.log(Logger.COMMON, TAG + "->login()->json:" + json.toString());
            mDcpInterface.Request(DcpFuncConfig.FUN_NAME_LOGIN, json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出登录
     *
     * @param userId 用户id
     */
    public void logout(int userId) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("_userID", userId);
            mDcpInterface.Request(DcpFuncConfig.FUN_NAME_LOGOUT, jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        try {
            JSONObject jsonObject = new JSONObject();
            mDcpInterface.Request(DcpFuncConfig.FUN_NAME_DISCONNECT, jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息
     *
     * @param senderID   发送者id
     * @param recverID   接受者id
     * @param msgType    消息类型
     * @param msgContent 消息内容
     */
    public void sendMessage(int senderID, int recverID, byte msgType, String msgContent) {
        try {
            JSONObject json = new JSONObject();
            json.put("_senderID", senderID);
            json.put("_recverID", recverID);
            json.put("_msgType", msgType);
            json.put("_seqID", System.currentTimeMillis() / 1000);
            json.put("_msgContent", msgContent);
            Logger.log(Logger.COMMON, TAG + "->sendMessage()->json:" + json.toString());
            mDcpInterface.Request(DcpFuncConfig.FUN_NAME_SEND_MESSAGE, json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收消息
     *
     * @param userId
     */
    public void getMessage(int userId) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("_userID", userId);

            mDcpInterface.request(DcpFuncConfig.FUN_NAME_GET_MESSAGE, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询余额
     */
    public void inquireBalance() {
        try {
            JSONObject json = new JSONObject();
            json.put("_userType", 2);
            getDcpInterface().Request(DcpFuncConfig.FUN_NAME_INQUIRE_BALANCE_NEW, json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测管理员
     */
    public void checkAdminInfo() {
        JSONObject json = new JSONObject();
        getDcpInterface().Request(DcpFuncConfig.FUN_NAME_CHECK_ADMIN_INFO, json.toString());
    }

    public void getRoomListInfo() {
        try {
            JSONObject json = new JSONObject();
            json.put("_actionType", 2);
            json.put("_reserved", "");
            getDcpInterface().Request(DcpFuncConfig.FUN_NAME_GET_ROOM_LIST_INFO, json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
