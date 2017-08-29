package cn.longmaster.ihmonitor.core.http.config;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.longmaster.doctorlibrary.util.common.FileUtil;
import cn.longmaster.doctorlibrary.util.common.StringUtil;
import cn.longmaster.doctorlibrary.util.imageloader.ImageLoader;
import cn.longmaster.doctorlibrary.util.json.JsonHelper;
import cn.longmaster.doctorlibrary.util.log.Logger;
import cn.longmaster.doctorlibrary.util.thread.AsyncResult;
import cn.longmaster.ihmonitor.core.app.AppApplication;
import cn.longmaster.ihmonitor.core.app.AppConfig;
import cn.longmaster.ihmonitor.core.db.DBHelper;
import cn.longmaster.ihmonitor.core.db.contract.BaseConfigContract;
import cn.longmaster.ihmonitor.core.entity.config.AreaGradeInfo;
import cn.longmaster.ihmonitor.core.entity.config.BaseConfigInfo;
import cn.longmaster.ihmonitor.core.entity.config.DepartmentInfo;
import cn.longmaster.ihmonitor.core.entity.config.DoctorBaseInfo;
import cn.longmaster.ihmonitor.core.entity.config.HospitalInfo;
import cn.longmaster.ihmonitor.core.entity.config.PatientInfo;
import cn.longmaster.ihmonitor.core.entity.config.TitleGradeInfo;
import cn.longmaster.ihmonitor.core.http.BaseResult;
import cn.longmaster.ihmonitor.core.http.OnResultListener;
import cn.longmaster.ihmonitor.core.http.OpTypeConfig;
import cn.longmaster.ihmonitor.core.manager.BaseManager;
import cn.longmaster.ihmonitor.core.manager.config.RequestParams;
import cn.longmaster.ihmonitor.core.manager.storage.DBManager;
import cn.longmaster.ihmonitor.core.manager.storage.DatabaseTask;
import cn.longmaster.ihmonitor.core.manager.storage.SdManager;

/**
 * 基本配置管理
 * Created by Yang² on 2016/7/26.
 */
public class BaseConfigManager extends BaseManager {
    private final String TAG = BaseConfigManager.class.getSimpleName();

    @Override
    public void onManagerCreate(AppApplication application) {

    }

    @Override
    public void onAllManagerCreated() {
        super.onAllManagerCreated();
    }

    /**
     * 获取基本配置信息
     * <p/>
     * RequestParams中配置不同的类型可以请求以下几种配置信息
     * 医院信息
     * 系统材料配置
     * 科室信息
     * 医生职称等级配置
     * 医生区域等级配置
     * 套餐信息配置
     * 价格档位配置
     * 常规配置
     * 医生基本信息
     * 患者基本信息
     * 客户端Banner与快捷入口配置
     * 民族信息配置
     * 省份城市信息配置
     *
     * @param params
     * @param listener
     */
    public void getBaseConfigFromNet(RequestParams params, OnResultListener listener) {
        Logger.log(Logger.COMMON, TAG + "->getBaseConfigFromNet()->params:" + params);
        switch (params.getType()) {
            case OpTypeConfig.CLIENTAPI_OPTYE_HOSPITAL_INFO:
                getHospitalInfo(params, listener);
                break;

            case OpTypeConfig.CLIENTAPI_OPTYE_DEPARTMENT_INFO:
                getDepartmentInfo(params, listener);
                break;

            case OpTypeConfig.CLIENTAPI_OPTYE_TITLE_GRADE_INFO:
                getTitleGradeInfoList(params, listener);
                break;

            case OpTypeConfig.CLIENTAPI_OPTYE_AREA_GRADE_INFO:
                getAreaGradeInfoList(params, listener);
                break;

            case OpTypeConfig.CLIENTAPI_OPTYE_DOCTOR_BASE_INFO:
                getDoctorBaseInfo(params, listener);
                break;

            case OpTypeConfig.CLIENTAPI_OPTYE_PATIENT_BASE_INFO:
                getPatientBaseInfo(params, listener);
                break;

            default:
                if (AppConfig.IS_DEBUG_MODE) {
                    throw new RuntimeException("不支持的类型");
                }
        }
    }

