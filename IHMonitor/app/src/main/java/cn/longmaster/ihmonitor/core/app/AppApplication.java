package cn.longmaster.ihmonitor.core.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
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
import java.util.concurrent.CountDownLatch;

import cn.longmaster.doctorlibrary.util.UtilStatus;
import cn.longmaster.ihmonitor.core.http.config.BaseConfigManager;
import cn.longmaster.ihmonitor.core.manager.BaseManager;
import cn.longmaster.ihmonitor.core.manager.LocationManager;
import cn.longmaster.ihmonitor.core.manager.appointment.AppointmentManager;
import cn.longmaster.ihmonitor.core.manager.dcp.DcpManager;
import cn.longmaster.ihmonitor.core.manager.message.MessageManager;
import cn.longmaster.ihmonitor.core.manager.room.AudioAdapterManager;
import cn.longmaster.ihmonitor.core.manager.room.ConsultRoomManager;
import cn.longmaster.ihmonitor.core.manager.storage.DBManager;
import cn.longmaster.ihmonitor.core.manager.storage.SdManager;
import cn.longmaster.ihmonitor.core.manager.user.AuthenticationManager;
import cn.longmaster.ihmonitor.core.manager.user.DoctorManager;
import cn.longmaster.ihmonitor.core.manager.user.UserInfoManager;
import cn.longmaster.ihmonitor.ui.BaseActivity;


public class AppApplication extends Application {
    public static final Handler HANDLER = new Handler();
    private static AppApplication mApplication;
    private HashMap<String, BaseManager> mManagers = new HashMap<>();
    private CountDownLatch mCountDownLatch;
    private boolean mIsAppStarted;
    private boolean mIsSetGKDomain;
    private int mOnlineState;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        CrashReport.initCrashReport(getApplicationContext(), "395393779f", AppConfig.IS_DEBUG_MODE);
        String pidName = getUIPName();
        if (pidName.equals(getPackageName())) {
            initManager();
            mCountDownLatch = new CountDownLatch(1);
            new Thread(mInitRunable).start();
            UtilStatus.init(this, AppConfig.IS_DEBUG_MODE, AppConfig.CLIENT_VERSION);
        }
    }

    public static AppApplication getInstance() {
        return mApplication;
    }

    public boolean isAppStarted() {
        return mIsAppStarted;
    }

    public void setAppStarted(boolean isAppStarted) {
        mIsAppStarted = isAppStarted;
    }

    public boolean isSetGKDomain() {
        return mIsSetGKDomain;
    }

    public void setIsSetGKDomain(boolean isSetGKDomain) {
        mIsSetGKDomain = isSetGKDomain;
    }

    public int getOnlineState() {
        return mOnlineState;
    }

    public void setOnlineState(int onlineState) {
        this.mOnlineState = onlineState;
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

    private final Runnable mInitRunable = new Runnable() {
        @Override
        public void run() {
            initMainService();
            mCountDownLatch.countDown();
        }
    };

    private void initMainService() {
        AppService.initApplication(this);
        startService(new Intent(this, AppService.class));
    }

    public void waitToInit() {
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    protected void registerManager(List<BaseManager> managers) {
        managers.add(new DcpManager());
        managers.add(new DBManager());
        managers.add(new SdManager());
        managers.add(new UserInfoManager());
        managers.add(new AuthenticationManager());
        managers.add(new AppointmentManager());
        managers.add(new BaseConfigManager());
        managers.add(new DoctorManager());
        managers.add(new ConsultRoomManager());
        managers.add(new AudioAdapterManager());
        managers.add(new LocationManager());
        managers.add(new MessageManager());

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface Manager {
    }
}
