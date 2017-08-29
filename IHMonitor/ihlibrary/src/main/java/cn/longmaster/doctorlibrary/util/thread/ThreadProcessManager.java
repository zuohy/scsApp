package cn.longmaster.doctorlibrary.util.thread;

/**
 * 大医生线程管理类
 */
public class ThreadProcessManager {
    private static ThreadProcess mThreadProcess = new ThreadProcess("doctor");

    public static void submit(Runnable runnable) {
        mThreadProcess.submit(runnable);
    }
}
