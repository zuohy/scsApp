package cn.longmaster.ihmonitor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.longmaster.doctorlibrary.util.handler.AppHandlerProxy;
import cn.longmaster.doctorlibrary.viewinject.FindViewById;
import cn.longmaster.doctorlibrary.viewinject.OnClick;
import cn.longmaster.doctorlibrary.viewinject.ViewInjecter;
import cn.longmaster.ihmonitor.R;
import cn.longmaster.ihmonitor.core.app.AppApplication;
import cn.longmaster.ihmonitor.core.entity.UserInfo;
import cn.longmaster.ihmonitor.core.manager.appointment.AppointmentManager;
import cn.longmaster.ihmonitor.core.manager.dcp.DcpManager;
import cn.longmaster.ihmonitor.core.manager.user.UserInfoManager;
import cn.longmaster.ihmonitor.view.dialog.LogoutDialog;


public class AppointmentListActivity extends BaseActivity implements AppointmentListAdapter.OnRecyclerViewItemClickListener, AppointmentManager.OnGetRoomInfoStateChangeListener {
    @FindViewById(R.id.activity_appointment_list_recyclerview)
    private RecyclerView mAppointmentRv;
    @FindViewById(R.id.activity_appointment_list_empty_textview)
    private TextView mEmptyTv;

    @AppApplication.Manager
    private AppointmentManager mAppointmentManager;
    @AppApplication.Manager
    private UserInfoManager mUserInfoManager;
    @AppApplication.Manager
    private DcpManager mDcpManager;

    private AppointmentListAdapter mAppointmentListAdapter;
    private List<Integer> mAppointmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);
        ViewInjecter.inject(this);

        initRecyclerView();
        mAppointmentManager.regOnGetRoomInfoStateChangeListener(this);
        mAppointmentManager.getRoomListInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAppointmentManager.unRegOnGetRoomInfoStateChangeListener();
    }

    private void initRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mAppointmentRv.setLayoutManager(layoutManager);

        mAppointmentListAdapter = new AppointmentListAdapter(this, mAppointmentList);
        mAppointmentListAdapter.setmOnRecyclerViewItemClickListener(this);
        mAppointmentRv.setAdapter(mAppointmentListAdapter);
    }

    @OnClick({R.id.activity_appointment_list_logout_imagebutton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_appointment_list_logout_imagebutton:
                logout();
                break;
        }
    }

    @Override
    public void onRecyclerViewItemClicked(int position) {
        Intent intent = new Intent(this, ConsultRoomActivity.class);
        intent.putExtra(ConsultRoomActivity.EXTRA_DATA_APPOINT_ID, mAppointmentList.get(position));
        startActivity(intent);
    }

    private void logout() {
        LogoutDialog logoutDialog = new LogoutDialog(this);
        logoutDialog.setLogoutDialogClickListener(new LogoutDialog.LogoutDialogClickListener() {
            @Override
            public void onCancleClicked() {
            }

            @Override
            public void onConfirmClicked() {
                UserInfo userInfo = mUserInfoManager.getCurrentUserInfo();
                mDcpManager.logout(userInfo.getUserId());
                mDcpManager.disconnect();
                mUserInfoManager.removeUserInfo();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        logoutDialog.show();
    }

    @Override
    public void onGetRoomInfoStateChanged(final List<Integer> roomInfos) {
        AppHandlerProxy.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                mAppointmentList.clear();
                mAppointmentList.addAll(roomInfos);
                mAppointmentListAdapter.notifyDataSetChanged();
                if (mAppointmentList.size() == 0) {
                    mEmptyTv.setVisibility(View.VISIBLE);
                } else {
                    mEmptyTv.setVisibility(View.GONE);
                }
            }
        });
    }
}
