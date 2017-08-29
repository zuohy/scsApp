package cn.longmaster.ihmonitor.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import cn.longmaster.ihmonitor.core.app.AppApplication;
import cn.longmaster.ihmonitor.core.manager.BaseManager;


public class BaseFragment extends Fragment {
    private long mBtnClickedTime;
    private static final long BTN_CLICK_MIN_INTERVAL = 1000;

    public static int dipToPx(Context context, float dip) {
        return (int) (context.getResources().getDisplayMetrics().density * dip + 0.5f);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppApplication.getInstance().injectManager(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return container;
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    /**
     * 获得管理器
     *
     * @param manager 管理器类型
     * @param <M>     管理器Class
     * @return 管理器
     */
    public <M extends BaseManager> M getManager(Class<M> manager) {
        return AppApplication.getInstance().getManager(manager);
    }

    public void showToast(int resId) {
        if (getActivity() == null) {
            return;
        }
        Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
    }

    public void showToast(String msg) {
        if (getActivity() == null || msg.length() == 0) {
            return;
        }
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void onBackClick(View view) {
        getActivity().finish();
    }

    /**
     * DIP 转 PX
     *
     * @param dip dip单位
     * @return 转换后的像素
     */
    public int dipToPx(float dip) {
        return (int) (getResources().getDisplayMetrics().density * dip + 0.5f);
    }

    /**
     * 显示输入法
     *
     * @param editText
     */
    public void showSoftInput(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    public void startActivity(Class<? extends Activity> cls) {
        Intent intent = new Intent(getActivity(), cls);
        super.startActivity(intent);
    }

    /**
     * 创建等待对话框
     *
     * @param msg        要显示的消息
     * @param cancelable 是否可以取消
     * @return 返回对话框
     */
    public ProgressDialog createProgressDialog(String msg, boolean cancelable) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(false);
        progressDialog.setMessage(msg);
        progressDialog.show();
        progressDialog.setCancelable(cancelable);
        return progressDialog;
    }

    /**
     * 创建等待对话框
     *
     * @param msg 要显示的消息
     * @return 返回对话框
     */
    public ProgressDialog createProgressDialog(String msg) {
        return createProgressDialog(msg, false);
    }

    /**
     * 判断是否连续快速点击
     *
     * @return
     */
    public boolean isFastClick() {
        if (System.currentTimeMillis() - mBtnClickedTime < BTN_CLICK_MIN_INTERVAL) {
            mBtnClickedTime = System.currentTimeMillis();
            return true;
        }
        mBtnClickedTime = System.currentTimeMillis();
        return false;
    }
}
