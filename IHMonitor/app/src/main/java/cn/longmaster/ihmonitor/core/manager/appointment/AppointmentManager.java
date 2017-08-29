package cn.longmaster.ihmonitor.core.manager.appointment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.longmaster.doctorlibrary.util.handler.OMMap;
import cn.longmaster.doctorlibrary.util.log.Logger;
import cn.longmaster.doctorlibrary.util.timeout.TimeoutHelper;
import cn.longmaster.ihmonitor.core.app.AppApplication;
import cn.longmaster.ihmonitor.core.entity.appointment.AppointmentInfo;
import cn.longmaster.ihmonitor.core.entity.config.BaseConfigInfo;
import cn.longmaster.ihmonitor.core.entity.config.PatientInfo;
import cn.longmaster.ihmonitor.core.http.BaseResult;
import cn.longmaster.ihmonitor.core.http.OnResultListener;
import cn.longmaster.ihmonitor.core.http.OpTypeConfig;
import cn.longmaster.ihmonitor.core.http.appointment.AppointmentByIdRequester;
import cn.longmaster.ihmonitor.core.http.config.BaseConfigManager;
import cn.longmaster.ihmonitor.core.manager.BaseManager;
import cn.longmaster.ihmonitor.core.manager.config.RequestParams;
import cn.longmaster.ihmonitor.core.manager.dcp.DcpManager;


public class AppointmentManager extends BaseManager implements TimeoutHelper.OnTimeoutCallback<Integer> {
    private final String TAG = AppointmentManager.class.getSimpleName();

    private Map<Integer, OnResultListener<AppointmentInfo>> mAppointmentInfoRequsters = new HashMap<>();
    private OMMap<Integer, OnResultListener> mPatientInfoRequesters = new OMMap<>();
    private OnGetRoomInfoStateChangeListener mOnGetRoomInfoStateChangeListener;
    private final int mDividerTime = 5 * 1000;
    private final int MAX_WAITING_TIME = 60 * 1000;//最长等待时间
    private TimeoutHelper<Integer> mTimeoutHelper;

    @Override
    public void onManagerCreate(AppApplication application) {
        mTimeoutHelper = new TimeoutHelper();
        mTimeoutHelper.setCallback(this);
    }

    public void regOnGetRoomInfoStateChangeListener(OnGetRoomInfoStateChangeListener listener) {
        this.mOnGetRoomInfoStateChangeListener = listener;
    }

    public void unRegOnGetRoomInfoStateChangeListener() {
        this.mOnGetRoomInfoStateChangeListener = null;
    }

    public void getRoomListInfo() {
        mTimeoutHelper.request(0, MAX_WAITING_TIME);
        AppApplication.getInstance().getManager(DcpManager.class).getRoomListInfo();
    }

    public void onGetRoomListInfo(int result, String content) {
        Logger.log(Logger.APPOINTMENT, TAG + "->onGetRoomListInfo()->result:" + result + ", content:" + content);
        mTimeoutHelper.cancel(0);
        if (mOnGetRoomInfoStateChangeListener == null) {
            return;
        }

        if (result == 0) {
            try {
                //{"_actionType":2,"_reserved":"","_result":0,"_roomList":[{"_roomID":11460},{"_roomID":13684}],"_roomNum":2}
                JSONObject jsonObject = new JSONObject(content);
                int roomNum = jsonObject.optInt("_roomNum", 0);
                if (roomNum == 0) {
                    mOnGetRoomInfoStateChangeListener.onGetRoomInfoStateChanged(new ArrayList<Integer>());
                } else {
                    List<Integer> appointmentList = new ArrayList<>();
                    JSONArray jsonArray = jsonObject.optJSONArray("_roomList");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        appointmentList.add(jsonArray.getJSONObject(i).optInt("_roomID"));
                    }
                    mOnGetRoomInfoStateChangeListener.onGetRoomInfoStateChanged(appointmentList);
                }
                AppApplication.HANDLER.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getRoomListInfo();
                    }
                }, mDividerTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            getRoomListInfo();
        }
    }

    /**
     * 根据预约id获取预约信息
     * 先获取本地数据的数据,如果没有为null就从服务器拉取
     *
     * @param appointmentId 预约id
     * @param listener
     */
    public void getAppointmentInfo(final int appointmentId, final OnResultListener<AppointmentInfo> listener) {
        if (mAppointmentInfoRequsters.containsKey(appointmentId)) {
            mAppointmentInfoRequsters.put(appointmentId, listener);
            return;
        }
        mAppointmentInfoRequsters.put(appointmentId, listener);
        AppointmentByIdRequester requester = new AppointmentByIdRequester(new OnResultListener<AppointmentInfo>() {
            @Override
            public void onResult(BaseResult baseResult, AppointmentInfo appointmentInfo) {
                OnResultListener<AppointmentInfo> resultListener = mAppointmentInfoRequsters.remove(appointmentInfo.getBaseInfo().getAppointmentId());
                if (resultListener != null) {
                    resultListener.onResult(baseResult, appointmentInfo);
                }
            }
        });
        requester.appointmentId = appointmentId;
        requester.doPost();
    }

    /**
     * 获取患者信息
     * 先获取本地数据的数据,如果没有为null就从服务器拉取
     *
     * @param appointmentId 预约id
     * @param listener      回调
     */
    public void getPatientInfo(final int appointmentId, final OnResultListener<PatientInfo> listener) {
        AppApplication.getInstance().getManager(BaseConfigManager.class).getBaseConfigFromDB(OpTypeConfig.CLIENTAPI_OPTYE_PATIENT_BASE_INFO, appointmentId, new BaseConfigManager.OnGetBaseConfigStateChangeListener() {
            @Override
            public void onGetBaseConfigStateChanged(BaseConfigInfo baseConfigInfo) {
                if (baseConfigInfo != null && baseConfigInfo.getData() != null) {
                    BaseResult baseResult = new BaseResult();
                    baseResult.setCode(0);
                    listener.onResult(baseResult, (PatientInfo) baseConfigInfo.getData());
                }

                if (mPatientInfoRequesters.containsKey(appointmentId)) {
                    mPatientInfoRequesters.put(appointmentId, listener);
                    return;
                }
                mPatientInfoRequesters.put(appointmentId, listener);
                RequestParams params = new RequestParams();
                params.setToken(baseConfigInfo == null ? "0" : baseConfigInfo.getToken());
                params.setAppointmentId(appointmentId);
                params.setType(OpTypeConfig.CLIENTAPI_OPTYE_PATIENT_BASE_INFO);
                AppApplication.getInstance().getManager(BaseConfigManager.class).getBaseConfigFromNet(params, new OnResultListener() {
                    @Override
                    public void onResult(BaseResult baseResult, Object o) {
                        List<OnResultListener> listeners = mPatientInfoRequesters.remove(appointmentId);
                        PatientInfo patientInfo = (PatientInfo) o;
                        for (OnResultListener resultListener : listeners) {
                            resultListener.onResult(baseResult, patientInfo);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onTimeout(TimeoutHelper timeoutHelper, Integer taskId) {
        timeoutHelper.cancel(0);
        if (mOnGetRoomInfoStateChangeListener != null) {
            getRoomListInfo();
        }
    }

    public interface OnGetRoomInfoStateChangeListener {
        void onGetRoomInfoStateChanged(List<Integer> roomInfos);
    }
}