    /**
     * 医院信息
     *
     * @param params   请求参数
     * @param listener 回调接口
     */
    private void getHospitalInfo(final RequestParams params, final OnResultListener listener) {
        HospitalRequester requester = new HospitalRequester(new OnResultListener<HospitalInfo>() {
            @Override
            public void onResult(BaseResult baseResult, HospitalInfo hospitalInfo) {
                listener.onResult(baseResult, hospitalInfo);
                if (baseResult.getCode() == RESULT_SUCCESS && hospitalInfo != null) {
                    saveData(baseResult.getOpType(), params.getHospitalId(), baseResult.getToken(), JsonHelper.toJSONObject(hospitalInfo).toString());
                }
            }
        });
        requester.token = params.getToken();
        requester.hospitalId = params.getHospitalId();
        requester.doPost();
    }


    /**
     * 科室信息
     *
     * @param params   请求参数
     * @param listener 回调接口
     */
    private void getDepartmentInfo(final RequestParams params, final OnResultListener listener) {
        DepartmentRequester requester = new DepartmentRequester(new OnResultListener<DepartmentInfo>() {
            @Override
            public void onResult(BaseResult baseResult, DepartmentInfo departmentInfo) {
                listener.onResult(baseResult, departmentInfo);
                if (baseResult.getCode() == RESULT_SUCCESS && departmentInfo != null) {
                    saveData(baseResult.getOpType(), params.getDepartmentId(), baseResult.getToken(), JsonHelper.toJSONObject(departmentInfo).toString());
                }
            }
        });
        requester.token = params.getToken();
        requester.departmentId = params.getDepartmentId();
        requester.doPost();
    }

    /**
     * 医生职称等级配置
     *
     * @param params   请求参数
     * @param listener 回调接口
     */
    private void getTitleGradeInfoList(final RequestParams params, final OnResultListener listener) {
        TitleGradeRequester requester = new TitleGradeRequester(new OnResultListener<List<TitleGradeInfo>>() {
            @Override
            public void onResult(BaseResult baseResult, List<TitleGradeInfo> titleGradeInfoList) {
                listener.onResult(baseResult, titleGradeInfoList);
                if (baseResult.getCode() == RESULT_SUCCESS && titleGradeInfoList != null) {
                    saveData(baseResult.getOpType(), 0, baseResult.getToken(), JsonHelper.toJSONArray(titleGradeInfoList).toString());
                }
            }
        });
        requester.token = params.getToken();
        requester.doPost();
    }

    /**
     * 医生区域等级配置
     *
     * @param params   请求参数
     * @param listener 回调接口
     */
    private void getAreaGradeInfoList(final RequestParams params, final OnResultListener listener) {
        AreaGradeRequester requester = new AreaGradeRequester(new OnResultListener<List<AreaGradeInfo>>() {
            @Override
            public void onResult(BaseResult baseResult, List<AreaGradeInfo> areaGradeInfoList) {
                listener.onResult(baseResult, areaGradeInfoList);
                if (baseResult.getCode() == RESULT_SUCCESS && areaGradeInfoList != null) {
                    saveData(baseResult.getOpType(), 0, baseResult.getToken(), JsonHelper.toJSONArray(areaGradeInfoList).toString());
                }
            }
        });
        requester.token = params.getToken();
        requester.doPost();
    }

