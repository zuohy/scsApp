package cn.longmaster.ihmonitor.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.longmaster.doctorlibrary.util.common.StringUtil;
import cn.longmaster.doctorlibrary.util.log.Logger;
import cn.longmaster.doctorlibrary.viewinject.FindViewById;
import cn.longmaster.doctorlibrary.viewinject.OnClick;
import cn.longmaster.doctorlibrary.viewinject.ViewInjecter;
import cn.longmaster.ihmonitor.R;


@SuppressWarnings("deprecation")
public class BrowserActivity extends BaseActivity {
    private static final String TAG = BrowserActivity.class.getSimpleName();

    public static final String EXTRA_DATA_KEY_LOADING_URL = BrowserActivity.class.getCanonicalName() + "EXTRA_DATA_KEY_LOADING_URL";
    public static final String EXTRA_DATA_KEY_TITLE = BrowserActivity.class.getCanonicalName() + "EXTRA_DATA_KEY_TITLE";

    @FindViewById(R.id.activity_browser_title_textview)
    private TextView mTitleTv;
    @FindViewById(R.id.activity_browser_browser_wv)
    private WebView mBrowserWv;
    @FindViewById(R.id.activity_browser_top_loading_pb)
    private ProgressBar mLoadingPb;

    private String mTitle;
    private String mCurrentLoadUrl;

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;

    public static void startBrowserActivity(Context context, String url) {
        startBrowserActivity(context, url, "");
    }

    public static void startBrowserActivity(Context context, String url, String title) {
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra(EXTRA_DATA_KEY_LOADING_URL, url);
        intent.putExtra(EXTRA_DATA_KEY_TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        ViewInjecter.inject(this);
        initData();
        initWebView();
        dealWebViewBrowser();
    }

    private void initData() {
        Intent intent = getIntent();
        mCurrentLoadUrl = intent.getStringExtra(EXTRA_DATA_KEY_LOADING_URL);
        mTitle = intent.getStringExtra(EXTRA_DATA_KEY_TITLE);
        Logger.log(Logger.COMMON, TAG + "->initData()->url:" + mCurrentLoadUrl + ", title:" + mTitle);
        if (!StringUtil.isEmpty(mTitle)) {
            mTitleTv.setText(mTitle);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        mBrowserWv.getSettings().setDefaultTextEncodingName("UTF-8");
        mBrowserWv.getSettings().setSupportZoom(true);
        mBrowserWv.getSettings().setBuiltInZoomControls(true);
        mBrowserWv.getSettings().setJavaScriptEnabled(true);
        mBrowserWv.getSettings().setDomStorageEnabled(true);
        mBrowserWv.setDownloadListener(new MyWebViewDownLoadListener());
        mBrowserWv.getSettings().setUseWideViewPort(true);
        mBrowserWv.getSettings().setLoadWithOverviewMode(true);
    }

    private void dealWebViewBrowser() {
        if (mCurrentLoadUrl == null) {
            showToast("该页面不存在，请检查你的地址");
            finish();
            return;
        }

        mBrowserWv.loadUrl(mCurrentLoadUrl);

        mBrowserWv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mCurrentLoadUrl = url;
                view.loadUrl(url);
                return true;
            }
        });

        mBrowserWv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                mLoadingPb.setProgress(progress);
                if (progress == 100) {
                    mLoadingPb.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (title.length() > 10)
                    title = title.substring(0, 10) + "...";

                mTitleTv.setText(title);
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
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
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

    @OnClick({R.id.activity_browser_back_imagebutton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_browser_back_imagebutton:
                finish();
                break;
        }
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

    @Override
    protected void onDestroy() {
        mBrowserWv.clearCache(true);
        mBrowserWv.clearHistory();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        callHiddenWebViewMethod("onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        callHiddenWebViewMethod("onResume");
        super.onResume();
    }

    private void callHiddenWebViewMethod(String name) {
        if (mBrowserWv != null) {
            try {
                Method method = WebView.class.getMethod(name);
                method.invoke(mBrowserWv);
            } catch (NoSuchMethodException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
        }
    }

    private class MyWebViewDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }
}
