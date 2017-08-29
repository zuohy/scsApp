package cn.longmaster.ihmonitor.core.manager.user;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import cn.longmaster.doctorlibrary.util.log.Logger;
import cn.longmaster.ihmonitor.core.app.AppApplication;
import cn.longmaster.ihmonitor.core.entity.config.BaseConfigInfo;
import cn.longmaster.ihmonitor.core.entity.config.DepartmentInfo;
import cn.longmaster.ihmonitor.core.entity.config.DoctorBaseInfo;
import cn.longmaster.ihmonitor.core.entity.config.HospitalInfo;
import cn.longmaster.ihmonitor.core.http.BaseResult;
import cn.longmaster.ihmonitor.core.http.OnResultListener;
import cn.longmaster.ihmonitor.core.http.OpTypeConfig;
import cn.longmaster.ihmonitor.core.http.config.BaseConfigManager;
import cn.longmaster.ihmonitor.core.manager.BaseManager;
import cn.longmaster.ihmonitor.core.manager.config.RequestParams;

/**
 * 医生信息管理类
 * Created by Tengshuxiang on 2016-08-25.
 */
public class DoctorManager extends BaseManager {
    private BaseConfigManager mBaseConfigManager;
    public Map<Integer, OnResultListener> mDoctorInfoRequesters = new HashMap<>();
    public Map<Integer, OnResultListener> mHospitalInfoRequesters = new HashMap<>();
    public Map<Integer, OnResultListener> mDepartmentInfoRequesters = new HashMap<>();

    @Override
    public void onManagerCreate(AppApplication application) {
    }

    @Override
    public void onAllManagerCreated() {
        super.onAllManagerCreated();
        mBaseConfigManager = getManager(BaseConfigManager.class);
    }

    public void getDoctorInfo(final int doctorId, final OnResultListener<DoctorBaseInfo> listener) {
        mBaseConfigManager.getBaseConfigFromDB(OpTypeConfig.CLIENTAPI_OPTYE_DOCTOR_BASE_INFO, doctorId, new BaseConfigManager.OnGetBaseConfigStateChangeListener() {
            @Override
            public void onGetBaseConfigStateChanged(BaseConfigInfo baseConfigInfo) {
                if (baseConfigInfo != null && baseConfigInfo.getData() != null) {
                    listener.onResult(new BaseResult(), (DoctorBaseInfo) baseConfigInfo.getData());
                    return;
                }
                if (mDoctorInfoRequesters.containsKey(doctorId)) {
                    mDoctorInfoRequesters.put(doctorId, listener);
                    return;
                }
                mDoctorInfoRequesters.put(doctorId, listener);
                RequestParams params = new RequestParams();
                params.setType(OpTypeConfig.CLIENTAPI_OPTYE_DOCTOR_BASE_INFO);
                params.setToken("0");
                params.setDoctorId(doctorId);
                mBaseConfigManager.getBaseConfigFromNet(params, new OnResultListener() {
                    @Override
                    public void onResult(BaseResult baseResult, Object o) {
                        if (baseResult.getCode() != RESULT_SUCCESS || o == null) {
                            mDoctorInfoRequesters.remove(doctorId);
                            return;
                        }

                        DoctorBaseInfo doctorBaseInfo = (DoctorBaseInfo) o;
                        OnResultListener resultListener = mDoctorInfoRequesters.remove(doctorId);
                        if (resultListener != null) {
                            resultListener.onResult(baseResult, doctorBaseInfo);
                        }
                    }
                });
            }
        });
    }

    public void getHospitalInfo(final int hospitalId, final OnResultListener<HospitalInfo> listener) {
        mBaseConfigManager.getBaseConfigFromDB(OpTypeConfig.CLIENTAPI_OPTYE_HOSPITAL_INFO, hospitalId, new BaseConfigManager.OnGetBaseConfigStateChangeListener() {
            @Override
            public void onGetBaseConfigStateChanged(BaseConfigInfo baseConfigInfo) {
                if (baseConfigInfo != null && baseConfigInfo.getData() != null) {
                    listener.onResult(new BaseResult(), (HospitalInfo) baseConfigInfo.getData());
                    return;
                }
                if (mHospitalInfoRequesters.containsKey(hospitalId)) {
                    mHospitalInfoRequesters.put(hospitalId, listener);
                    return;
                }
                mHospitalInfoRequesters.put(hospitalId, listener);
                RequestParams params = new RequestParams();
                params.setType(OpTypeConfig.CLIENTAPI_OPTYE_HOSPITAL_INFO);
                params.setToken("0");
                params.setHospitalId(hospitalId);
                mBaseConfigManager.getBaseConfigFromNet(params, new OnResultListener() {
                    @Override
                    public void onResult(BaseResult baseResult, Object o) {
                        if (baseResult.getCode() != RESULT_SUCCESS) {
                            mHospitalInfoRequesters.remove(hospitalId);
                            return;
                        }
                        OnResultListener resultListener = mHospitalInfoRequesters.remove(hospitalId);
                        if (resultListener != null) {
                            resultListener.onResult(baseResult, (HospitalInfo) o);
                        }
                    }
                });
            }
        });
    }

