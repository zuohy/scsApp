package cn.longmaster.ihmonitor.core.http.login;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cn.longmaster.doctorlibrary.util.common.AppUtil;
import cn.longmaster.doctorlibrary.util.common.MD5Util;
import cn.longmaster.doctorlibrary.util.common.OsUtil;
import cn.longmaster.doctorlibrary.util.json.JsonHelper;
import cn.longmaster.ihmonitor.core.app.AppConfig;
import cn.longmaster.ihmonitor.core.app.AppConstant;
import cn.longmaster.ihmonitor.core.entity.user.UserResultInfo;
import cn.longmaster.ihmonitor.core.http.OnResultListener;
import cn.longmaster.ihmonitor.core.http.OpTypeConfig;
import cn.longmaster.ihmonitor.core.http.SimpleHttpRequester;

/**
 * 账号激活请求
 */
public class AccountActiveRequester extends SimpleHttpRequester<UserResultInfo> {
    public String account;
    public String pwd;
    public byte accountType;

    public AccountActiveRequester(@NonNull OnResultListener<UserResultInfo> onResultListener) {
        super(onResultListener);
    }

    @Override
    protected String getUrl() {
        return AppConfig.getDuwsUrl();
    }

    @Override
    protected int getOpType() {
        return OpTypeConfig.DUWS_OPTYPE_ACTIVE_ACCOUNT;
    }

    @Override
    protected String getTaskId() {
        return getOpType() + "";
    }

    @Override
    protected UserResultInfo onDumpData(JSONObject data) throws JSONException {
        return JsonHelper.toObject(data, UserResultInfo.class);
    }

    @Override
    protected void onPutParams(Map<String, Object> params) {
        params.put("user_name", account);
        params.put("accounttype", accountType);
        params.put("user_pwd", pwd);
        params.put("phoneos", 1);
        params.put("phonetype", OsUtil.getPhoneType());
        params.put("phoneosversion", OsUtil.getPhoneOSVersion());
        params.put("romversion", OsUtil.getRomVersion());
        params.put("clientversion", AppConfig.CLIENT_VERSION);
        params.put("isdoctor", 0);
        params.put("userorigin", 2);
        params.put("devtoken", "");
        params.put("userfrom", AppUtil.getChannelCode());
        params.put("mac", OsUtil.getMacAddress());
        params.put("imei", OsUtil.getIMEI());
        params.put("sign", MD5Util.md5(account + "_" + pwd + "_" + AppConstant.DUWS_APP_KEY));
    }
}
