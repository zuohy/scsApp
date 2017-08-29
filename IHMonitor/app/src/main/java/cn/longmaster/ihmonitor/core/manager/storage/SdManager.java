package cn.longmaster.ihmonitor.core.manager.storage;

import android.os.Environment;

import java.io.File;

import cn.longmaster.doctorlibrary.util.common.MD5Util;
import cn.longmaster.doctorlibrary.util.log.Logger;
import cn.longmaster.ihmonitor.core.app.AppApplication;
import cn.longmaster.ihmonitor.core.manager.BaseManager;


/**
 * SD卡文件管理类
 */
public class SdManager extends BaseManager {
    private final String TAG = SdManager.class.getSimpleName();

    /**
     * 主目录
     */
    private String DIR_DOCTOR = "/ihmonitor/";
    /**
     * 临时目录
     */
    private final String DIR_TEMP = "temp/";
    /**
     * crash目录
     */
    private final String DIR_CRASH = "crash/";
    /**
     * 头像
     */
    private final String DIR_AVATAR = "avatar/";
    /**
     * log目录
     */
    private final String DIR_LOG = "log/";

    private String mTempPath, mAvatarPath, mLogPath, mCrashPath;

    private String mDoctorRootPath;

    @Override
    public void onManagerCreate(AppApplication application) {
        mDoctorRootPath = getDirectoryPath() + DIR_DOCTOR;
        mTempPath = mDoctorRootPath + DIR_TEMP;
        mAvatarPath = mDoctorRootPath + DIR_AVATAR;
        mLogPath = mDoctorRootPath + DIR_LOG;
        mCrashPath = mDoctorRootPath + DIR_CRASH;
        mkdir(mTempPath, mAvatarPath, mLogPath, mCrashPath);
        hintMediaFile(mTempPath, mAvatarPath, mLogPath, mCrashPath);
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

    /**
     * 获得头像目录路径
     *
     * @return
     */
    public final String getAvatarPath() {
        return mAvatarPath;
    }

    /**
     * 得到指定用户id目录下该用户的头像路径
     *
     * @param userId 用户id
     * @return 头像路径
     */
    public final String getAppointAvatarFilePath(String userId) {
        String result = mAvatarPath + MD5Util.md5(userId);
        return result;
    }

    public String getCrashPath() {
        return mCrashPath;
    }

    /**
     * 获取log文件目录
     *
     * @return log文件目录
     */
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
                Logger.log(Logger.COMMON, TAG + "->mkdir()->filePath:" + filePath);

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
