package cn.longmaster.ihmonitor.core.http;

/**
 * 接口配置
 */
public class OpTypeConfig {
    //duws
    private static final int DUWS_OPTYPE_BASE = 6000;
    public static final int DUWS_OPTYPE_REG_ACCOUNT = DUWS_OPTYPE_BASE + 1;//注册
    public static final int DUWS_OPTYPE_CHECK_VERIFY_CODE = DUWS_OPTYPE_BASE + 2;//验证码校验
    public static final int DUWS_OPTYPE_REG_CODE = DUWS_OPTYPE_BASE + 4;//获取验证码
    public static final int DUWS_OPTYPE_ACTIVE_ACCOUNT = DUWS_OPTYPE_BASE + 6;//账号激活
    public static final int DUWS_OPTYPE_QUERY_ACCOUNT = DUWS_OPTYPE_BASE + 7;//第三方登录
    public static final int DUWS_OPTYPE_PASSWORD_CHANGE = DUWS_OPTYPE_BASE + 8; //密码修改
    public static final int DUWS_OPTYPE_MESSAGE_REGISTER = DUWS_OPTYPE_BASE + 9;//短信注册
    public static final int DUWS_OPTYPE_SEND_MESSAGE = DUWS_OPTYPE_BASE + 10;//发送短信
    public static final int DUWS_OPTYPE_AGENT_SERVICE = DUWS_OPTYPE_BASE + 16;//获取代理服务器
    public static final int DUWS_OPTYPE_CHECK_ACCOUNT_EXIST = DUWS_OPTYPE_BASE + 11;//查询账号是否存在
    public static final int DUWS_OPTYPE_APP_KEY = DUWS_OPTYPE_BASE + 101;//获取AppKey

    //dws
    private static final int DWS_OPTYPE_BASE = 1000;
    public static final int DWS_OPTYPE_UPLOAD_MATERIAL = DWS_OPTYPE_BASE + 8;//上传病历文件
    public static final int DWS_OPTYPE_DEL_MATERIAL = DWS_OPTYPE_BASE + 16;//删除未审核辅助资料
    public static final int DWS_OPTYPE_COMMIT_MATERIAL = 5024;//上传病历文件

    //dfs
    private static final int NGINX_OPTYPE_BASE = 3000;
    public static final int NGINX_OPTYPE_UPLOAD_MATERIAL = NGINX_OPTYPE_BASE + 4;//上传病历文件
    public static final int NGINX_OPTYPE_UPLOAD_VOICE = NGINX_OPTYPE_BASE + 5;//上传语音文件
    public static final int NGINX_OPTYPE_UPLOAD_CONSULT = NGINX_OPTYPE_BASE + 10;//上传/下载医嘱图片

    //ivws
    private static final int IVWS_OPTYPE_BASE = 7000;
    public static final int IVWS_OPTYPE_IDCARD_VERIFY = IVWS_OPTYPE_BASE + 1;//身份证验证

    //clientApi
    private static final int CLIENTAPI_OPTYE_BASE = 100000;
    public static final int CLIENTAPI_OPTYE_HOSPITAL_INFO = CLIENTAPI_OPTYE_BASE + 1;//医院信息
    public static final int CLIENTAPI_OPTYE_MATERIAL_INFO = CLIENTAPI_OPTYE_BASE + 2;//系统材料配置
    public static final int CLIENTAPI_OPTYE_DEPARTMENT_INFO = CLIENTAPI_OPTYE_BASE + 3;//科室信息
    public static final int CLIENTAPI_OPTYE_TITLE_GRADE_INFO = CLIENTAPI_OPTYE_BASE + 4;//医生职称等级配置
    public static final int CLIENTAPI_OPTYE_AREA_GRADE_INFO = CLIENTAPI_OPTYE_BASE + 5;//医生区域等级配置
    public static final int CLIENTAPI_OPTYE_PACKAGE_INFO = CLIENTAPI_OPTYE_BASE + 6;//套餐信息配置
    public static final int CLIENTAPI_OPTYE_GRADE_PRICE_INFO = CLIENTAPI_OPTYE_BASE + 7;//价格档位配置
    public static final int CLIENTAPI_OPTYE_RUTINE_CONFIG = CLIENTAPI_OPTYE_BASE + 8;//常规配置
    public static final int CLIENTAPI_OPTYE_DOCTOR_BASE_INFO = CLIENTAPI_OPTYE_BASE + 9;//医生基本信息
    public static final int CLIENTAPI_OPTYE_PATIENT_BASE_INFO = CLIENTAPI_OPTYE_BASE + 10;//患者基本信息
    public static final int CLIENTAPI_OPTYE_BANNER_AND_QUICK_ENTRY = CLIENTAPI_OPTYE_BASE + 11;//客户端Banner与快捷入口配置
    public static final int CLIENTAPI_OPTYE_NATION_INFO_CONFIG = CLIENTAPI_OPTYE_BASE + 12;//民族信息配置
    public static final int CLIENTAPI_OPTYE_PRIVINCE_CITY_INFO_CONFIG = CLIENTAPI_OPTYE_BASE + 13;//省份城市信息配置

