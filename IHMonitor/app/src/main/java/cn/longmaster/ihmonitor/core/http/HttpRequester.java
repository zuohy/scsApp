package cn.longmaster.ihmonitor.core.http;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.longmaster.doctorlibrary.util.json.JsonHelper;
import cn.longmaster.doctorlibrary.util.log.Logger;
import cn.longmaster.ihmonitor.BuildConfig;
import cn.longmaster.ihmonitor.core.app.AppApplication;
import cn.longmaster.ihmonitor.core.app.AppConfig;
import cn.longmaster.ihmonitor.core.app.AppPreference;
import cn.longmaster.ihmonitor.core.entity.UserInfo;
import cn.longmaster.ihmonitor.core.manager.user.UserInfoManager;
import cz.msebera.android.httpclient.Header;

/**
 * 基本的HTTP请求, 所有的HTTP请求都通过该类发起
 */
public abstract class HttpRequester {
    private static final String TAG = "HttpRequester";
    protected static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
//    protected Map<String,RequestParams> authenticationMap = new HashMap<>();

    @AppApplication.Manager
    private UserInfoManager userInfoManager;

    static {
        asyncHttpClient.setCookieStore(new PersistentCookieStore(AppApplication.getInstance()));
        asyncHttpClient.setTimeout(60 * 1000);
    }

    protected AsyncHttpResponseHandler asyncHttpResponseHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            if (BuildConfig.DEBUG)
                Logger.log(Logger.HTTP, "onFailure->code:" + statusCode + ", responseString:" + responseString);

            BaseResult baseResult = new BaseResult();
            baseResult.setOpType(getOpType());
            baseResult.setTaskId(getTaskId());
            if (statusCode == 0) {
                baseResult.setCode(OnResultListener.RESULT_FAILED);
                HttpRequester.this.onFinish(baseResult, null);
            } else {
                if (BuildConfig.DEBUG) {
                    baseResult.setCode(OnResultListener.RESULT_SERVER_CODE_ERROR);
                    HttpRequester.this.onFinish(baseResult, null);
                } else {
                    baseResult.setCode(OnResultListener.RESULT_SERVER_CODE_ERROR);
                }
            }
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            if (BuildConfig.DEBUG)
                Logger.log(Logger.HTTP, "onSuccess->code:" + statusCode + ", responseString:" + responseString);

            try {
                JSONObject jsonObject = new JSONObject(responseString);
                BaseResult baseResult = JsonHelper.toObject(jsonObject, BaseResult.class);
                HttpRequester.this.onFinish(baseResult, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
                BaseResult baseResult = new BaseResult();
                baseResult.setOpType(getOpType());
                baseResult.setTaskId(getTaskId());
                baseResult.setCode(OnResultListener.RESULT_FAILED);
                HttpRequester.this.onFinish(baseResult, null);
            }
        }
    };

    public HttpRequester() {
        AppApplication.getInstance().injectManager(this);
    }

    protected static RequestParams getRequestParams(Map<String, Object> params) {
        RequestParams requestParams = new RequestParams();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            requestParams.put(entry.getKey(), entry.getValue());
        }
        return requestParams;
    }

    protected static String changeParams(Map<String, Object> params) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> keys = new ArrayList<>(params.keySet());
        boolean isFirst = true;
        for (String key : keys) {
            if (!isFirst)
                stringBuilder.append("&");
            else
                isFirst = false;

            stringBuilder.append(key).append("=").append(params.get(key).toString());
        }
        return stringBuilder.toString();
    }

    /**
     * 返回服务器地址
     */
    protected abstract String getUrl();

    /**
     * 返回接口id
     */
    protected abstract int getOpType();

    protected abstract String getTaskId();

    /**
     * 放入请求参数
     */
    protected abstract void onPutParams(Map<String, Object> params);

    protected void putParams(Map<String, Object> params) {
        onPutParams(params);
    }

    /**
     * 请求结束
     */
    protected abstract void onFinish(BaseResult baseResult, JSONObject data);

    /**
     * 发起get请求
     */
    public void doGet() {
        String url = getUrl();
        Map<String, Object> params = new HashMap<>();
        initBaseParams(params);
        putParams(params);

        JSONObject jsonObject = new JSONObject(params);
        RequestParams requestParams = new RequestParams();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            requestParams.put("json", jsonObject);
        }

        if (BuildConfig.DEBUG)
            Logger.log(Logger.HTTP, "request url :" + url + "?" + requestParams.toString());

        asyncHttpClient.get(url, requestParams, asyncHttpResponseHandler);
    }

    public void doPost(int delay) {
        AppApplication.HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                doPost();
            }
        }, delay);
    }

    public void doPostDelay() {
        doPost(500);
    }

    /**
     * 发起Post请求
     */
    public void doPost() {
        String url = getUrl();
        Map<String, Object> params = new HashMap<>();
        initBaseParams(params);
        putParams(params);

        JSONObject jsonObject = new JSONObject(params);
        RequestParams requestParams = new RequestParams();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            requestParams.put("json", jsonObject);
        }

        if (BuildConfig.DEBUG)
            Logger.log(Logger.HTTP, "request url :" + url + "?" + requestParams.toString());
//        if (AppApplication.getInstance().getManager(UserInfoManager.class).getCurrentUserInfo().getUserId() != 0
//                && System.currentTimeMillis() > AppPreference.getLongValue(AppPreference.KEY_AUTHENTICATION_OUT_TIME, 0)) {
//            //获取鉴权信息
//            AppApplication.getInstance().getManager(AuthenticationManager.class).getAuthenticationInfo();
//            AppApplication.getInstance().getManager(AuthenticationManager.class).addFailRequester(this);
//            return;
//        }

        asyncHttpClient.post(url, requestParams, asyncHttpResponseHandler);
    }

    protected void initBaseParams(Map<String, Object> params) {
        params.put("c_auth", AppPreference.getStringValue(AppPreference.KEY_AUTHENTICATION_AUTH, ""));
        params.put("op_type", getOpType());
        params.put("task_id", getTaskId());
        params.put("c_ver", AppConfig.CLIENT_VERSION);
        params.put("c_type", "6");
        params.put("pid", "4");
        params.put("user_type", "6");
        params.put("usertype", "1");
        UserInfo userInfo = userInfoManager.getCurrentUserInfo();
        params.put("user_id", userInfo.getUserId());
        params.put("gender", "0");
        if (userInfo.getUserId() != 0) {
            params.put("sid", userInfo.getLoginAuthKey());
        }
    }
}
