package cn.longmaster.ihuvc.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.hardware.usb.UsbDevice;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import cn.longmaster.doctorlibrary.utils.common.MD5Util;
import cn.longmaster.doctorlibrary.utils.common.StringUtil;
import cn.longmaster.doctorlibrary.utils.log.Logger;
import cn.longmaster.doctorlibrary.viewinject.FindViewById;
import cn.longmaster.doctorlibrary.viewinject.OnClick;
import cn.longmaster.doctorlibrary.viewinject.ViewInjecter;
import cn.longmaster.ihuvc.R;
import cn.longmaster.ihuvc.core.AppApplication;
import cn.longmaster.ihuvc.core.AppPreference;
import cn.longmaster.ihuvc.core.entity.AppointmentInfo;
import cn.longmaster.ihuvc.core.entity.CheckVersionInfo;
import cn.longmaster.ihuvc.core.entity.MaterialTypeInfo;
import cn.longmaster.ihuvc.core.entity.PatientInfo;
import cn.longmaster.ihuvc.core.http.BaseResult;
import cn.longmaster.ihuvc.core.http.OnResultListener;
import cn.longmaster.ihuvc.core.http.requesters.CheckVersionRequester;
import cn.longmaster.ihuvc.core.manager.SdManager;
import cn.longmaster.ihuvc.core.manager.VersionChangeListener;
import cn.longmaster.ihuvc.core.manager.VersionManager;
import cn.longmaster.ihuvc.core.upload.AbsTask;
import cn.longmaster.ihuvc.core.upload.MaterialTask;
import cn.longmaster.ihuvc.core.upload.SimpleNotifyListener;
import cn.longmaster.ihuvc.core.upload.SingleFileInfo;
import cn.longmaster.ihuvc.core.upload.UploadTaskManager;
import cn.longmaster.ihuvc.core.usb.DeviceHelper;
import cn.longmaster.ihuvc.core.usb.IFrameCallback;
import cn.longmaster.ihuvc.core.usb.USBMonitor;
import cn.longmaster.ihuvc.core.usb.UVCCamera;
import cn.longmaster.ihuvc.core.usb.encode.MediaAudioEncoder;
import cn.longmaster.ihuvc.core.usb.encode.MediaEncoder;
import cn.longmaster.ihuvc.core.usb.encode.MediaMuxerWrapper;
import cn.longmaster.ihuvc.core.usb.encode.MediaVideoEncoder;
import cn.longmaster.ihuvc.core.usb.widget.CameraViewInterface;
import cn.longmaster.ihuvc.core.usb.widget.UVCCameraTextureView;
import cn.longmaster.ihuvc.core.utils.BitmapUtil;
import cn.longmaster.ihuvc.core.utils.FileUtil;
import cn.longmaster.ihuvc.core.utils.handler.AppHandlerProxy;
import cn.longmaster.ihuvc.core.utils.thread.AppAsyncTask;
import cn.longmaster.ihuvc.core.utils.thread.AsyncResult;
import cn.longmaster.ihuvc.view.AppActionBar;
import cn.longmaster.ihuvc.view.dialog.CommonDialog;
import cn.longmaster.ihuvc.view.dialog.CustomProgressDialog;

public class CaptureActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback, DeviceHelper.CameraDialogParent {
    private static final String TAG = CaptureActivity.class.getSimpleName();
    public static final String EXTRA_DATA_APPOINTMENT_INFO = TAG + "_extra_data_appointment_info";
    public static final String EXTRA_DATA_PATIENT_INFO = TAG + "_extra_data_patient_info";
    public static final String EXTRA_DATA_MATERIAL_TYPE_INFO = TAG + "_extra_data_material_type_info";
    public static final String EXTRA_DATA_TYPE = TAG + "_extra_data_type";
    public static final String EXTRA_DATA_UPLOAD_SUCCESS_NUM = TAG + "_extra_data_upload_success_num";
    private static final int RESULT_CODE = 100;

    private final int CAPUTURE_TYPE_UVC = 0;
    private final int CAPUTURE_TYPE_DERMATOSCOPE = 1;
    private final int CAPUTURE_TYPE_CAMERA = 2;

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

    private static final String DEVICE_NAME_UVC = "Doccamera";
    private static final String DEVICE_NAME_DERMATOSCOPE = "PC Cam";

    private static int UVC_PREVIEW_WIDTH = 1024;
    private static int UVC_PREVIEW_HEIGHT = 768;

    private static int DERMATOSCOPE_PREVIEW_WIDTH = 1600;
    private static int DERMATOSCOPE_PREVIEW_HEIGHT = 1200;

    private static final int PREVIEW_MODE = UVCCamera.FRAME_FORMAT_MJPEG;

    @FindViewById(R.id.activity_capture_viewpager)
    private ViewPager mViewPager;

    @FindViewById(R.id.activity_shot_bar)
    private AppActionBar mActionBar;
    @FindViewById(R.id.activity_capture_appointment_id_tv)
    private TextView mAppointIdTv;
    @FindViewById(R.id.activity_capture_patient_name_tv)
    private TextView mPatientNameTv;
    @FindViewById(R.id.activity_capture_uvc_title_tv)
    private TextView mUVCTitleTv;
    @FindViewById(R.id.activity_capture_dermatoscope_title_tv)
    private TextView mDermatoScopeTitleTv;
    @FindViewById(R.id.activity_capture_camera_title_tv)
    private TextView mCameraTitleTv;
    @FindViewById(R.id.activity_capture_pic_layout_ll)
    private LinearLayout mPicLayoutLl;
    @FindViewById(R.id.activity_capture_mask_view)
    private View mMaskView;
    @FindViewById(R.id.activity_capture_pic_iv)
    private ImageView mPicIv;
    @FindViewById(R.id.activity_capture_progressbar)
    private ProgressBar mProgressBar;

