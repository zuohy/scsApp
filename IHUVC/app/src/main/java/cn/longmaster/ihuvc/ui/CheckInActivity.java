package cn.longmaster.ihuvc.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.longmaster.doctorlibrary.utils.common.MD5Util;
import cn.longmaster.doctorlibrary.utils.common.StringUtil;
import cn.longmaster.doctorlibrary.utils.log.Logger;
import cn.longmaster.doctorlibrary.viewinject.FindViewById;
import cn.longmaster.doctorlibrary.viewinject.OnClick;
import cn.longmaster.doctorlibrary.viewinject.ViewInjecter;
import cn.longmaster.ihuvc.R;
import cn.longmaster.ihuvc.core.AppConfig;
import cn.longmaster.ihuvc.core.AppPreference;
import cn.longmaster.ihuvc.core.entity.AppointmentInfo;
import cn.longmaster.ihuvc.core.entity.CheckVersionInfo;
import cn.longmaster.ihuvc.core.entity.PatientInfo;
import cn.longmaster.ihuvc.core.http.BaseResult;
import cn.longmaster.ihuvc.core.http.OnResultListener;
import cn.longmaster.ihuvc.core.http.requesters.AppointmentByIdRequester;
import cn.longmaster.ihuvc.core.http.requesters.CheckAppointnentPwdRequester;
import cn.longmaster.ihuvc.core.http.requesters.CheckVersionRequester;
import cn.longmaster.ihuvc.core.http.requesters.PatientInfoRequester;
import cn.longmaster.ihuvc.core.manager.SdManager;
import cn.longmaster.ihuvc.core.manager.VersionChangeListener;
import cn.longmaster.ihuvc.core.manager.VersionManager;
import cn.longmaster.ihuvc.view.AppActionBar;
import cn.longmaster.ihuvc.view.dialog.CommonDialog;
import cn.longmaster.ihuvc.view.dialog.CustomProgressDialog;

