package cn.longmaster.ihmonitor.core.manager;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.Timer;
import java.util.TimerTask;

import cn.longmaster.doctorlibrary.util.handler.AppHandlerProxy;
import cn.longmaster.doctorlibrary.util.log.Logger;
import cn.longmaster.ihmonitor.core.app.AppApplication;
import cn.longmaster.ihmonitor.core.entity.UserInfo;
import cn.longmaster.ihmonitor.core.http.BaseResult;
import cn.longmaster.ihmonitor.core.http.OnResultListener;
import cn.longmaster.ihmonitor.core.http.user.LocationUpdateRequester;
import cn.longmaster.ihmonitor.core.manager.user.UserInfoManager;

/**
 * 百度定位
 * Created by Yang² on 2017/2/16.
 */

public class LocationManager extends BaseManager {
    private static final String TAG = LocationManager.class.getSimpleName();
    private AppApplication mApplication;
    private int mAppointmentId;
    private LocationClient mLocationClient = null;//百度定位核心类
    private int mCount;//失败计数，连续失败3次内，从新获取位置
    private boolean mGetLocation;//退出诊室后，置为false

    @AppApplication.Manager
    private UserInfoManager userInfoManager;

    boolean isDick=true;

    @Override
    public void onManagerCreate(AppApplication application) {
        mApplication = application;
        initLocation();
        regListener();
        timer.schedule(task,1000,600000);
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        mLocationClient = new LocationClient(mApplication);
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(1000);//定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//设置是否需要地址信息，默认不需要
        option.setIgnoreKillProcess(false);//默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        mLocationClient.setLocOption(option);
    }

    private void regListener() {
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                mLocationClient.stop();
                if (bdLocation != null) {
                    uploadPositionInfo(bdLocation);
                }
            }
        });
    }

    public void start() {
        Logger.log(Logger.COMMON, TAG + "->start()->");
//        this.mAppointmentId = appointmentId;
        if(!isDick)
        {
            initLocation();
            regListener();

        }
        isDick =false;
        mCount = 0;
        mGetLocation = true;
        mLocationClient.start();
    }

    /**
     * 是否要获取地理位置信息
     * 退出诊室后设置为false
     *
     * @param getLocation
     */
    public void setGetLocation(boolean getLocation) {
        this.mGetLocation = getLocation;
    }

    private void uploadPositionInfo(final BDLocation bdLocation) {
        if (!mGetLocation) {
            return;
        }
        if (TextUtils.isEmpty(bdLocation.getProvince()) && TextUtils.isEmpty(bdLocation.getCity()) &&
                TextUtils.isEmpty(bdLocation.getDistrict()) && TextUtils.isEmpty(bdLocation.getStreet())) {
            mCount++;
            Logger.log(Logger.COMMON, TAG + "->uploadPositionInfo()->地理位置为空->mCount:" + mCount);
            //如果没有获取到位置信息，重试两次
            if (mCount < 3) {
                AppHandlerProxy.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLocationClient.start();
                    }
                }, 5000);
            }
            return;
        }
        mCount = 0;

         final String address = bdLocation.getAddrStr();
        Logger.log(Logger.COMMON, TAG + "->uploadPositionInfo()->bdLocation:" + bdLocation.getCountry()
                + bdLocation.getProvince() + bdLocation.getCity() + bdLocation.getDistrict() + bdLocation.getStreet()
                + bdLocation.getAddrStr());

        LocationUpdateRequester requester = new LocationUpdateRequester(new OnResultListener<Void>() {
            @Override
            public void onResult(BaseResult baseResult, Void Void) {
//                Logger.log(Logger.HTTP, "PatientVerifyRequester-->baseResult.getCode():" + baseResult.getCode() + "appointmentId" + appointmentId);
            }
        });
            requester.longtitude = Double.toString(bdLocation.getLongitude());
            requester.latitude = Double.toString(bdLocation.getLatitude());
            UserInfo userInfo = userInfoManager.getCurrentUserInfo();
            requester.userID = userInfo.getUserId();
            requester.doPost();

//            AppHandlerProxy.runOnUIThread(new Runnable() {
//            @Override
//            public void run() {
//                MainActivity.getInstance().onLocationUpdate(address, bdLocation.getLongitude(), bdLocation.getLatitude());
//            }
//        });
    }

    Timer timer = new Timer( );
    TimerTask task = new TimerTask( ) {
        public void run ( ) {
            Message message = new Message( );
            message.what = 1;
            handler.sendMessage(message);
        }
    };
    final Handler handler = new Handler( ) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    start();
                    Log.e("Timer","Timer ");
                    break;
            }
            super.handleMessage(msg);
        }
    };
    protected void onDestroy ( ) {
        if (timer != null) {
            timer.cancel( );
            timer = null;
        }
        //super.onDestroy( );
    }
//        AppHandlerProxy.runOnUIThread(new Runnable() {
//            @Override
//            public void run() {
//                MainActivity.getInstance().onLocationUpdate(address, bdLocation.getLongitude(), bdLocation.getLatitude());
//            }
//        });

//        if (AppApplication.getInstance().getManager(DcpManager.class).getDcpInterface() == null || mAppointmentId == 0) {
//            Logger.log(Logger.COMMON, TAG + "->uploadPositionInfo()->dcp接口未初始化!!");
//            return;
//        }

//        try {
//            JSONObject locationJson = new JSONObject();
//            locationJson.put("_appID", mAppointmentId);
//            locationJson.put("_latitude", String.valueOf(bdLocation.getLatitude()));
//            locationJson.put("_longitude", String.valueOf(bdLocation.getLongitude()));
//            Logger.log(Logger.COMMON, TAG + "->uploadPositionInfo()->locationJson:" + locationJson.toString());
//            AppApplication.getInstance().getManager(DcpManager.class).getDcpInterface().Request(DcpFuncConfig.FUN_NAME_UPLOAD_LOCATION_INFO, locationJson.toString());
//
//            JSONObject positionJson = new JSONObject();
//            positionJson.put("_appID", mAppointmentId);
//            positionJson.put("_province", bdLocation.getProvince());
//            positionJson.put("_city", bdLocation.getCity());
//            positionJson.put("_district", bdLocation.getDistrict());
//            positionJson.put("_streets", bdLocation.getStreet());
//            Logger.log(Logger.COMMON, TAG + "->uploadPositionInfo()->positionJson:" + positionJson.toString());
//            AppApplication.getInstance().getManager(DcpManager.class).getDcpInterface().Request(DcpFuncConfig.FUN_NAME_UPLOAD_POSITION_INFO, positionJson.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

}
