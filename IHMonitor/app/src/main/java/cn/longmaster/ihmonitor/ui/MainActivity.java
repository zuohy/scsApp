package cn.longmaster.ihmonitor.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import cn.longmaster.doctorlibrary.util.log.Logger;
import cn.longmaster.doctorlibrary.viewinject.FindViewById;
import cn.longmaster.doctorlibrary.viewinject.OnClick;
import cn.longmaster.doctorlibrary.viewinject.ViewInjecter;
import cn.longmaster.ihmonitor.R;
import cn.longmaster.ihmonitor.core.app.AppApplication;
import cn.longmaster.ihmonitor.core.app.AppConfig;
import cn.longmaster.ihmonitor.core.entity.UserInfo;
import cn.longmaster.ihmonitor.core.entity.common.BaseMessageInfo;
import cn.longmaster.ihmonitor.core.manager.LocationManager;
import cn.longmaster.ihmonitor.core.manager.dcp.DcpManager;
import cn.longmaster.ihmonitor.core.manager.message.MessageManager;
import cn.longmaster.ihmonitor.core.manager.message.MessageStateChangeListener;
import cn.longmaster.ihmonitor.core.manager.user.UserInfoManager;
import cn.longmaster.ihmonitor.view.dialog.LogoutDialog;

/**
 * h5入口主页面
 * Created by zuohy on 2017/7/23.
 * merged by yangty ion 2017/7/30.
 */