    private CameraView mCameraView;
    private UVCCameraTextureView mUVCCameraTextureView;
    private UVCCameraTextureView mDermatoScopeCameraTextureView;
    private CameraViewInterface mUVCCameraView;
    private CameraViewInterface mDermatoScopeCameraView;
    private CommonDialog mUVCDialog;
    private CommonDialog mDermatoScopeDialog;
    private CommonDialog mCameraDialog;
    private static CustomProgressDialog mProgressDialog;

    private UploadTaskManager mUploadTaskManager;
    private List<View> mCameraViews;
    private AppointmentInfo mAppointmentInfo;
    private PatientInfo mPatientInfo;
    private int mCaptureType = CAPUTURE_TYPE_UVC;
    private Handler mBackgroundHandler;

    private USBMonitor mUSBMonitor;
    private CameraHandler mCameraHandler;
    private CameraHandler mDermatoScopeCameraHandler;
    private DeviceHelper mDeviceHelper;
    private Surface mSurface;
    private boolean mNewVersion = false;
    private PopupWindow mPopWindow;
    private boolean isUploading = false;
    private Animation animShadowAlphaIn, animShadowAlphaOut;
    private long mScrollTime;//记录最后一次滑动时间，在弹UVCdialog时，过滤滑动产生的断开情况；

    private static Handler mUVCHandler;
    private int mDataType;
    private List<MaterialTypeInfo> mMaterialTypeInfo;
    private int mUploadSuccessNum = 0;
    private PowerManager.WakeLock mWakeLock;
    private static int mPreviewWidth;
    private static int mPreviewHeight;


