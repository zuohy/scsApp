package cn.longmaster.ihuvc.core.http.requesters;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import cn.longmaster.doctorlibrary.utils.json.JsonHelper;
import cn.longmaster.ihuvc.core.AppConfig;
import cn.longmaster.ihuvc.core.entity.MaterialTypeInfo;
import cn.longmaster.ihuvc.core.http.OnResultListener;
import cn.longmaster.ihuvc.core.http.OpTypeConfig;
import cn.longmaster.ihuvc.core.http.SimpleHttpRequester;

/**
 * Created by WangHaiKun on 2017/4/25.
 */

public class MaterialTypeRequester extends SimpleHttpRequester<List<MaterialTypeInfo>> {
    public int appointmentId;

    public MaterialTypeRequester(@NonNull OnResultListener<List<MaterialTypeInfo>> onResultListener) {
        super(onResultListener);
    }

    @Override
    protected String getUrl() {
        return AppConfig.getClientApiUrl();
    }

    @Override
    protected int getOpType() {
        return OpTypeConfig.CLIENTAPI_OPTYE_MATERIAL_TYPE;
    }

    @Override
    protected String getTaskId() {
        return getOpType() + "";
    }

    @Override
    protected List<MaterialTypeInfo> onDumpData(JSONObject data) throws JSONException {
        return JsonHelper.toList(data.getJSONArray("data"), MaterialTypeInfo.class);
    }

    @Override
    protected void onPutParams(Map<String, Object> params) {
        params.put("appointment_id", appointmentId);
        params.put("user_id", 0);
        params.put("action_type", 1);
    }
}
