package cn.longmaster.ihmonitor.core.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import cn.longmaster.doctorlibrary.util.log.Logger;
import cn.longmaster.doctorlibrary.util.thread.ThreadProcess;
import cn.longmaster.ihmonitor.core.manager.dcp.AppAlarmManager;
import cn.longmaster.ihmonitor.core.manager.dcp.DcpManager;
import cn.longmaster.ihmonitor.core.manager.user.UserInfoManager;

/**
 * 应用主服务
 */
public class AppService extends Service {
    private static final String TAG = AppService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(startInitRunnable).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Runnable startInitRunnable = new Runnable() {
        @Override
        public void run() {
            Logger.logW(Logger.COMMON, "开始启动服务。");

            // 等待程序初始化完全后开始
            ((AppApplication) getApplicationContext()).waitToInit();

            // 每次启动程序设置GK
            AppApplication.getInstance().getManager(DcpManager.class).openClient();
            AppApplication.getInstance().getManager(DcpManager.class).setGKDomain();
            AppApplication.getInstance().getManager(UserInfoManager.class).loadUserInfo(new UserInfoManager.LoadUserInfoFished() {
                @Override
                public void onLoadUserInfoFished(long pesIp, int pesPort) {
                    AppApplication.getInstance().getManager(DcpManager.class).setPesInfo(pesIp, pesPort);
                }
            });
            AppApplication.getInstance().setAppStarted(true);
            AppAlarmManager.getInstance().register();
            Logger.logW(Logger.COMMON, "服务启动完成");
        }
    };

    /**
     * 初始化应用程序
     *
     * @param context 上下文
     * @return 初始化结果，true 初始化成功， false 初始化失败
     */
    protected static boolean initApplication(Context context) {
        AppConfig.setUrl();
        return true;
    }

    private static ThreadProcess threadProcess = new ThreadProcess("AppService");

    public static void submit(Runnable runnable) {
        threadProcess.submit(runnable);
    }
}
