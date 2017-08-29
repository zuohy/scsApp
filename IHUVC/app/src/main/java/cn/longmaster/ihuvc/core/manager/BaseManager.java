package cn.longmaster.ihuvc.core.manager;

import android.os.Handler;
import android.os.Message;

import cn.longmaster.ihuvc.core.AppApplication;

/**
 * 管理器基类，所有管理器都必须继承自本类
 * Created by yangyong on 2017-04-21.
 */
public abstract class BaseManager {
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            onHandlerMessage(msg);
            return true;
        }
    });

    public Handler getHandler() {
        return handler;
    }

    protected void onHandlerMessage(Message msg) {
    }

    /**
     * 管理器被初始化的回调，初始化整个管理器
     *
     * @param application
     */
    public abstract void onManagerCreate(AppApplication application);

    /**
     * 所有管理器都初始化后执行
     */
    public void onAllManagerCreated() {

    }

    /**
     * 获得应用程序实例
     *
     * @return 应用实例
     */
    public AppApplication getApplication() {
        return AppApplication.getInstance();
    }

    /**
     * 获得管理器
     *
     * @param manager 管理器类型
     * @param <M>     管理器Class
     * @return 管理器
     */
    public <M extends BaseManager> M getManager(Class<M> manager) {
        return getApplication().getManager(manager);
    }
}
