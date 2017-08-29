package cn.longmaster.ihmonitor.core.manager.user;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.longmaster.doctorlibrary.util.common.AppHelper;
import cn.longmaster.doctorlibrary.util.common.MD5Util;
import cn.longmaster.doctorlibrary.util.common.StringUtil;
import cn.longmaster.doctorlibrary.util.handler.AppHandlerProxy;
import cn.longmaster.doctorlibrary.util.log.Logger;
import cn.longmaster.doctorlibrary.util.thread.AsyncResult;
import cn.longmaster.doctorlibrary.util.timeout.TimeoutHelper;
import cn.longmaster.ihmonitor.R;
import cn.longmaster.ihmonitor.core.app.AppApplication;
import cn.longmaster.ihmonitor.core.app.AppConfig;
import cn.longmaster.ihmonitor.core.app.AppConstant;
import cn.longmaster.ihmonitor.core.app.AppPreference;
import cn.longmaster.ihmonitor.core.app.DcpErrorcodeDef;
import cn.longmaster.ihmonitor.core.app.DcpFuncConfig;
import cn.longmaster.ihmonitor.core.db.DBHelper;
import cn.longmaster.ihmonitor.core.db.contract.UserInfoContract;
import cn.longmaster.ihmonitor.core.entity.UserInfo;
import cn.longmaster.ihmonitor.core.entity.user.UserResultInfo;
import cn.longmaster.ihmonitor.core.http.BaseResult;
import cn.longmaster.ihmonitor.core.http.OnResultListener;
import cn.longmaster.ihmonitor.core.http.login.AccountActiveRequester;
import cn.longmaster.ihmonitor.core.manager.BaseManager;
import cn.longmaster.ihmonitor.core.manager.dcp.DcpManager;
import cn.longmaster.ihmonitor.core.manager.storage.DBManager;
import cn.longmaster.ihmonitor.core.manager.storage.DatabaseTask;
import cn.longmaster.ihmonitor.view.dialog.KickOffDialog;

/**
 * 用户信息管理类
 */
public class UserInfoManager extends BaseManager {
    private final String TAG = UserInfoManager.class.getSimpleName();
    private final String PHONE_NUMBER_PREFIX = "86";//手机号码前缀
    private static final int MAX_WAITING_TIME = 60 * 1000;//最长等待时间

    private UserInfo mCurrentUserInfo, mTempUser;
    private List<LoginStateChangeListener> mLoginStateChangeListeners = new ArrayList<>();
    private TimeoutHelper<Integer> mTimeoutHelper;
    private AppApplication mApplication;
    private List<VersionChangeListener> mVersionChangeListeners = new ArrayList<>();
    private int queryPesTime = 0;//请求pes次数
    private int mIdentity;

    @Override
    public void onManagerCreate(AppApplication application) {
        mTimeoutHelper = new TimeoutHelper<Integer>();
        mTimeoutHelper.setCallback(callback);
        mApplication = application;
    }

    @Override
    public void onAllManagerCreated() {
        super.onAllManagerCreated();
    }

    public void regVersionChangeListener(VersionChangeListener listenter) {
        mVersionChangeListeners.add(listenter);
    }

    public void unRegVersionChangeListener(VersionChangeListener listenter) {
        mVersionChangeListeners.remove(listenter);
    }

    public UserInfo getCurrentUserInfo() {
        if (mCurrentUserInfo == null) {
            mCurrentUserInfo = new UserInfo();
        }
        Logger.log(Logger.USER, TAG + "->getCurrentUserInfo()->CurrentUserInfo:" + mCurrentUserInfo);
        return mCurrentUserInfo;
    }

    public boolean isAdmin() {
        return mIdentity == 1;
    }

    /**
     * 判断是否是顾问
     */
    public boolean isAdviser() {
        return (mIdentity >> 5) == 1;
    }

