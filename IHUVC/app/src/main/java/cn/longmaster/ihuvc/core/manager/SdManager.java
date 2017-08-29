package cn.longmaster.ihuvc.core.manager;

import android.os.Environment;

import java.io.File;

/**
 * SD卡文件管理类
 * Created by yangyong on 2015/7/13.
 */
public class SdManager {
    private final String TAG = SdManager.class.getSimpleName();

    private static SdManager mSdManager;
    private final String DIR_ROOT = "/ihuvc/";
    private final String DIR_TEMP = "temp/";
    private final String DIR_LOG = "log/";

    private String mRootPath;
    private String mTempPath, mLogPath;

    public final static SdManager getInstance() {
        if (mSdManager == null) {
            synchronized (SdManager.class) {
                if (mSdManager == null)
                    mSdManager = new SdManager();
            }
        }
        return mSdManager;
    }

    private SdManager() {
        mRootPath = getDirectoryPath() + DIR_ROOT;
        mTempPath = mRootPath + DIR_TEMP;
        mLogPath = mRootPath + DIR_LOG;
        mkdir(mTempPath, mLogPath);
    }

    public void init() {
        hintMediaFile(mTempPath, mLogPath);
    }

    private void hintMediaFile(String... filePaths) {
        for (String filePath : filePaths) {
            try {
                if (filePath == null)
                    break;

                if (!filePath.endsWith(File.separator))
                    filePath = filePath + File.separatorChar;

                File file = new File(filePath + ".nomedia");

                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final String getTempPath() {
        return mTempPath;
    }

    public final String getLogPath() {
        return mLogPath;
    }
    // *****************************神奇分割线*****************************

    /**
     * 获得系统sdcard路径
     *
     * @return
     */
    public final static String getDirectoryPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 检测sdcard是否可用
     *
     * @return
     */
    public final static boolean available() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 创建文件目录
     *
     * @param filePaths
     * @return
     */
    public final synchronized boolean mkdir(String... filePaths) {
        try {
            for (String filePath : filePaths) {
                if (filePath == null) {
                    return false;
                }
                if (!filePath.endsWith(File.separator))
                    filePath = filePath.substring(0, filePath.lastIndexOf(File.separatorChar));
                File file = new File(filePath);
                if (!file.exists()) {
                    file.mkdirs();
                } else if (file.isFile()) {
                    file.delete();
                    file.mkdirs();
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized boolean mkdirs(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isDirectory()) {
            return true;
        } else {
            if (file.exists()) {
                file.delete();
            }
            file.mkdirs();
            return true;
        }
    }

    private void cleanFilePath(String filePath) {
        File f = new File(filePath);
        if (f.exists() && f.isDirectory() && f.listFiles().length > 0) {
            File delFile[] = f.listFiles();
            int i = delFile.length;
            for (int j = 0; j < i; j++) {
                if (delFile[j].isDirectory()) {
                    cleanFilePath(delFile[j].getAbsolutePath());
                }
                delFile[j].delete();
            }
        }
    }
}
