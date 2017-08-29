package cn.longmaster.ihmonitor.core.app;

/**
 * 应用配置文件
 */

public class AppConfig {
    //服务器名称
    public static ServerName serverName = ServerName.SERVER_CLOUD;
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
    //clientApi服务器路径
    private static String clientApiUrl;

    //版本更新url地址
    private static String upgradeXmlUrl;
    //诊室网络监测url地址
    private static String networkMonitorUrl;
    //病历url地址
    private static String recordUrl;

    /**
     * 设置地址
     */
    public static void setUrl() {
        switch (serverName) {
            case SERVER_QN:// 正式服务器
                serverPort = 15000;
                serverAddress = "entry.39hospital.com";
                ndwsUrl = "http://ndws.39hospital.com/";
                dfsUrl = "http://dfs.39hospital.com/";
                duwsUrl = "http://61.189.223.215:1680/duws/";
                clientApiUrl = "http://61.189.223.215:1680/sign_api/index.php?";
                upgradeXmlUrl = "http://dl.39hospital.com/android/doctor/";
                networkMonitorUrl = "http://61.189.223.215:1680/sign_server/index.php?entry_type=4";
                recordUrl = "http://www.baidu.com";
                break;

            case SERVER_CLOUD://阿里云测试服务器
                serverPort = 15000;
                serverAddress = "10.254.33.109";
                ndwsUrl = "http://10.254.33.109/dws/";
                dfsUrl = "http://10.254.33.109/dfs/";
                duwsUrl = "http://120.77.61.179/duws/";
                clientApiUrl = "http://120.77.61.179/sign_api/index.php?";
                upgradeXmlUrl = "http://10.254.33.108/iws/doctor/";
//                networkMonitorUrl = "http://120.77.61.179/zhsq_wapb23/index.html?";
                networkMonitorUrl = "http://120.77.61.179/sign_server/index.php?entry_type=4";
                recordUrl = "http://www.baidu.com";
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

    public static String getClientApiUrl() {
        return clientApiUrl;
    }

    public static String getUpgradeXmlUrl() {
        return upgradeXmlUrl;
    }

    public static String getNetworkMonitorUrl() {
        return networkMonitorUrl;
    }

    public static String getRecordUrl() {
        return recordUrl;
    }

    public enum ServerName {
        SERVER_QN,
        SERVER_CLOUD
    }
}
