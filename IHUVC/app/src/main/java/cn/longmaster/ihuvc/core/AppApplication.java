package cn.longmaster.ihuvc.core;


import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.View;

import com.tencent.bugly.crashreport.CrashReport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.longmaster.doctorlibrary.utils.UtilStatus;
import cn.longmaster.ihuvc.core.manager.BaseManager;
import cn.longmaster.ihuvc.core.manager.SdManager;
import cn.longmaster.ihuvc.core.upload.UploadTaskManager;
import cn.longmaster.ihuvc.ui.BaseActivity;

public class AppApplication extends Application {
    public static final Handler HANDLER = new Handler();

    private static AppApplication mAppApplication;
    private HashMap<String, BaseManager> mManagers = new HashMap<>();
    /**
     * 应用是否启动
     */
    private boolean mIsAppStarted;

	@Override
	public void onCreate() {
		super.onCreate();
		CrashReport.initCrashReport(getApplicationContext(), "d5f1ac3f20", false);
        mAppApplication = this;
        AppConfig.setUrl();

        UtilStatus.init(this, AppConfig.IS_DEBUG_MODE, AppConfig.CLIENT_VERSION);
        if (getUIPName().equals(getPackageName())) {
            SdManager.getInstance().init();
            initManager();
        }
	}

	public static AppApplication getInstance() {
        return mAppApplication;
    }

    @NonNull
    private String getUIPName() {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }

    /**
     * 应用是否已经启动
     *
     * @return true 启动完成， false 启动未完成
     */
    public boolean isAppStarted() {
        return mIsAppStarted;
    }

    /**
     * 设置应用是否启动完成
     *
     * @param isAppStarted true 启动完成， false 启动未完成
     */
    public void setAppStarted(boolean isAppStarted) {
        mIsAppStarted = isAppStarted;
    }

    private void initManager() {
        List<BaseManager> managerList = new ArrayList<>();
        registerManager(managerList);
        for (BaseManager baseManager : managerList) {
            injectManager(baseManager);
            baseManager.onManagerCreate(this);
            mManagers.put(baseManager.getClass().getName(), baseManager);
        }

        for (Map.Entry<String, BaseManager> entry : mManagers.entrySet()) {
            entry.getValue().onAllManagerCreated();
        }
    }

    @UiThread
    public <V extends BaseManager> V getManager(Class<V> cls) {
        return (V) mManagers.get(cls.getName());
    }

    public void injectManager(Object object) {
        Class<?> aClass = object.getClass();

        while (aClass != Object.class && aClass != View.class && aClass != BaseManager.class && aClass != BaseActivity.class) {
            Field[] declaredFields = aClass.getDeclaredFields();
            if (declaredFields != null && declaredFields.length > 0) {
                for (Field field : declaredFields) {
                    int modifiers = field.getModifiers();
                    if (Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers)) {
                        // 忽略掉static 和 final 修饰的变量
                        continue;
                    }

                    if (!field.isAnnotationPresent(Manager.class)) {
                        continue;
                    }

                    Class<?> type = field.getType();
                    if (!BaseManager.class.isAssignableFrom(type)) {
                        throw new RuntimeException("@Manager 注解只能应用到BaseManager的子类");
                    }

                    BaseManager baseManager = getManager((Class<? extends BaseManager>) type);

                    if (baseManager == null) {
                        throw new RuntimeException(type.getSimpleName() + " 管理类还未初始化！");
                    }

                    if (!field.isAccessible())
                        field.setAccessible(true);

                    try {
                        field.set(object, baseManager);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            aClass = aClass.getSuperclass();
        }
    }

    protected void registerManager(List<BaseManager> lists) {
        lists.add(new UploadTaskManager());
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface Manager {
    }
}
