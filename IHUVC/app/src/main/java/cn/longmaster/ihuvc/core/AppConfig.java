package cn.longmaster.ihuvc.core;

/**
 * 应用配置类
 * Created by yangyong on 2016/4/14.
 */
public class AppConfig {
    //服务器名称
    public static ServerName serverName = ServerName.SERVER_BEIJING;
    //当前是否为调试模式，正式打包时需要置为false
    public static final boolean IS_DEBUG_MODE = true;
    //客户端版本
    public static final int CLIENT_VERSION = 1001;

    //服务器地址
    private static String serverAddress;
    //服务器端口
    private static int serverPort;
    //ndws服务器路径
    private static String ndwsUrl;
    //dfs服务器路径
    private static String dfsUrl;
    //duws服务器路径
    private static String duwsUrl;
    //ivws服务器路径
    private static String ivwsUrl;
    //adws服务器路径
    private static String adwsUrl;
    //clientApi服务器路径
    private static String clientApiUrl;
    //banner拉取路径
    private static String bannerDownloadUrl;
    //辅助资料拉取路径
    public static String materialDownloadUrl;

    //版本更新url地址
    private static String upgradeXmlUrl;
    //nginx上传url
    private static String nginxUploadUrl;

    //常见问题url
    private static String commonProblemUrl;
    //服务条款url
    private static String agreenUrl;
    //服务说明url
    private static String serverDescUrl;
    //新手引导url
    private static String newbieGuideUrl;
    //资格认证url
    private static String qualificationUrl;
    //首页无权限跳转地址
    private static String noAuthorityUrl;

    public static ServerName getServerName() {
        return serverName;
    }