public class MainActivity extends BaseActivity implements MessageStateChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_DATA_APPOINT_ID = TAG + ".extra_data_appoint_id";

    private static final int BIG_VIDEO_DEFAULT_TAG = -1;//大视频默认Tag
    private static final int CODE_NETWOR_KMONITOR = 1;//大视频默认Tag
    private static final int CODE_RECORD = 2;//大视频默认Tag
    private static MainActivity mMainActivity;
    private boolean color = true;

    @FindViewById(R.id.activity_browser_title_textview)
    private TextView mAddress;//就诊编号
    @FindViewById(R.id.activity_browser_msg)
    private TextView msg;//就诊编号
    @FindViewById(R.id.activity_browser_msg_imagebutton)
    private ImageButton msgRemind;
    @FindViewById(R.id.activity_browser_back_imagebutton)
    private TextView backButton;
    @FindViewById(R.id.activity_browser_browser_wv)
    private WebView mNetworkMonitorWv;//状态监控

    @AppApplication.Manager
    private LocationManager mLocationManager;
    @AppApplication.Manager
    private UserInfoManager mUserInfoManager;
    @AppApplication.Manager
    private MessageManager mMessageManager;

    @AppApplication.Manager
    private DcpManager mDcpManager;

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        ViewInjecter.inject(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initView();
        addListener();
        mMainActivity = this;
    }

    public static MainActivity getInstance() {
        return mMainActivity;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // if (mLMVideoMgr != null) {
        //     mLMVideoMgr.resume();
        //}
    }

    @Override
    protected void onPause() {
        super.onPause();
        //if (mLMVideoMgr != null) {
        //     mLMVideoMgr.pause();
        // }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mMessageManager.unRegMsgStateChangeListener(this);
        Logger.log(Logger.ROOM, TAG + "onDestroy()");
    }

    /**
     * 初始化控件
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        mNetworkMonitorWv = (WebView) findViewById(R.id.activity_browser_browser_wv);

        mNetworkMonitorWv.setBackgroundColor(0);
//        初始化状态监控器
        mNetworkMonitorWv.getSettings().setDefaultTextEncodingName("UTF-8");
        mNetworkMonitorWv.getSettings().setSupportZoom(true);
        mNetworkMonitorWv.getSettings().setBuiltInZoomControls(true);
        mNetworkMonitorWv.getSettings().setJavaScriptEnabled(true);
        mNetworkMonitorWv.getSettings().setDomStorageEnabled(true);
        mNetworkMonitorWv.getSettings().setUseWideViewPort(true);
        mNetworkMonitorWv.getSettings().setLoadWithOverviewMode(true);
        mNetworkMonitorWv.clearCache(true);

        mNetworkMonitorWv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        mNetworkMonitorWv.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                // TODO Auto-generated method stub
//                textView.setText(title);
                super.onReceivedTitle(view, title);
            }
        });
        mNetworkMonitorWv.setDownloadListener(new MyDownloadStart());



        //getAuthenticationInfo(CODE_NETWOR_KMONITOR);
        String networkMonitorUrl = AppConfig.getNetworkMonitorUrl()
        //+ "app_id=" + 1
        //+ "&c_auth=" + AppPreference.getStringValue(AppPreference.KEY_AUTHENTICATION_AUTH, "")
        + "&user_id=" + mUserInfoManager.getCurrentUserInfo().getUserId()
        + "&phone_num=" + mUserInfoManager.getCurrentUserInfo().getPhoneNum();
//        + "?entry_type=" + 1;
        msgRemind.setVisibility(View.GONE);
//        mNetworkMonitorWv.loadUrl(networkMonitorUrl);
        dealWebViewBrowser(networkMonitorUrl);
        Logger.logI(Logger.COMMON, "initWebView-->url:" + networkMonitorUrl);
        mLocationManager.start();

        //setCurrentTime();
        //mHandler.postDelayed(mRunnable, 1000);
    }

    private void dealWebViewBrowser(String url) {
        if (url == null) {
            showToast("该页面不存在，请检查你的地址");
            finish();
            return;
        }

        mNetworkMonitorWv.loadUrl(url);

        mNetworkMonitorWv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                url = url;
                view.loadUrl(url);
                return true;
            }
        });

        mNetworkMonitorWv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
//                mLoadingPb.setProgress(progress);
//                if (progress == 100) {
//                    mLoadingPb.setVisibility(View.GONE);
//                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
//                if (mTitle == null || "".equals(mTitle)) {
                if (title.length() > 10)
                    title = title.substring(0, 10) + "...";

//                mActionBar.setTitle(title);
//                }
                super.onReceivedTitle(view, title);
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                openImageChooserActivity();
                return true;
            }
        });
    }

    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }

    @OnClick({R.id.activity_browser_msg})
    public void onMsgClick(View view) {
        switch (view.getId()) {
            case R.id.activity_browser_msg:
                String networkMonitorUrl = AppConfig.getRecordUrl();
//                mNetworkMonitorWv.loadUrl(networkMonitorUrl);
                dealWebViewBrowser(networkMonitorUrl);
//                msg.setText("消息");
                msgRemind.setVisibility(View.GONE);
//                msg.setTextColor(Color.BLUE);

                break;
        }
    }

    @OnClick({R.id.activity_browser_back_imagebutton})
    public void onBackClick(View view) {
        switch (view.getId()) {
            case R.id.activity_browser_back_imagebutton:
//                msgRemind.setVisibility(View.GONE);
//                mNetworkMonitorWv.goBack();
                logout();
                break;
        }
    }

    @OnClick({R.id.activity_browser_title_textview})
    public void onLocationClick(View view) {
        switch (view.getId()) {
            case R.id.activity_browser_title_textview:
//                logout();
//                mLocationManager.start();
                break;
        }
    }

    public void onLocationUpdate(final String address, double longtitude, double latitude){
//        mAddress.setText("经度：" + longtitude + " " + "纬度：" + latitude + "\n" + address);
//        mAddress.setTextSize(11);

        showToast("地址：" + address + "\n" + "经度：" + longtitude + "\n" + "纬度：" + latitude);
    }

    private void initPage() {
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
    public void onNewMessage(BaseMessageInfo baseMessageInfo) {
        Logger.logI(Logger.USER, "onNewMessage->被调用");
        msgRemind.setVisibility(View.VISIBLE);
    }

    private void addListener() {
        mMessageManager.regMsgStateChangeListener(this);
    }


    class MyDownloadStart implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent,
                                    String contentDisposition, String mimetype, long contentLength) {
            // TODO Auto-generated method stub
            //调用自己的下载方式
//          new HttpThread(url).start();
            //调用系统浏览器下载
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }
}