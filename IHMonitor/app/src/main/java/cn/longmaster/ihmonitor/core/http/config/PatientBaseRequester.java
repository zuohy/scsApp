package cn.longmaster.ihmonitor.core.http.config;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cn.longmaster.doctorlibrary.util.json.JsonHelper;
import cn.longmaster.ihmonitor.core.app.AppConfig;
import cn.longmaster.ihmonitor.core.entity.config.DrugPlanInfo;
import cn.longmaster.ihmonitor.core.entity.config.HistoryInfo;
import cn.longmaster.ihmonitor.core.entity.config.OperationPlanInfo;
import cn.longmaster.ihmonitor.core.entity.config.PatientBaseInfo;
import cn.longmaster.ihmonitor.core.entity.config.PatientInfo;
import cn.longmaster.ihmonitor.core.entity.config.PhysicalPlanInfo;
import cn.longmaster.ihmonitor.core.http.OnResultListener;
import cn.longmaster.ihmonitor.core.http.OpTypeConfig;
import cn.longmaster.ihmonitor.core.http.SimpleHttpRequester;

/**
 * 患者基本信息 requester
 * Created by JinKe on 2016-07-26.
 */
public class PatientBaseRequester extends SimpleHttpRequester<PatientInfo> {
    public String token;//同步token标识
    public int appointmentId;//预约ID

    public PatientBaseRequester(@NonNull OnResultListener<PatientInfo> onResultListener) {
        super(onResultListener);
    }

    @Override
    protected String getUrl() {
        return AppConfig.getClientApiUrl();
    }

    @Override
    protected int getOpType() {
        return OpTypeConfig.CLIENTAPI_OPTYE_PATIENT_BASE_INFO;
    }

    @Override
    protected String getTaskId() {
        return getOpType() + "";
    }

    @Override
    protected PatientInfo onDumpData(JSONObject data) throws JSONException {
        PatientInfo patientInfo = new PatientInfo();
        patientInfo.setPatientBaseInfo(JsonHelper.toObject(data.getJSONObject("data"), PatientBaseInfo.class));
        patientInfo.setPhysicalPlanList(JsonHelper.toList(data.getJSONArray("physical_plan"), PhysicalPlanInfo.class));
        patientInfo.setOperationPlanList(JsonHelper.toList(data.getJSONArray("operation_plan"), OperationPlanInfo.class));
        patientInfo.setDrugPlanList(JsonHelper.toList(data.getJSONArray("drug_plan"), DrugPlanInfo.class));
        patientInfo.setHistoryInfoList(JsonHelper.toList(data.getJSONArray("history_info"), HistoryInfo.class));
        return patientInfo;
    }

    @Override
    protected void onPutParams(Map<String, Object> params) {
        params.put("token", token);
        params.put("appointment_id", appointmentId);
    }
}
