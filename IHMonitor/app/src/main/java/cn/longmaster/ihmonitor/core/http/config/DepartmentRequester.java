package cn.longmaster.ihmonitor.core.http.config;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cn.longmaster.doctorlibrary.util.json.JsonHelper;
import cn.longmaster.ihmonitor.core.app.AppConfig;
import cn.longmaster.ihmonitor.core.entity.config.DepartmentInfo;
import cn.longmaster.ihmonitor.core.http.OnResultListener;
import cn.longmaster.ihmonitor.core.http.OpTypeConfig;
import cn.longmaster.ihmonitor.core.http.SimpleHttpRequester;

/**
 * 科室信息Requester
 * Created by Yang² on 2016/7/26.
 */
public class DepartmentRequester extends SimpleHttpRequester<DepartmentInfo> {
    public String token;//同步token标识
    public int departmentId;//科室ID

    public DepartmentRequester(@NonNull OnResultListener<DepartmentInfo> onResultListener) {
        super(onResultListener);
    }

    @Override
    protected String getUrl() {
        return AppConfig.getClientApiUrl();
    }

    @Override
    protected int getOpType() {
        return OpTypeConfig.CLIENTAPI_OPTYE_DEPARTMENT_INFO;
    }

    @Override
    protected String getTaskId() {
        return getOpType() + "";
    }

    @Override
    protected DepartmentInfo onDumpData(JSONObject data) throws JSONException {
        return JsonHelper.toObject(data.getJSONObject("data"), DepartmentInfo.class);
    }

    @Override
    protected void onPutParams(Map<String, Object> params) {
        params.put("token", token);
        params.put("department_id", departmentId);

    }
}