    public static final int CLIENTAPI_OPTYE_BANK_CARD = CLIENTAPI_OPTYE_BASE + 101;//用户银行卡信息
    public static final int CLIENTAPI_OPTYE_DEPARTMENT_RELATE = CLIENTAPI_OPTYE_BASE + 102;//科室关联信息
    public static final int CLIENTAPI_OPTYE_ISSUE_INVOICE = CLIENTAPI_OPTYE_BASE + 103;//开具发票
    public static final int CLIENTAPI_OPTYE_SERVICE_AUTHORITY = CLIENTAPI_OPTYE_BASE + 104;//根据医生ID获取服务权限
    public static final int CLIENTAPI_OPTYE_DOCTOR_GRADE_PRICE = CLIENTAPI_OPTYE_BASE + 105;//根据医生ID获取关联的价格档位
    public static final int CLIENTAPI_OPTYE_ALL_DEPARTMENT_LIST = CLIENTAPI_OPTYE_BASE + 106;//获取全部科室列表
    public static final int CLIENTAPI_OPTYE_ASSISTANT_DOCTOR = CLIENTAPI_OPTYE_BASE + 107;//拉取助理医师信息
    public static final int CLIENTAPI_OPTYE_AUXILIARY_MATERIAL_LIST = CLIENTAPI_OPTYE_BASE + 108;//获取辅助资料列表
    public static final int CLIENTAPI_OPTYE_GET_RECORD = CLIENTAPI_OPTYE_BASE + 109;//根据预约ID获取关联病历
    public static final int CLIENTAPI_OPTYE_DOCTOR_DIAGNOSIS = CLIENTAPI_OPTYE_BASE + 110;//根据预约ID拉取医嘱
    public static final int CLIENTAPI_OPTYE_ADD_REMARK = CLIENTAPI_OPTYE_BASE + 111;//给病历添加备注
    public static final int CLIENTAPI_OPTYE_USER_BILL_DETAIL = CLIENTAPI_OPTYE_BASE + 112;//拉取用户账单明细
    public static final int CLIENTAPI_OPTYE_DOCTOR_BY_DEPARTMENT = CLIENTAPI_OPTYE_BASE + 113;//按科室ID拉取医生列表
    public static final int CLIENTAPI_OPTYE_SEARCH_DOCTOR_BY_NAME = CLIENTAPI_OPTYE_BASE + 114;//根据医生姓名搜索医生
    public static final int CLIENTAPI_OPTYE_FINISH_APPOINT = CLIENTAPI_OPTYE_BASE + 115;//修改预约（视频完成）
    public static final int CLIENTAPI_OPTYE_DOCTOR_SCHEDULE = CLIENTAPI_OPTYE_BASE + 116;//获取医生排班信息
    public static final int CLIENTAPI_OPTYE_SCHEDULING_DOCTOR = CLIENTAPI_OPTYE_BASE + 117;//获取有排班的医生
    public static final int CLIENTAPI_OPTYE_FINISHED_SCHEDULE_LIST = CLIENTAPI_OPTYE_BASE + 118;//获取已完成排班列表
    public static final int CLIENTAPI_OPTYE_DOCTOR_CONSULT_STATISTIC = CLIENTAPI_OPTYE_BASE + 119;//统计医生的排班、影像服务、就诊、复诊数量
    public static final int CLIENTAPI_OPTYE_DOCTOR_FINANCE_STATISTIC = CLIENTAPI_OPTYE_BASE + 120;//医生财务统计
    public static final int CLIENTAPI_OPTYE_SCHEDULING_BY_DATE = CLIENTAPI_OPTYE_BASE + 121;//根据日期获取排班
    public static final int CLIENTAPI_OPTYE_SKIP_APPOINT = CLIENTAPI_OPTYE_BASE + 122;//跳过预约
    public static final int CLIENTAPI_OPTYE_RELATE_RECORDER = CLIENTAPI_OPTYE_BASE + 123;//关联病历
    public static final int CLIENTAPI_OPTYE_SUPER_CONSULT_BY_RECURE = CLIENTAPI_OPTYE_BASE + 124;//根据复诊获取上级就诊信息
    public static final int CLIENTAPI_OPTYE_SCREEN_APPOINTMENT = CLIENTAPI_OPTYE_BASE + 125;//根据条件获取筛选预约信息
    public static final int CLIENTAPI_OPTYE_ISSUE_DOCTOR_ADVICE = CLIENTAPI_OPTYE_BASE + 126;//出具医嘱
    public static final int CLIENTAPI_OPTYE_SET_DEFAULT_ACCOUNT = CLIENTAPI_OPTYE_BASE + 127;//设置用户银行账户为使用状态
    public static final int CLIENTAPI_OPTYE_WRITE_CONSULT_ADVICE = CLIENTAPI_OPTYE_BASE + 128;//给预约填写会诊意见，结束预约
    public static final int CLIENTAPI_OPTYE_DELETE_VOICE_RECORD = CLIENTAPI_OPTYE_BASE + 129;//删除会诊意见录音
    public static final int CLIENTAPI_OPTYE_DELETE_CONSULT_ADVICE_PICTURE = CLIENTAPI_OPTYE_BASE + 130;//删除会诊意见图片
    public static final int CLIENTAPI_OPTYE_SET_PARTICIPATE_RECONSULT = CLIENTAPI_OPTYE_BASE + 131;//设置首诊医生是否参与复诊
    public static final int CLIENTAPI_OPTYE_LAUNCH_CONSULT = CLIENTAPI_OPTYE_BASE + 132;//发起会诊
    public static final int CLIENTAPI_OPTYE_GET_DOCTOR_LIST = CLIENTAPI_OPTYE_BASE + 133;//获取全部医生列表
    public static final int CLIENTAPI_OPTYE_GET_SCHEDULING_BY_DEPARTMENT = CLIENTAPI_OPTYE_BASE + 134;//根据科室ID和日期区间获取排班/影像服务列表
    public static final int CLIENTAPI_OPTYE_ADD_SCHEDULING = CLIENTAPI_OPTYE_BASE + 135;//新增排班
    public static final int CLIENTAPI_OPTYE_REVISE_SCHEDULE = CLIENTAPI_OPTYE_BASE + 136;//修改排班
    public static final int CLIENTAPI_OPTYE_ADD_SERVICE = CLIENTAPI_OPTYE_BASE + 137;//新增影像服务
    public static final int CLIENTAPI_OPTYE_REVISE_IMAGE_SERVICE = CLIENTAPI_OPTYE_BASE + 138;//修改影像服务
    public static final int CLIENTAPI_OPTYE_OPEN_SCHEDULING = CLIENTAPI_OPTYE_BASE + 139;//开启排班/影像服务
    public static final int CLIENTAPI_OPTYE_CLOSE_SERVICE = CLIENTAPI_OPTYE_BASE + 140;//关闭排班/影像服务
    public static final int CLIENTAPI_OPTYE_DELETE_SCHEDULING = CLIENTAPI_OPTYE_BASE + 141;//删除排班/影像服务
    public static final int CLIENTAPI_OPTYE_SHCEDULE_OR_IMAGE = CLIENTAPI_OPTYE_BASE + 142;//获取排班/影像服务项目
    public static final int CLIENTAPI_OPTYE_SHCEDULE_APPOINTMENT_LIST = CLIENTAPI_OPTYE_BASE + 144;//获取排班预约列表
    public static final int CLIENTAPI_OPTYE_SERVICE_APPOINTMENT = CLIENTAPI_OPTYE_BASE + 145;//获取影像服务预约列表
    public static final int CLIENTAPI_OPTYE_PATIENT_CONSULT_LIST = CLIENTAPI_OPTYE_BASE + 146;//根据状态获取患者就诊列表
    public static final int CLIENTAPI_OPTYE_BALANCE_WITHDRAWAL = CLIENTAPI_OPTYE_BASE + 147;//余额提现接口
    public static final int CLIENTAPI_OPTYE_CASE_REMARK_LIST = CLIENTAPI_OPTYE_BASE + 148;//获取病历备注列表
    public static final int CLIENTAPI_OPTYE_APPOINTMENT_BY_ID = CLIENTAPI_OPTYE_BASE + 149;//根据预约ID获取预约信息
    public static final int CLIENTAPI_OPTYE_APPOINTMENT_LIST_BY_DOCTOR = CLIENTAPI_OPTYE_BASE + 150;//根据医生ID和日期及发起人类型拉取医生的预约列表
    public static final int CLIENTAPI_OPTYE_APPOINTMENT_ORDER = CLIENTAPI_OPTYE_BASE + 151;//根据预约ID拉取预约订单信息
    public static final int CLIENTAPI_OPTYE_SCHEDULING_STATISTIC = CLIENTAPI_OPTYE_BASE + 152;//根据医生ID和服务模式统计排班模式价格区间
    public static final int CLIENTAPI_OPTYE_BIND_RECORD = CLIENTAPI_OPTYE_BASE + 153;//关联病历
    public static final int CLIENTAPI_OPTYE_COMMIT_MATERIAL = CLIENTAPI_OPTYE_BASE + 154;//根据预约ID提交材料审核
    public static final int CLIENTAPI_OPTYE_ADD_BANK_ACCOUNT = CLIENTAPI_OPTYE_BASE + 155;//添加银行账户
    public static final int CLIENTAPI_OPTYE_DELETE_BANK_ACCOUNT = CLIENTAPI_OPTYE_BASE + 156;//删除银行账户
    public static final int CLIENTAPI_OPTYE_BACKLOG = CLIENTAPI_OPTYE_BASE + 164;//根据医生ID拉取待办事项(包括我收到和我发起)
    public static final int CLIENTAPI_OPTYE_MESSAGE_LIST = CLIENTAPI_OPTYE_BASE + 205;//根据用户拉取通知信息列表
    public static final int CLIENTAPI_OPTYE_INFORMATION_VERIFY = CLIENTAPI_OPTYE_BASE + 207;//根据预约ID和患者姓名验证信息正确性

    public static final int CLIENTAPI_OPTYE_LOCATION_UPDATE = 9001; //更新定位信息

}
