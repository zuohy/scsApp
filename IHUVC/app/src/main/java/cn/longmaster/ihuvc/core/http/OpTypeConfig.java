package cn.longmaster.ihuvc.core.http;

/**
 * 接口配置
 * Created by yangyong on 16/7/14.
 */
public class OpTypeConfig {
    //dws
    private static final int DWS_OPTYPE_BASE = 1000;
    public static final int DWS_OPTYPE_UPLOAD_MATERIAL = DWS_OPTYPE_BASE + 8;//上传病历文件

    //dfs
    private static final int NGINX_OPTYPE_BASE = 3000;
    public static final int NGINX_OPTYPE_UPLOAD_MATERIAL = NGINX_OPTYPE_BASE + 4;//上传病历文件

    //clientApi
    public static final int CLIENTAPI_OPTYE_PATIENT_BASE_INFO = 100010;//患者基本信息
    public static final int CLIENTAPI_OPTYE_CHECK_APPOINTMENT_PASSWORD = 100208;//验证预约ID和关联密码的正确性
    public static final int CLIENTAPI_OPTYE_CHECK_VERSION = 100209;//根据客户端类型获取版本更新数据
    public static final int CLIENTAPI_OPTYE_APPOINTMENT_BY_ID = 100149;//根据预约ID获取预约信息
    public static final int CLIENTAPI_OPTYE_MATERIAL_TYPE= 100210;//根据预约ID获取材料类型信息以及类型对应的材料数量

}
