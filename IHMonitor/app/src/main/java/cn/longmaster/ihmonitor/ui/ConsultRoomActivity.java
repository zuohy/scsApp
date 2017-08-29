package cn.longmaster.ihmonitor.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.longmaster.video.LMVLog;
import com.longmaster.video.LMVideoMgr;
import com.longmaster.video.VideoRendererGui;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import cn.longmaster.doctorlibrary.util.common.CommonUtils;
import cn.longmaster.doctorlibrary.util.handler.AppHandlerProxy;
import cn.longmaster.doctorlibrary.util.log.Logger;
import cn.longmaster.doctorlibrary.util.screen.ScreenUtil;
import cn.longmaster.doctorlibrary.viewinject.FindViewById;
import cn.longmaster.doctorlibrary.viewinject.OnClick;
import cn.longmaster.doctorlibrary.viewinject.ViewInjecter;
import cn.longmaster.ihmonitor.R;
import cn.longmaster.ihmonitor.core.app.AppApplication;
import cn.longmaster.ihmonitor.core.app.AppConfig;
import cn.longmaster.ihmonitor.core.app.AppConstant;
import cn.longmaster.ihmonitor.core.app.AppPreference;
import cn.longmaster.ihmonitor.core.entity.appointment.AppointmentInfo;
import cn.longmaster.ihmonitor.core.entity.config.DepartmentInfo;
import cn.longmaster.ihmonitor.core.entity.config.DoctorBaseInfo;
import cn.longmaster.ihmonitor.core.entity.config.HospitalInfo;
import cn.longmaster.ihmonitor.core.entity.config.PatientInfo;
import cn.longmaster.ihmonitor.core.entity.video.VideoInfo;
import cn.longmaster.ihmonitor.core.entity.video.VideoRoomMember;
import cn.longmaster.ihmonitor.core.entity.video.VideoRoomResultInfo;
import cn.longmaster.ihmonitor.core.http.BaseResult;
import cn.longmaster.ihmonitor.core.http.OnResultListener;
import cn.longmaster.ihmonitor.core.http.VideoRoomResultCode;
import cn.longmaster.ihmonitor.core.manager.appointment.AppointmentManager;
import cn.longmaster.ihmonitor.core.manager.room.AudioAdapterManager;
import cn.longmaster.ihmonitor.core.manager.room.ConsultRoomManager;
import cn.longmaster.ihmonitor.core.manager.room.VideoRoomInterface;
import cn.longmaster.ihmonitor.core.manager.user.AuthenticationManager;
import cn.longmaster.ihmonitor.core.manager.user.DoctorManager;
import cn.longmaster.ihmonitor.core.manager.user.UserInfoManager;
import cn.longmaster.ihmonitor.core.receiver.NetStateReceiver;
import cn.longmaster.ihmonitor.view.SmallVideoView;
import cn.longmaster.ihmonitor.view.ToastLayoutView;
import cn.longmaster.ihmonitor.view.dialog.CommonDialog;
import cn.longmaster.phoneplus.audioadapter.model.AudioAdapter;
import cn.longmaster.phoneplus.audioadapter.model.AudioModule;

import static cn.longmaster.ihmonitor.R.id.activity_consult_room_exit_room_btn;

