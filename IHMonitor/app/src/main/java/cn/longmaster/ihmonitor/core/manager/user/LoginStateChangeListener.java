package cn.longmaster.ihmonitor.core.manager.user;

/**
 * 登录状态变化回调
 */
public interface LoginStateChangeListener {
    void onLoginStateChanged(int code, int msg);
}
