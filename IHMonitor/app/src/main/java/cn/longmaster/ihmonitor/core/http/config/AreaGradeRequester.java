package cn.longmaster.ihmonitor.core.http.config;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import cn.longmaster.doctorlibrary.util.json.JsonHelper;
import cn.longmaster.ihmonitor.core.app.AppConfig;
import cn.longmaster.ihmonitor.core.entity.config.AreaGradeInfo;
import cn.longmaster.ihmonitor.core.http.OnResultListener;
import cn.longmaster.ihmonitor.core.http.OpTypeConfig;
import cn.longmaster.ihmonitor.core.http.SimpleHttpRequester;

/**
 * 医生区域等级配置Requester
 * Created by Yang² on 2016/7/26.
 */
public class AreaGradeRequester extends SimpleHttpRequester<List<AreaGradeInfo>> {
    public String token;//同步token标识

    public AreaGradeRequester(@NonNull OnResultListener<List<AreaGradeInfo>> onResultListener) {
        super(onResultListener);
    }

    @Override
    protected String getUrl() {
        return AppConfig.getClientApiUrl();
    }

    @Override
    protected int getOpType() {
        return OpTypeConfig.CLIENTAPI_OPTYE_AREA_GRADE_INFO;
    }

    @Override
    protected String getTaskId() {
        return getOpType() + "";
    }

    @Override
    protected List<AreaGradeInfo> onDumpData(JSONObject data) throws JSONException {
        return JsonHelper.toList(data.getJSONArray("data"), AreaGradeInfo.class);
    }

    @Override
    protected void onPutParams(Map<String, Object> params) {
        params.put("token", token);
    }
}
