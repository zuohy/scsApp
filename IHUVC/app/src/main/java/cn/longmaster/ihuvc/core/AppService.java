package cn.longmaster.ihuvc.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import cn.longmaster.ihuvc.core.utils.thread.ThreadProcess;

/**
 * 应用主服务
 * Created by yangyong on 2015/7/2.
 */
public class AppService extends Service {
    private static final String TAG = AppService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        AppApplication.getInstance().setAppStarted(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static ThreadProcess threadProcess = new ThreadProcess("AppService");

    public static void submit(Runnable runnable) {
        threadProcess.submit(runnable);
    }
}
