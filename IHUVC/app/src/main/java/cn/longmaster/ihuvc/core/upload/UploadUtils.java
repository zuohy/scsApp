package cn.longmaster.ihuvc.core.upload;

import java.util.Random;

import cn.longmaster.doctorlibrary.utils.common.MD5Util;
import cn.longmaster.doctorlibrary.utils.common.OsUtil;

/**
 * 上传工具类
 * Created by Tengshuxiang on 2016-08-15.
 */
public class UploadUtils {
    public static String applyTaskId() {
        return MD5Util.md5(OsUtil.getIMEI() + new Random(System.currentTimeMillis()).nextLong());
    }

    public static String applySessionId(SingleFileInfo file) {
        return MD5Util.md5(file.getLocalFilePath() + applyTaskId());
    }
}