public class CheckInActivity extends BaseActivity implements TextView.OnEditorActionListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;

    @FindViewById(R.id.activity_check_in_bar)
    private AppActionBar mAppActionBar;
    @FindViewById(R.id.activity_check_in_serial_number_et)
    private EditText mIdEt;
    @FindViewById(R.id.activity_check_in_serial_number_error_tv)
    private TextView mIdErrorTv;
    @FindViewById(R.id.activity_check_in_password_et)
    private EditText mPwdEt;
    @FindViewById(R.id.activity_check_in_password_error_tv)
    private TextView mPwdErrorTv;
    @FindViewById(R.id.activity_check_in_confirm)
    private Button mConfirm;
    @FindViewById(R.id.action_check_version_name)
    private TextView mVersionName;

    private CustomProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        ViewInjecter.inject(this);
        iniView();
        initListener();
        checkPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SdManager.getInstance().init();
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    showToast(R.string.sd_permission_not_granted);
                }
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            checkAppontmentIdAndPwd();
            return true;
        }
        return false;
    }

    private void iniView() {
        mVersionName.setText(getString(R.string.version_number, VersionManager.getInstance().getFullVersionName("", AppConfig.CLIENT_VERSION)));
    }

    private void initListener() {
        mPwdEt.setOnEditorActionListener(this);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
            }
        }
    }

    public void onRightClick(View view) {
        CheckVersionRequester requester = new CheckVersionRequester(new OnResultListener<CheckVersionInfo>() {
            @Override
            public void onResult(BaseResult baseResult, CheckVersionInfo checkVersionInfo) {
                Logger.log(Logger.COMMON, "baseResult:" + baseResult + "---checkVersionInfo:" + checkVersionInfo);
                if (baseResult.getCode() == RESULT_SUCCESS && checkVersionInfo != null) {
                    VersionManager.getInstance().setClientVersionLimit(checkVersionInfo.getClienVersionLimit());
                    VersionManager.getInstance().setClientVersionLatest(checkVersionInfo.getGetClienVersionLatest());
                    showCheckVersion(checkVersionInfo);
                } else {
                    showNoNewVersionDialog();
                }
            }
        });
        requester.doPost();
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

    private void showCheckVersion(CheckVersionInfo checkVersionInfo) {
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
            VersionManager.getInstance().upgrade(CheckInActivity.this);
        }

        @Override
        public void onVersionLimited() {
            VersionManager.getInstance().upgrade(CheckInActivity.this);
        }
    };

    /**
     * 检查更新
     *
     * @param
     */
    private void checkVersion() {
        if (VersionManager.getInstance().getCurentClientVersion() < AppPreference.getIntValue(AppPreference.KEY_SERVER_LIMIT_VERSION, 0)) {
            versionChangeListener.onVersionLimited();
        } else if (VersionManager.getInstance().getCurentClientVersion() < AppPreference.getIntValue(AppPreference.KEY_SERVER_LASTEST_VERSION, 0)) {
            versionChangeListener.onNewVersion();
        }
    }

    @OnClick({R.id.activity_check_in_confirm})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_check_in_confirm:
                checkAppontmentIdAndPwd();
                break;
        }
    }

    private void checkAppontmentIdAndPwd() {
        mIdErrorTv.setVisibility(View.GONE);
        mPwdErrorTv.setVisibility(View.GONE);

        final String idStr = mIdEt.getText().toString().trim();
        if (StringUtil.isEmpty(idStr)) {
            mIdErrorTv.setVisibility(View.VISIBLE);
            return;
        }
        String pwdStr = mPwdEt.getText().toString().trim();
        if (StringUtil.isEmpty(pwdStr)) {
            mPwdErrorTv.setVisibility(View.VISIBLE);
            return;
        }

        mProgressDialog = new CustomProgressDialog(this, getString(R.string.custom_progress_dialog_loading));
        mProgressDialog.setIsConsumeKeyBack(false);
        mProgressDialog.show();

        CheckAppointnentPwdRequester requester = new CheckAppointnentPwdRequester(new OnResultListener<Void>() {
            @Override
            public void onResult(BaseResult baseResult, Void aVoid) {
                if (baseResult.getCode() == RESULT_SUCCESS) {
                    getAppointment(Integer.valueOf(idStr));
                } else if (baseResult.getCode() == RESULT_APPOINTMENT_UNEXIST) {
                    mProgressDialog.dismiss();
                    mIdErrorTv.setVisibility(View.VISIBLE);
                } else if (baseResult.getCode() == RESULT_RELATION_PWD_ERROR) {
                    mProgressDialog.dismiss();
                    mPwdErrorTv.setVisibility(View.VISIBLE);
                } else {
                    mProgressDialog.dismissWithFailure();
                }
            }
        });
        requester.appointmentId = Integer.valueOf(idStr);
        requester.relationPwd = MD5Util.md5(pwdStr);
        requester.doPost();
    }

    private void getAppointment(int appointId) {
        AppointmentByIdRequester requester = new AppointmentByIdRequester(new OnResultListener<AppointmentInfo>() {
            @Override
            public void onResult(BaseResult baseResult, AppointmentInfo appointmentInfo) {
                if (baseResult.getCode() == RESULT_SUCCESS) {
                    getPatientInfo(appointmentInfo);
                } else {
                    mProgressDialog.dismissWithFailure();
                }
            }
        });
        requester.appointmentId = appointId;
        requester.doPost();
    }

    private void getPatientInfo(final AppointmentInfo appointmentInfo) {
        PatientInfoRequester requester = new PatientInfoRequester(new OnResultListener<PatientInfo>() {
            @Override
            public void onResult(BaseResult baseResult, PatientInfo patientInfo) {
                if (baseResult.getCode() == RESULT_SUCCESS) {
                    mProgressDialog.dismiss();
                    MaterialTypeActivity.startMaterialTypeActivity(CheckInActivity.this, appointmentInfo, patientInfo);
                } else {
                    mProgressDialog.dismissWithFailure();
                }
            }
        });
        requester.appointmentId = appointmentInfo.getBaseInfo().getAppointmentId();
        requester.token = "0";
        requester.doPost();
    }

}
