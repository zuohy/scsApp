package cn.longmaster.doctorlibrary.util.thread;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * 本地IO线程，专门用于读取本地IO
 */
public class LocalIOProcess {
    private static LocalIOProcess mLocalIoProcess;
    private Executor mExecutor;

    public static LocalIOProcess getInstance() {
        if (mLocalIoProcess == null) {
            synchronized (LocalIOProcess.class) {
                if (mLocalIoProcess == null) {
                    mLocalIoProcess = new LocalIOProcess();
                }
            }
        }
        return mLocalIoProcess;
    }

    private LocalIOProcess() {
        mExecutor = new ThreadPool(30, TimeUnit.SECONDS, Thread.NORM_PRIORITY - 1);
    }

    public void execute(Runnable a_doInBackground) {
        mExecutor.execute(a_doInBackground);
    }
}