    /**
     * 医生基本信息
     *
     * @param params   请求参数
     * @param listener 回调接口
     */
    private void getDoctorBaseInfo(final RequestParams params, final OnResultListener listener) {
        DoctorBaseRequester requester = new DoctorBaseRequester(new OnResultListener<DoctorBaseInfo>() {
            @Override
            public void onResult(BaseResult baseResult, DoctorBaseInfo doctorBaseInfo) {
                listener.onResult(baseResult, doctorBaseInfo);
                if (baseResult.getCode() == RESULT_SUCCESS && doctorBaseInfo != null) {
                    saveData(baseResult.getOpType(), params.getDoctorId(), baseResult.getToken(), JsonHelper.toJSONObject(doctorBaseInfo).toString());
                }
            }
        });
        requester.token = params.getToken();
        requester.doctorId = params.getDoctorId();
        requester.doPost();
    }

    /**
     * 患者基本信息
     *
     * @param params   请求参数
     * @param listener 回调接口
     */
    private void getPatientBaseInfo(final RequestParams params, final OnResultListener listener) {
        PatientBaseRequester requester = new PatientBaseRequester(new OnResultListener<PatientInfo>() {
            @Override
            public void onResult(BaseResult baseResult, PatientInfo patientInfo) {
                listener.onResult(baseResult, patientInfo);
                if (baseResult.getCode() == RESULT_SUCCESS && patientInfo != null) {
                    saveData(baseResult.getOpType(), params.getAppointmentId(), baseResult.getToken(), JsonHelper.toJSONObject(patientInfo).toString());
                }
            }
        });
        requester.token = params.getToken();
        requester.appointmentId = params.getAppointmentId();
        requester.doPost();
    }

    private void saveData(final int type, final int dataId, final String token, final String content) {
        if (!TextUtils.isEmpty(content)) {
            DatabaseTask<Void> task = new DatabaseTask<Void>() {
                @Override
                public AsyncResult<Void> runOnDBThread(AsyncResult<Void> asyncResult, DBHelper dbHelper) {
                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                    database.beginTransaction();
                    try {
                        ContentValues values = new ContentValues();
                        values.put(BaseConfigContract.BaseConfigEntry.COLUMN_NAME_TYPE, type);
                        values.put(BaseConfigContract.BaseConfigEntry.COLUMN_NAME_DATE_ID, dataId);
                        values.put(BaseConfigContract.BaseConfigEntry.COLUMN_NAME_TOKEN, token);
                        values.put(BaseConfigContract.BaseConfigEntry.COLUMN_NAME_CONTENT, content);
                        database.delete(BaseConfigContract.BaseConfigEntry.TABLE_NAME, BaseConfigContract.BaseConfigEntry.COLUMN_NAME_TYPE + " =? AND " +
                                BaseConfigContract.BaseConfigEntry.COLUMN_NAME_DATE_ID + " =?", new String[]{String.valueOf(type), String.valueOf(dataId)});
                        Logger.log(Logger.COMMON, TAG + "存入数据->库基本配置信息:" + values.toString());
                        database.insert(BaseConfigContract.BaseConfigEntry.TABLE_NAME, null, values);
                        database.setTransactionSuccessful();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        database.endTransaction();
                    }
                    return asyncResult;
                }

                @Override
                public void runOnUIThread(AsyncResult<Void> asyncResult) {
                }
            };
            AppApplication.getInstance().getManager(DBManager.class).submitDatabaseTask(task);
        }
    }

