package cn.longmaster.ihmonitor.core.http.user;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cn.longmaster.ihmonitor.core.app.AppConfig;
import cn.longmaster.ihmonitor.core.http.OnResultListener;
import cn.longmaster.ihmonitor.core.http.OpTypeConfig;
import cn.longmaster.ihmonitor.core.http.SimpleHttpRequester;

/**
 * Created by yty on 2017-08-02.
 */
public class LocationUpdateRequester extends SimpleHttpRequester<Void> {
    public int userID;
    public String longtitude;//经度
    public String latitude;//纬度
    public JSONObject jsonObject = new JSONObject();;

    public LocationUpdateRequester(@NonNull OnResultListener<Void> onResultListener) {
        super(onResultListener);
    }

    @Override
    protected int getOpType() {
        return OpTypeConfig.CLIENTAPI_OPTYE_LOCATION_UPDATE;
    }

    @Override
    protected String getUrl() {
        return AppConfig.getClientApiUrl();
    }

    @Override
    protected String getTaskId() {
        return getOpType() + "";
    }

    @Override
    protected Void onDumpData(JSONObject data) throws JSONException {
        return null;
    }

    protected Void dataProcess()  {
        try {
            jsonObject.put("user_id", userID);
            jsonObject.put("longtitude", longtitude);
            jsonObject.put("latitude", latitude);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //dataArray = jsonObject;

        return null;
    }

    @Override
    protected void onPutParams(Map<String, Object> params) {
        dataProcess();
        params.put("datalist", jsonObject);
    }
}