    public static void startCaputureActivity(Activity context, AppointmentInfo appointmentInfo, PatientInfo patientInfo, int type, List<MaterialTypeInfo> materialTypeInfo) {
        Intent intent = new Intent(context, CaptureActivity.class);
        intent.putExtra(EXTRA_DATA_APPOINTMENT_INFO, appointmentInfo);
        intent.putExtra(EXTRA_DATA_PATIENT_INFO, patientInfo);
        intent.putExtra(EXTRA_DATA_MATERIAL_TYPE_INFO, (Serializable) materialTypeInfo);
        intent.putExtra(EXTRA_DATA_TYPE, type);
        context.startActivityForResult(intent, RESULT_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        ViewInjecter.inject(this);
        acquireWakeLock();
        initData();
        initAnim();
        initView();
        initCamera();
        startUVCCamera();
    }

    private void initData() {
        mAppointmentInfo = (AppointmentInfo) getIntent().getSerializableExtra(EXTRA_DATA_APPOINTMENT_INFO);
        mPatientInfo = (PatientInfo) getIntent().getSerializableExtra(EXTRA_DATA_PATIENT_INFO);
        mDataType = getIntent().getIntExtra(EXTRA_DATA_TYPE, 0);
        mMaterialTypeInfo = (List<MaterialTypeInfo>) getIntent().getSerializableExtra(EXTRA_DATA_MATERIAL_TYPE_INFO);
        mUploadTaskManager = AppApplication.getInstance().getManager(UploadTaskManager.class);
        mUploadTaskManager.addUploadNotifyListener(mNotifyListener);
        mUVCHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1000:
                        stopUVCCamera();
                        mProgressDialog.dismissWithSuccess(getString(R.string.capture_success));
                        uploadPic((String) msg.obj);
                        break;
                }
            }
        };
    }

    private void initAnim() {
        animShadowAlphaIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shadow_alpha_in);
        animShadowAlphaOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shadow_alpha_out);
    }

    private void initView() {
        mAppointIdTv.setText(getString(R.string.number, mAppointmentInfo.getBaseInfo().getAppointmentId() + ""));
        mPatientNameTv.setText(getString(R.string.patient_number, mMaterialTypeInfo.get(mDataType).getmMaterialTypeName() + ""));

        View uvcLayout = getLayoutInflater().inflate(R.layout.layout_capture_uvc, null, false);
        mUVCCameraTextureView = (UVCCameraTextureView) uvcLayout.findViewById(R.id.layout_caputure_uvccamera);

        View dermatoScopeLayout = getLayoutInflater().inflate(R.layout.layout_capture_dermatoscope, null, false);
        mDermatoScopeCameraTextureView = (UVCCameraTextureView) dermatoScopeLayout.findViewById(R.id.layout_caputure_dermatoscope_texttureview);

        View cameraLayout = getLayoutInflater().inflate(R.layout.layout_capture_camera, null, false);
        mCameraView = (CameraView) cameraLayout.findViewById(R.id.layout_capture_camera_cameraview);

        mCameraViews = new ArrayList<>();
        mCameraViews.add(uvcLayout);
        mCameraViews.add(dermatoScopeLayout);
        mCameraViews.add(cameraLayout);

        CameraPagerAdapter adapter = new CameraPagerAdapter(mCameraViews);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                mScrollTime = System.currentTimeMillis();
                mCaptureType = position;
                displayCameraTitle();
                mUVCHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mCaptureType == CAPUTURE_TYPE_UVC || mCaptureType == CAPUTURE_TYPE_DERMATOSCOPE) {
                            stopCamera();
                            startUVCCamera();
                        } else {
                            stopUVCCamera();
                            startCamera();
                        }
                    }
                }, 500);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCaptureType == CAPUTURE_TYPE_UVC) {
            if (mUVCCameraView != null) {
                mUVCCameraView.onResume();
            }
        } else if (mCaptureType == CAPUTURE_TYPE_DERMATOSCOPE) {
            if (mDermatoScopeCameraView != null) {
                mDermatoScopeCameraView.onResume();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCaptureType == CAPUTURE_TYPE_UVC) {
            if (mUVCCameraView != null) {
                mUVCCameraView.onPause();
            }
        } else if (mCaptureType == CAPUTURE_TYPE_DERMATOSCOPE) {
            if (mDermatoScopeCameraView != null) {
                mDermatoScopeCameraView.onPause();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCamera();
        if (mCaptureType == CAPUTURE_TYPE_UVC) {
            if (mCameraHandler != null) {
                mCameraHandler.closeCamera();
            }
        } else {
            if (mDermatoScopeCameraHandler != null) {
                mDermatoScopeCameraHandler.closeCamera();
            }
        }

        if (mUSBMonitor != null) {
            mUSBMonitor.unregister();
            mUSBMonitor.destroy();
        }
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
        }
        mUploadTaskManager.removeUploadNotifyListener(mNotifyListener);
        releaseWakeLock();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (permissions.length != 1 || grantResults.length != 1) {
                    showToast(R.string.camera_permission_not_granted);
                    return;
                }
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mCameraView.start();
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    showToast(R.string.camera_permission_not_granted);
                }
                break;
        }
    }

    @OnClick({R.id.activity_capture_capture_ib,
            R.id.activity_capture_mask_view})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_capture_capture_ib:
                if (mCaptureType == CAPUTURE_TYPE_UVC) {
                    if (mCameraHandler.isCameraOpened()) {
                        mProgressDialog = new CustomProgressDialog(this, getString(R.string.be_captureing));
                        mProgressDialog.setIsConsumeKeyBack(true);
                        mProgressDialog.show();

                        mCameraHandler.captureStill();
                    }
                } else if (mCaptureType == CAPUTURE_TYPE_DERMATOSCOPE) {
                    if (mDermatoScopeCameraHandler.isCameraOpened()) {
                        mProgressDialog = new CustomProgressDialog(this, getString(R.string.be_captureing));
                        mProgressDialog.setIsConsumeKeyBack(true);
                        mProgressDialog.show();

                        mDermatoScopeCameraHandler.captureStill();
                    }
                } else if (mCaptureType == CAPUTURE_TYPE_CAMERA) {
                    mCameraView.takePicture();
                }
                break;
            case R.id.activity_capture_mask_view:
                //什么也不干，只为拦截点击和滑动事件
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isUploading) {
            return true;
        }
        setResult();
        return super.onKeyDown(keyCode, event);
    }

    private void acquireWakeLock() {
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, this.getClass().getCanonicalName());
            mWakeLock.acquire();
        }

    }


    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }

    }

    public void onLeftClick(View view) {
        if (mCaptureType == CAPUTURE_TYPE_UVC || mCaptureType == CAPUTURE_TYPE_DERMATOSCOPE) {
            stopUVCCamera();
        } else if (mCaptureType == CAPUTURE_TYPE_CAMERA) {
            stopCamera();
        }
        setResult();
        finish();
    }

    private void setResult() {
        if (mUploadSuccessNum > 0) {
            Intent mIntent = new Intent();
            mIntent.putExtra(EXTRA_DATA_TYPE, mDataType);
            mIntent.putExtra(EXTRA_DATA_UPLOAD_SUCCESS_NUM, mUploadSuccessNum);
            setResult(RESULT_OK, mIntent);
        }
    }

    public void onRightClick(View view) {
        showPopupWindow(view);
    }

    private void showPopupWindow(View view) {
        View contentView = LayoutInflater.from(CaptureActivity.this).inflate(R.layout.popuplayout, null);
        mPopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);
        mPopWindow.setBackgroundDrawable(new ColorDrawable(0));
        mPopWindow.setFocusable(true);
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.showAsDropDown(view);

        TextView highShotInstrument = (TextView) contentView.findViewById(R.id.pop_high_shot_instrument);
        TextView camera = (TextView) contentView.findViewById(R.id.pop_rear_camera);
        TextView dermatoscope = (TextView) contentView.findViewById(R.id.pop_dermatoscope);
        TextView update = (TextView) contentView.findViewById(R.id.pop_update);
        highShotInstrument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUVCDialog(true);
                mPopWindow.dismiss();
            }
        });
        dermatoscope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDermatoScopeDialog(true);
                mPopWindow.dismiss();
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCameraDialog();
                mPopWindow.dismiss();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckVersionRequester requester = new CheckVersionRequester(new OnResultListener<CheckVersionInfo>() {
                    @Override
                    public void onResult(BaseResult baseResult, CheckVersionInfo checkVersionInfo) {
                        Logger.log(Logger.COMMON, "baseResult:" + baseResult + "---checkVersionInfo:" + checkVersionInfo);
                        if (baseResult.getCode() == RESULT_SUCCESS && checkVersionInfo != null) {
                            VersionManager.getInstance().setClientVersionLimit(checkVersionInfo.getClienVersionLimit());
                            VersionManager.getInstance().setClientVersionLatest(checkVersionInfo.getGetClienVersionLatest());
                            showUpdateDialog(checkVersionInfo);
                        } else {
                            showNoNewVersionDialog();
                        }
                    }
                });
                requester.doPost();
                mPopWindow.dismiss();
            }
        });
    }

    private void showNoNewVersionDialog() {
        new CommonDialog.Builder(getActivity())
                .setTitle(R.string.no_available_update)
                .setMessageOne(R.string.update_content)
                .setPositiveButton(R.string.close, new CommonDialog.OnPositiveBtnClickListener() {
                    @Override
                    public void onPositiveBtnClicked() {

                    }
                })
                .setCanceledOnTouchOutside(false)
                .setCancelable(false)
                .show();
    }

    private void showUVCDialog(boolean isForce) {
        if (isActivityDestroyed()) {
            return;
        }
        if (!isForce && mCaptureType != CAPUTURE_TYPE_UVC || System.currentTimeMillis() - mScrollTime < 500) {
            return;
        }
        if (mUVCDialog == null) {
            mUVCDialog = new CommonDialog.Builder(this)
                    .setTitle(R.string.high_shot_data_collection)
                    .setMessageOne(R.string.high_shot_content)
                    .setMessageTwo(R.string.high_shot_content_method)
                    .setPositiveButton(R.string.close, new CommonDialog.OnPositiveBtnClickListener() {
                        @Override
                        public void onPositiveBtnClicked() {

                        }
                    })
                    .setCanceledOnTouchOutside(false)
                    .setCancelable(false)
                    .show();
        } else {
            if (mUVCDialog.isShowing()) {
                return;
            }
            mUVCDialog.show();
        }

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void dismissUVCDialog() {
        if (mUVCDialog != null && mUVCDialog.isShowing()) {
            mUVCDialog.dismiss();
        }
    }

    private void showDermatoScopeDialog(boolean isForce) {
        if (isActivityDestroyed()) {
            return;
        }
        if (!isForce && mCaptureType != CAPUTURE_TYPE_DERMATOSCOPE || System.currentTimeMillis() - mScrollTime < 500) {
            return;
        }
        if (mDermatoScopeDialog == null) {
            mDermatoScopeDialog = new CommonDialog.Builder(this)
                    .setTitle(R.string.dermatoscope_data_collection)
                    .setMessageOne(R.string.dermatoscope_content)
                    .setMessageTwo(R.string.dermatoscope_content_method)
                    .setPositiveButton(R.string.close, new CommonDialog.OnPositiveBtnClickListener() {
                        @Override
                        public void onPositiveBtnClicked() {

                        }
                    })
                    .setCanceledOnTouchOutside(false)
                    .setCancelable(false)
                    .show();
        } else {
            if (mDermatoScopeDialog.isShowing()) {
                return;
            }
            mDermatoScopeDialog.show();
        }

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void dismissDermatoScopeDialog() {
        if (mDermatoScopeDialog != null && mDermatoScopeDialog.isShowing()) {
            mDermatoScopeDialog.dismiss();
        }
    }

    private void showCameraDialog() {
        if (isActivityDestroyed()) {
            return;
        }
        if (mCameraDialog == null) {
            mCameraDialog = new CommonDialog.Builder(this)
                    .setTitle(R.string.rear_camera_data_collection)
                    .setMessageOne(R.string.rear_camera_content)
                    .setMessageTwo(R.string.rear_camera_content_method)
                    .setPositiveButton(R.string.close, new CommonDialog.OnPositiveBtnClickListener() {
                        @Override
                        public void onPositiveBtnClicked() {

                        }
                    })
                    .setCanceledOnTouchOutside(false)
                    .setCancelable(false)
                    .show();
        } else {
            if (mCameraDialog.isShowing()) {
                return;
            }
            mCameraDialog.show();
        }
    }

    private void showUpdateDialog(CheckVersionInfo checkVersionInfo) {
        if (VersionManager.getInstance().getCurentClientVersion() < checkVersionInfo.getGetClienVersionLatest() ||
                VersionManager.getInstance().getCurentClientVersion() < checkVersionInfo.getClienVersionLimit()) {
            new CommonDialog.Builder(getActivity())
                    .setTitle(R.string.new_version_title)
                    .setMessageOne(getString(R.string.new_version, VersionManager.getInstance().getFullVersionName("", checkVersionInfo.getGetClienVersionLatest())))
                    .setPositiveButton(R.string.close, new CommonDialog.OnPositiveBtnClickListener() {
                        @Override
                        public void onPositiveBtnClicked() {

                        }
                    })
                    .setNegativeButton(R.string.update_date, new CommonDialog.OnNegativeBtnClickListener() {
                        @Override
                        public void onNegativeBtnClicked() {
                            checkVersion();
                        }
                    })
                    .setCanceledOnTouchOutside(false)
                    .setCancelable(false)
                    .show();
        } else {
            showNoNewVersionDialog();
        }
    }

    private VersionChangeListener versionChangeListener = new VersionChangeListener() {
        @Override
        public void onNewVersion() {
            VersionManager.getInstance().upgrade(CaptureActivity.this);
        }

        @Override
        public void onVersionLimited() {
            VersionManager.getInstance().upgrade(CaptureActivity.this);
        }
    };

    /**
     * 检查更新
     */
    private void checkVersion() {
        if (VersionManager.getInstance().getCurentClientVersion() < AppPreference.getIntValue(AppPreference.KEY_SERVER_LIMIT_VERSION, 0)) {
            versionChangeListener.onVersionLimited();
        } else if (VersionManager.getInstance().getCurentClientVersion() < AppPreference.getIntValue(AppPreference.KEY_SERVER_LASTEST_VERSION, 0)) {
            versionChangeListener.onNewVersion();
        }
    }

    private void displayCameraTitle() {
        if (mCaptureType == CAPUTURE_TYPE_UVC) {
            mUVCTitleTv.setTextColor(getResColor(R.color.color_ffffff));
            mDermatoScopeTitleTv.setTextColor(getResColor(R.color.color999999));
            mCameraTitleTv.setTextColor(getResColor(R.color.color999999));
        } else if (mCaptureType == CAPUTURE_TYPE_DERMATOSCOPE) {
            mUVCTitleTv.setTextColor(getResColor(R.color.color999999));
            mDermatoScopeTitleTv.setTextColor(getResColor(R.color.color_ffffff));
            mCameraTitleTv.setTextColor(getResColor(R.color.color999999));
        } else {
            mUVCTitleTv.setTextColor(getResColor(R.color.color999999));
            mDermatoScopeTitleTv.setTextColor(getResColor(R.color.color999999));
            mCameraTitleTv.setTextColor(getResColor(R.color.color_ffffff));
        }
    }

    private void uploadPic(final String path) {
        AppHandlerProxy.post(new Runnable() {
            @Override
            public void run() {
                showPicLayout();
                mPicIv.setImageBitmap(BitmapUtil.getBitmapFromPath(path));
                startUpload(path);
            }
        });
    }

    private void uploadTaskFinished() {
        isUploading = false;
        hidePicLayout();
        if (mCaptureType == CAPUTURE_TYPE_UVC || mCaptureType == CAPUTURE_TYPE_DERMATOSCOPE) {
            startUVCCamera();
        } else if (mCaptureType == CAPUTURE_TYPE_CAMERA) {
            startCamera();
        }
    }

    private void showPicLayout() {
        mProgressBar.setProgress(0);
        mMaskView.setVisibility(View.VISIBLE);
        mMaskView.startAnimation(animShadowAlphaIn);
        mPicLayoutLl.setVisibility(View.VISIBLE);
    }

    private void hidePicLayout() {
        mPicLayoutLl.setVisibility(View.GONE);
        mMaskView.setVisibility(View.GONE);
        mMaskView.startAnimation(animShadowAlphaOut);
    }

    //=============================================camera=============================================
    private void initCamera() {
        mCameraView.addCallback(mCallback);
        mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);
        mUVCCameraView = (CameraViewInterface) mUVCCameraTextureView;
        mUVCCameraView.setAspectRatio(UVC_PREVIEW_WIDTH / (float) UVC_PREVIEW_HEIGHT);
        mCameraHandler = CaptureActivity.CameraHandler.createHandler(this, mUVCCameraView);

        mDermatoScopeCameraView = (CameraViewInterface) mDermatoScopeCameraTextureView;
        mDermatoScopeCameraView.setAspectRatio(DERMATOSCOPE_PREVIEW_WIDTH / (float) DERMATOSCOPE_PREVIEW_HEIGHT);
        mDermatoScopeCameraHandler = CaptureActivity.CameraHandler.createHandler(this, mDermatoScopeCameraView);

        try {
            mDeviceHelper = new DeviceHelper(CaptureActivity.this);
        } catch (Exception e) {
            showToast("请检查USB外部设备是否连接！");
            e.printStackTrace();
            return;
        }

        if (mDeviceHelper == null) {
            showToast("请检查USB外部设备是否连接！");
            return;
        }
    }

    private void startCamera() {
//        if (Build.VERSION.SDK_INT > 22) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mCameraView.start();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ConfirmationDialogFragment
                    .newInstance(R.string.camera_permission_confirmation,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA_PERMISSION,
                            R.string.camera_permission_not_granted)
                    .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
//        }
    }

    private void stopCamera() {
        mCameraView.stop();
    }

    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }

    private CameraView.Callback mCallback = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {

        }

        @Override
        public void onCameraClosed(CameraView cameraView) {

        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            getBackgroundHandler().post(new Runnable() {
                @Override
                public void run() {
                    String fileName = SdManager.getInstance().getTempPath() + System.currentTimeMillis() + ".png";
                    final File file = new File(fileName);
                    OutputStream os = null;
                    try {
                        os = new FileOutputStream(file);
                        os.write(data);
                        os.close();
                        Logger.log(Logger.CAMERA, "图片保存成功");
                        stopCamera();
                        uploadPic(fileName);
                    } catch (IOException e) {
                        Logger.logE(Logger.CAMERA, "Cannot write to " + file, e);
                        e.printStackTrace();
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    };


    //=============================================uvc=============================================

    private void startUVCCamera() {
        if (mCaptureType == CAPUTURE_TYPE_UVC) {
            mPreviewWidth = UVC_PREVIEW_WIDTH;
            mPreviewHeight = UVC_PREVIEW_HEIGHT;
        } else {
            mPreviewWidth = DERMATOSCOPE_PREVIEW_WIDTH;
            mPreviewHeight = DERMATOSCOPE_PREVIEW_HEIGHT;
        }

        AppApplication.HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                mUSBMonitor.register();
                mDeviceHelper.requestDevices();
            }
        }, 1000);
    }

    private void stopUVCCamera() {
        if (mCaptureType == CAPUTURE_TYPE_UVC) {
            mCameraHandler.closeCamera();
        } else {
            mDermatoScopeCameraHandler.closeCamera();
        }
    }

    private void startPreview() {
        SurfaceTexture st = null;

        if (mCaptureType == CAPUTURE_TYPE_UVC) {
            st = mUVCCameraView.getSurfaceTexture();
        } else {
            st = mDermatoScopeCameraView.getSurfaceTexture();
        }

        if (mSurface != null) {
            mSurface.release();
        }

        if (st != null) {
            mSurface = new Surface(st);
            if (mCaptureType == CAPUTURE_TYPE_UVC) {
                mCameraHandler.startPreview(mSurface);
            } else {
                mDermatoScopeCameraHandler.startPreview(mSurface);
            }
        }
    }

    @Override
    public USBMonitor getUSBMonitor() {
        return mUSBMonitor;
    }

    private static final class CameraHandler extends Handler {
        private static final int MSG_OPEN = 0;
        private static final int MSG_CLOSE = 1;
        private static final int MSG_PREVIEW_START = 2;
        private static final int MSG_PREVIEW_STOP = 3;
        private static final int MSG_CAPTURE_STILL = 4;
        private static final int MSG_CAPTURE_START = 5;
        private static final int MSG_CAPTURE_STOP = 6;
        private static final int MSG_MEDIA_UPDATE = 7;
        private static final int MSG_RELEASE = 9;

        private final WeakReference<CaptureActivity.CameraHandler.CameraThread> mWeakThread;

        public static final CaptureActivity.CameraHandler createHandler(final CaptureActivity parent, final CameraViewInterface cameraView) {
            final CaptureActivity.CameraHandler.CameraThread thread = new CaptureActivity.CameraHandler.CameraThread(parent, cameraView);
            thread.start();
            return thread.getHandler();
        }

        private CameraHandler(final CaptureActivity.CameraHandler.CameraThread thread) {
            mWeakThread = new WeakReference<CaptureActivity.CameraHandler.CameraThread>(thread);
        }

        public boolean isCameraOpened() {
            final CaptureActivity.CameraHandler.CameraThread thread = mWeakThread.get();
            return thread != null ? thread.isCameraOpened() : false;
        }

        public boolean isRecording() {
            final CaptureActivity.CameraHandler.CameraThread thread = mWeakThread.get();
            return thread != null ? thread.isRecording() : false;
        }

        public void openCamera(final USBMonitor.UsbControlBlock ctrlBlock) {
            sendMessage(obtainMessage(MSG_OPEN, ctrlBlock));
        }

        public void closeCamera() {
            stopPreview();
        }

        public void startPreview(final Surface sureface) {
            if (sureface != null)
                sendMessage(obtainMessage(MSG_PREVIEW_START, sureface));
        }

        public void stopPreview() {
            stopRecording();
            final CaptureActivity.CameraHandler.CameraThread thread = mWeakThread.get();
            if (thread == null) return;
            synchronized (thread.mSync) {
                sendEmptyMessage(MSG_PREVIEW_STOP);
                try {
                    thread.mSync.wait();
                } catch (final InterruptedException e) {
                }
            }
        }

        public void captureStill() {
            sendEmptyMessage(MSG_CAPTURE_STILL);
        }

        public void startRecording() {
            sendEmptyMessage(MSG_CAPTURE_START);
        }

        public void stopRecording() {
            sendEmptyMessage(MSG_CAPTURE_STOP);
        }

        @Override
        public void handleMessage(final Message msg) {
            final CaptureActivity.CameraHandler.CameraThread thread = mWeakThread.get();
            if (thread == null) return;
            switch (msg.what) {
                case MSG_OPEN:
                    thread.handleOpen((USBMonitor.UsbControlBlock) msg.obj);
                    break;

                case MSG_CLOSE:
                    thread.handleClose();
                    break;

                case MSG_PREVIEW_START:
                    thread.handleStartPreview((Surface) msg.obj);
                    break;

                case MSG_PREVIEW_STOP:
                    thread.handleStopPreview();
                    break;

                case MSG_CAPTURE_STILL:
                    thread.handleCaptureStill();
                    break;

                case MSG_CAPTURE_START:
                    thread.handleStartRecording();
                    break;

                case MSG_CAPTURE_STOP:
                    thread.handleStopRecording();
                    break;

                case MSG_MEDIA_UPDATE:
                    thread.handleUpdateMedia((String) msg.obj);
                    break;

                case MSG_RELEASE:
                    thread.handleRelease();
                    break;

                default:
                    throw new RuntimeException("unsupported message:what=" + msg.what);
            }
        }

        private static final class CameraThread extends Thread {
            private static final String TAG_THREAD = "CameraThread";
            private final Object mSync = new Object();
            private final WeakReference<CaptureActivity> mWeakParent;
            private final WeakReference<CameraViewInterface> mWeakCameraView;
            private boolean mIsRecording;

            private SoundPool mSoundPool;
            private int mSoundId;
            private CaptureActivity.CameraHandler mHandler;

            private UVCCamera mUVCCamera;
            private MediaMuxerWrapper mMuxer;

            private MediaVideoEncoder mVideoEncoder;

            private CameraThread(final CaptureActivity parent, final CameraViewInterface cameraView) {
                super("CameraThread");
                mWeakParent = new WeakReference<CaptureActivity>(parent);
                mWeakCameraView = new WeakReference<CameraViewInterface>(cameraView);
                loadSutterSound(parent);
            }

            @Override
            protected void finalize() throws Throwable {
                super.finalize();
            }

            public CaptureActivity.CameraHandler getHandler() {
                synchronized (mSync) {
                    if (mHandler == null)
                        try {
                            mSync.wait();
                        } catch (final InterruptedException e) {
                        }
                }
                return mHandler;
            }

            public boolean isCameraOpened() {
                return mUVCCamera != null;
            }

            public boolean isRecording() {
                return (mUVCCamera != null) && (mMuxer != null);
            }

            public void handleOpen(final USBMonitor.UsbControlBlock ctrlBlock) {
                handleClose();
                mUVCCamera = new UVCCamera();
                mUVCCamera.open(ctrlBlock);
//                Toast.makeText(mWeakParent.get(), "OutCamera Support Size: "
//                        + mUVCCamera.getSupportedSize(), Toast.LENGTH_LONG).show();
            }

            public void handleClose() {
                if (mUVCCamera != null) {
                    mUVCCamera.stopPreview();
                    mUVCCamera.destroy();
                    mUVCCamera = null;
                }
            }

            public void handleStartPreview(final Surface surface) {
                if (mUVCCamera == null) return;
                try {
                    mUVCCamera.setPreviewSize(mPreviewWidth, mPreviewHeight, PREVIEW_MODE);
                } catch (final IllegalArgumentException e) {
                    try {
                        mUVCCamera.setPreviewSize(mPreviewWidth, mPreviewHeight, UVCCamera.DEFAULT_PREVIEW_MODE);
                        e.printStackTrace();
                    } catch (final IllegalArgumentException e1) {
                        handleClose();
                        e1.printStackTrace();
                    }
                }
                if (mUVCCamera != null) {
                    mUVCCamera.setPreviewDisplay(surface);
                    mUVCCamera.startPreview();
                }
            }

            public void handleStopPreview() {
                if (mUVCCamera != null) {
                    mUVCCamera.stopPreview();
                }
                synchronized (mSync) {
                    mSync.notifyAll();
                }
            }

            public void handleCaptureStill() {
                final CaptureActivity parent = mWeakParent.get();
                if (parent == null) {
                    mProgressDialog.dismissWithFailure(AppApplication.getInstance().getString(R.string.capture_failed));
                    return;
                }
                //mSoundPool.play(mSoundId, 0.2f, 0.2f, 0, 0, 1.0f);
                final Bitmap bitmap = mWeakCameraView.get().captureStillImage();
                try {
                    String fileName = SdManager.getInstance().getTempPath() + System.currentTimeMillis() + ".png";
                    final File outputFile = new File(fileName);
                    final BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(outputFile));
                    try {
                        try {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                            os.flush();
                            mHandler.sendMessage(mHandler.obtainMessage(MSG_MEDIA_UPDATE, outputFile.getPath()));
                            mUVCHandler.sendMessage(mUVCHandler.obtainMessage(1000, outputFile.getPath()));
                        } catch (final IOException e) {
                            e.printStackTrace();
                            mProgressDialog.dismissWithFailure(AppApplication.getInstance().getString(R.string.capture_failed));
                        }
                    } finally {
                        os.close();
                    }
                } catch (final FileNotFoundException e) {
                    e.printStackTrace();
                    mProgressDialog.dismissWithFailure(AppApplication.getInstance().getString(R.string.capture_failed));
                } catch (final IOException e) {
                    e.printStackTrace();
                    mProgressDialog.dismissWithFailure(AppApplication.getInstance().getString(R.string.capture_failed));
                }
            }

            public void handleStartRecording() {
                try {
                    if ((mUVCCamera == null) || (mMuxer != null)) return;
                    mMuxer = new MediaMuxerWrapper(".mp4");
                    mVideoEncoder = new MediaVideoEncoder(mMuxer, mMediaEncoderListener);
                    mVideoEncoder.setVideoWidth(mPreviewWidth);
                    mVideoEncoder.setVideoHeight(mPreviewHeight);
                    if (true) {
                        new MediaAudioEncoder(mMuxer, mMediaEncoderListener);
                    }
                    mMuxer.prepare();
                    mMuxer.startRecording();
                    mUVCCamera.setFrameCallback(mIFrameCallback, UVCCamera.PIXEL_FORMAT_NV21);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }

            public void handleStopRecording() {
                mVideoEncoder = null;
                if (mMuxer != null) {
                    mMuxer.stopRecording();
                    mMuxer = null;
                }
                if (mUVCCamera != null)
                    mUVCCamera.setFrameCallback(null, 0);
            }

            public void handleUpdateMedia(final String path) {
                final CaptureActivity parent = mWeakParent.get();
                if (parent != null && parent.getApplicationContext() != null) {
                    try {
                        MediaScannerConnection.scanFile(parent.getApplicationContext(), new String[]{path}, null, null);
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                    if (parent.isDestroyed())
                        handleRelease();
                } else {
                    handleRelease();
                }
            }

            public void handleRelease() {
                handleClose();
                if (!mIsRecording)
                    Looper.myLooper().quit();
            }

            private final IFrameCallback mIFrameCallback = new IFrameCallback() {
                @Override
                public void onFrame(final ByteBuffer frame) {
                    if (mVideoEncoder != null) {
                        mVideoEncoder.frameAvailableSoon();
                        mVideoEncoder.encode(frame);
                    }
                }
            };

            private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
                @Override
                public void onPrepared(final MediaEncoder encoder) {
                    mIsRecording = true;
                }

                @Override
                public void onStopped(final MediaEncoder encoder) {
                    if (encoder instanceof MediaVideoEncoder)
                        try {
                            mIsRecording = false;
                            final CaptureActivity parent = mWeakParent.get();
                            final String path = encoder.getOutputPath();
                            if (!TextUtils.isEmpty(path)) {
                                mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_MEDIA_UPDATE, path), 1000);
                            } else {
                                if (parent == null || parent.isDestroyed()) {
                                    handleRelease();
                                }
                            }
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                }
            };

            @SuppressWarnings("deprecation")
            private void loadSutterSound(final Context context) {
                int streamType;
                try {
                    final Class<?> audioSystemClass = Class.forName("android.media.AudioSystem");
                    final Field sseField = audioSystemClass.getDeclaredField("STREAM_SYSTEM_ENFORCED");
                    streamType = sseField.getInt(null);
                } catch (final Exception e) {
                    streamType = AudioManager.STREAM_SYSTEM;
                }
                if (mSoundPool != null) {
                    try {
                        mSoundPool.release();
                    } catch (final Exception e) {
                    }
                    mSoundPool = null;
                }
                mSoundPool = new SoundPool(2, streamType, 0);
                mSoundId = mSoundPool.load(context, R.raw.camera_click, 1);
            }

            @Override
            public void run() {
                Looper.prepare();
                synchronized (mSync) {
                    mHandler = new CaptureActivity.CameraHandler(this);
                    mSync.notifyAll();
                }
                Looper.loop();
                synchronized (mSync) {
                    mHandler = null;
                    mSoundPool.release();
                    mSoundPool = null;
                    mSync.notifyAll();
                }
            }
        }
    }

    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            handleDeviceAttach(device);
        }

        @Override
        public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
            handleConnect(device, ctrlBlock);
        }

        @Override
        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
            handleDeviceDettach(device);
        }

        @Override
        public void onDettach(final UsbDevice device) {

        }

        @Override
        public void onCancel() {
        }
    };

    private void handleDeviceAttach(final UsbDevice device) {
        if (!checkDevice(device)) {
            return;
        }

        if (mCaptureType == CAPUTURE_TYPE_UVC) {
            dismissUVCDialog();
        } else if (mCaptureType == CAPUTURE_TYPE_DERMATOSCOPE) {
            dismissDermatoScopeDialog();
        }

        mUSBMonitor.register();
        mDeviceHelper.requestDevices();
    }

    private void handleConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
        if (!checkDevice(device)) {
            return;
        }

        if (mCaptureType == CAPUTURE_TYPE_UVC) {
            if (mCameraHandler != null) {
                mCameraHandler.openCamera(ctrlBlock);
                startPreview();
            }
        } else {
            if (mDermatoScopeCameraHandler != null) {
                mDermatoScopeCameraHandler.openCamera(ctrlBlock);
                startPreview();
            }
        }
    }

    private void handleDeviceDettach(final UsbDevice device) {
        if (!checkDevice(device)) {
            return;
        }

        AppHandlerProxy.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (mCaptureType == CAPUTURE_TYPE_UVC) {
                    showUVCDialog(false);
                } else if (mCaptureType == CAPUTURE_TYPE_DERMATOSCOPE) {
                    showDermatoScopeDialog(false);
                }
            }
        });

        if (mCaptureType == CAPUTURE_TYPE_UVC) {
            if (mCameraHandler != null) {
                mCameraHandler.closeCamera();
            }
        } else {
            if (mDermatoScopeCameraHandler != null) {
                mDermatoScopeCameraHandler.closeCamera();
            }
        }
    }

    private boolean checkDevice(final UsbDevice device) {
        String deviceName = device.getProductName();
        if (StringUtil.isEmpty(deviceName)) {
            return false;
        }
        if (mCaptureType != CAPUTURE_TYPE_UVC && mCaptureType != CAPUTURE_TYPE_DERMATOSCOPE) {
            return false;
        }
        if (mCaptureType == CAPUTURE_TYPE_UVC && !deviceName.equals(DEVICE_NAME_UVC)) {
            return false;
        } else if (mCaptureType == CAPUTURE_TYPE_DERMATOSCOPE && !deviceName.equals(DEVICE_NAME_DERMATOSCOPE)) {
            return false;
        }
        return true;
    }

    /**
     * 开始上传
     *
     * @param path 本地图片绝对路径
     */
    private void startUpload(final String path) {
        isUploading = true;
        final List<String> paths = new ArrayList<>();
        paths.add(path);
        Logger.log(Logger.CAMERA, TAG + "->startUpload()->" + (paths == null ? "paths == null" : paths.toString()));
        if (paths == null || paths.isEmpty()) {
            Logger.logI(Logger.CAMERA, "startUpload()->paths == null || paths.isEmpty()");
            return;
        }

        final MaterialTask task = new MaterialTask(mAppointmentInfo.getBaseInfo().getAppointmentId(),
                mAppointmentInfo.getBaseInfo().getUserId(), mAppointmentInfo.getBaseInfo().getDoctorUserId(), mMaterialTypeInfo.get(mDataType).getmMaterialTypeId());
        final List<SingleFileInfo> fileList = new ArrayList<>();
        AppAsyncTask<Void> asyncTask = new AppAsyncTask<Void>() {
            @Override
            protected void runOnBackground(AsyncResult<Void> asyncResult) {
                super.runOnBackground(asyncResult);
                for (String path : paths) {
                    if (TextUtils.isEmpty(path))
                        continue;

                    //重命名缓存图片，防止原图片名字含有特殊字符导致上传失败
                    String newFileName = MD5Util.md5(System.currentTimeMillis() + "" + Math.random() * 1000) + path.substring(path.lastIndexOf("."));
                    String newFilePath = SdManager.getInstance().getTempPath() + newFileName;
                    String extraName = newFilePath.substring(newFilePath.lastIndexOf(".") + 1);
                    Logger.log(Logger.CAMERA, TAG + "->startUpload()->文件后缀名:" + extraName);

                    if (extraName.matches("[pP][nN][gG]")
                            || extraName.matches("[jJ][pP][eE][gG]")
                            || extraName.matches("[jJ][pP][gG]")) {//需要将png或者jpeg格式的图片保存为jpg格式,不然服务器无法生成缩略图
                        newFilePath = newFilePath.replace(extraName, "jpg");
                        BitmapUtil.savePNG2JPEG(path, newFilePath);
                        FileUtil.deleteFile(path);
                    } else {
                        newFilePath = path;
                    }
                    BitmapUtil.compressImageFile(newFilePath);
                    fileList.add(new SingleFileInfo(task.getTaskId(), newFilePath));
                }
            }

            @Override
            protected void runOnUIThread(AsyncResult<Void> asyncResult) {
                super.runOnUIThread(asyncResult);
                if (fileList.isEmpty())
                    return;

                task.setRecurNum(0);
                task.setFileList(fileList);
                task.setDocUserId(mAppointmentInfo.getBaseInfo().getDoctorUserId());

                mUploadTaskManager.startTask(task);
            }
        };
        asyncTask.execute();
    }

    /**
     * 上传文件状态监听
     */
    private SimpleNotifyListener mNotifyListener = new SimpleNotifyListener() {
        @Override
        public void onFileUploadStart(AbsTask task, final SingleFileInfo fileInfo) {
            mProgressBar.setProgress(0);
        }

        @Override
        public void onFileUploadProgressChange(AbsTask task, SingleFileInfo fileInfo) {
            if (fileInfo.getProgress() < 100) {
                mProgressBar.setProgress(fileInfo.getProgress());
            }
        }

        @Override
        public void onFileUploadFailed(AbsTask task, SingleFileInfo fileInfo, Throwable e) {
            Logger.log(Logger.CAMERA, TAG + "->onFileUploadFailed()->task:" + task + "->fileInfo:" + fileInfo);
            FileUtil.deleteFile(fileInfo.getLocalFilePath());
            uploadTaskFinished();
            showToast("上传失败");
        }

        @Override
        public void onFileUploadSuccess(AbsTask task, SingleFileInfo fileInfo) {
            Logger.log(Logger.CAMERA, TAG + "->onFileUploadSuccess()->task:" + task + "->fileInfo:" + fileInfo);
            FileUtil.deleteFile(fileInfo.getLocalFilePath());
            uploadTaskFinished();
            mUploadSuccessNum++;
            showToast("上传成功");
        }

        @Override
        public void onTaskFailed(AbsTask task, Throwable e) {
            Logger.log(Logger.CAMERA, TAG + "->onTaskFailed()->task:" + task);
        }

        @Override
        public void onTaskSuccessful(AbsTask task, Object response) {
            Logger.log(Logger.CAMERA, TAG + "->onTaskSuccessful()->task:" + task);
        }
    };
}
