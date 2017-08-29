package cn.longmaster.ihuvc.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.HashSet;
import java.util.Set;

import cn.longmaster.doctorlibrary.utils.AppUtil;
import cn.longmaster.ihuvc.core.AppApplication;
import cn.longmaster.ihuvc.R;
import cn.longmaster.ihuvc.core.manager.BaseManager;

/**
 * 基本的Activity，程序所有Activity都必须继承本类。
 * Created by yangyong on 2017-04-21.
 */
public class BaseActivity extends FragmentActivity {
    protected SystemBarTintManager tintManager;
    private boolean mForeGround;
    private boolean mEnableShowKickoff = true;
    private boolean mIsDestroy = false;
    private boolean mIsFinish = false;
    /**
     * 当前界面中的EditText集合，当触摸屏幕的非EditTextView区域时，隐藏软键盘
     */
    private Set<EditText> mEditTextViewSet;
    private long mBtnClickedTime;
    private static final long BTN_CLICK_MIN_INTERVAL = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsDestroy = false;
        mIsFinish = false;
        AppApplication.getInstance().injectManager(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mEditTextViewSet = new HashSet<EditText>();
        addEditTextView(getWindow().getDecorView());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mForeGround = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mForeGround = false;
    }

    @Override
    public void finish() {
        super.finish();
        mIsFinish = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
    }

    public final Drawable getResDrawable(int id) {
        return getResources().getDrawable(id);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    public static int dipToPx(Context context, float dip) {
        return (int) (context.getResources().getDisplayMetrics().density * dip + 0.5f);
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected int getResColor(@ColorRes int res) {
        return getResources().getColor(res);
    }

    public SystemBarTintManager getTintManager() {
        return tintManager;
    }

    protected void initSystemBar() {
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setTintColor(getResources().getColor(R.color.color_272727));
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initSystemBar();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initSystemBar();
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
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    public BaseActivity getActivity() {
        return this;
    }

    public void onBackClick(View view) {
        finish();
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
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    public void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // 请使用 FindViewById
    @Deprecated
    @Override
    public View findViewById(int id) {
        return super.findViewById(id);
    }

    public void startActivity(Class<? extends Activity> cls) {
        Intent intent = new Intent(this, cls);
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
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(false);
        progressDialog.setMessage(msg);
        progressDialog.show();
        progressDialog.setCancelable(cancelable);
        return progressDialog;
    }

    /**
     * 创建等待对话框，不可取消
     *
     * @param msg 要显示的消息
     * @return 返回对话框
     */
    public ProgressDialog createProgressDialog(String msg) {
        return createProgressDialog(msg, false);
    }

    public boolean isForeGround() {
        return mForeGround;
    }

    /**
     * 判断该Activity是否被销毁，因为onDestroyed()回调方法不一定能及时执行，所以建议使用{@link #isActivityFinished()}方法
     */
    public boolean isActivityDestroyed() {
        return mIsDestroy;
    }

    /**
     * 判断该Activity是否关闭
     */
    public boolean isActivityFinished() {
        return mIsFinish;
    }

    public void setEnableShowKickoff(boolean isEnable) {
        mEnableShowKickoff = isEnable;
    }

    /**
     * 将界面中所有的EditText放到集合中
     *
     * @param v view
     */
    private void addEditTextView(View v) {
        if (v instanceof EditText) {
            mEditTextViewSet.add((EditText) v);
        } else if (v instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) v;
            for (int i = 0; i < group.getChildCount(); i++) {
                addEditTextView(group.getChildAt(i));
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {//触摸屏幕位置不在EditText View内，隐藏软键盘
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            //long start = System.currentTimeMillis();
            boolean isContains = false;
            for (EditText v : mEditTextViewSet) {
                final Rect rect = new Rect();
                final int[] location = new int[2];

                v.getLocationOnScreen(location);
                rect.set(location[0], location[1], location[0] + v.getWidth(), location[1] + v.getHeight());

                final int x = (int) ev.getX();
                final int y = (int) ev.getY();

                if (rect.contains(x, y)) {
                    isContains = true;
                    break;
                }
            }
            if (!isContains) {
                AppUtil.hideSoftPad(this);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判断是否连续快速点击
     *
     * @return
     */
    public boolean isFastClick() {
        if (System.currentTimeMillis() - mBtnClickedTime < BTN_CLICK_MIN_INTERVAL) {
            showToast(R.string.press_again_back_home);
            mBtnClickedTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }
}