    /**
     * 账号激活
     *
     * @param account     账号
     * @param accountType 帐户类型
     * @param pwd         密码
     */
    public void activeAccount(String account, final byte accountType, String pwd, final LoginStateChangeListener listener) {
        if (mLoginStateChangeListeners.contains(listener)) {
            mLoginStateChangeListeners.add(listener);
            return;
        }
        account = PHONE_NUMBER_PREFIX + account;
        pwd = MD5Util.md5(pwd);
        final String finalAccount = account;
        AccountActiveRequester requester = new AccountActiveRequester(new OnResultListener<UserResultInfo>() {
            @Override
            public void onResult(BaseResult baseResult, UserResultInfo userResultInfo) {
                Logger.log(Logger.USER, TAG + "->activeAccount()->baseResult:" + baseResult + ", userResultInfo:" + userResultInfo);
                if (baseResult.getCode() == RESULT_SUCCESS) {
                    mLoginStateChangeListeners.add(listener);
                    mTempUser = new UserInfo();
                    mTempUser.setUserId(userResultInfo.getUserID());
                    mTempUser.setAccountType(accountType);
                    mTempUser.setPhoneNum(finalAccount);
                    mTempUser.setLoginAuthKey(userResultInfo.getLoginAuthKey());
                    mTempUser.setPesAddr(userResultInfo.getPesAddr());
                    mTempUser.setPesIp(userResultInfo.getPesIP());
                    mTempUser.setPesPort(userResultInfo.getPesPort());
                    mTempUser.setIsDoctor(userResultInfo.getIsDoctor());
                    saveUserInfo(mTempUser);
                    mTimeoutHelper.request(userResultInfo.getUserID(), MAX_WAITING_TIME);
                    AppApplication.getInstance().getManager(DcpManager.class).setPesInfo(userResultInfo.getPesIP(), userResultInfo.getPesPort());
                } else {
                    listener.onLoginStateChanged(baseResult.getCode(), DcpErrorcodeDef.buildErrorMsg(baseResult.getReson()));
                }
            }
        });
        requester.account = account;
        requester.pwd = pwd;
        requester.accountType = accountType;
        requester.doPost();
    }