public class ConsultRoomActivity extends BaseActivity implements NetStateReceiver.NetworkStateChangedListener,
        SmallVideoView.onSmallVideoClickListener, ToastLayoutView.OnToastPositiveClickListener {
    private static final String TAG = ConsultRoomActivity.class.getSimpleName();
    public static final String EXTRA_DATA_APPOINT_ID = TAG + ".extra_data_appoint_id";

    private static final int BIG_VIDEO_DEFAULT_TAG = -1;//大视频默认Tag
    private static final int CODE_NETWOR_KMONITOR = 1;//大视频默认Tag
    private static final int CODE_RECORD = 2;//大视频默认Tag

    @FindViewById(R.id.activity_consult_room_num_tv)
    private TextView mConsultNumTv;//就诊编号
    @FindViewById(R.id.activity_consult_room_read_record_tv)
    private TextView mReadRecordTv;//查看病历
    @FindViewById(R.id.activity_consult_room_superior_doctor_tv)
    private TextView mSuperiorDoctorTv;//上级专家
    @FindViewById(R.id.activity_consult_room_attending_doctor_tv)
    private TextView mAttendingDoctorTv;//首诊医生
    @FindViewById(R.id.activity_consult_room_network_monitor_wv)
    private WebView mNetworkMonitorWv;//状态监控
    @FindViewById(activity_consult_room_exit_room_btn)
    private Button mExitRoomBtn;//退出诊室
    @FindViewById(R.id.activity_consult_room_big_video_rl)
    private RelativeLayout mBigVideoRl;//大视频容器
    @FindViewById(R.id.activity_consult_room_title_rl)
    private RelativeLayout mTitleRl;//标题
    @FindViewById(R.id.activity_consult_room_network_monitor_ll)
    private LinearLayout mNetworkMonitorll;//监控布局
    @FindViewById(R.id.activity_consult_room_time_tv)
    private TextView mTimeTv;//时间
    //大视频信息
    @FindViewById(R.id.activity_consult_room_user_avatar_iv)
    private ImageView mUserAvatarIv;
    @FindViewById(R.id.activity_consult_room_user_type_tv)
    private TextView mUserTypeTv;
    @FindViewById(R.id.activity_consult_room_name_tv)
    private TextView mUserNameTv;
    @FindViewById(R.id.activity_consult_room_hospital_tv)
    private TextView mHospitalTv;
    @FindViewById(R.id.activity_consult_room_network_iv)
    private ImageView mBigVideoSignalIv;
    @FindViewById(R.id.activity_consult_room_voice_iv)
    private ImageView mBigVideoVoiceIv;
    @FindViewById(R.id.activity_consult_room_bigVideo_info_ll)
    private LinearLayout mBigVideoInfoLl;//大视频内容详情
    @FindViewById(R.id.activity_consult_room_mode_voice_tip_tv)
    private TextView mVoiceTipTv;//大视频对方正使用语音模式
    @FindViewById(R.id.activity_consult_room_members_leave_tv)
    private TextView mMembersLeaveTv;//大视频对方已离开显示区
    @FindViewById(R.id.activity_consult_room_small_window_ll)
    private LinearLayout mSmallWindowLl;
    @FindViewById(R.id.activity_consult_room_small_video_ll)
    private LinearLayout mVideoLl;//小视频容器
    @FindViewById(R.id.activity_consult_room_hide_show_video_btn)
    private ImageView mHideShowVideoIv;//小视频显示隐藏按钮

    @FindViewById(R.id.activity_consult_room_toast_tlv)
    private ToastLayoutView mToastTlv;

    @AppApplication.Manager
    private UserInfoManager mUserInfoManager;
    @AppApplication.Manager
    private AppointmentManager mAppointmentManager;
    @AppApplication.Manager
    private DoctorManager mDoctorManager;
    @AppApplication.Manager
    private AuthenticationManager mAuthenticationManager;
    @AppApplication.Manager
    private ConsultRoomManager mConsultRoomManager;
    @AppApplication.Manager
    private AudioAdapterManager mAudioAdapterManager;

    private LMVideoMgr mLMVideoMgr;//视频管理
    private LMVideoMgr.LMVideoEvents mVideoEvents;//视频事件回调监听
    private VideoRoomInterface.OnRoomSelfStateChangeListener mSelfStateChangeListener;//自身房间状态变化通知

    private GLSurfaceView mBigScreenSv;//大视频窗口
    private ProgressDialog mProgressDialog;


    private int mUserType = AppConstant.UserType.USER_TYPE_ADMINISTRATOR;
    private int mAppointmentId;
    private AppointmentInfo mAppointmentInfo;
    private int mRoomId;
    private int mUserId;
    private int mPatientId;
    private float mDownLoadLostRate;

    //逻辑数据
    private Map<Integer, VideoInfo> mVideoInfoMap = new HashMap<>();
    private SparseArray<Byte> mSetStates = new SparseArray<>();
//    private byte mCurrentSeat;

    //视频变量
    private String mRemoteIP;
    private int mRemotePort;
    private int mVideoSsrc;

    //状态变量区域
    private boolean mFlagInRoom = false;//是否在房间
    private boolean mFlagExitRoom = false;//是否退出房间
    private boolean mFlagKickOff = false;//是否被提出房间
    private boolean mFlagShowNetworkDisconnect = true;//是否弹网络连接失败提示
    private boolean mIsAnimationEnd = false;//动画是否结束
    private boolean mEnableAutoAdjust = true;//
    private int mLatestVideoNetState = -1;//底层视频网络状态

    private AudioManager mAudioManager;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL); // 关键：添加该句可以调节音量大小
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        setContentView(R.layout.activity_consult_room);
        ViewInjecter.inject(this);
        initData();
        initLMVideoMgr();
        setPccParam();
        initView();
        initListener();
        initPage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLMVideoMgr != null) {
            mLMVideoMgr.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLMVideoMgr != null) {
            mLMVideoMgr.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mAudioAdapterManager.setMode(AudioModule.NAME_RESET);
        mConsultRoomManager.unRegisterMemberChangeListener();
        NetStateReceiver.removeNetworkStateChangedListener(this);
        mLMVideoMgr = null;
        Logger.log(Logger.ROOM, TAG + "onDestroy()");
        mHandler.removeCallbacks(mRunnable);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mAppointmentId = getIntent().getIntExtra(EXTRA_DATA_APPOINT_ID, 0);
        mUserId = mUserInfoManager.getCurrentUserInfo().getUserId();
    }

    /**
     * 初始化视频管理类
     */
    private void initLMVideoMgr() {
        mLMVideoMgr = LMVideoMgr.getInstance();
        mLMVideoMgr.initLog(LMVideoMgr.kLevelDebug, LMVideoMgr.kLevelNolog, null);//初始化日志配置
        mLMVideoMgr.init(this);//初始化LMVideoMgr对象
        mVideoEvents = new InnerLMVideoEvents();
    }

    /**
     * 设置音频参数，解决音频兼容性问题
     */
    private void setPccParam() {
        AudioAdapter adapter = mAudioAdapterManager.getAudioAdapter();
        String eq = adapter.getAudioConfig().getEqualizerValue();
        String level = adapter.getAudioConfig().getSamplingRates();
        int sourceType = adapter.getAudioConfig().getRecordSourceType();
        int streamType = adapter.getAudioConfig().getStreamType();
        mConsultRoomManager.setPccParam(eq, level, sourceType, streamType);
    }

    /**
     * 初始化控件
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        mBigScreenSv = new GLSurfaceView(this);
        mBigScreenSv.setZOrderMediaOverlay(false);
        mHideShowVideoIv.setSelected(true);
        mBigVideoRl.setTag(-1);
        mConsultNumTv.setText(getString(R.string.consult_room_consult_num, mAppointmentId));

        mNetworkMonitorWv.setBackgroundColor(0);
        //初始化状态监控器
        mNetworkMonitorWv.getSettings().setDefaultTextEncodingName("UTF-8");
        mNetworkMonitorWv.getSettings().setSupportZoom(true);
        mNetworkMonitorWv.getSettings().setBuiltInZoomControls(true);
        mNetworkMonitorWv.getSettings().setJavaScriptEnabled(true);
        mNetworkMonitorWv.getSettings().setDomStorageEnabled(true);
        mNetworkMonitorWv.getSettings().setUseWideViewPort(true);
        mNetworkMonitorWv.getSettings().setLoadWithOverviewMode(true);
        mNetworkMonitorWv.clearCache(true);
        mNetworkMonitorWv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        getAuthenticationInfo(CODE_NETWOR_KMONITOR);

        setCurrentTime();
        mHandler.postDelayed(mRunnable, 1000);
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        mToastTlv.setPositiveClickListener(this);
        NetStateReceiver.setOnNetworkStateChangedListener(this);
        mSelfStateChangeListener = new InnerRoomSelfStateListener();
        mConsultRoomManager.registerMemberChangeListener(new InnerRoomMemberStateListener());
    }

    private void initPage() {
        showProgressDialog();
        //拉取预约详情，判断是否是上级专家
        mAppointmentManager.getAppointmentInfo(mAppointmentId, new OnResultListener<AppointmentInfo>() {
            @Override
            public void onResult(BaseResult baseResult, AppointmentInfo appointmentInfo) {
                dismissProgressDialog();
                if (appointmentInfo == null)
                    return;
                Logger.log(Logger.ROOM, "initDoctorInfo():appointmentInfo：" + appointmentInfo.toString());

                mAppointmentInfo = appointmentInfo;
                mPatientId = appointmentInfo.getBaseInfo().getUserId();
                mRoomId = appointmentInfo.getExtendsInfo() == null ? 0 : appointmentInfo.getExtendsInfo().getRoomId();
                joinRoom(mRoomId);

                mDoctorManager.getDoctorInfo(appointmentInfo.getBaseInfo().getDoctorUserId(), new OnResultListener<DoctorBaseInfo>() {
                    @Override
                    public void onResult(BaseResult baseResult, DoctorBaseInfo doctorBaseInfo) {
                        mSuperiorDoctorTv.setText(getString(R.string.consult_room_superior_doctor,
                                TextUtils.isEmpty(doctorBaseInfo.getRealName()) ? "" : doctorBaseInfo.getRealName()));

                    }
                });
                mDoctorManager.getDoctorInfo(appointmentInfo.getBaseInfo().getAttendingDoctorUserId(), new OnResultListener<DoctorBaseInfo>() {
                    @Override
                    public void onResult(BaseResult baseResult, DoctorBaseInfo doctorBaseInfo) {
                        mAttendingDoctorTv.setText(getString(R.string.consult_room_attending_doctor,
                                TextUtils.isEmpty(doctorBaseInfo.getRealName()) ? "" : doctorBaseInfo.getRealName()));

                    }
                });
            }
        });
    }

    private Runnable mRunnable = new Runnable() {

        public void run() {
            setCurrentTime();
            mHandler.postDelayed(mRunnable, 1000);
        }

    };

    /**
     * 设置当前时间
     */
    private void setCurrentTime() {
        long sysTime = System.currentTimeMillis();
        CharSequence sysTimeStr = DateFormat.format("HH:mm:ss", sysTime);
        mTimeTv.setText(sysTimeStr); //更新时间
    }

    private void showProgressDialog() {
        if (isFinishing()) {
            return;
        }
        if (mProgressDialog == null)
            mProgressDialog = createProgressDialog(getString(R.string.loading));
        else if (!mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (isFinishing()) {
            return;
        }
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                showExitDialog();
                return true;

            case KeyEvent.KEYCODE_VOLUME_UP:
                mAudioManager.adjustStreamVolume(
                        AudioManager.STREAM_VOICE_CALL,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                mConsultRoomManager.pausePlay(false);
                return true;

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                mAudioManager.adjustStreamVolume(
                        AudioManager.STREAM_VOICE_CALL,
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                if (mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL) == 0) {
                    mConsultRoomManager.pausePlay(true);
                }
                return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({activity_consult_room_exit_room_btn,
            R.id.activity_consult_room_read_record_tv,
            R.id.activity_consult_room_hide_show_video_btn,
            R.id.activity_consult_room_big_video_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_consult_room_exit_room_btn:
                showExitDialog();
                break;

            case R.id.activity_consult_room_read_record_tv:
                getAuthenticationInfo(CODE_RECORD);
                break;

            case R.id.activity_consult_room_hide_show_video_btn:
                mHideShowVideoIv.setEnabled(false);
                if (mHideShowVideoIv.isSelected()) {
                    showHideSmallVideo(mVideoLl.getWidth());
                } else {
                    showHideSmallVideo(-mVideoLl.getWidth());
                }
                break;

            case R.id.activity_consult_room_big_video_rl:
                if (mNetworkMonitorll.isShown()) {
                    mNetworkMonitorll.setVisibility(View.GONE);
                    mTitleRl.setVisibility(View.GONE);
                } else {
                    mNetworkMonitorll.setVisibility(View.VISIBLE);
                    mTitleRl.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    /*********************************************
     * 数据拉取start
     **************************************************/
    /**
     * 拉取最新鉴权
     *
     * @param code 加载WebView,还是查看病历
     */
    private void getAuthenticationInfo(final int code) {
        mAuthenticationManager.getAuthenticationInfo(new AuthenticationManager.GetAuthenticationListener() {
            @Override
            public void onGetAuthentication() {
                AppHandlerProxy.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (code) {
                            case CODE_NETWOR_KMONITOR:
                                String networkMonitorUrl = AppConfig.getNetworkMonitorUrl()
                                        + "app_id=" + mAppointmentId
                                        + "&c_auth=" + AppPreference.getStringValue(AppPreference.KEY_AUTHENTICATION_AUTH, "")
                                        + "&user_id=" + mUserInfoManager.getCurrentUserInfo().getUserId();
                                mNetworkMonitorWv.loadUrl(networkMonitorUrl);
                                Logger.logI(Logger.COMMON, "initWebView-->url:" + networkMonitorUrl);
                                break;

                            case CODE_RECORD:
                                String recordUrl = AppConfig.getRecordUrl()
                                        + "aid=" + mAppointmentId
                                        + "&c_auth=" + AppPreference.getStringValue(AppPreference.KEY_AUTHENTICATION_AUTH, "")
                                        + "&user_id=" + mUserInfoManager.getCurrentUserInfo().getUserId();
                                BrowserActivity.startBrowserActivity(getActivity(), recordUrl);
//                                String newNetworkMonitorUrl = AppConfig.getNetworkMonitorUrl()
//                                        + "app_id=" + mAppointmentId
//                                        + "&c_auth=" + AppPreference.getStringValue(AppPreference.KEY_AUTHENTICATION_AUTH, "")
//                                        + "&user_id=" + mUserInfoManager.getCurrentUserInfo().getUserId();
//                                mNetworkMonitorWv.loadUrl(newNetworkMonitorUrl);
//                                Logger.logI(Logger.COMMON, "initWebView-->url:" + newNetworkMonitorUrl);
                                break;
                        }

                    }
                });
            }

            @Override
            public void onTimeOut() {
                showToast(R.string.no_network_connection);
            }
        });
    }

    /**
     * 获取医生信息
     *
     * @param doctorId
     */
    private void getDoctorInfo(final int doctorId) {
        mDoctorManager.getDoctor(doctorId, new DoctorManager.OnGetDoctorListener() {

            @Override
            public void onGetDoctor(DoctorBaseInfo doctorBaseInfo) {
                if (doctorBaseInfo != null && mVideoInfoMap.get(doctorId) != null) {
                    Log.i("onGetDoctor", "_--" + mVideoInfoMap.get(doctorId));
                    mVideoInfoMap.get(doctorId).setUseName(doctorBaseInfo.getRealName());
                    if (doctorId == (Integer) mBigVideoRl.getTag()) {
                        mUserNameTv.setText(doctorBaseInfo.getRealName());
                    } else {
                        for (int i = 0; i < mVideoLl.getChildCount(); i++) {
                            if ((Integer) (mVideoLl.getChildAt(i).getTag()) == doctorId) {
                                ((SmallVideoView) mVideoLl.getChildAt(i)).setName(doctorBaseInfo.getRealName());
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onGetHospital(HospitalInfo hospitalInfo) {
                if (hospitalInfo != null && mVideoInfoMap.get(doctorId) != null) {
                    Logger.log(Logger.ROOM, "getDoctorInfo():hospitalInfo.getHospitalName()：" + hospitalInfo.getHospitalName());
                    mVideoInfoMap.get(doctorId).setHospitalName(hospitalInfo.getHospitalName());
                    if (doctorId == (Integer) mBigVideoRl.getTag()) {
                        mHospitalTv.setText(hospitalInfo.getHospitalName());
                    }
                }
            }

            @Override
            public void onGetDepartment(DepartmentInfo departmentInfo) {

            }
        });
    }

    /**
     * 获取患者信息
     *
     * @param mAppointmentID
     */
    public void getPatientInfo(final int mAppointmentID) {
        mAppointmentManager.getPatientInfo(mAppointmentID, new OnResultListener<PatientInfo>() {
            @Override
            public void onResult(BaseResult baseResult, PatientInfo patientInfo) {
                if (baseResult.getCode() == RESULT_SUCCESS && patientInfo != null) {
                    if (mVideoInfoMap.get(mPatientId) == null) {
                        return;
                    }
                    mVideoInfoMap.get(mPatientId).setUseName(patientInfo.getPatientBaseInfo().getRealName());
                    if (mPatientId == (Integer) mBigVideoRl.getTag()) {
                        mUserNameTv.setText(patientInfo.getPatientBaseInfo().getRealName());
                    } else {
                        for (int i = 0; i < mVideoLl.getChildCount(); i++) {
                            if ((Integer) (mVideoLl.getChildAt(i).getTag()) == mPatientId) {
                                ((SmallVideoView) mVideoLl.getChildAt(i)).setName(patientInfo.getPatientBaseInfo().getRealName());
                                break;
                            }
                        }
                    }
                }
            }
        });
    }
    /*********************************************
     * 数据拉取end
     **************************************************/

    /*********************************************
     * 视频操作区start
     **************************************************/
    /**
     * 开始视频就诊
     */
    private void joinRoom(final int roomId) {
        Logger.log(Logger.ROOM, "joinRoom()-->");
        if (NetStateReceiver.hasNetConnected(this)) {
            mAudioAdapterManager.setMode(AudioModule.NAME_PROCESSINCALL);
            joinVideoRoom(roomId);
        }
    }

    private void joinVideoRoom(int roomId) {
        Logger.log(Logger.ROOM, "joinVideoRoom()-->roomId:" + roomId);
        showProgressDialog();
        mConsultRoomManager.joinVideoRoom(mAppointmentId, roomId, mUserType, "", mSelfStateChangeListener);
    }

    /**
     * 配置视频参数
     *
     * @param videoInfo 需配置视频的信息
     */
    private void setVideoDisPlay(VideoInfo videoInfo) {
        Logger.logI(Logger.ROOM, TAG + "setVideoDisPlay()->videoInfo:" + videoInfo);
        int userId = videoInfo.getUseId();
        Logger.logI(Logger.ROOM, TAG + "setVideoDisPlay()->userId:" + userId + "->ssrcList.get(i).getSsrc():" + videoInfo.getSsrc());
        if (mVideoInfoMap.get(userId) != null && videoInfo.getSsrc() != 0) {
            mVideoInfoMap.get(userId).setSsrc(videoInfo.getSsrc());
            Logger.logI(Logger.ROOM, TAG + "setVideoDisPlay()->mVideoInfoMap.get(userId).getSsrc():" + mVideoInfoMap.get(userId).getSsrc());
            //开启视频播放
            LMVideoMgr.LMVideoDisplayConfig displayConfig = new LMVideoMgr.LMVideoDisplayConfig();
            displayConfig.mVideoView = mVideoInfoMap.get(userId).getGlSurfaceView();
            displayConfig.mRender = mVideoInfoMap.get(userId).getRendererGui();
            displayConfig.mPlayStreamSsrc = videoInfo.getSsrc();
            mVideoInfoMap.get(userId).setDisplayConfig(displayConfig);
            mLMVideoMgr.startVideoDisplay(displayConfig);
            if ((Integer) mBigVideoRl.getTag() == userId) {
                setVideoQuality(videoInfo, false);
            }
        }
    }

    private void startVideoSession() {
        //开启视频会话
        LMVideoMgr.LMVideoSessionConfig sessionConfig = new LMVideoMgr.LMVideoSessionConfig();
        sessionConfig.mRemoteIP = mRemoteIP;
        sessionConfig.mRemoteVideoPort = mRemotePort;
        sessionConfig.mP2pLocalVideoPort = 0;//P2P模式下视频输入流的接收端口（非P2P模式需将此参数置零）
        sessionConfig.mVideoOutputSsrc = mVideoSsrc;//视频输出流的SSRC标识，自己捕获的视频上传给视频服务器时的标识
        mLMVideoMgr.startVideoSession(sessionConfig, mVideoEvents);
    }


    /**
     * 停止视频通话
     */
    public void stopVideo() {
        Logger.logI(Logger.ROOM, "stopVideo-->mLMVideoMgr != null:" + (mLMVideoMgr != null));
        if (mLMVideoMgr != null)
            mLMVideoMgr.stopVideoSession();
    }


    /**
     * 获取网络状态
     *
     * @param v 丢包率
     * @return
     */
    private int getNetState(float v) {
        int state = LMVideoMgr.kVideoNetworkStateVeryGood;
        if (v <= 0.1) {
            state = LMVideoMgr.kVideoNetworkStateVeryGood;
        } else if (v <= 0.5) {
            state = LMVideoMgr.kVideoNetworkStateOK;
        } else {
            state = LMVideoMgr.kVideoNetworkStateNotGood;
        }
        Logger.logI(Logger.ROOM, "InnerLMVideoEvents->getNetState->v" + v + "->v" + v + "->state:" + state);
        return state;
    }

    /**
     * 查询网络状态
     */
    private void startGetNetState() {
        if (mLMVideoMgr == null)
            return;
        mLMVideoMgr.getCaptureStreamLostRate();
        for (Map.Entry<Integer, VideoInfo> entry : mVideoInfoMap.entrySet()) {
            if (entry.getKey() != mUserId) {
                if (entry.getValue().getSsrc() != 0) {
                    mLMVideoMgr.getPlayStreamLostRate(entry.getValue().getSsrc());
                } else if (entry.getValue().getVideoState() == AppConstant.RoomState.STATE_BEING_VIDEO) {
                    VideoRoomMember videoRoomMember = new VideoRoomMember();
                    videoRoomMember.setUserId(entry.getKey());
                    List<VideoRoomMember> ssrcList = new ArrayList<>();
                    ssrcList.add(videoRoomMember);
                    mConsultRoomManager.videoSubscribe(ssrcList, mSelfStateChangeListener);
                }
            }
        }
    }

    /**
     * 设置视频质量
     *
     * @param videoInfo    视频信息
     * @param isLowQuality true->低质量 false->高质量
     */

    private void setVideoQuality(VideoInfo videoInfo, boolean isLowQuality) {
        if (videoInfo.getUseId() != mUserId && videoInfo.getSsrc() != 0) {
            mLMVideoMgr.toggleSubPlayStream(videoInfo.getSsrc(), isLowQuality);
        }
    }

    /**
     * 添加席位信息
     *
     * @param userId 用户id
     * @param seat   席位号
     */
    private void addSeatInfo(int userId, byte seat) {
        mSetStates.put(userId, seat);
//        if (seat >= 1) {
//            mCurrentSeat |= (1 << (seat - 1));
//        }
    }

    /**
     * 移除席位信息
     *
     * @param userId 用户id
     * @param seat   席位号
     */
    private void removeSeatInfo(int userId, byte seat) {
        if (mSetStates.indexOfKey(userId) != -1) {
            mSetStates.remove(userId);
        }
//        if (seat >= 1) {
//            mCurrentSeat &= ~(1 << (seat - 1));
//        }
    }

    /**
     * 成员切换音视频模式
     *
     * @param userId       用户id
     * @param remoteAvType AV模式
     */
    private void memberChangeAvType(int userId, int remoteAvType) {
        Logger.log(Logger.ROOM, TAG + "->memberChangeAvType()->userId:" + userId + ", remoteAvType:" + remoteAvType);
        int videoId = (Integer) mBigVideoRl.getTag();
        int state = remoteAvType == ConsultRoomManager.AV_TYPE_VIDEO ? AppConstant.RoomState.STATE_BEING_VIDEO : AppConstant.RoomState.STATE_BEING_VOICE;
        mVideoInfoMap.get(userId).setVideoState(state);
        if (remoteAvType == ConsultRoomManager.AV_TYPE_VIDEO) {
            if (mVideoInfoMap.get(userId).getSsrc() != 0) {
                setVideoDisPlay(mVideoInfoMap.get(userId));
            } else {
                VideoRoomMember videoRoomMember = new VideoRoomMember();
                videoRoomMember.setUserId(userId);
                List<VideoRoomMember> ssrcList = new ArrayList<>();
                ssrcList.add(videoRoomMember);
                mConsultRoomManager.videoSubscribe(ssrcList, mSelfStateChangeListener);
            }
        }
        if (videoId == userId) {
            setBigVideoState(state);
        } else {
            for (int i = 0; i < mVideoLl.getChildCount(); i++) {
                if ((int) mVideoLl.getChildAt(i).getTag() == userId) {
                    ((SmallVideoView) mVideoLl.getChildAt(i)).setRoomState(state);
                    break;
                }
            }
        }
    }

    /**
     * 退出聊天室
     */
    private void exitVideoRoom() {
        if (!mFlagInRoom) {
            finish();
            return;
        }
        Logger.log(Logger.ROOM, "exitVideoRoom（）->请求退出视频房间");
        if (mAppointmentId == 0)
            return;
        showProgressDialog();
        mFlagExitRoom = true;
        stopVideo();
        mConsultRoomManager.exitVideoRoom(mRoomId, mSelfStateChangeListener);
    }

    /**
     * 退出房间处理
     */
    private void exitRoomDeal() {
        Logger.log(Logger.ROOM, "是否退出房间：" + mFlagExitRoom);
        if (!mFlagExitRoom)
            return;
        dismissProgressDialog();
        mAudioAdapterManager.setMode(AudioModule.NAME_RESET);
        mVideoInfoMap.clear();
        finish();
        mFlagExitRoom = false;
    }

    /*********************************************
     * 视频操作区end
     **************************************************/

    /*********************************************
     * 逻辑操作区start
     **************************************************/
    /**
     * 重新设置状态
     */
    private void resetState() {
        mVideoInfoMap.clear();
        mBigVideoRl.setTag(BIG_VIDEO_DEFAULT_TAG);
        mBigVideoRl.removeAllViews();
        mVideoLl.removeAllViews();
        mBigVideoInfoLl.setVisibility(View.GONE);
        setVideoState(mUserId, AppConstant.RoomState.STATE_BEING_VIDEO);
    }

    /**
     * 设置指定窗口的视频状态
     *
     * @param userId
     * @param state
     */
    private void setVideoState(int userId, int state) {
        int videoId = (Integer) mBigVideoRl.getTag();
        if (videoId == userId) {
            setBigVideoState(state);
        } else {
            for (int i = 0; i < mVideoLl.getChildCount(); i++) {
                if ((int) mVideoLl.getChildAt(i).getTag() == userId) {
                    ((SmallVideoView) mVideoLl.getChildAt(i)).setRoomState(state);
                    break;
                }
            }
        }
    }

    /**
     * 设置大视频状态
     *
     * @param state
     */
    private void setBigVideoState(int state) {
        switch (state) {
            case AppConstant.RoomState.STATE_BEING_VIDEO:
                mBigVideoRl.setVisibility(View.VISIBLE);
                mVoiceTipTv.setVisibility(View.GONE);
                break;

            case AppConstant.RoomState.STATE_BEING_VOICE:
                mBigVideoRl.setVisibility(View.GONE);
                mVoiceTipTv.setVisibility(View.VISIBLE);
                mVoiceTipTv.setText(getString(R.string.consult_room_voice_mode_other_party_voice_tip));
                break;

            default:
                mBigVideoRl.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 添加所有视频
     *
     * @param videoRoomMemberList
     */
    private void addVideos(List<VideoRoomMember> videoRoomMemberList) {
        for (VideoRoomMember videoRoomMember : videoRoomMemberList) {
            if (videoRoomMember.getUserType() == AppConstant.UserType.USER_TYPE_ASSISTANT_DOCTOR) {
                mToastTlv.showToastView(getString(R.string.video_room_state_doctor_assistant_entered_room));
            }

            if (videoRoomMember.getUserType() != AppConstant.UserType.USER_TYPE_ADMINISTRATOR && videoRoomMember.getUserId() != mUserId) {
                addOtherVideo(videoRoomMember);
                addSeatInfo(videoRoomMember.getUserId(), videoRoomMember.getSeat());
            }
        }
    }

    /**
     * 添加其他视频
     *
     * @param videoRoomMember
     */
    private void addOtherVideo(VideoRoomMember videoRoomMember) {
        Logger.log(Logger.ROOM, TAG + "-->addOtherVideo()-->");
        initVideoInfo(videoRoomMember);
        if (mBigVideoRl.getTag() == null || (int) mBigVideoRl.getTag() == BIG_VIDEO_DEFAULT_TAG) {
            initBigVideo(videoRoomMember);
        } else {
            addSmallVideo(mVideoInfoMap.get(videoRoomMember.getUserId()));
        }
    }

    /**
     * 添加大视频初始化
     */
    private void initBigVideo(VideoRoomMember videoRoomMember) {
        mBigVideoInfoLl.setVisibility(View.VISIBLE);
        if (videoRoomMember.getUserType() == AppConstant.UserType.USER_TYPE_ATTENDING_DOCTOR ||
                videoRoomMember.getUserType() == AppConstant.UserType.USER_TYPE_SUPERIOR_DOCTOR ||
                videoRoomMember.getUserType() == AppConstant.UserType.USER_TYPE_ASSISTANT_DOCTOR) {
            mHospitalTv.setVisibility(View.VISIBLE);
        } else {
            mHospitalTv.setVisibility(View.GONE);
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        RelativeLayout rl = new RelativeLayout(this);
        rl.setLayoutParams(lp);
        rl.setBackgroundColor(getResources().getColor(R.color.color_2f2f2f));
        mBigVideoRl.addView(rl);
        mVideoInfoMap.get(videoRoomMember.getUserId()).setParentRl(rl);
        mVideoInfoMap.get(videoRoomMember.getUserId()).getParentRl().addView(mVideoInfoMap.get(videoRoomMember.getUserId()).getGlSurfaceView());
        setUserType(videoRoomMember.getUserType());
        if (videoRoomMember.getUserType() == AppConstant.UserType.USER_TYPE_MDT_DOCTOR) {
            mUserNameTv.setText(getString(R.string.consult_room_mdt_doctor));
        } else if (videoRoomMember.getUserType() == AppConstant.UserType.USER_TYPE_MDT_PATIENT) {
            mUserNameTv.setText(getString(R.string.consult_room_mdt_patient));
        } else {
            mUserNameTv.setText(mVideoInfoMap.get(videoRoomMember.getUserId()).getUseName());
        }
        mBigVideoRl.setTag(videoRoomMember.getUserId());
        setBigVideoState(mVideoInfoMap.get(videoRoomMember.getUserId()).getVideoState());
    }

    /**
     * 添加其他视频初始化
     */
    private void initVideoInfo(VideoRoomMember videoRoomMember) {
        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
        MyVideoRendererGui rendererGui = new MyVideoRendererGui(glSurfaceView, this);
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setUseId(videoRoomMember.getUserId());
        videoInfo.setUserType(videoRoomMember.getUserType());
        videoInfo.setGlSurfaceView(glSurfaceView);
        videoInfo.setRendererGui(rendererGui);
        videoInfo.setVideoState(videoRoomMember.getAvType() == ConsultRoomManager.AV_TYPE_VIDEO
                ? AppConstant.RoomState.STATE_BEING_VIDEO : AppConstant.RoomState.STATE_BEING_VOICE);
        if (videoRoomMember.getUserType() == AppConstant.UserType.USER_TYPE_MDT_DOCTOR) {
            videoInfo.setUseName(getString(R.string.consult_room_mdt_doctor));
        } else if (videoRoomMember.getUserType() == AppConstant.UserType.USER_TYPE_MDT_PATIENT) {
            videoInfo.setUseName(getString(R.string.consult_room_mdt_patient));
        }
        mVideoInfoMap.put(videoRoomMember.getUserId(), videoInfo);
        Logger.logI(Logger.ROOM, "mVideoInfoMap.put:" + videoInfo);
    }

    /**
     * 添加小视频
     *
     * @param videoInfo
     */
    private void addSmallVideo(VideoInfo videoInfo) {
        Logger.log(Logger.ROOM, TAG + "-->addSmallVideo()-->");
        SmallVideoView smallVideoView = new SmallVideoView(this);
        videoInfo.setParentRl(smallVideoView.getVideoParentRl());
        smallVideoView.getVideoParentRl().addView(videoInfo.getGlSurfaceView());
        smallVideoView.removeSurfaceView(videoInfo.getParentRl());
        smallVideoView.setTag(videoInfo.getUseId());
        Log.i("TAG", videoInfo.getUseId() + "");
        smallVideoView.setSmallVideoClickListener(this);
        smallVideoView.initSmallVideo(videoInfo);
        mVideoInfoMap.get(videoInfo.getUseId()).getGlSurfaceView().setZOrderMediaOverlay(true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(CommonUtils.dipToPx(this, 100), CommonUtils.dipToPx(this, 75));
        lp.setMargins(0, 0, CommonUtils.dipToPx(this, 10), 0);
        mVideoLl.addView(smallVideoView, lp);
        if (videoInfo.getUserType() == AppConstant.UserType.USER_TYPE_PATIENT) {
            getPatientInfo(mAppointmentId);
        } else if (videoInfo.getUserType() == AppConstant.UserType.USER_TYPE_ATTENDING_DOCTOR
                || videoInfo.getUserType() == AppConstant.UserType.USER_TYPE_SUPERIOR_DOCTOR
                || videoInfo.getUserType() == AppConstant.UserType.USER_TYPE_ASSISTANT_DOCTOR) {
            getDoctorInfo(videoInfo.getUseId());
        }
    }

    /**
     * 移除视频
     *
     * @param userId
     */
    private void removeVideo(int userId) {
        int videoId = (Integer) mBigVideoRl.getTag();
        if (videoId == userId) {
            mBigVideoRl.removeView(mVideoInfoMap.get(userId).getParentRl());
            mBigVideoRl.setTag(-1);
            mBigVideoInfoLl.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < mVideoLl.getChildCount(); i++) {
                if ((int) mVideoLl.getChildAt(i).getTag() == userId) {
                    mVideoLl.removeView(mVideoLl.getChildAt(i));
                    break;
                }
            }
            if (mVideoLl.getChildCount() <= 0) {
                mHideShowVideoIv.setSelected(true);
            }
        }
        VideoInfo info = mVideoInfoMap.remove(userId);
        if (info != null) {
            info.destoryRendererGui();
        }
    }

    /**
     * 切换视频界面
     *
     * @param smallView 被点击的小视频用户
     */
    private void switchVideoView(SmallVideoView smallView) {
        final int smallId = (int) smallView.getTag();
        final int bigViewId = (int) mBigVideoRl.getTag();
        if (mVideoInfoMap != null && mVideoInfoMap.get(smallId).getGlSurfaceView() != null) {
            int[] location = new int[2];
            mVideoInfoMap.get(smallId).getGlSurfaceView().getLocationOnScreen(location);
            int viewX = location[0];
            int viewY = location[1];
            smallView.removeSurfaceView(mVideoInfoMap.get(smallId).getParentRl());
            if (mVideoInfoMap != null && bigViewId != BIG_VIDEO_DEFAULT_TAG && mVideoInfoMap.get(bigViewId) != null && mVideoInfoMap.get(bigViewId).getGlSurfaceView() != null) {
                mBigVideoRl.removeView(mVideoInfoMap.get(bigViewId).getParentRl());
                //小视频
                mVideoInfoMap.get(bigViewId).getGlSurfaceView().setZOrderMediaOverlay(true);
                smallView.initSmallVideo(mVideoInfoMap.get(bigViewId));
                smallView.setTag(mVideoInfoMap.get(bigViewId).getUseId());
                setVideoQuality(mVideoInfoMap.get(bigViewId), true);
            } else {
                mBigVideoInfoLl.setVisibility(View.VISIBLE);
                mVideoLl.removeView(smallView);
                if (mVideoLl.getChildCount() <= 0) {
                    mHideShowVideoIv.setSelected(true);
                }
            }
            if (mVideoInfoMap.get(smallId).getUserType() == AppConstant.UserType.USER_TYPE_ATTENDING_DOCTOR ||
                    mVideoInfoMap.get(smallId).getUserType() == AppConstant.UserType.USER_TYPE_SUPERIOR_DOCTOR ||
                    mVideoInfoMap.get(smallId).getUserType() == AppConstant.UserType.USER_TYPE_ASSISTANT_DOCTOR) {
                mHospitalTv.setVisibility(View.VISIBLE);
                mHospitalTv.setText(mVideoInfoMap.get(smallId).getHospitalName());
            } else {
                mHospitalTv.setVisibility(View.GONE);
            }
            //大视频
            setUserType(mVideoInfoMap.get(smallId).getUserType());
            mUserNameTv.setText(mVideoInfoMap.get(smallId).getUseName());
            mBigVideoRl.setTag(mVideoInfoMap.get(smallId).getUseId());
            setBigVideo(mVideoInfoMap.get(smallId), viewX, viewY);
            setVideoQuality(mVideoInfoMap.get(smallId), false);
        }
    }

    /**
     * 设置大视频
     *
     * @param videoInfo
     */
    private void setBigVideo(VideoInfo videoInfo, int viewX, int viewY) {
        mBigVideoRl.setVisibility(View.VISIBLE);
        mVoiceTipTv.setVisibility(View.GONE);
        videoInfo.getGlSurfaceView().setZOrderMediaOverlay(false);
        mEnableAutoAdjust = false;
        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(videoInfo.getGlSurfaceView().getLayoutParams());
        margin.setMargins(viewX, viewY, 0, 0);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        mBigVideoRl.addView(videoInfo.getParentRl(), layoutParams);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        videoInfo.getGlSurfaceView().setLayoutParams(lp);
        smallVideoAnimation(videoInfo, viewX, viewY);
    }

    /**
     * 设置角色头像和名称
     *
     * @param userType
     */
    public void setUserType(int userType) {
        if (userType == AppConstant.UserType.USER_TYPE_SUPERIOR_DOCTOR) {
            mUserAvatarIv.setImageResource(R.drawable.ic_big_video_super_expert);
            mUserTypeTv.setText(getString(R.string.consult_room_higher_class_doctor));
        } else if (userType == AppConstant.UserType.USER_TYPE_ATTENDING_DOCTOR) {
            mUserAvatarIv.setImageResource(R.drawable.ic_big_video_first_expert);
            mUserTypeTv.setText(getString(R.string.consult_room_first_doctor));
        } else if (userType == AppConstant.UserType.USER_TYPE_PATIENT) {
            mUserAvatarIv.setImageResource(R.drawable.ic_big_video_patient_icon);
            mUserTypeTv.setText(getString(R.string.consult_room_patient));
        } else if (userType == AppConstant.UserType.USER_TYPE_ASSISTANT_DOCTOR) {
            mUserAvatarIv.setImageResource(R.drawable.ic_big_video_patient_icon);
            mUserTypeTv.setText(getString(R.string.consult_room_doctor_assistant));
        } else if (userType == AppConstant.UserType.USER_TYPE_MDT_DOCTOR) {
            mUserAvatarIv.setImageResource(R.drawable.ic_big_video_super_expert);
            mUserTypeTv.setText(getString(R.string.consult_room_mdt));
        } else if (userType == AppConstant.UserType.USER_TYPE_MDT_PATIENT) {
            mUserAvatarIv.setImageResource(R.drawable.ic_big_video_patient_icon);
            mUserTypeTv.setText(getString(R.string.consult_room_mdt));
        } else if (userType == AppConstant.UserType.USER_TYPE_CONFERENCE_STAFF) {
            mUserAvatarIv.setImageResource(R.drawable.ic_big_video_patient_icon);
            mUserTypeTv.setText(getString(R.string.consult_room_conference_staff));
        }
    }

    /**
     * 设置大视频信号图标
     *
     * @param signalState
     */
    public void setBigVideoSignal(int signalState) {
        if (signalState == AppConstant.SignalState.SIGNAL_BAD) {
            mBigVideoSignalIv.setImageResource(R.drawable.ic_big_video_signal_bad);
        } else if (signalState == AppConstant.SignalState.SIGNAL_GENERAL) {
            mBigVideoSignalIv.setImageResource(R.drawable.ic_big_video_signal_general);
        } else {
            mBigVideoSignalIv.setImageResource(R.drawable.ic_big_video_signal_good);
        }
    }

    /**
     * 设置视频信号
     *
     * @param userId      用户id
     * @param signalState 信号状态
     */
    private void setVideoSignal(int userId, int signalState) {
        int videoId = (Integer) mBigVideoRl.getTag();
        if (videoId == userId) {
            setBigVideoSignal(signalState);
        } else {
            for (int i = 0; i < mVideoLl.getChildCount(); i++) {
                if ((int) mVideoLl.getChildAt(i).getTag() == userId) {
                    ((SmallVideoView) mVideoLl.getChildAt(i)).setSignal(signalState);
                    break;
                }
            }
        }
    }

    /**
     * 设置是否显示语音图标
     *
     * @param isShow true 显示
     */
    public void setShowBigVideoVoice(boolean isShow) {
        this.mBigVideoVoiceIv.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置视频语音图标是否显示
     *
     * @param userId
     * @param isShow
     */
    private void setVideoVoice(int userId, boolean isShow) {
        int videoId = (Integer) mBigVideoRl.getTag();
        if (videoId == userId) {
            setShowBigVideoVoice(isShow);
        } else {
            for (int i = 0; i < mVideoLl.getChildCount(); i++) {
                if ((int) mVideoLl.getChildAt(i).getTag() == userId) {
                    ((SmallVideoView) mVideoLl.getChildAt(i)).setShowVoice(isShow);
                    break;
                }
            }
        }
    }

    /**
     * 小视频点击后缩放和移动效果
     *
     * @param videoInfo 视频信息
     * @param viewX     起始x坐标
     * @param viewY     起始y坐标
     */
    private void smallVideoAnimation(final VideoInfo videoInfo, final int viewX, final int viewY) {
        mIsAnimationEnd = false;
        int screenWidth = ScreenUtil.getScreenWidth();
        int screenHeight = ScreenUtil.getScreenHeight();
        final int viewWidth = videoInfo.getGlSurfaceView().getWidth();
        final int viewHeight = videoInfo.getGlSurfaceView().getHeight();
        final float scale;//缩放大小
        final float moveX;//x方向移动距离
        final float moveY;//y方向移动距离
        if (screenWidth * viewHeight < screenHeight * viewWidth) {
            //宽度缩放小于高度的缩放
            double scaleTemp = Double.valueOf(screenWidth + "") / Double.valueOf(viewWidth + "");
            scale = Float.valueOf(scaleTemp + "");
            moveX = viewX;
            moveY = viewY - (screenHeight - viewHeight * scale) / 2;
        } else {
            double scaleTemp = Double.valueOf(screenHeight + "") / Double.valueOf(viewHeight + "");
            scale = Float.valueOf(scaleTemp + "");
            moveY = viewY;
            moveX = viewX - (screenWidth - viewWidth * scale) / 2;
        }
        Log.i(TAG, "smallVideoAnimation->scale:" + scale);

        //尺寸缩放设置
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, scale, 1f, scale,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //平移设置
        Animation translateAnimation = new TranslateAnimation(1.0f, -moveX + (viewWidth / 2.0f) * (scale - 1),
                1.0f, -moveY + (viewHeight / 2.0f) * (scale - 1));

        final ImageView imageView = new ImageView(getActivity());

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(translateAnimation);
        animationSet.setDuration(900);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Logger.log(Logger.ROOM, TAG + "-->smallVideoAnimation->onAnimationStart()-->");
                setEnableSmallViews(false);
                if (videoInfo.getVideoState() == AppConstant.RoomState.STATE_BEING_VIDEO) {
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    videoInfo.getParentRl().addView(imageView, lp);
                    ((MyVideoRendererGui) videoInfo.getRendererGui()).setCutBitmap(viewWidth, viewHeight, new MyVideoRendererGui.OnCutBitmapListener() {
                        @Override
                        public void OnCutBitmapfinished(final Bitmap bmp) {
                            AppHandlerProxy.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!mIsAnimationEnd) {
                                        Logger.log(Logger.ROOM, TAG + "-->smallVideoAnimation->OnCutBitmapfinished()-->");
                                        if (imageView != null)
                                            imageView.setImageBitmap(bmp);
                                    }
                                }
                            });
                        }
                    });
                } else {
                    imageView.setImageResource(R.color.color_black);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    videoInfo.getParentRl().addView(imageView, lp);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsAnimationEnd = true;
                Logger.log(Logger.ROOM, TAG + "-->smallVideoAnimation->onAnimationEnd()-->");
                int left = (int) (viewX - moveX);
                int top = (int) (viewY - moveY);
                int width = (int) (viewWidth * scale);
                int height = (int) (viewHeight * scale);
                videoInfo.getParentRl().clearAnimation();

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
                lp.setMargins(left, top, left, top);
                videoInfo.getParentRl().setLayoutParams(lp);
                mEnableAutoAdjust = true;
                setBigVideoState(videoInfo.getVideoState());
                AppHandlerProxy.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        videoInfo.getParentRl().removeView(imageView);
                    }
                }, 100);
                setEnableSmallViews(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        videoInfo.getParentRl().startAnimation(animationSet);

    }

    /**
     * 设置小视屏是否能点击
     *
     * @param isEnable
     */
    private void setEnableSmallViews(boolean isEnable) {
        for (int i = 0; i < mVideoLl.getChildCount(); i++) {
            mVideoLl.getChildAt(i).setEnabled(isEnable);
        }
    }

    /**
     * 隐藏或显示小视屏
     *
     * @param xDelta 平移距离
     */
    private void showHideSmallVideo(final float xDelta) {
        mHideShowVideoIv.setEnabled(false);
        if (mVideoLl.getChildCount() > 0) {
            PropertyValuesHolder translationX;
            if (xDelta > 0) {
                translationX = PropertyValuesHolder.ofFloat("translationX", 0, xDelta);
            } else {
                translationX = PropertyValuesHolder.ofFloat("translationX", -xDelta, 0);
            }
            ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mSmallWindowLl, translationX);
            animator.setDuration(500);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    setEnableSmallViews(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    setEnableSmallViews(true);
                    mSmallWindowLl.clearAnimation();
                    mHideShowVideoIv.setEnabled(true);
                    mHideShowVideoIv.setSelected(!mHideShowVideoIv.isSelected());
                }
            });
            animator.start();
        } else {
            mHideShowVideoIv.setEnabled(true);
        }
    }

    /**
     * 调整surfaceView大小
     *
     * @param glSurfaceView
     * @param videoWidth
     * @param videoHeight
     * @return
     */
    private void adjustSurfaceSize(final GLSurfaceView glSurfaceView, final int videoWidth, final int videoHeight) {
        Logger.logI(Logger.ROOM, "adjustSurfaceSize");
        if (!mEnableAutoAdjust)
            return;
        AppHandlerProxy.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                int rawWidth;
                int rawHeight;
                int bigId = (int) mBigVideoRl.getTag();
                if (mVideoInfoMap.get(bigId) != null && glSurfaceView == mVideoInfoMap.get(bigId).getGlSurfaceView()) {
                    rawWidth = ScreenUtil.getScreenWidth();
                    rawHeight = ScreenUtil.getScreenHeight();
                } else {
                    rawWidth = CommonUtils.dipToPx(getActivity(), 100);
                    rawHeight = CommonUtils.dipToPx(getActivity(), 75);
                }
                Logger.logI(Logger.ROOM, "adjustSurfaceSize（）->rawWidth:" + rawWidth + "->rawHeight:" + rawHeight);
                int newWidth = 0;
                int newHeight = 0;
                if (rawWidth * videoHeight > videoWidth * rawHeight) {
                    newHeight = rawHeight;
                    newWidth = videoWidth * newHeight / videoHeight;
                } else {
                    newWidth = rawWidth;
                    newHeight = videoHeight * newWidth / videoWidth;
                }

                RelativeLayout.LayoutParams viewLp = new RelativeLayout.LayoutParams(newWidth, newHeight);
                viewLp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                glSurfaceView.setLayoutParams(viewLp);
            }
        });
    }

    /**
     * 成员加入房间Toast
     *
     * @param roomResultInfo
     */
    private void showMemberJoinRoomToast(VideoRoomResultInfo roomResultInfo) {
        if (roomResultInfo.getUserType() == AppConstant.UserType.USER_TYPE_PATIENT) {
            mToastTlv.showToastView(getString(R.string.video_room_state_patient_entered_room));
        } else if (roomResultInfo.getUserType() == AppConstant.UserType.USER_TYPE_ATTENDING_DOCTOR) {
            mToastTlv.showToastView(getString(R.string.video_room_state_doctor_entered_room));
        } else if (roomResultInfo.getUserType() == AppConstant.UserType.USER_TYPE_SUPERIOR_DOCTOR) {
            mToastTlv.showToastView(getString(R.string.video_room_state_higher_doctor_entered_room));
        } else if (roomResultInfo.getUserType() == AppConstant.UserType.USER_TYPE_ASSISTANT_DOCTOR) {
            mToastTlv.showToastView(getString(R.string.video_room_state_assistant_entered_room));
        }
    }

    /**
     * 成员退出房间Toast
     *
     * @param roomResultInfo
     */
    private void showMemberExitRoomToast(VideoRoomResultInfo roomResultInfo) {
        if (!mVideoInfoMap.containsKey(roomResultInfo.getUserID())) {
            return;
        }
        VideoInfo videoInfo = mVideoInfoMap.get(roomResultInfo.getUserID());
        String strToast = "";
        switch (videoInfo.getUserType()) {
            case AppConstant.UserType.USER_TYPE_PATIENT:
                strToast = getString(R.string.video_room_state_patient_leave);
                break;

            case AppConstant.UserType.USER_TYPE_ATTENDING_DOCTOR:
                strToast = getString(R.string.video_room_state_doctor_leave);
                break;

            case AppConstant.UserType.USER_TYPE_SUPERIOR_DOCTOR:
                strToast = getString(R.string.video_room_state_higher_doctor_leave);
                break;

            case AppConstant.UserType.USER_TYPE_ASSISTANT_DOCTOR:
                strToast = getString(R.string.video_room_state_assistant_leave);
                break;

            default:
                return;

        }
        showToastViewDelayed(strToast);
    }

    /**
     * 延迟弹Toast提示
     *
     * @param strToast
     */
    private void showToastViewDelayed(final String strToast) {
        AppHandlerProxy.postDelayed(new Runnable() {
            @Override
            public void run() {
                mToastTlv.showToastView(strToast);
            }
        }, 1000);
    }

    /**
     * 退出Dialog
     */
    private void showExitDialog() {
        if (isFinishing()) {
            return;
        }
        if (!CommonUtils.checkActivityExist(this, AppApplication.getInstance().getPackageName())) {
            return;
        }
        new CommonDialog.Builder(this)
                .setMessage(R.string.video_dialog_exit_message)
                .setPositiveButton(R.string.cancel, new CommonDialog.OnPositiveBtnClickListener() {
                    @Override
                    public void onPositiveBtnClicked() {
                    }
                })
                .setNegativeButton(R.string.confirm, new CommonDialog.OnNegativeBtnClickListener() {
                    @Override
                    public void onNegativeBtnClicked() {
                        exitVideoRoom();
                    }
                })
                .setCanceledOnTouchOutside(false)
                .setCancelable(false)
                .show();
    }

    /*********************************************
     * 逻辑操作区end
     **************************************************/

    /*********************************************
     * 接口实现 start
     **************************************************/

    @Override
    public void onPositiveClicked(String clickText) {
        if (clickText.equals(getString(R.string.room_state_btn_retry))) {
            if (!mFlagInRoom) {
                if (NetStateReceiver.NETWORK_TYPE_WIFI == NetStateReceiver.getCurrentNetType(this)) {
                    mFlagShowNetworkDisconnect = true;
                    resetState();
                    joinRoom(mRoomId);
                }
            }
        }
    }

    @Override
    public void onNetworkStateChanged(int netWorkType) {
        Logger.log(Logger.ROOM, "onNetworkStateChanged()网络状态（-1无网络）：" + netWorkType);
        if (netWorkType == NetStateReceiver.NETWORK_TYPE_NONE) {
            mFlagInRoom = false;

            if (mFlagShowNetworkDisconnect) {
                Logger.log(Logger.ROOM, "onNetworkStateChanged()（-1无网络）Toast：");
                mToastTlv.showToastView(getString(R.string.room_state_net_disconnected), getString(R.string.room_state_btn_retry));
                mFlagShowNetworkDisconnect = false;
            }
            stopVideo();
        }
    }

    @Override
    public void onSmallVideoClicked(SmallVideoView view) {
        Logger.log(Logger.ROOM, "onSmallVideoClicked->type:" + mVideoInfoMap.get(view.getTag()).getVideoState());
        int roomState = mVideoInfoMap.get(view.getTag()).getVideoState();
        switch (roomState) {
            case AppConstant.RoomState.STATE_BEING_VIDEO://正在视频
            case AppConstant.RoomState.STATE_DEFAULT:
            case AppConstant.RoomState.STATE_BEING_VOICE://正在音频
                if (mMembersLeaveTv.getVisibility() == View.VISIBLE) {
                    mMembersLeaveTv.setVisibility(View.GONE);
                }
                if ((int) view.getTag() == mPatientId) {
                    mToastTlv.showToastView(getString(R.string.video_room_state_subscribe_patients));
                } else if ((int) view.getTag() == mAppointmentInfo.getBaseInfo().getAttendingDoctorUserId()) {
                    mToastTlv.showToastView(getString(R.string.video_room_state_subscribe_first_doctor));
                } else if ((int) view.getTag() == mAppointmentInfo.getBaseInfo().getDoctorUserId()) {
                    mToastTlv.showToastView(getString(R.string.video_room_state_subscribe_experts));
                }
                switchVideoView(view);
                break;
        }
    }

    /*********************************************
     * 接口实现 end
     **************************************************/

    /**
     * 视频通话事件通知接口
     */
    private class InnerLMVideoEvents implements LMVideoMgr.LMVideoEvents {

        @Override
        public void onVideoSessionStarted(int error) {
            Logger.log(Logger.ROOM, "启动视频会话结果：" + error);
        }

        @Override
        public void onVideoSessionStopped() {
            Logger.log(Logger.ROOM, "视频会话结束");
        }

        @Override
        public void onVideoCaptureStarted(int error) {
            Logger.log(Logger.ROOM, "视频捕获（输出）结果：" + error);
        }

        @Override
        public void onVideoCaptureStopped() {
            Logger.log(Logger.ROOM, "视频捕获（输出）结束");
        }

        @Override
        public void onVideoDisplayStarted(long streamSsrc, int error) {
            Logger.log(Logger.ROOM, "视频播放（输入）结果：" + error);
        }

        @Override
        public void onVideoDisplayStopped(long streamSsrc) {
            Logger.log(Logger.ROOM, "视频播放（输入）结束");
        }

        @Override
        public void onCapturePlaybackVideoSizeReset(final GLSurfaceView glSurfaceView, final int width, final int height) {
            LMVLog.info("<<LMVideoEvents>> Video capture playback video size notified: width = " + width + ", height = " + height);
            if (width == 0 || height == 0)
                return;
            adjustSurfaceSize(glSurfaceView, width, height);
        }

        @Override
        public void onDisplayVideoSizeReset(final GLSurfaceView glSurfaceView, long ssrc, final int width, final int height) {
            LMVLog.info("<<LMVideoEvents>> Displaying video size notified: ssrc = " + ssrc + ", width = " + width + ", height = " + height);
            adjustSurfaceSize(glSurfaceView, width, height);
        }

        @Override
        public void onVideoNetworkState(long streamSsrc, final int state) {
//            Logger.log(Logger.ROOM, "onVideoNetworkState->streamSsrc:" + streamSsrc + "->state:" + state);
        }

        @Override
        public void onDisplayingImageCaptured(long streamSsrc, Bitmap bitmap) {
            Logger.log(Logger.ROOM, "视频播放截图完成");
        }

        @Override
        public void onAlivePacketTimeOut() {
            Logger.log(Logger.ROOM, "视频模块与视频服务器之间的心跳包超时");
            stopVideo();
            AppHandlerProxy.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    mToastTlv.showToastView(getString(R.string.room_state_net_disconnected), getString(R.string.room_state_btn_retry));
                }
            });
        }

        @Override
        public void onCaptureStreamLostRateGet(final float v) {
            AppHandlerProxy.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    int state;
                    if ((Integer) mBigVideoRl.getTag() == mUserId) {
                        state = getNetState(v);
                    } else {
                        state = getNetState(v > mDownLoadLostRate ? v : mDownLoadLostRate);
                    }
                    setVideoSignal(mUserId, state);

                    // 在视频模式下网络状态差时自动切换到语音模式,切换过程为异步方式,需要等到上一次切换结果回调后才能开始下一次切换
                    if (!mFlagInRoom || NetStateReceiver.getCurrentNetType(getActivity()) == NetStateReceiver.NETWORK_TYPE_NONE || mLatestVideoNetState == state)
                        return;

                    mLatestVideoNetState = state;
                    if (state != LMVideoMgr.kVideoNetworkStateNotGood) {
                        mToastTlv.showToastView(getString(R.string.video_room_state_appoint_going));
                    }
                }
            });
        }

        @Override
        public void onPlayStreamLostRateGet(final long l, final float v, final float v1) {
            if (mVideoInfoMap == null)
                return;
            AppHandlerProxy.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    VideoInfo videoInfo = null;
                    int bigVideoId = (Integer) mBigVideoRl.getTag();
                    for (final Map.Entry<Integer, VideoInfo> entry : mVideoInfoMap.entrySet()) {
                        if (entry.getValue() != null && entry.getValue().getSsrc() == l) {
                            videoInfo = entry.getValue();
                            break;
                        }
                    }
                    if (videoInfo == null)
                        return;
                    int state = videoInfo.getVideoState() == AppConstant.RoomState.STATE_BEING_VOICE ? LMVideoMgr.kVideoNetworkStateVeryGood : getNetState(v);
                    setVideoSignal(videoInfo.getUseId(), state);
                    if (videoInfo.getUseId() == bigVideoId) {
                        mDownLoadLostRate = v1;
                    }
                }
            });

        }
    }

    /**
     * 房间自身状态变化监听
     */
    private class InnerRoomSelfStateListener implements VideoRoomInterface.OnRoomSelfStateChangeListener {
        @Override
        public void onSelfJoinRoom(int result, VideoRoomResultInfo resultInfo) {
            if (result == VideoRoomResultCode.VIDEO_ROOM_RESULT_CODE_SUCCESS) {//加入成功
                Logger.log(Logger.ROOM, "onSelfJoinRoom()->加入成功->result:" + result + ", resultInfo:" + resultInfo);
                mFlagInRoom = true;
                mRoomId = resultInfo.getRoomID();
                List<VideoRoomMember> memberList = resultInfo.getMemberList();
                mToastTlv.showToastView(getString(R.string.video_room_state_appoint_going));

                mAudioAdapterManager.setMode(AudioModule.NAME_AFTERVOICE);
                mAudioAdapterManager.setMode(AudioModule.NAME_SPEAKERON);
                mRemoteIP = resultInfo.getPvsIP();
                mRemotePort = resultInfo.getVideoPort();
                mVideoSsrc = resultInfo.getVideoSsrc();

                mConsultRoomManager.pauseMic(true);

                addVideos(resultInfo.getMemberList());

                for (int i = 0; i < memberList.size(); i++) {
                    if (memberList.get(i).getUserType() == AppConstant.UserType.USER_TYPE_PATIENT) {
                        getPatientInfo(mAppointmentId);
                    } else if (memberList.get(i).getUserType() == AppConstant.UserType.USER_TYPE_ATTENDING_DOCTOR
                            || memberList.get(i).getUserType() == AppConstant.UserType.USER_TYPE_SUPERIOR_DOCTOR
                            || memberList.get(i).getUserType() == AppConstant.UserType.USER_TYPE_ASSISTANT_DOCTOR) {
                        getDoctorInfo(memberList.get(i).getUserId());
                    }
                }

                startVideoSession();
                mConsultRoomManager.videoSubscribe(resultInfo.getMemberList(), mSelfStateChangeListener);

            } else {
                mToastTlv.showToastView(getString(R.string.room_state_net_disconnected), getString(R.string.room_state_btn_retry));
            }
            dismissProgressDialog();
        }

        @Override
        public void onSelfExitRoom(int result, VideoRoomResultInfo resultInfo) {
            Logger.log(Logger.ROOM, "onSelfExitRoom->result:" + result + "->resultInfo:" + resultInfo + "->mFlagInRoom:" + mFlagInRoom);
            if (mFlagKickOff) { //被移出房间不做处理,
                return;
            }
            if (result == VideoRoomResultCode.VIDEO_ROOM_RESULT_CODE_SUCCESS) {
                mFlagInRoom = false;
                Logger.log(Logger.ROOM, "onSelfExitRoom->mFlagExitRoom:" + mFlagExitRoom);
                exitRoomDeal();
            } else {
                dismissProgressDialog();
                showToast(getString(R.string.video_room_state_exist_room_failed, result));
            }
        }

        @Override
        public void onSelfVideoSubscribe(int result, VideoRoomResultInfo resultInfo) {
            Logger.log(Logger.ROOM, "C-->resultInfo:" + resultInfo);
            if (resultInfo == null)
                return;
            if (result == VideoRoomResultCode.VIDEO_ROOM_RESULT_CODE_SUCCESS) {
                if (resultInfo.getSsrcList() != null && resultInfo.getSsrcList().size() > 0) {
                    for (int i = 0; i < resultInfo.getSsrcList().size(); i++) {
                        if (resultInfo.getSsrcList().get(i).getSsrc() != 0 && resultInfo.getSsrcList().get(i).getUseId() != mUserId) {
                            mVideoInfoMap.get(resultInfo.getSsrcList().get(i).getUseId()).setSsrc(resultInfo.getSsrcList().get(i).getSsrc());
                            setVideoDisPlay(mVideoInfoMap.get(resultInfo.getSsrcList().get(i).getUseId()));
                        }
                    }
                }
            }
        }
    }


    /**
     * 房间其他成员状态变化监听
     */
    private class InnerRoomMemberStateListener implements VideoRoomInterface.OnRoomMemberStateChangeListener {
        @Override
        public void onMemberJoinRoom(VideoRoomResultInfo roomResultInfo) {
            Logger.log(Logger.ROOM, "onMemberJoinRoom-->roomResultInfo:" + roomResultInfo);
            if (roomResultInfo == null) {
                return;
            } else if (roomResultInfo.getUserType() == AppConstant.UserType.USER_TYPE_ADMINISTRATOR) {
                for (int i = 0; i < mAppointmentInfo.getApplyDoctorInfoList().size(); i++) {
                    if (roomResultInfo.getUserID() != mAppointmentInfo.getApplyDoctorInfoList().get(i).getDoctorUserId()) {
                        return;
                    }
                }
            }
            if (roomResultInfo.getUserType() == AppConstant.UserType.USER_TYPE_ASSISTANT_DOCTOR) {
                getDoctorInfo(roomResultInfo.getUserID());
            } else if (roomResultInfo.getUserType() == AppConstant.UserType.USER_TYPE_PATIENT) {
                getPatientInfo(mAppointmentId);
            } else {
                if (roomResultInfo.getUserType() != AppConstant.UserType.USER_TYPE_MDT_DOCTOR
                        && roomResultInfo.getUserType() != AppConstant.UserType.USER_TYPE_MDT_PATIENT) {
                    getDoctorInfo(roomResultInfo.getUserID());
                }
            }
            mMembersLeaveTv.setVisibility(View.GONE);
            showMemberJoinRoomToast(roomResultInfo);
            addSeatInfo(roomResultInfo.getUserID(), roomResultInfo.getUserSeat());

            VideoRoomMember videoRoomMember = new VideoRoomMember();
            videoRoomMember.setUserId(roomResultInfo.getUserID());
            videoRoomMember.setUserName(roomResultInfo.getUserName());
            videoRoomMember.setUserType(roomResultInfo.getUserType());
            List<VideoRoomMember> ssrcList = new ArrayList<>();
            ssrcList.add(videoRoomMember);
            mConsultRoomManager.videoSubscribe(ssrcList, mSelfStateChangeListener);

            setMemberVideo(videoRoomMember);

            int state = roomResultInfo.getAvType() == ConsultRoomManager.AV_TYPE_VOICE ? AppConstant.RoomState.STATE_BEING_VOICE : AppConstant.RoomState.STATE_BEING_VIDEO;
            setVideoState(roomResultInfo.getUserID(), state);
            if (mVideoInfoMap.get(roomResultInfo.getUserID()) != null) {
                mVideoInfoMap.get(roomResultInfo.getUserID()).setVideoState(state);
            }
        }

        /**
         * 设置成员进入诊室视频显示，包括呼叫进入，敲门进入
         */
        private void setMemberVideo(VideoRoomMember videoRoomMember) {
            for (int i = 0; i < mVideoLl.getChildCount(); i++) {
                if ((int) mVideoLl.getChildAt(i).getTag() == videoRoomMember.getUserId()) {
                    mVideoLl.removeView(mVideoLl.getChildAt(i));
                    break;
                }
            }
            addOtherVideo(videoRoomMember);
        }

        @Override
        public void onMemberExitRoom(VideoRoomResultInfo roomResultInfo) {
            Logger.log(Logger.ROOM, "onMemberExitRoom-->roomResultInfo:" + roomResultInfo);
            if (roomResultInfo == null) {
                return;
            }
            if (roomResultInfo.getUserID() == (int) (mBigVideoRl.getTag())) {
                mMembersLeaveTv.setVisibility(View.VISIBLE);
            }
            showMemberExitRoomToast(roomResultInfo);
            if (mFlagInRoom) {
                removeSeatInfo(roomResultInfo.getUserID(), roomResultInfo.getUserSeat());
                if (roomResultInfo.getUserID() != 0 && mUserId == roomResultInfo.getUserID()) {
                    mFlagKickOff = true;
                    exitVideoRoom();
                    stopVideo();
                    mAudioAdapterManager.setMode(AudioModule.NAME_RESET);
                    mVideoInfoMap.clear();
                    showToast(R.string.video_room_state_kick_off);
                    finish();
                } else {
                    removeVideo(roomResultInfo.getUserID());
                }
            }
        }

        @Override
        public void onMemberChangeAvType(VideoRoomResultInfo roomResultInfo) {
            Logger.log(Logger.ROOM, TAG + "->onMemberChangeAvType->roomResultInfo:" + roomResultInfo);
            if (!mFlagInRoom) {
                return;
            }
            if (roomResultInfo.getUserType() != AppConstant.UserType.USER_TYPE_ADMINISTRATOR) {
                memberChangeAvType(roomResultInfo.getUserID(), roomResultInfo.getAvType());
            }
        }

        @Override
        public void onSpecialMemberChangeAVType(VideoRoomResultInfo roomResultInfo) {
            Logger.log(Logger.ROOM, "onSpecialMemberChangeAVType-->roomResultInfo:" + roomResultInfo);
        }

        @Override
        public void onShutDown(VideoRoomResultInfo roomResultInfo) {
            Logger.log(Logger.ROOM, "onShutDown-->roomResultInfo:" + roomResultInfo);
            stopVideo();
            mFlagInRoom = false;
        }

        @Override
        public void onMemberSpeaking(VideoRoomResultInfo roomResultInfo) {
//            Logger.log(Logger.ROOM, TAG + "->onMemberSpeaking()->userId:" + roomResultInfo.getUserID() + ", seatState:" + roomResultInfo.getSeatSate());
            for (Map.Entry<Integer, VideoInfo> entry : mVideoInfoMap.entrySet()) {
                byte seatIndex = mSetStates.get(entry.getKey()) != null ? (byte) mSetStates.get(entry.getKey()) : 0;
                if (seatIndex >= 1) {
                    int seat = 1 << (seatIndex - 1);
                    setVideoVoice(entry.getValue().getUseId(), (roomResultInfo.getSeatSate() & seat) == seat);
                }
            }
        }

        @Override
        public void onMemberSeatStateChanged(VideoRoomResultInfo roomResultInfo) {
            Logger.log(Logger.ROOM, TAG + "->onMemberSeatStateChanged()->userId:" + roomResultInfo.getUserID() + ", userSeat:" + roomResultInfo.getUserSeat());
            if (mSetStates.indexOfKey(roomResultInfo.getUserID()) >= 0) {
                mSetStates.remove(roomResultInfo.getUserID());
            }
            addSeatInfo(roomResultInfo.getUserID(), roomResultInfo.getUserSeat());
        }
    }

    private static class MyVideoRendererGui extends VideoRendererGui {
        private int mWidth;
        private int mHeight;
        private boolean mIsCutBitmap;
        private OnCutBitmapListener mOnCutBitmapListener;

        public MyVideoRendererGui(GLSurfaceView surface, Context context) {
            super(surface, context);
        }

        public void setCutBitmap(int width, int height, OnCutBitmapListener listener) {
            mWidth = width;
            mHeight = height;
            mIsCutBitmap = true;
            mOnCutBitmapListener = listener;
        }

        @Override
        public void onDrawFrame(GL10 unused) {
            super.onDrawFrame(unused);
            if (mIsCutBitmap) {
                mIsCutBitmap = false;
                Bitmap bmp = createBitmapFromGLSurface(0, 0, mWidth,
                        mHeight, unused);
                mOnCutBitmapListener.OnCutBitmapfinished(bmp);
            }
        }

        private Bitmap createBitmapFromGLSurface(int x, int y, int w, int h, GL10 gl) {
            int bitmapBuffer[] = new int[w * h];
            int bitmapSource[] = new int[w * h];
            IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
            intBuffer.position(0);
            try {
                gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE,
                        intBuffer);
                int offset1, offset2;
                for (int i = 0; i < h; i++) {
                    offset1 = i * w;
                    offset2 = (h - i - 1) * w;
                    for (int j = 0; j < w; j++) {
                        int texturePixel = bitmapBuffer[offset1 + j];
                        int blue = (texturePixel >> 16) & 0xff;
                        int red = (texturePixel << 16) & 0x00ff0000;
                        int pixel = (texturePixel & 0xff00ff00) | red | blue;
                        bitmapSource[offset2 + j] = pixel;
                    }
                }
            } catch (GLException e) {
                return null;
            }
            return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
        }

        public interface OnCutBitmapListener {
            void OnCutBitmapfinished(Bitmap bmp);
        }
    }

}