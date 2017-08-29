package cn.longmaster.ihmonitor.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.longmaster.doctorlibrary.util.common.DateUtil;
import cn.longmaster.doctorlibrary.util.common.StringUtil;
import cn.longmaster.ihmonitor.R;
import cn.longmaster.ihmonitor.core.app.AppApplication;
import cn.longmaster.ihmonitor.core.app.AppConstant;
import cn.longmaster.ihmonitor.core.entity.appointment.AppointmentDisplayProperty;
import cn.longmaster.ihmonitor.core.entity.appointment.AppointmentInfo;
import cn.longmaster.ihmonitor.core.entity.config.DepartmentInfo;
import cn.longmaster.ihmonitor.core.entity.config.DoctorBaseInfo;
import cn.longmaster.ihmonitor.core.entity.config.HospitalInfo;
import cn.longmaster.ihmonitor.core.entity.config.PatientInfo;
import cn.longmaster.ihmonitor.core.http.BaseResult;
import cn.longmaster.ihmonitor.core.http.OnResultListener;
import cn.longmaster.ihmonitor.core.manager.appointment.AppointmentManager;
import cn.longmaster.ihmonitor.core.manager.user.DoctorManager;



public class AppointmentListAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<Integer> mAppointmentList;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;
    private LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    private Map<Integer, AppointmentInfo> mAppointmentInfos = new HashMap<>();
    private Map<Integer, PatientInfo> mPatientMap = new HashMap<>();
    private Map<Integer, DoctorBaseInfo> mDoctorInfos = new HashMap<>();
    private Map<Integer, HospitalInfo> mHospitalInfos = new HashMap<>();
    private Map<Integer, DepartmentInfo> mDepartmentInfos = new HashMap<>();

    public AppointmentListAdapter(Context context, List<Integer> itemList) {
        mContext = context;
        mAppointmentList = itemList;
        mLayoutParams.topMargin = BaseActivity.dipToPx(context, 5);
        mLayoutParams.bottomMargin = BaseActivity.dipToPx(context, 5);
    }

    public void setmOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnRecyclerViewItemClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_appointment_list, parent, false);
        return new AppointmentListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        AppointmentListViewHolder viewHolder = (AppointmentListViewHolder) holder;
        setViewTag(viewHolder, mAppointmentList.get(position));

        mLayoutParams.rightMargin = BaseActivity.dipToPx(mContext, 5);
        mLayoutParams.leftMargin = BaseActivity.dipToPx(mContext, 5);
        if (position == 0 || position == 1) {
            mLayoutParams.topMargin = BaseActivity.dipToPx(mContext, 15);
        } else {
            mLayoutParams.topMargin = BaseActivity.dipToPx(mContext, 5);
        }
        if (position % 2 == 0) {
            mLayoutParams.leftMargin = BaseActivity.dipToPx(mContext, 20);
        } else {
            mLayoutParams.rightMargin = BaseActivity.dipToPx(mContext, 20);
        }
        viewHolder.itemLayout.setLayoutParams(mLayoutParams);

        viewHolder.roomIdTv.setText(mContext.getString(R.string.appointment_room_id, mAppointmentList.get(position)));
        setAppointmentInfo(viewHolder, mAppointmentList.get(position));
        setPatientInfo(viewHolder, mAppointmentList.get(position));
        viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnRecyclerViewItemClickListener.onRecyclerViewItemClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAppointmentList.size();
    }

    private void setViewTag(AppointmentListViewHolder viewHolder, int appointmentId) {
        viewHolder.roomIdTv.setTag(appointmentId);
        viewHolder.typeTv.setTag(appointmentId);
        viewHolder.diseaseNameTv.setTag(appointmentId);
        viewHolder.superiorDoctorHospitalTv.setTag(appointmentId);
        viewHolder.superiorDoctorNameTv.setTag(appointmentId);
        viewHolder.firstDoctorHospitalTv.setTag(appointmentId);
        viewHolder.firstDoctorNameTv.setTag(appointmentId);
        viewHolder.patientNameTv.setTag(appointmentId);
        viewHolder.dateTv.setTag(appointmentId);

        viewHolder.roomIdTv.setText("");
        viewHolder.typeTv.setText("");
        viewHolder.typeTv.setVisibility(View.GONE);
        viewHolder.diseaseNameTv.setText("");
        viewHolder.superiorDoctorHospitalTv.setText("D:");
        viewHolder.superiorDoctorNameTv.setText("");
        viewHolder.firstDoctorHospitalTv.setText("d:");
        viewHolder.firstDoctorNameTv.setText("");
        viewHolder.patientNameTv.setText("");
        viewHolder.dateTv.setText("");
    }

    private void setAppointmentInfo(AppointmentListViewHolder viewHolder, int appointmentId) {
        if (!mAppointmentInfos.containsKey(appointmentId)) {
            getAppointmentInfo(viewHolder, appointmentId);
        } else {
            AppointmentInfo appointmentInfo = mAppointmentInfos.get(appointmentId);
            displayAppointmentInfo(viewHolder, appointmentInfo);
        }
    }

    private void getAppointmentInfo(final AppointmentListViewHolder viewHolder, final int appointmentId) {
        AppApplication.getInstance().getManager(AppointmentManager.class).getAppointmentInfo(appointmentId, new OnResultListener<AppointmentInfo>() {
            @Override
            public void onResult(BaseResult baseResult, AppointmentInfo appointmentInfo) {
                if (baseResult.getCode() == RESULT_SUCCESS && appointmentInfo != null) {
                    mAppointmentInfos.put(appointmentId, appointmentInfo);
                    displayAppointmentInfo(viewHolder, appointmentInfo);
                }
            }
        });
    }

    private void displayAppointmentInfo(AppointmentListViewHolder viewHolder, final AppointmentInfo appointmentInfo) {
        if ((int) viewHolder.dateTv.getTag() == appointmentInfo.getBaseInfo().getAppointmentId()) {
            long date = DateUtil.dateToMillisecond(appointmentInfo.getBaseInfo().getInsertDt());
            viewHolder.dateTv.setText(DateUtil.millisecondToYMDHM(date));
        }
        setDoctorInfo(viewHolder, appointmentInfo, appointmentInfo.getBaseInfo().getDoctorUserId(), true);
        setDoctorInfo(viewHolder, appointmentInfo, appointmentInfo.getBaseInfo().getAttendingDoctorUserId(), false);
    }

    private void setPatientInfo(AppointmentListViewHolder viewHolder, int appointmentId) {
        if (!mPatientMap.containsKey(appointmentId)) {
            getPatientInfo(viewHolder, appointmentId);
        } else {
            displayPatientInfo(viewHolder, appointmentId, mPatientMap.get(appointmentId));
        }
    }

    private void getPatientInfo(final AppointmentListViewHolder viewHolder, final int appointmentId) {
        AppApplication.getInstance().getManager(AppointmentManager.class).getPatientInfo(appointmentId, new OnResultListener<PatientInfo>() {
            @Override
            public void onResult(BaseResult baseResult, PatientInfo patientInfo) {
                if (baseResult.getCode() == RESULT_SUCCESS && patientInfo != null) {
                    mPatientMap.put(appointmentId, patientInfo);
                    displayPatientInfo(viewHolder, appointmentId, patientInfo);
                }
            }
        });
    }

    private void displayPatientInfo(AppointmentListViewHolder viewHolder, int appointmentId, PatientInfo patientInfo) {
        if ((int) viewHolder.dateTv.getTag() == appointmentId) {
            viewHolder.patientNameTv.setText(mContext.getString(R.string.appointment_patient_name, patientInfo.getPatientBaseInfo().getRealName()));
            viewHolder.diseaseNameTv.setText(mContext.getString(R.string.appointment_disease_name, getDiseaseName(patientInfo.getPatientBaseInfo().getFirstCureResult())));
        }
    }

    private void setDoctorInfo(AppointmentListViewHolder viewHolder, AppointmentInfo appointmentInfo, final int doctorId, boolean isSuperiorDoctor) {
        if (mDoctorInfos.containsKey(doctorId)) {
            displayDoctorInfo(viewHolder, appointmentInfo, mDoctorInfos.get(doctorId), isSuperiorDoctor);
        } else {
            getDoctorInfo(viewHolder, appointmentInfo, doctorId, isSuperiorDoctor);
        }
    }

    private void getDoctorInfo(final AppointmentListViewHolder viewHolder, final AppointmentInfo appointmentInfo, final int doctorId, final boolean isSuperiorDoctor) {
        AppApplication.getInstance().getManager(DoctorManager.class).getDoctorInfo(doctorId, new OnResultListener<DoctorBaseInfo>() {
            @Override
            public void onResult(BaseResult baseResult, DoctorBaseInfo doctorBaseInfo) {
                if (baseResult.getCode() == RESULT_SUCCESS && doctorBaseInfo != null) {
                    mDoctorInfos.put(doctorBaseInfo.getUserId(), doctorBaseInfo);
                    displayDoctorInfo(viewHolder, appointmentInfo, doctorBaseInfo, isSuperiorDoctor);
                }
            }
        });
    }

    private void displayDoctorInfo(AppointmentListViewHolder viewHolder, AppointmentInfo appointmentInfo, DoctorBaseInfo doctorBaseInfo, boolean isSuperiorDoctor) {
        if ((int) viewHolder.dateTv.getTag() == appointmentInfo.getBaseInfo().getAppointmentId()) {
            if (isSuperiorDoctor) {
                viewHolder.superiorDoctorNameTv.setText(doctorBaseInfo.getRealName());
                setDepartmentInfo(viewHolder, appointmentInfo, doctorBaseInfo.getDepartmentId());
            } else {
                viewHolder.firstDoctorNameTv.setText(doctorBaseInfo.getRealName());
            }
        }
        setHospitalInfo(viewHolder, doctorBaseInfo.getHospitalId(), isSuperiorDoctor);
    }

    private void setHospitalInfo(AppointmentListViewHolder viewHolder, final int hospitalId, boolean isSuperiorDoctor) {
        if (mHospitalInfos.containsKey(hospitalId)) {
            displayHospitalInfo(viewHolder, mHospitalInfos.get(hospitalId), isSuperiorDoctor);
        } else {
            getHospitalInfo(viewHolder, hospitalId, isSuperiorDoctor);
        }
    }

    private void getHospitalInfo(final AppointmentListViewHolder viewHolder, final int hospitalId, final boolean isSuperiorDoctor) {
        AppApplication.getInstance().getManager(DoctorManager.class).getHospitalInfo(hospitalId, new OnResultListener<HospitalInfo>() {
            @Override
            public void onResult(BaseResult baseResult, HospitalInfo hospitalInfo) {
                if (baseResult.getCode() == RESULT_SUCCESS && hospitalInfo != null) {
                    mHospitalInfos.put(hospitalId, hospitalInfo);
                    displayHospitalInfo(viewHolder, hospitalInfo, isSuperiorDoctor);
                }
            }
        });
    }

    private void displayHospitalInfo(AppointmentListViewHolder viewHolder, HospitalInfo hospitalInfo, boolean isSuperiorDoctor) {
        if (isSuperiorDoctor) {
            viewHolder.superiorDoctorHospitalTv.setText(mContext.getString(R.string.appointment_superior_doctor, StringUtil.isEmpty(hospitalInfo.getHospitalName()) ? "" : hospitalInfo.getHospitalName()));
        } else {
            viewHolder.firstDoctorHospitalTv.setText(mContext.getString(R.string.appointment_first_doctor, StringUtil.isEmpty(hospitalInfo.getHospitalName()) ? "" : hospitalInfo.getHospitalName()));
        }
    }

    private void setDepartmentInfo(AppointmentListViewHolder viewHolder, AppointmentInfo appointmentInfo, final int departmentId) {
        if ((int) viewHolder.dateTv.getTag() == appointmentInfo.getBaseInfo().getAppointmentId()) {
            if (mDepartmentInfos.containsKey(departmentId)) {
                displayDepartmentInfo(viewHolder, appointmentInfo, mDepartmentInfos.get(departmentId));
            } else {
                getDepartmentInfo(viewHolder, appointmentInfo, departmentId);
            }
        }
    }

    private void getDepartmentInfo(final AppointmentListViewHolder viewHolder, final AppointmentInfo appointmentInfo, final int departmentId) {
        AppApplication.getInstance().getManager(DoctorManager.class).getDepartmentInfo(departmentId, new OnResultListener<DepartmentInfo>() {
            @Override
            public void onResult(BaseResult baseResult, DepartmentInfo departmentInfo) {
                if (baseResult.getCode() == RESULT_SUCCESS && departmentInfo != null) {
                    mDepartmentInfos.put(departmentId, departmentInfo);
                    displayDepartmentInfo(viewHolder, appointmentInfo, departmentInfo);
                }
            }
        });
    }

    private void displayDepartmentInfo(AppointmentListViewHolder viewHolder, AppointmentInfo appointmentInfo, DepartmentInfo departmentInfo) {
        if ((int) viewHolder.dateTv.getTag() == appointmentInfo.getBaseInfo().getAppointmentId()) {
            viewHolder.typeTv.setVisibility(View.VISIBLE);
            AppointmentDisplayProperty property = getAppointmentDisplayProperty(appointmentInfo, departmentInfo);
            viewHolder.typeTv.setText(property.getDepartment() == null ? "" : property.getDepartment() + property.getType());
            viewHolder.typeTv.setBackgroundResource(property.getBackground());
            viewHolder.typeTv.setTextColor(mContext.getResources().getColor(property.getTextColor()));
        } else {
            viewHolder.typeTv.setVisibility(View.GONE);
        }
    }

    private AppointmentDisplayProperty getAppointmentDisplayProperty(AppointmentInfo appointmentInfo, DepartmentInfo departmentInfo) {
        AppointmentDisplayProperty property = new AppointmentDisplayProperty();
        property.setDepartment(departmentInfo.getDepartmentName());
        switch (appointmentInfo.getExtendsInfo().getServiceType()) {
            case AppConstant.ServiceType.SERVICE_TYPE_REMOTE_CONSULT:
            case AppConstant.ServiceType.SERVICE_TYPE_RETURN_CONSULT:
                property.setType(mContext.getString(R.string.service_type_remote_consult));
                property.setBackground(R.drawable.bg_appointment_video);
                property.setTextColor(R.color.color_95d009);
                break;

            case AppConstant.ServiceType.SERVICE_TYPE_MEDICAL_ADVICE:
            case AppConstant.ServiceType.SERVICE_TYPE_RETURN_ADVICE:
                property.setType(mContext.getString(R.string.service_type_remote_advice));
                property.setBackground(R.drawable.bg_appointment_video);
                property.setTextColor(R.color.color_95d009);
                break;

            case AppConstant.ServiceType.SERVICE_TYPE_REMOTE_OUTPATIENT:
                property.setType(mContext.getString(R.string.service_type_remote_outpatient));
                property.setBackground(R.drawable.bg_appointment_outpatient);
                property.setTextColor(R.color.color_1d9eef);
                break;

            case AppConstant.ServiceType.SERVICE_TYPE_REMOTE_WARDS:
                property.setType(mContext.getString(R.string.service_type_remote_wards));
                property.setBackground(R.drawable.bg_appointment_rounds);
                property.setTextColor(R.color.color_efa71d);
                break;

            case AppConstant.ServiceType.SERVICE_TYPE_IMAGE_CONSULT:
                property.setType(mContext.getString(R.string.service_type_remote_image_consult));
                property.setBackground(R.drawable.bg_appointment_imaging);
                property.setTextColor(R.color.color_05cfa7);
                break;

            default:
                property.setType(mContext.getString(R.string.service_type_remote_consult));
                property.setBackground(R.drawable.bg_appointment_video);
                property.setTextColor(R.color.color_95d009);
                break;
        }
        return property;
    }

    private String getDiseaseName(String diseaseName) {
        if (diseaseName.trim().length() < 19) {
            return diseaseName.trim();
        }
        return diseaseName.trim().substring(0, 19) + mContext.getString(R.string.appointment_points);
    }

    public static class AppointmentListViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout itemLayout;
        public TextView roomIdTv;
        public TextView typeTv;
        public TextView diseaseNameTv;
        public TextView superiorDoctorHospitalTv;
        public TextView superiorDoctorNameTv;
        public TextView firstDoctorHospitalTv;
        public TextView firstDoctorNameTv;
        public TextView patientNameTv;
        public TextView dateTv;

        public AppointmentListViewHolder(View itemView) {
            super(itemView);
            itemLayout = (LinearLayout) itemView.findViewById(R.id.adapter_appointment_item_layout);
            roomIdTv = (TextView) itemView.findViewById(R.id.adapter_appointment_list_room_id_textview);
            typeTv = (TextView) itemView.findViewById(R.id.adapter_appointment_list_type_textview);
            diseaseNameTv = (TextView) itemView.findViewById(R.id.adapter_appointment_list_disease_name_textview);
            superiorDoctorHospitalTv = (TextView) itemView.findViewById(R.id.adapter_appointment_list_superior_doctor_hospital_textview);
            superiorDoctorNameTv = (TextView) itemView.findViewById(R.id.adapter_appointment_list_superior_doctor_name_textview);
            firstDoctorHospitalTv = (TextView) itemView.findViewById(R.id.adapter_appointment_list_first_doctor_hospital_textview);
            firstDoctorNameTv = (TextView) itemView.findViewById(R.id.adapter_appointment_list_first_doctor_name_textview);
            patientNameTv = (TextView) itemView.findViewById(R.id.adapter_appointment_list_patient_name_textview);
            dateTv = (TextView) itemView.findViewById(R.id.adapter_appointment_list_date_textview);
        }
    }

    public interface OnRecyclerViewItemClickListener {
        void onRecyclerViewItemClicked(int position);
    }
}
