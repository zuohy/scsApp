package cn.longmaster.ihuvc.core.entity;

import java.io.Serializable;

import cn.longmaster.doctorlibrary.utils.json.JsonField;

/**
 * Created by WangHaiKun on 2017/4/25.
 */

public class CheckVersionInfo implements Serializable {
    @JsonField("pes_id")
    private int pesId;//pes编号
    @JsonField("phone_os_type")
    private int phoneOsType;////客户端类型0 android患者端,1 android 医生端,2 ios手机患者端,3 ios手机医生端,4  ipad医生端,5 资料采集端
    @JsonField("open_user_power")
    private int openUserPower;//是否开放更新权限
    @JsonField("client_version_limit")
    private int clienVersionLimit;//最低版本限制
    @JsonField("client_version_latest")
    private int getClienVersionLatest;//客户端最新版本
    @JsonField("client_version_latest_wizard")
    private int clienVersionLatestWizard;//客户端提示选择
    @JsonField("last_upd_dt")
    private String lastUpdDt;//更新时间

    public int getPesId() {
        return pesId;
    }

    public void setPesId(int pesId) {
        this.pesId = pesId;
    }

    public int getPhoneOsType() {
        return phoneOsType;
    }

    public void setPhoneOsType(int phoneOsType) {
        this.phoneOsType = phoneOsType;
    }

    public int getOpenUserPower() {
        return openUserPower;
    }

    public void setOpenUserPower(int openUserPower) {
        this.openUserPower = openUserPower;
    }

    public int getClienVersionLimit() {
        return clienVersionLimit;
    }

    public void setClienVersionLimit(int clienVersionLimit) {
        this.clienVersionLimit = clienVersionLimit;
    }

    public int getGetClienVersionLatest() {
        return getClienVersionLatest;
    }

    public void setGetClienVersionLatest(int getClienVersionLatest) {
        this.getClienVersionLatest = getClienVersionLatest;
    }

    public int getClienVersionLatestWizard() {
        return clienVersionLatestWizard;
    }

    public void setClienVersionLatestWizard(int clienVersionLatestWizard) {
        this.clienVersionLatestWizard = clienVersionLatestWizard;
    }

    public String getLastUpdDt() {
        return lastUpdDt;
    }

    public void setLastUpdDt(String lastUpdDt) {
        this.lastUpdDt = lastUpdDt;
    }

    @Override
    public String toString() {
        return "CheckVersionInfo{" +
                "pesId=" + pesId +
                ", phoneOsType=" + phoneOsType +
                ", openUserPower=" + openUserPower +
                ", clienVersionLimit=" + clienVersionLimit +
                ", getClienVersionLatest=" + getClienVersionLatest +
                ", clienVersionLatestWizard=" + clienVersionLatestWizard +
                ", lastUpdDt='" + lastUpdDt + '\'' +
                '}';
    }
}
