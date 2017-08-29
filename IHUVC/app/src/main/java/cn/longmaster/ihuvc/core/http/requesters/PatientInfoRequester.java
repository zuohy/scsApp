package cn.longmaster.ihuvc.core.http.requesters;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cn.longmaster.doctorlibrary.utils.json.JsonHelper;
import cn.longmaster.ihuvc.core.AppConfig;
import cn.longmaster.ihuvc.core.entity.DrugPlanInfo;
import cn.longmaster.ihuvc.core.entity.HistoryInfo;
import cn.longmaster.ihuvc.core.entity.OperationPlanInfo;
import cn.longmaster.ihuvc.core.entity.PatientBaseInfo;
import cn.longmaster.ihuvc.core.entity.PatientInfo;
import cn.longmaster.ihuvc.core.entity.PhysicalPlanInfo;
import cn.longmaster.ihuvc.core.http.OnResultListener;
import cn.longmaster.ihuvc.core.http.OpTypeConfig;
import cn.longmaster.ihuvc.core.http.SimpleHttpRequester;

/**
 * 患者基本信息 requester
 * Created by JinKe on 2016-07-26.
 */
public class PatientInfoRequester extends SimpleHttpRequester<PatientInfo> {
    public String token;//同步token标识
    public int appointmentId;//预约ID

    public PatientInfoRequester(@NonNull OnResultListener<PatientInfo> onResultListener) {
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
        params.put("user_id", 0);
    }
}
