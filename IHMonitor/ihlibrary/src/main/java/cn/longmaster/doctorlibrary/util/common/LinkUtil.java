package cn.longmaster.doctorlibrary.util.common;

import cn.longmaster.doctorlibrary.util.log.Logger;

/**
 * 网页链接相关工具
 * Created by LJ.Cao on 2016/1/11.
 */
public class LinkUtil {
    public static final String TAG = LinkUtil.class.getSimpleName();

    public static final String SPACE = " ";

    public static String processText(String text) {
        String res = text;
        Logger.logI(Logger.COMMON, "ORG: " + text);

        if (text.matches(".*[hH][tT][tT][pP][sS]?://.+\\..*")) {
            // 先处理https
            String[] ta = text.split("[hH][tT][tT][pP][sS]:/");
            String pro = "";
            for (int i = 0; i < ta.length; i++) {
                String ti = ta[i];
                if (ti.matches("/.+\\..*")) {
                    ti = "https:/" + ti;
                    // 寻找链接与文字内容的分界
                    for (int j = 0; j < ti.length(); j++) {
                        String ss = ti.substring(j, j + 1);// 取单个字符
                        if (!ss.matches("[a-zA-Z0-9\\-_%.=?&/:;]")) {
                            String s1 = ti.substring(0, j);
                            String s2 = ti.substring(j);
                            if (!s2.startsWith(SPACE))
                                ti = s1 + SPACE + s2;
                            if (i > 0) {
                                String tf = ta[i - 1];
                                if (tf.length() > 0) {
                                    String tfl = tf.substring(tf.length() - 1);
                                    if (!tfl.equals(SPACE)) {
                                        ti = SPACE + ti;
                                    }
                                }
                            }
                            break;
                        } else if (j == ti.length() - 1) {
                            if (i > 0) {
                                String tf = ta[i - 1];
                                if (tf.length() > 0) {
                                    String tfl = tf.substring(tf.length() - 1);
                                    if (!tfl.equals(SPACE)) {
                                        ti = SPACE + ti;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                pro += ti;
            }

            Logger.logI(Logger.COMMON, "PROCESS: " + pro);

            ta = pro.split("[hH][tT][tT][pP]:/");
            String pro2 = "";
            for (int i = 0; i < ta.length; i++) {
                String ti = ta[i];
                if (ti.matches("/.+\\..*")) {
                    ti = "http:/" + ti;
                    // 寻找链接与文字内容的分界
                    for (int j = 0; j < ti.length(); j++) {
                        String ss = ti.substring(j, j + 1);// 取单个字符
                        if (!ss.matches("[a-zA-Z0-9\\-_%.=?&/:;]")) {
                            String s1 = ti.substring(0, j);
                            String s2 = ti.substring(j);
                            if (!s2.startsWith(SPACE))
                                ti = s1 + SPACE + s2;
                            if (i > 0) {
                                String tf = ta[i - 1];
                                if (tf.length() > 0) {
                                    String tfl = tf.substring(tf.length() - 1);
                                    if (!tfl.equals(SPACE)) {
                                        ti = SPACE + ti;
                                    }
                                }
                            }
                            break;
                        } else if (j == ti.length() - 1) {
                            if (i > 0) {
                                String tf = ta[i - 1];
                                if (tf.length() > 0) {
                                    String tfl = tf.substring(tf.length() - 1);
                                    if (!tfl.equals(SPACE)) {
                                        ti = SPACE + ti;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                pro2 += ti;
            }

            res = pro2;
        }

        Logger.logI(Logger.COMMON, "RES: " + res);
        return res;
    }
}
