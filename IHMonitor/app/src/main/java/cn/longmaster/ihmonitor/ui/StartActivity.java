package cn.longmaster.ihmonitor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.concurrent.TimeUnit;

import cn.longmaster.doctorlibrary.util.log.Logger;
import cn.longmaster.doctorlibrary.viewinject.ViewInjecter;
import cn.longmaster.ihmonitor.R;
import cn.longmaster.ihmonitor.core.app.AppApplication;
import cn.longmaster.ihmonitor.core.entity.UserInfo;
import cn.longmaster.ihmonitor.core.manager.user.UserInfoManager;

/**
 * 启动页
 * Create
 */

public class StartActivity extends BaseActivity {
    @AppApplication.Manager
    private UserInfoManager mUserInfoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 判断程序的启动方式：打开一个新的任务、还是将后台的应用给提到前台来
         * 解决程序第一次安装，通过“打开”按钮启动，home键后点击桌面图标重新启动程序问题。
         */
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.activity_start);
        ViewInjecter.inject(this);
        setEnableShowKickoff(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                start();
            }
        }, 3000);
    }

    /**
     * 判断程序入口
     */
    private void judgeEnter() {
        UserInfo userInfo = mUserInfoManager.getCurrentUserInfo();
        if (userInfo.getUserId() == 0) {
            startActivity(LoginActivity.class);
        } else {
            //startActivity(AppointmentListActivity.class);
            startActivity(MainActivity.class);
        }
        finish();
    }

    private void start() {
        int count = 20;//最多等待10秒钟，直到AppStarted
        while (count > 0 && !AppApplication.getInstance().isAppStarted()) {
            long curTimes = System.currentTimeMillis();
            count--;
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long duration = System.currentTimeMillis() - curTimes;
            Logger.log(Logger.COMMON, "duration:" + duration);
        }
        if (AppApplication.getInstance().isAppStarted()) {//APP启动成功
            Logger.log(Logger.COMMON, "App is started!");
            judgeEnter();
        }
        finish();
    }
}
