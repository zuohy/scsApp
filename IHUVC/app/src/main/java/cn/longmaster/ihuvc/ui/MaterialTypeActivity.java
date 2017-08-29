package cn.longmaster.ihuvc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import cn.longmaster.doctorlibrary.utils.log.Logger;
import cn.longmaster.doctorlibrary.viewinject.FindViewById;
import cn.longmaster.doctorlibrary.viewinject.ViewInjecter;
import cn.longmaster.ihuvc.R;
import cn.longmaster.ihuvc.core.entity.AppointmentInfo;
import cn.longmaster.ihuvc.core.entity.MaterialTypeInfo;
import cn.longmaster.ihuvc.core.entity.PatientInfo;
import cn.longmaster.ihuvc.core.http.BaseResult;
import cn.longmaster.ihuvc.core.http.OnResultListener;
import cn.longmaster.ihuvc.core.http.requesters.MaterialTypeRequester;
import cn.longmaster.ihuvc.view.AppActionBar;
import cn.longmaster.ihuvc.view.dialog.CustomProgressDialog;

public class MaterialTypeActivity extends BaseActivity {
    private static final String TAG = CaptureActivity.class.getSimpleName();
    public static final String MATERIAL_TYPE_APPOINTMENT_INFO = TAG + "_material_type_appointment_info";
    public static final String MATERIAL_TYPE_PATIENT_INFO = TAG + "_material_type_patient_info";

    @FindViewById(R.id.activity_data_type_bar)
    private AppActionBar mDataTypeBar;
    @FindViewById(R.id.activity_data_list)
    private ListView mListView;

    private MaterialTypeAdapter mDataTypeAdapter;
    private AppointmentInfo mAppointmentInfo;
    private PatientInfo mPatientInfo;
    private List<MaterialTypeInfo> mMaterialTypeInfo;

    public static void startMaterialTypeActivity(Context context, AppointmentInfo appointmentInfo, PatientInfo patientInfo) {
        Intent intent = new Intent(context, MaterialTypeActivity.class);
        intent.putExtra(MATERIAL_TYPE_APPOINTMENT_INFO, appointmentInfo);
        intent.putExtra(MATERIAL_TYPE_PATIENT_INFO, patientInfo);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_type);
        ViewInjecter.inject(this);
        iniData();
        initListener();
    }

    private void iniData() {
        Intent intent = getIntent();
        mAppointmentInfo = (AppointmentInfo) intent.getSerializableExtra(MATERIAL_TYPE_APPOINTMENT_INFO);
        mPatientInfo = (PatientInfo) intent.getSerializableExtra(MATERIAL_TYPE_PATIENT_INFO);
        mDataTypeBar.setTitle(getString(R.string.doctor_number, mAppointmentInfo.getBaseInfo().getAppointmentId() + ""));

        final CustomProgressDialog progressDialog = new CustomProgressDialog(this, getString(R.string.custom_progress_dialog_loading));
        progressDialog.setIsConsumeKeyBack(false);
        progressDialog.show();

        MaterialTypeRequester requester = new MaterialTypeRequester(new OnResultListener<List<MaterialTypeInfo>>() {
            @Override
            public void onResult(BaseResult baseResult, List<MaterialTypeInfo> typeInfoList) {
                progressDialog.dismiss();
                Logger.log(Logger.COMMON, "baseResult:" + baseResult + "---typeInfoList:" + typeInfoList);
                if (baseResult.getCode() == RESULT_SUCCESS && typeInfoList != null) {
                    mMaterialTypeInfo = typeInfoList;
                    mDataTypeAdapter = new MaterialTypeAdapter(MaterialTypeActivity.this, mMaterialTypeInfo);
                    mListView.setAdapter(mDataTypeAdapter);
                }
            }
        });
        requester.appointmentId = mAppointmentInfo.getBaseInfo().getAppointmentId();
        requester.doPost();
    }

    public void onLeftClick(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.log(Logger.COMMON, "requestCode:" + resultCode);
        if (resultCode == RESULT_OK) {
            int type = data.getIntExtra(CaptureActivity.EXTRA_DATA_TYPE, 0);
            int uploadSuccessNum = data.getIntExtra(CaptureActivity.EXTRA_DATA_UPLOAD_SUCCESS_NUM, 0);
            mMaterialTypeInfo.get(type).setmMaterialNum(mMaterialTypeInfo.get(type).getmMaterialNum() + uploadSuccessNum);
            mDataTypeAdapter.notifyDataSetChanged();
        }
    }

    private void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CaptureActivity.startCaputureActivity(MaterialTypeActivity.this, mAppointmentInfo, mPatientInfo, position, mMaterialTypeInfo);
            }
        });
    }
}
