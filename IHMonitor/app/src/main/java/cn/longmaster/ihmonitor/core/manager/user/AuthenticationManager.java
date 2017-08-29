package cn.longmaster.ihmonitor.core.manager.user;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cn.longmaster.doctorlibrary.util.handler.AppHandlerProxy;
import cn.longmaster.doctorlibrary.util.log.Logger;
import cn.longmaster.doctorlibrary.util.timeout.TimeoutHelper;
import cn.longmaster.ihmonitor.core.app.AppApplication;
import cn.longmaster.ihmonitor.core.app.AppPreference;
import cn.longmaster.ihmonitor.core.app.DcpErrorcodeDef;
import cn.longmaster.ihmonitor.core.app.DcpFuncConfig;
import cn.longmaster.ihmonitor.core.http.HttpRequester;
import cn.longmaster.ihmonitor.core.manager.BaseManager;
import cn.longmaster.ihmonitor.core.manager.dcp.DcpManager;

/**
 * 鉴权信息
 * Created by Yang² on 2017/3/23.
 */

public class AuthenticationManager extends BaseManager {
    private static final String TAG = AuthenticationManager.class.getSimpleName();
    private static final int MAX_WAITING_TIME = 60 * 1000;//最长等待时间
    @AppApplication.Manager
    private UserInfoManager mUserInfoManager;

    private ArrayList<HttpRequester> failRequesterList = new ArrayList();
    private TimeoutHelper<Integer> mTimeoutHelper;
    private GetAuthenticationListener mGetAuthenticationListener;

    @Override
    public void onManagerCreate(AppApplication application) {
        mTimeoutHelper = new TimeoutHelper<>();
        mTimeoutHelper.setCallback(callback);

    }

    /**
     * 获取鉴权信息
     */
    public void getAuthenticationInfo() {
        Logger.log(Logger.COMMON, TAG + "->getAuthenticationInfo()->");
        JSONObject json = new JSONObject();
        AppApplication.getInstance().getManager(DcpManager.class).getDcpInterface().Request(DcpFuncConfig.FUN_NAME_GET_AUTHENTICATION_INFO, json.toString());
    }

    /**
     * 获取鉴权信息
     */
    public void getAuthenticationInfo(GetAuthenticationListener getAuthenticationListener) {
        mGetAuthenticationListener = getAuthenticationListener;
        Logger.log(Logger.COMMON, TAG + "->getAuthenticationInfo()->");
        JSONObject json = new JSONObject();
        mTimeoutHelper.request(mUserInfoManager.getCurrentUserInfo().getUserId(), MAX_WAITING_TIME);
        AppApplication.getInstance().getManager(DcpManager.class).getDcpInterface().Request(DcpFuncConfig.FUN_NAME_GET_AUTHENTICATION_INFO, json.toString());
    }

    /**
     * 获取鉴权信息
     *
     * @param result
     * @param json
     */
    public void onGetAuthenticationInfo(int result, String json) {
        Logger.log(Logger.COMMON, TAG + "->onGetAuthenticationInfo()->result:" + result + ",json:" + json);
        mTimeoutHelper.cancel(mUserInfoManager.getCurrentUserInfo().getUserId());
        if (result == DcpErrorcodeDef.RET_SUCCESS) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                AppPreference.setStringValue(AppPreference.KEY_AUTHENTICATION_AUTH, jsonObject.optString("_authentication", ""));
                AppPreference.setLongValue(AppPreference.KEY_AUTHENTICATION_OUT_TIME, System.currentTimeMillis() + (jsonObject.optLong("_duration", 0)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (!failRequesterList.isEmpty()) {
            for (Iterator<HttpRequester> it = failRequesterList.iterator(); it.hasNext(); ) {
                HttpRequester requester = it.next();
                requester.doPost();
                it.remove();
            }
        }
        if (mGetAuthenticationListener != null) {
            mGetAuthenticationListener.onGetAuthentication();
            mGetAuthenticationListener = null;
        }
    }

    /**
     * 把鉴权错误失败的接口存入Map
     *
     * @param httpRequester
     */
    public void addFailRequester(HttpRequester httpRequester) {
        failRequesterList.add(httpRequester);
    }

    //请求超时处理
    private TimeoutHelper.OnTimeoutCallback callback = new TimeoutHelper.OnTimeoutCallback() {
        @Override
        public void onTimeout(TimeoutHelper timeoutHelper, Object taskId) {
            if (mUserInfoManager.getCurrentUserInfo().getUserId() == (int) taskId) {
                AppHandlerProxy.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mGetAuthenticationListener != null) {
                            mGetAuthenticationListener.onTimeOut();
                            mGetAuthenticationListener = null;
                        }
                    }
                });
            }
        }
    };

    public interface GetAuthenticationListener {
        void onGetAuthentication();

        void onTimeOut();
    }
}
