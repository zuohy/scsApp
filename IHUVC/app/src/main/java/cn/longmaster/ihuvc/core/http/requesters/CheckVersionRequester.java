package cn.longmaster.ihuvc.core.http.requesters;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cn.longmaster.doctorlibrary.utils.json.JsonHelper;
import cn.longmaster.ihuvc.core.AppConfig;
import cn.longmaster.ihuvc.core.entity.CheckVersionInfo;
import cn.longmaster.ihuvc.core.http.OnResultListener;
import cn.longmaster.ihuvc.core.http.OpTypeConfig;
import cn.longmaster.ihuvc.core.http.SimpleHttpRequester;

/**
 * Created by WangHaiKun on 2017/4/25.
 */

public class CheckVersionRequester extends SimpleHttpRequester<CheckVersionInfo> {
    public CheckVersionRequester(@NonNull OnResultListener<CheckVersionInfo> onResultListener) {
        super(onResultListener);
    }

    @Override
    protected String getUrl() {
        return AppConfig.getClientApiUrl();
    }

    @Override
    protected int getOpType() {
        return OpTypeConfig.CLIENTAPI_OPTYE_CHECK_VERSION;
    }

    @Override
    protected String getTaskId() {
        return getOpType() + "";
    }

    @Override
    protected CheckVersionInfo onDumpData(JSONObject data) throws JSONException {
        return JsonHelper.toObject(data.getJSONObject("data"), CheckVersionInfo.class);
    }

    @Override
    protected void onPutParams(Map<String, Object> params) {
        params.put("client_type", 5);
        params.put("user_id", 0);
    }
}