    /**
     * 读取基本配置信息数据库数据
     *
     * @param type     数据类型
     * @param listener 回调
     */
    public void getBaseConfigFromDB(final int type, final OnGetBaseConfigStateChangeListener listener) {
        DatabaseTask<BaseConfigInfo> task = new DatabaseTask<BaseConfigInfo>() {
            @Override
            public AsyncResult<BaseConfigInfo> runOnDBThread(AsyncResult<BaseConfigInfo> asyncResult, DBHelper dbHelper) {
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = null;
                try {
                    String sql = "SELECT * FROM " + BaseConfigContract.BaseConfigEntry.TABLE_NAME + " WHERE "
                            + BaseConfigContract.BaseConfigEntry.COLUMN_NAME_TYPE + " = ?";
                    cursor = database.rawQuery(sql, new String[]{String.valueOf(type)});
                    Logger.log(Logger.COMMON, TAG + "getBaseConfigFormDB数据消息总条数：" + cursor.getCount());
                    BaseConfigInfo baseConfigInfo = new BaseConfigInfo();
                    String content = "";
                    while (cursor.moveToNext()) {
                        baseConfigInfo.setType(cursor.getInt(cursor.getColumnIndex(BaseConfigContract.BaseConfigEntry.COLUMN_NAME_TYPE)));
                        baseConfigInfo.setDataId(cursor.getInt(cursor.getColumnIndex(BaseConfigContract.BaseConfigEntry.COLUMN_NAME_DATE_ID)));
                        baseConfigInfo.setToken(cursor.getString(cursor.getColumnIndex(BaseConfigContract.BaseConfigEntry.COLUMN_NAME_TOKEN)));
                        content = cursor.getString(cursor.getColumnIndex(BaseConfigContract.BaseConfigEntry.COLUMN_NAME_CONTENT));
                    }
                    baseConfigInfo.setData(parseDataByType(type, content));
                    Logger.logI(Logger.COMMON, TAG + "BaseConfigInfo查询到的数据:" + baseConfigInfo.toString());
                    asyncResult.setData(baseConfigInfo);
                    database.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    database.endTransaction();
                    if (cursor != null && !cursor.isClosed())
                        cursor.close();
                }
                return asyncResult;
            }

            @Override
            public void runOnUIThread(AsyncResult<BaseConfigInfo> asyncResult) {
                listener.onGetBaseConfigStateChanged(asyncResult.getData());
            }
        };
        AppApplication.getInstance().getManager(DBManager.class).submitDatabaseTask(task);
    }

    /**
     * 获取基本配置
     *
     * @param type     数据类型
     * @param dataId   数据id
     * @param listener 回调接口
     */
    public void getBaseConfigFromDB(final int type, final int dataId, final OnGetBaseConfigStateChangeListener listener) {
        DatabaseTask<BaseConfigInfo> task = new DatabaseTask<BaseConfigInfo>() {
            @Override
            public AsyncResult<BaseConfigInfo> runOnDBThread(AsyncResult<BaseConfigInfo> asyncResult, DBHelper dbHelper) {
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                database.beginTransaction();
                Cursor cursor = null;
                try {
                    String sql = "SELECT * FROM " + BaseConfigContract.BaseConfigEntry.TABLE_NAME + " WHERE "
                            + BaseConfigContract.BaseConfigEntry.COLUMN_NAME_TYPE + " = ? AND " + BaseConfigContract.BaseConfigEntry.COLUMN_NAME_DATE_ID + " = ?";
                    cursor = database.rawQuery(sql, new String[]{String.valueOf(type), String.valueOf(dataId)});
                    Logger.log(Logger.COMMON, TAG + "getBaseConfigByType数据消息总条数：" + cursor.getCount());
                    BaseConfigInfo baseConfigInfo = null;
                    while (cursor.moveToNext()) {
                        baseConfigInfo = new BaseConfigInfo();
                        baseConfigInfo.setType(cursor.getInt(cursor.getColumnIndex(BaseConfigContract.BaseConfigEntry.COLUMN_NAME_TYPE)));
                        baseConfigInfo.setDataId(cursor.getInt(cursor.getColumnIndex(BaseConfigContract.BaseConfigEntry.COLUMN_NAME_DATE_ID)));
                        baseConfigInfo.setToken(cursor.getString(cursor.getColumnIndex(BaseConfigContract.BaseConfigEntry.COLUMN_NAME_TOKEN)));
                        String content = cursor.getString(cursor.getColumnIndex(BaseConfigContract.BaseConfigEntry.COLUMN_NAME_CONTENT));
                        baseConfigInfo.setData(parseDataByType(type, content));
                    }

                    Logger.logI(Logger.COMMON, TAG + "BaseConfigInfo查询到的数据:" + baseConfigInfo);
                    asyncResult.setData(baseConfigInfo);
                    database.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    database.endTransaction();
                    if (cursor != null && !cursor.isClosed())
                        cursor.close();
                }
                return asyncResult;
            }

            @Override
            public void runOnUIThread(AsyncResult<BaseConfigInfo> asyncResult) {
                listener.onGetBaseConfigStateChanged(asyncResult.getData());
            }
        };
        AppApplication.getInstance().getManager(DBManager.class).submitDatabaseTask(task);
    }

