package cn.longmaster.ihuvc.core.http;

/**
 * 结果监听器
 * Created by yangyong on 2015-07-07
 */
public interface OnResultListener<Data> {

    //通用返回结果定义
    int RESULT_SUCCESS = 0;//请求成功
    int RESULT_FAILED = -1;//请求失败 一般是网络错误
    int RESULT_SERVER_CODE_ERROR = -2;//请求失败:服务器返回结果不为200
    int RESULT_APPOINTMENT_UNEXIST = -102;//就诊不存在
    int RESULT_RELATION_PWD_ERROR = -106;//密码错误

    int RESULT_AUTH_CODE_ERROR = 1030056;//参数（鉴权key）：c_auth 校验失败

    /**
     * 请求结果回调
     *
     * @param baseResult 接口返回
     * @param data   请求回调数据
     */
    void onResult(BaseResult baseResult, Data data);
}