    /**
     * 接收pes服务器回调数据
     *
     * @param result     请求结果码
     * @param funcAction 操作类型
     * @param json       毁掉数据
     */
    public void onRecvData(final int result, int funcAction, final String json) {
        Logger.log(Logger.USER, TAG + "->onRecvData()->result:" + result + ", funcAction:" + funcAction + ", json:" + json);

        UserInfo userInfo = getCurrentUserInfo();
        if (userInfo.getUserId() == 0 && mTempUser != null) {
            userInfo = mTempUser;
        }

        mTimeoutHelper.cancel(userInfo.getUserId());
        if (result == DcpErrorcodeDef.RET_SUCCESS) {
            AppApplication.getInstance().setOnlineState(AppConstant.OnlineState.ONLINE_STATE_ONLINE);
            if (funcAction == DcpFuncConfig.ACTION_TYPE_SETPESINFO) {
                AppApplication.getInstance().getManager(DcpManager.class).login(userInfo.getUserId(), userInfo.getLoginAuthKey());
            } else if (funcAction == DcpFuncConfig.ACTION_TYPE_LOGIN) {
                userInfo.setIsUsing(UserInfo.INUSE);
                userInfo.setIsActivity(UserInfo.HAS_ACTIVATED);
                userInfo.setLastLoginDt(System.currentTimeMillis());
                saveUserInfo(userInfo);
                mCurrentUserInfo = userInfo;
                checkVersion(json);
                AppApplication.getInstance().getManager(DcpManager.class).checkAdminInfo();
                AppHandlerProxy.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        //获取鉴权信息
                        AppApplication.getInstance().getManager(AuthenticationManager.class).getAuthenticationInfo();
                    }
                });
            }
        } else if (result == DcpErrorcodeDef.RET_LOGIN_AUTHKEY_ERROR) {//被踢下线
            showKickoffDialog();
        } else if (result == DcpErrorcodeDef.RET_USER_CLIENT_VERSION_TOO_LOWER) {//强制升级
            checkVersion(json);
        }

        if ((funcAction == DcpFuncConfig.ACTION_TYPE_SETPESINFO && result != DcpErrorcodeDef.RET_SUCCESS) || funcAction == DcpFuncConfig.ACTION_TYPE_LOGIN) {
            responseResult(result, DcpErrorcodeDef.buildErrorMsg(result));
        }
    }

    /**
     * 检查版本号码
     *
     * @param json 服务器回调json
     */
    private void checkVersion(final String json) {
        AppHandlerProxy.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int limiteVersion = jsonObject.optInt("_clientVersionLimit", AppConfig.CLIENT_VERSION);
                    int latestVersion = jsonObject.optInt("_clientVersionLatest", AppConfig.CLIENT_VERSION);
                    int currentVersion = VersionManager.getInstance().getCurentClientVersion();
                    if (currentVersion < limiteVersion && AppPreference.getIntValue(AppPreference.KEY_SERVER_LIMIT_VERSION, 0) < limiteVersion) {
                        VersionManager.setClientVersionLimit(limiteVersion);
                        for (VersionChangeListener listener : mVersionChangeListeners) {
                            listener.onVersionLimited();
                        }
                    } else if (currentVersion < latestVersion && AppPreference.getIntValue(AppPreference.KEY_SERVER_LASTEST_VERSION, 0) < latestVersion) {
                        VersionManager.setClientVersionLatest(latestVersion);
                        for (VersionChangeListener listener : mVersionChangeListeners) {
                            listener.onNewVersion();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //请求超时处理
    private TimeoutHelper.OnTimeoutCallback callback = new TimeoutHelper.OnTimeoutCallback() {
        @Override
        public void onTimeout(TimeoutHelper timeoutHelper, Object taskId) {
            if (mTempUser.getUserId() == (int) taskId) {
                responseResult(-1, R.string.no_network_connection);
            }
        }
    };

    /**
     * 回调结果
     *
     * @param result 返回码
     * @param msg    提示信息
     */
    private void responseResult(final int result, final int msg) {
        Logger.log(Logger.USER, TAG + "->responseResult()->result:" + result + ", msg:" + msg);
        AppHandlerProxy.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                for (LoginStateChangeListener listener : mLoginStateChangeListeners) {
                    listener.onLoginStateChanged(result, msg);
                }
                mLoginStateChangeListeners.clear();
            }
        });
    }

    /**
     * 检测管理员回调
     *
     * @param result 返回码
     * @param json   回调数据
     */
    public void onCheckAdminInfo(int result, String json) {
        if (result == DcpErrorcodeDef.RET_SUCCESS) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                int userId = jsonObject.optInt("_userID", 0);
                if (userId == getCurrentUserInfo().getUserId()) {
                    mIdentity = jsonObject.optInt("_isAdmin", 0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Logger.log(Logger.USER, TAG + "->onCheckAdminInfo()->result:" + result);
        }
    }

    /**
     * 保存用户信息
     *
     * @param userInfo 用户信息
     */
    public void saveUserInfo(final UserInfo userInfo) {
        Logger.log(Logger.USER, TAG + "->saveUserInfo()->userInfo:" + userInfo);
        DatabaseTask<Void> dbTask = new DatabaseTask<Void>() {
            @Override
            public AsyncResult<Void> runOnDBThread(AsyncResult<Void> asyncResult, DBHelper dbHelper) {
                SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
                writableDatabase.beginTransaction();
                try {
                    //1.将所有账号置为不在使用-----------------------------------------------------------------
                    ContentValues unusedValues = new ContentValues();
                    unusedValues.put(UserInfoContract.UserInfoEntry.COLUMN_NAME_IS_USING, UserInfo.UNUSED);
                    long rows = writableDatabase.update(UserInfoContract.UserInfoEntry.TABLE_NAME, unusedValues, null, null);
                    Logger.log(Logger.USER, "将所有账号置为不在使用update rows:" + rows);

                    //2.查询对应userid的账号是否已存在----------------------------------------------------------
                    boolean isExist = false;
                    String sql = "SELECT * FROM " + UserInfoContract.UserInfoEntry.TABLE_NAME
                            + " WHERE " + UserInfoContract.UserInfoEntry.COLUMN_NAME_USER_ID + "=? ";
                    String[] selectionArgs = new String[]{String.valueOf(userInfo.getUserId())};
                    Cursor cursor = writableDatabase.rawQuery(sql, selectionArgs);
                    if (cursor != null) {
                        int count = cursor.getCount();
                        cursor.close();
                        isExist = count > 0;
                        Logger.log(Logger.USER, "查询 userid 为" + userInfo.getUserId() + "的账号 count:" + count + ";isExist:" + isExist);
                    }

                    //3.插入或更新账号信息----------------------------------------------------------
                    ContentValues addValues = new ContentValues();
                    addValues.put(UserInfoContract.UserInfoEntry.COLUMN_NAME_ACCOUNT_TYPE, userInfo.getAccountType());
                    addValues.put(UserInfoContract.UserInfoEntry.COLUMN_NAME_USER_ID, userInfo.getUserId());
                    addValues.put(UserInfoContract.UserInfoEntry.COLUMN_NAME_USER_NAME, userInfo.getUserName());
                    addValues.put(UserInfoContract.UserInfoEntry.COLUMN_NAME_PHONE_NUM, userInfo.getPhoneNum());
                    addValues.put(UserInfoContract.UserInfoEntry.COLUMN_NAME_LOGIN_AUTH_KEY, userInfo.getLoginAuthKey());
                    addValues.put(UserInfoContract.UserInfoEntry.COLUMN_NAME_PES_ADDR, userInfo.getPesAddr());
                    addValues.put(UserInfoContract.UserInfoEntry.COLUMN_NAME_PES_IP, userInfo.getPesIp());
                    addValues.put(UserInfoContract.UserInfoEntry.COLUMN_NAME_PES_PORT, userInfo.getPesPort());
                    addValues.put(UserInfoContract.UserInfoEntry.COLUMN_NAME_IS_USING, userInfo.getIsUsing());
                    addValues.put(UserInfoContract.UserInfoEntry.COLUMN_NAME_LAST_LOGIN_DT, userInfo.getLastLoginDt());
                    addValues.put(UserInfoContract.UserInfoEntry.COLUMN_NAME_IS_ACTIVITY, userInfo.getIsActivity());

                    if (isExist) {
                        String whereClause = UserInfoContract.UserInfoEntry.COLUMN_NAME_USER_ID + " =? ";
                        String[] whereArgs = new String[]{String.valueOf(userInfo.getUserId())};
                        rows = writableDatabase.update(UserInfoContract.UserInfoEntry.TABLE_NAME, addValues, whereClause, whereArgs);
                        Logger.log(Logger.USER, "更新账号信息 userid 为" + userInfo.getUserId() + "的账号 update rows:" + rows);
                    } else {
                        rows = writableDatabase.insert(UserInfoContract.UserInfoEntry.TABLE_NAME, null, addValues);
                        Logger.log(Logger.USER, "插入账号信息 userid 为" + userInfo.getUserId() + "的账号 insert rowID:" + rows);
                    }
                    writableDatabase.setTransactionSuccessful();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    writableDatabase.endTransaction();
                }
                return asyncResult;
            }

            @Override
            public void runOnUIThread(AsyncResult<Void> asyncResult) {
            }
        };
        AppApplication.getInstance().getManager(DBManager.class).submitDatabaseTask(dbTask);
    }

    /**
     * 从数据库中获取当前正在使用的账号信息
     */
    public void loadUserInfo(final LoadUserInfoFished loadUserInfoFished) {
        DatabaseTask<UserInfo> dbTask = new DatabaseTask<UserInfo>() {
            @Override
            public AsyncResult<UserInfo> runOnDBThread(AsyncResult<UserInfo> asyncResult, DBHelper dbHelper) {
                SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
                Cursor cursor = null;
                String sql = "SELECT * FROM " + UserInfoContract.UserInfoEntry.TABLE_NAME + " WHERE " + UserInfoContract.UserInfoEntry.COLUMN_NAME_IS_USING + "=? ";
                writableDatabase.beginTransaction();
                try {
                    cursor = writableDatabase.rawQuery(sql, new String[]{String.valueOf(UserInfo.INUSE)});
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            UserInfo userInfo = new UserInfo();
                            userInfo.setUserId(cursor.getInt(cursor.getColumnIndex(UserInfoContract.UserInfoEntry.COLUMN_NAME_USER_ID)));
                            userInfo.setAccountType(cursor.getInt(cursor.getColumnIndex(UserInfoContract.UserInfoEntry.COLUMN_NAME_ACCOUNT_TYPE)));
                            userInfo.setUserName(cursor.getString(cursor.getColumnIndex(UserInfoContract.UserInfoEntry.COLUMN_NAME_USER_NAME)));
                            userInfo.setPhoneNum(cursor.getString(cursor.getColumnIndex(UserInfoContract.UserInfoEntry.COLUMN_NAME_PHONE_NUM)));
                            userInfo.setLoginAuthKey(cursor.getString(cursor.getColumnIndex(UserInfoContract.UserInfoEntry.COLUMN_NAME_LOGIN_AUTH_KEY)));
                            userInfo.setPesAddr(cursor.getString(cursor.getColumnIndex(UserInfoContract.UserInfoEntry.COLUMN_NAME_PES_ADDR)));
                            userInfo.setPesIp(cursor.getLong(cursor.getColumnIndex(UserInfoContract.UserInfoEntry.COLUMN_NAME_PES_IP)));
                            userInfo.setPesPort(cursor.getInt(cursor.getColumnIndex(UserInfoContract.UserInfoEntry.COLUMN_NAME_PES_PORT)));
                            userInfo.setLastLoginDt(cursor.getLong(cursor.getColumnIndex(UserInfoContract.UserInfoEntry.COLUMN_NAME_LAST_LOGIN_DT)));
                            userInfo.setIsUsing(cursor.getInt(cursor.getColumnIndex(UserInfoContract.UserInfoEntry.COLUMN_NAME_IS_USING)));
                            userInfo.setIsActivity(cursor.getInt(cursor.getColumnIndex(UserInfoContract.UserInfoEntry.COLUMN_NAME_IS_ACTIVITY)));
                            asyncResult.setData(userInfo);
                        }
                    }
                    writableDatabase.setTransactionSuccessful();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) cursor.close();
                    writableDatabase.endTransaction();
                }
                return asyncResult;
            }

            @Override
            public void runOnUIThread(AsyncResult<UserInfo> asyncResult) {
                mCurrentUserInfo = asyncResult.getData();
                Logger.log(Logger.USER, TAG + "->loadUserInfo()->CurrentUserInfo:" + mCurrentUserInfo);
                if (loadUserInfoFished != null && mCurrentUserInfo != null) {
                    loadUserInfoFished.onLoadUserInfoFished(mCurrentUserInfo.getPesIp(), mCurrentUserInfo.getPesPort());
                }
            }
        };
        AppApplication.getInstance().getManager(DBManager.class).submitDatabaseTask(dbTask);
    }

    public interface LoadUserInfoFished {
        void onLoadUserInfoFished(long pesIp, int pesPort);
    }

    /**
     * 转为访客身份
     */
    public void removeUserInfo() {
        DatabaseTask<Integer> dbTask = new DatabaseTask<Integer>() {
            @Override
            public AsyncResult<Integer> runOnDBThread(AsyncResult<Integer> asyncResult, DBHelper dbHelper) {
                SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
                writableDatabase.beginTransaction();
                int rows = 0;
                try {
                    rows = writableDatabase.delete(UserInfoContract.UserInfoEntry.TABLE_NAME,
                            UserInfoContract.UserInfoEntry.COLUMN_NAME_USER_ID + " = ?",
                            new String[]{String.valueOf(getCurrentUserInfo().getUserId())});
                    writableDatabase.setTransactionSuccessful();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    writableDatabase.endTransaction();
                }
                asyncResult.setData(rows);
                return asyncResult;
            }

            @Override
            public void runOnUIThread(AsyncResult<Integer> asyncResult) {
                if (asyncResult.getData() > 0) {
                    mCurrentUserInfo = null;
                }
            }
        };
        AppApplication.getInstance().getManager(DBManager.class).submitDatabaseTask(dbTask);
    }

    /***********************************************其它pes用户状态变化回调处理***********************************************/
    /**
     * 收到被踢下线通知
     *
     * @param result 返回码
     * @param json   返回数据
     */
    public void onKickOff(int result, String json) {
        Logger.log(Logger.USER, TAG + "->onKickOff()->result:" + result + ", json:" + json);
        showKickoffDialog();
    }

    /**
     * 收到离线通知
     *
     * @param result 返回码
     * @param json   返回数据
     */
    public void onOffline(int result, String json) {
        Logger.log(Logger.USER, TAG + "->onOffline()");
        AppApplication.getInstance().setOnlineState(AppConstant.OnlineState.ONLINE_STATE_OFFLINE);
    }

    /**
     * 收到发送活跃包结果
     *
     * @param result 返回码
     * @param json   返回数据
     */
    public void onSendAction(int result, String json) {
        Logger.log(Logger.USER, TAG + "->onSendAction()->result:" + result + ", json:" + json);
        if (result == 0 && !StringUtil.isEmpty(json)) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                int actionType = jsonObject.optInt("_actionType", 0);
                int returnResult = jsonObject.optInt("_result", 0);
                if (returnResult == 0 && actionType == 0) {
                    AppPreference.setLongValue(AppPreference.KEY_SEND_ACTION_DT + getCurrentUserInfo().getUserId(), System.currentTimeMillis());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 显示被踢下线对话框
     */
    public void showKickoffDialog() {
        Logger.log(Logger.USER, TAG + "->showKickoffDialog()");
        AppApplication.getInstance().setOnlineState(AppConstant.OnlineState.ONLINE_STATE_KICKOFF);
        AppApplication.getInstance().getManager(DcpManager.class).logout(getCurrentUserInfo().getUserId());
        AppApplication.getInstance().getManager(DcpManager.class).disconnect();
        if (AppHelper.isAppRunningForeground(mApplication, mApplication.getPackageName())) {
            Intent intent = new Intent(mApplication, KickOffDialog.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mApplication.startActivity(intent);
        } else {
            AppPreference.setBooleanValue(AppPreference.FLAG_BACKGROUND_KICKOFF, true);
        }
    }

}
