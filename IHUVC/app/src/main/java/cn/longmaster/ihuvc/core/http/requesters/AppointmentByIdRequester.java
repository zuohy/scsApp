package cn.longmaster.ihuvc.core.http.requesters;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cn.longmaster.doctorlibrary.utils.json.JsonHelper;
import cn.longmaster.ihuvc.core.AppConfig;
import cn.longmaster.ihuvc.core.entity.AppDialogInfo;
import cn.longmaster.ihuvc.core.entity.ApplyDescInfo;
import cn.longmaster.ihuvc.core.entity.ApplyDoctorInfo;
import cn.longmaster.ihuvc.core.entity.AppointmentBaseInfo;
import cn.longmaster.ihuvc.core.entity.AppointmentExtendsInfo;
import cn.longmaster.ihuvc.core.entity.AppointmentInfo;
import cn.longmaster.ihuvc.core.http.OnResultListener;
import cn.longmaster.ihuvc.core.http.OpTypeConfig;
import cn.longmaster.ihuvc.core.http.SimpleHttpRequester;

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
        params.put("user_id", 0);
    }
}
