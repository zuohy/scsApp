package cn.longmaster.ihuvc.core.http.requesters;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cn.longmaster.ihuvc.core.AppConfig;
import cn.longmaster.ihuvc.core.http.OnResultListener;
import cn.longmaster.ihuvc.core.http.OpTypeConfig;
import cn.longmaster.ihuvc.core.http.SimpleHttpRequester;

/**
 * 校验预约id及关联密码请求
 * Created by YY on 17/4/24.
 */

public class CheckAppointnentPwdRequester extends SimpleHttpRequester<Void> {
    public int appointmentId;
    public String relationPwd;

    public CheckAppointnentPwdRequester(@NonNull OnResultListener<Void> onResultListener) {
        super(onResultListener);
    }

    @Override
    protected String getUrl() {
        return AppConfig.getClientApiUrl();
    }

    @Override
    protected int getOpType() {
        return OpTypeConfig.CLIENTAPI_OPTYE_CHECK_APPOINTMENT_PASSWORD;
    }

    @Override
    protected String getTaskId() {
        return getOpType() + "";
    }

    @Override
    protected Void onDumpData(JSONObject data) throws JSONException {
        return null;
    }

    @Override
    protected void onPutParams(Map<String, Object> params) {
        params.put("appointment_id", appointmentId);
        params.put("relation_pwd", relationPwd);
        params.put("user_id", 0);
    }
}