    /**
     * 设置地址
     */
    public static void setUrl() {
//        if (IS_DEBUG_MODE) {
//            serverName = ServerName.values()[AppPreference.getIntValue(AppPreference.KEY_SERVICE_ADDRESS, ServerName.SERVER_TEST.ordinal())];
//        }
        switch (serverName) {
            case SERVER_BEIJING:// 北京服务器
                serverPort = 15000;
                serverAddress = "entry.39hospital.com";
                ndwsUrl = "http://ndws.39hospital.com/";
                dfsUrl = "http://dfs.39hospital.com/";
                duwsUrl = "http://duws.39hospital.com/";
                ivwsUrl = "http://ivws.39hospital.com/";
                adwsUrl = "http://adws.39hospital.com/";
                clientApiUrl = "http://clientapi.39hospital.com/";
                bannerDownloadUrl = "http://dfs.39hospital.com/3003/";
                materialDownloadUrl = "http://dfs.39hospital.com/3004/2/";
                upgradeXmlUrl = "http://dl.39hospital.com/android/ihuvc/";
                nginxUploadUrl = "http://dfs.39hospital.com:81/upload";

                commonProblemUrl = "http://help.39hospital.com/question_pad.html";
                agreenUrl = "http://help.39hospital.com/serviceTerms_pad.html";
                serverDescUrl = "http://help.39hospital.com/serviceDes_doctor.html";
                newbieGuideUrl = "http://help.39hospital.com/novice_guide.html";
                qualificationUrl = "http://adws.39hospital.com/Settled/main.html";
                noAuthorityUrl = "http://adws.39hospital.com/Settled/main.html";
                break;

            case SERVER_ISSUE://北京发布服务器
                serverPort = 15000;
                serverAddress = "issue-entry.39hospital.com";
                ndwsUrl = "http://issue-ndws.39hospital.com/";
                dfsUrl = "http://issue-dfs.39hospital.com/";
                duwsUrl = "http://issue-duws.39hospital.com/";
                ivwsUrl = "http://issue-ivws.39hospital.com/";
                adwsUrl = "http://issue-adws.39hospital.com/";
                clientApiUrl = "http://issue-clientapi.39hospital.com/";
                bannerDownloadUrl = "http://issue-dfs.39hospital.com/3003/";
                materialDownloadUrl = "http://issue-dfs.39hospital.com/3004/2/";
                upgradeXmlUrl = "http://issue-dl.39hospital.com/android/ihuvc/";
                nginxUploadUrl = "http://issue-dfs.39hospital.com:81/upload/";

                commonProblemUrl = "http://issue-help.39hospital.com/question_pad.html";
                agreenUrl = "http://issue-help.39hospital.com/serviceTerms_pad.html";
                serverDescUrl = "http://issue-help.39hospital.com/serviceDes_doctor.html";
                newbieGuideUrl = "http://issue-help.39hospital.com/novice_guide.html";
                qualificationUrl = "http://issue-adws.39hospital.com/Settled/main.html";
                noAuthorityUrl = "http://issue-adws.39hospital.com/Settled/main.html";
                break;

            case SERVER_TEST://测试服务器
                serverPort = 25000;
                serverAddress = "test-entry.39hospital.com";
                ndwsUrl = "http://test-ndws.39hospital.com/";
                dfsUrl = "http://test-dfs.39hospital.com/";
                duwsUrl = "http://test-duws.39hospital.com/";
                ivwsUrl = "http://test-ivws.39hospital.com/";
                adwsUrl = "http://test-adws.39hospital.com/";
                clientApiUrl = "http://test-clientapi.39hospital.com/";
                bannerDownloadUrl = "http://test-dfs.39hospital.com/3003/";
                materialDownloadUrl = "http://test-dfs.39hospital.com/3004/2/";
                upgradeXmlUrl = "http://test-dl.39hospital.com/android/ihuvc/";
                nginxUploadUrl = "http://test-dfs.39hospital.com:83/upload";

                commonProblemUrl = "http://test-help.39hospital.com/question_pad.html";
                agreenUrl = "http://test-help.39hospital.com/serviceTerms_pad.html";
                serverDescUrl = "http://test-help.39hospital.com/serviceDes_doctor.html";
                newbieGuideUrl = "http://test-help.39hospital.com/novice_guide.html";
                qualificationUrl = "http://test.iosask.cn/adws/Settled/main.html";
                noAuthorityUrl = "http://test.iosask.cn/adws/Settled/main.html";
                break;

            case SERVER_SANDBOX://109测试服务器
                serverPort = 15000;
                serverAddress = "10.254.33.109";
                ndwsUrl = "http://10.254.33.109/dws/";
                dfsUrl = "http://10.254.33.109/dfs/";
                duwsUrl = "http://10.254.33.109/duws/";
                ivwsUrl = "http://10.254.33.109/ivws/";
                adwsUrl = "http://10.254.33.109/adws/";
                materialDownloadUrl = "http://10.254.33.109/dfs/3004/2/";
                upgradeXmlUrl = "http://10.254.33.108/iws/ihuvc/";
                nginxUploadUrl = "http://10.254.33.109:81/upload";

                commonProblemUrl = "http://10.254.33.109/help/question_pad.html";
                agreenUrl = "http://10.254.33.109/help/serviceTerms_pad.html";
                serverDescUrl = "http://10.254.33.109/help/serviceDes_doctor.html";
                newbieGuideUrl = "http://10.254.33.109/help/novice_guide.html";
                qualificationUrl = "http://10.254.33.109/adws/Settled/main.html";
                noAuthorityUrl = "http://10.254.33.109/adws/Settled/main.html";
                break;

            default:
                break;
        }
    }

    public static String getServerAddress() {
        return serverAddress;
    }

    public static int getServerPort() {
        return serverPort;
    }

    public static String getNdwsUrl() {
        return ndwsUrl;
    }

    public static String getDfsUrl() {
        return dfsUrl;
    }

    public static String getDuwsUrl() {
        return duwsUrl;
    }

    public static String getIvwsUrl() {
        return ivwsUrl;
    }

    public static String getAdwsUrl() {
        return adwsUrl;
    }

    public static String getClientApiUrl() {
        return clientApiUrl;
    }

    public static String getBannerDownloadUrl() {
        return bannerDownloadUrl;
    }

    public static String getMaterialDownloadUrl() {
        return materialDownloadUrl;
    }

    public static String getUpgradeXmlUrl() {
        return upgradeXmlUrl;
    }

    public static String getNginxUploadUrl() {
        return nginxUploadUrl;
    }

    public static String getCommonProblemUrl() {
        return commonProblemUrl;
    }

    public static String getAgreenUrl() {
        return agreenUrl;
    }

    public static String getServerDescUrl() {
        return serverDescUrl;
    }

    public static String getNewbieGuideUrl() {
        return newbieGuideUrl;
    }

    public static String getQualificationUrl() {
        return qualificationUrl;
    }

    public static String getNoAuthorityUrl() {
        return noAuthorityUrl;
    }

    public enum ServerName {
        SERVER_BEIJING,
        SERVER_ISSUE,
        SERVER_TEST,
        SERVER_SANDBOX
    }
}
