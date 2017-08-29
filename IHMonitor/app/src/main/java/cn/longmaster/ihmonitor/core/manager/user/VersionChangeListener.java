package cn.longmaster.ihmonitor.core.manager.user;

/**
 * 版本变化回调
 */
public interface VersionChangeListener {
    /**
     * 新版本
     */
    void onNewVersion();

    /**
     * 当前版本受限
     */
    void onVersionLimited();
}
