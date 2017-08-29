package cn.longmaster.ihuvc.core.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 预约信息
 * Created by JinKe on 2016-08-03.
 */
public class AppointmentInfo implements Serializable {
    private AppointmentBaseInfo mBaseInfo;
    private AppDialogInfo mAppDialogInfo;
    private AppointmentExtendsInfo mExtendsInfo;
    private ApplyDescInfo mApplyDescInfo;
    private List<ApplyDoctorInfo> mApplyDoctorInfoList;

    public AppointmentBaseInfo getBaseInfo() {
        return mBaseInfo;
    }

    public void setBaseInfo(AppointmentBaseInfo baseInfo) {
        mBaseInfo = baseInfo;
    }

    public AppDialogInfo getAppDialogInfo() {
        return mAppDialogInfo;
    }

    public void setAppDialogInfo(AppDialogInfo appDialogInfo) {
        mAppDialogInfo = appDialogInfo;
    }

    public AppointmentExtendsInfo getExtendsInfo() {
        return mExtendsInfo;
    }

    public void setExtendsInfo(AppointmentExtendsInfo extendsInfo) {
        mExtendsInfo = extendsInfo;
    }

    public ApplyDescInfo getApplyDescInfo() {
        return mApplyDescInfo;
    }

    public void setApplyDescInfo(ApplyDescInfo applyDescInfo) {
        this.mApplyDescInfo = applyDescInfo;
    }

    public List<ApplyDoctorInfo> getApplyDoctorInfoList() {
        return mApplyDoctorInfoList;
    }

    public void setApplyDoctorInfoList(List<ApplyDoctorInfo> applyDoctorInfoList) {
        this.mApplyDoctorInfoList = applyDoctorInfoList;
    }

    @Override
    public String toString() {
        return "AppointmentInfo{" +
                "mBaseInfo=" + mBaseInfo +
                ", mAppDialogInfo=" + mAppDialogInfo +
                ", mExtendsInfo=" + mExtendsInfo +
                ", mApplyDescInfo=" + mApplyDescInfo +
                ", mApplyDoctorInfoList=" + mApplyDoctorInfoList +
                '}';
    }
}