    public void getDepartmentInfo(final int departmentId, final OnResultListener<DepartmentInfo> listener) {
        mBaseConfigManager.getBaseConfigFromDB(OpTypeConfig.CLIENTAPI_OPTYE_DEPARTMENT_INFO, departmentId, new BaseConfigManager.OnGetBaseConfigStateChangeListener() {
            @Override
            public void onGetBaseConfigStateChanged(BaseConfigInfo baseConfigInfo) {
                if (baseConfigInfo != null && baseConfigInfo.getData() != null) {
                    listener.onResult(new BaseResult(), (DepartmentInfo) baseConfigInfo.getData());
                    return;
                }
                if (mDepartmentInfoRequesters.containsKey(departmentId)) {
                    mDepartmentInfoRequesters.put(departmentId, listener);
                    return;
                }
                mDepartmentInfoRequesters.put(departmentId, listener);
                RequestParams params = new RequestParams();
                params.setType(OpTypeConfig.CLIENTAPI_OPTYE_DEPARTMENT_INFO);
                params.setToken("0");
                params.setDepartmentId(departmentId);
                mBaseConfigManager.getBaseConfigFromNet(params, new OnResultListener() {
                    @Override
                    public void onResult(BaseResult baseResult, Object o) {
                        if (baseResult.getCode() != RESULT_SUCCESS) {
                            mDepartmentInfoRequesters.remove(departmentId);
                            return;
                        }
                        OnResultListener resultListener = mDepartmentInfoRequesters.remove(departmentId);
                        if (resultListener != null) {
                            resultListener.onResult(baseResult, (DepartmentInfo) o);
                        }
                    }
                });
            }
        });
    }

    /**
     * 获取医生信息
     *
     * @param id                  医生id
     * @param onGetDoctorListener 回调{@link OnGetDoctorListener}
     */
    public void getDoctor(final int id, final OnGetDoctorListener onGetDoctorListener) {
        mBaseConfigManager.getBaseConfigFromDB(OpTypeConfig.CLIENTAPI_OPTYE_DOCTOR_BASE_INFO, id, new BaseConfigManager.OnGetBaseConfigStateChangeListener() {
            @Override
            public void onGetBaseConfigStateChanged(BaseConfigInfo baseConfigInfo) {
                if (baseConfigInfo == null || baseConfigInfo.getData() == null) {
                    getDoctorFromNet("0", id, onGetDoctorListener, "0");
                    return;
                }
                onGetDoctorListener.onGetDoctor((DoctorBaseInfo) baseConfigInfo.getData());
                getHospitalFromDB(((DoctorBaseInfo) baseConfigInfo.getData()).getHospitalId(), onGetDoctorListener);
                getDepartmentFromDB(((DoctorBaseInfo) baseConfigInfo.getData()).getDepartmentId(), onGetDoctorListener);
                getDoctorFromNet(baseConfigInfo.getToken(), id, onGetDoctorListener, ((DoctorBaseInfo) baseConfigInfo.getData()).getAvaterToken());
            }
        });
    }

