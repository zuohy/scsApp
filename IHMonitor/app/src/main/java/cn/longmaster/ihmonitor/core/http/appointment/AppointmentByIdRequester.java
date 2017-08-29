package cn.longmaster.ihmonitor.core.http.appointment;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cn.longmaster.doctorlibrary.util.json.JsonHelper;
import cn.longmaster.ihmonitor.core.app.AppConfig;
import cn.longmaster.ihmonitor.core.entity.appointment.AppDialogInfo;
import cn.longmaster.ihmonitor.core.entity.appointment.ApplyDescInfo;
import cn.longmaster.ihmonitor.core.entity.appointment.ApplyDoctorInfo;
import cn.longmaster.ihmonitor.core.entity.appointment.AppointmentBaseInfo;
import cn.longmaster.ihmonitor.core.entity.appointment.AppointmentExtendsInfo;
import cn.longmaster.ihmonitor.core.entity.appointment.AppointmentInfo;
import cn.longmaster.ihmonitor.core.http.OnResultListener;
import cn.longmaster.ihmonitor.core.http.OpTypeConfig;
import cn.longmaster.ihmonitor.core.http.SimpleHttpRequester;

/**
 * 根据预约ID获取预约信息
 * Created by Yang² on 2016/7/28.
 */
public class AppointmentByIdRequester extends SimpleHttpRequester<AppointmentInfo> {
    public int appointmentId;//预约ID

    public AppointmentByIdRequester(@NonNull OnResultListener<AppointmentInfo> onResultListener) {
        super(onResultListener);
    }

    @Override
    protected String getUrl() {
        return AppConfig.getClientApiUrl();
    }

    @Override
    protected int getOpType() {
        return OpTypeConfig.CLIENTAPI_OPTYE_APPOINTMENT_BY_ID;
    }

    @Override
    protected String getTaskId() {
        return getOpType() + "";
    }

    @Override
    protected AppointmentInfo onDumpData(JSONObject data) throws JSONException {
        AppointmentInfo appointmentInfo = new AppointmentInfo();
        appointmentInfo.setBaseInfo(JsonHelper.toObject(data.getJSONObject("appointment"), AppointmentBaseInfo.class));
        String appointmentExtends = data.optString("appointment_extends").toString();
        if (!"".equals(appointmentExtends))
            appointmentInfo.setExtendsInfo(JsonHelper.toObject(data.getJSONObject("appointment_extends"), AppointmentExtendsInfo.class));
        String applyDesc = data.optString("apply_desc").toString();
        if (!"".equals(applyDesc))
            appointmentInfo.setApplyDescInfo(JsonHelper.toObject(data.getJSONObject("apply_desc"), ApplyDescInfo.class));
        appointmentInfo.setApplyDoctorInfoList(JsonHelper.toList(data.getJSONArray("apply_doctor"), ApplyDoctorInfo.class));
        appointmentInfo.setAppDialogInfo(JsonHelper.toObject(data.getJSONObject("app_diag"), AppDialogInfo.class));
        return appointmentInfo;
    }

    @Override
    protected void onPutParams(Map<String, Object> params) {
        params.put("appointment_id", appointmentId);
    }
}
