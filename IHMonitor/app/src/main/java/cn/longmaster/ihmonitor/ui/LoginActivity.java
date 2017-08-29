package cn.longmaster.ihmonitor.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import cn.longmaster.doctorlibrary.viewinject.FindViewById;
import cn.longmaster.doctorlibrary.viewinject.OnClick;
import cn.longmaster.doctorlibrary.viewinject.ViewInjecter;
import cn.longmaster.ihmonitor.R;
import cn.longmaster.ihmonitor.core.app.AppApplication;
import cn.longmaster.ihmonitor.core.app.AppConstant;
import cn.longmaster.ihmonitor.core.http.OnResultListener;
import cn.longmaster.ihmonitor.core.manager.user.LoginStateChangeListener;
import cn.longmaster.ihmonitor.core.manager.user.UserInfoManager;
import cn.longmaster.ihmonitor.core.receiver.NetStateReceiver;
import cn.longmaster.ihmonitor.view.LoadingButton;

public class LoginActivity extends BaseActivity implements TextView.OnEditorActionListener {
    @FindViewById(R.id.activity_login_username_et)
    private EditText mUsernameEt;
    @FindViewById(R.id.activity_login_password_et)
    private EditText mPasswordEt;
    @FindViewById(R.id.activity_login_confirm_btn)
    private LoadingButton mConfirmBtn;

    @AppApplication.Manager
    private UserInfoManager mUserInfoManager;

    private String mPhoneNum;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewInjecter.inject(this);
        initListener();
    }

    private void initListener() {
        mPasswordEt.setOnEditorActionListener(this);
    }

    @OnClick({R.id.activity_login_confirm_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_login_confirm_btn:
                if (mConfirmBtn.isLoadingShowing()) {
                    return;
                }
                if (checkInput()) {
                    if (!NetStateReceiver.hasNetConnected(this)) {
                        showToast(R.string.no_network_connection);
                        return;
                    }
                    hideSoftInput(mPasswordEt);
                    activeAccount();
                }
                break;

        }
    }

    private boolean checkInput() {
        mPhoneNum = mUsernameEt.getText().toString().trim();
        mPassword = mPasswordEt.getText().toString().trim();
        if (TextUtils.isEmpty(mPhoneNum)) {
            showToast(R.string.login_username_error);
            return false;
        }
        if (TextUtils.isEmpty(mPassword)) {
            showToast(R.string.login_password_error);
            return false;
        }
        return true;
    }

    /**
     * 激活账号
     */
    private void activeAccount() {
        mConfirmBtn.showLoading();
        mUserInfoManager.activeAccount(mPhoneNum, AppConstant.UserAccountType.ACCOUNT_PHONE_NUMBER, mPassword, new LoginStateChangeListener() {
            @Override
            public void onLoginStateChanged(int code, int msg) {
                if (code == OnResultListener.RESULT_SUCCESS) {
                    //startActivity(AppointmentListActivity.class);
                    startActivity(MainActivity.class);
                    finish();
                } else {
                    showToast(R.string.login_error);
                    mConfirmBtn.showButtonText();
                }
            }
        });
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            if (mConfirmBtn.isLoadingShowing()) {
                return false;
            }
            if (checkInput()) {
                if (!NetStateReceiver.hasNetConnected(this)) {
                    showToast(R.string.no_network_connection);
                    return false;
                }
                hideSoftInput(mPasswordEt);
                activeAccount();
            }
        }
        return false;
    }
}