    private void getDoctorFromNet(String token, final int id, final OnGetDoctorListener onGetDoctorListener, final String avatarToken) {
        RequestParams params = new RequestParams();
        params.setType(OpTypeConfig.CLIENTAPI_OPTYE_DOCTOR_BASE_INFO);
        params.setToken(token);
        params.setDoctorId(id);
        mBaseConfigManager.getBaseConfigFromNet(params, new OnResultListener() {
            @Override
            public void onResult(BaseResult baseResult, Object o) {
                if (baseResult.getCode() != RESULT_SUCCESS) {
//                    onGetDoctorListener.onGetDoctor(null);
                    return;
                }
                if (o == null) {
                    return;
                }
                Logger.logI(Logger.COMMON, "getDoctorFromNet-->avatarToken：" + avatarToken + ",((DoctorBaseInfo) o).getAvaterToken():" + ((DoctorBaseInfo) o).getAvaterToken());
                if (!TextUtils.isEmpty(((DoctorBaseInfo) o).getAvaterToken()) && !((DoctorBaseInfo) o).getAvaterToken().equals(avatarToken)) {
                    mBaseConfigManager.deleteAvatar(id);
                }
                onGetDoctorListener.onGetDoctor((DoctorBaseInfo) o);
                getHospitalFromDB(((DoctorBaseInfo) o).getHospitalId(), onGetDoctorListener);
                getDepartmentFromDB(((DoctorBaseInfo) o).getDepartmentId(), onGetDoctorListener);
            }
        });
    }

    private void getHospitalFromDB(final int id, final OnGetDoctorListener onGetDoctorListener) {
        mBaseConfigManager.getBaseConfigFromDB(OpTypeConfig.CLIENTAPI_OPTYE_HOSPITAL_INFO, id, new BaseConfigManager.OnGetBaseConfigStateChangeListener() {
            @Override
            public void onGetBaseConfigStateChanged(BaseConfigInfo baseConfigInfo) {
                if (baseConfigInfo == null || baseConfigInfo.getData() == null) {
                    getHospitalFromNet("0", id, onGetDoctorListener);
                    return;
                }
                onGetDoctorListener.onGetHospital((HospitalInfo) baseConfigInfo.getData());
                getHospitalFromNet(baseConfigInfo.getToken(), id, onGetDoctorListener);
            }
        });
    }

    private void getHospitalFromNet(String token, int id, final OnGetDoctorListener onGetDoctorListener) {
        RequestParams params = new RequestParams();
        params.setType(OpTypeConfig.CLIENTAPI_OPTYE_HOSPITAL_INFO);
        params.setToken(token);
        params.setHospitalId(id);
        mBaseConfigManager.getBaseConfigFromNet(params, new OnResultListener() {
            @Override
            public void onResult(BaseResult baseResult, Object o) {
                if (baseResult.getCode() != RESULT_SUCCESS) {
//                    onGetDoctorListener.onGetHospital(null);
                    return;
                }
                onGetDoctorListener.onGetHospital((HospitalInfo) o);
            }
        });
    }

    private void getDepartmentFromDB(final int id, final OnGetDoctorListener onGetDoctorListener) {
        mBaseConfigManager.getBaseConfigFromDB(OpTypeConfig.CLIENTAPI_OPTYE_DEPARTMENT_INFO, id, new BaseConfigManager.OnGetBaseConfigStateChangeListener() {
            @Override
            public void onGetBaseConfigStateChanged(BaseConfigInfo baseConfigInfo) {
                if (baseConfigInfo == null || baseConfigInfo.getData() == null) {
                    getDepartmentFromNet("0", id, onGetDoctorListener);
                    return;
                }
                onGetDoctorListener.onGetDepartment((DepartmentInfo) baseConfigInfo.getData());
                getDepartmentFromNet(baseConfigInfo.getToken(), id, onGetDoctorListener);
            }
        });
    }

    private void getDepartmentFromNet(String token, int id, final OnGetDoctorListener onGetDoctorListener) {
        RequestParams params = new RequestParams();
        params.setType(OpTypeConfig.CLIENTAPI_OPTYE_DEPARTMENT_INFO);
        params.setToken(token);
        params.setDepartmentId(id);
        mBaseConfigManager.getBaseConfigFromNet(params, new OnResultListener() {
            @Override
            public void onResult(BaseResult baseResult, Object o) {
                if (baseResult.getCode() != RESULT_SUCCESS) {
//                    onGetDoctorListener.onGetDepartment(null);
                    return;
                }
                onGetDoctorListener.onGetDepartment((DepartmentInfo) o);
            }
        });
    }

    public interface OnGetDoctorListener {
        void onGetDoctor(DoctorBaseInfo doctorBaseInfo);

        void onGetHospital(HospitalInfo hospitalInfo);

        void onGetDepartment(DepartmentInfo departmentInfo);

    }
}