    private Object parseDataByType(int type, String content) {
        Logger.log(Logger.COMMON, TAG + "->parseDataByType()->type:" + type + ", content:" + content);
        if (StringUtil.isEmpty(content)) {
            return null;
        }

        switch (type) {
            case OpTypeConfig.CLIENTAPI_OPTYE_HOSPITAL_INFO:
                try {
                    return JsonHelper.toObject(new JSONObject(content), HospitalInfo.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case OpTypeConfig.CLIENTAPI_OPTYE_DEPARTMENT_INFO:
                try {
                    return JsonHelper.toObject(new JSONObject(content), DepartmentInfo.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case OpTypeConfig.CLIENTAPI_OPTYE_TITLE_GRADE_INFO:
                try {
                    return JsonHelper.toList(new JSONArray(content), TitleGradeInfo.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case OpTypeConfig.CLIENTAPI_OPTYE_AREA_GRADE_INFO:
                try {
                    return JsonHelper.toList(new JSONArray(content), AreaGradeInfo.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case OpTypeConfig.CLIENTAPI_OPTYE_DOCTOR_BASE_INFO:
                try {
                    return JsonHelper.toObject(new JSONObject(content), DoctorBaseInfo.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case OpTypeConfig.CLIENTAPI_OPTYE_PATIENT_BASE_INFO:
                try {
                    return JsonHelper.toObject(new JSONObject(content), PatientInfo.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        return null;
    }

    /**
     * 删除信息
     *
     * @param type 数据类型
     */
    public void deleteData(int type) {
        deleteData(type, -1);
    }

    /**
     * 删除信息
     *
     * @param type 数据类型
     * @param id   数据id
     */
    public void deleteData(final int type, final int id) {
        DatabaseTask<Void> task = new DatabaseTask<Void>() {
            @Override
            public AsyncResult<Void> runOnDBThread(AsyncResult<Void> asyncResult, DBHelper dbHelper) {
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                database.beginTransaction();
                try {
                    if (id == -1) {
                        database.delete(BaseConfigContract.BaseConfigEntry.TABLE_NAME, BaseConfigContract.BaseConfigEntry.COLUMN_NAME_TYPE + " =? ", new String[]{String.valueOf(type)});
                    } else {
                        database.delete(BaseConfigContract.BaseConfigEntry.TABLE_NAME, BaseConfigContract.BaseConfigEntry.COLUMN_NAME_TYPE + " =?AND " +
                                BaseConfigContract.BaseConfigEntry.COLUMN_NAME_DATE_ID + " =?", new String[]{String.valueOf(type), String.valueOf(id)});
                    }
                    database.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    database.endTransaction();
                }
                return asyncResult;
            }

            @Override
            public void runOnUIThread(AsyncResult<Void> asyncResult) {
            }
        };
        AppApplication.getInstance().getManager(DBManager.class).submitDatabaseTask(task);
    }

    /**
     * 清除缓存和本地文件
     *
     * @param id
     */
    public void deleteAvatar(int id) {
        Logger.logI(Logger.COMMON, "deleteAvatar-->清除头像缓存和本地文件");
        //清除缓存和本地文件
        String avatarFilePath = AppApplication.getInstance().getManager(SdManager.class).getAppointAvatarFilePath(id + "");
        //删除头像缓存
        ImageLoader.getInstance().removeCache(avatarFilePath);
        FileUtil.deleteFile(avatarFilePath);
    }


    public interface OnGetBaseConfigStateChangeListener {
        void onGetBaseConfigStateChanged(BaseConfigInfo baseConfigInfo);
    }
}
