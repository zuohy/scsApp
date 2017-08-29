package cn.longmaster.ihmonitor.core.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import cn.longmaster.ihmonitor.core.app.AppApplication;
import cn.longmaster.ihmonitor.core.db.contract.BaseConfigContract.BaseConfigEntry;
import cn.longmaster.ihmonitor.core.db.contract.UserInfoContract.UserInfoEntry;

/**
 * 数据库帮助类
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String CREATE_TABLE = "CREATE TABLE ";
    public static final String ALTER_TABLE = "ALTER TABLE ";
    public static final String PRIMARY_KEY = " PRIMARY KEY";
    public static final String AUTO_INCREMENT = " AUTOINCREMENT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ", ";
    public static final String ADD = " ADD ";
    public static final String SPACE = " ";

    private static final String SQL_CREATE_TABLE_USER_INFO = CREATE_TABLE + UserInfoEntry.TABLE_NAME +
            " (" +
            UserInfoEntry.COLUMN_NAME_USER_ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP +
            UserInfoEntry.COLUMN_NAME_ACCOUNT_TYPE + INTEGER_TYPE + COMMA_SEP +
            UserInfoEntry.COLUMN_NAME_USER_NAME + TEXT_TYPE + COMMA_SEP +
            UserInfoEntry.COLUMN_NAME_PHONE_NUM + TEXT_TYPE + COMMA_SEP +
            UserInfoEntry.COLUMN_NAME_LOGIN_AUTH_KEY + TEXT_TYPE + COMMA_SEP +
            UserInfoEntry.COLUMN_NAME_PES_ADDR + TEXT_TYPE + COMMA_SEP +
            UserInfoEntry.COLUMN_NAME_PES_IP + TEXT_TYPE + COMMA_SEP +
            UserInfoEntry.COLUMN_NAME_PES_PORT + INTEGER_TYPE + COMMA_SEP +
            UserInfoEntry.COLUMN_NAME_IS_USING + INTEGER_TYPE + COMMA_SEP +
            UserInfoEntry.COLUMN_NAME_LAST_LOGIN_DT + TEXT_TYPE + COMMA_SEP +
            UserInfoEntry.COLUMN_NAME_IS_ACTIVITY + INTEGER_TYPE +
            ")";

    private static final String SQL_CREATE_TABLE_BASE_CONFIG = CREATE_TABLE + BaseConfigEntry.TABLE_NAME +
            " (" +
            BaseColumns._ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP +
            BaseConfigEntry.COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
            BaseConfigEntry.COLUMN_NAME_DATE_ID + TEXT_TYPE + COMMA_SEP +
            BaseConfigEntry.COLUMN_NAME_TOKEN + TEXT_TYPE + COMMA_SEP +
            BaseConfigEntry.COLUMN_NAME_CONTENT + TEXT_TYPE +
            ")";

    public static final String DB_NAME = "ihmonitor.db";
    public static final int DB_VERSION = 1;

    private static DBHelper mDBHelper;

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DBHelper getInstance() {
        if (mDBHelper == null) {
            synchronized (DBHelper.class) {
                if (mDBHelper == null)
                    mDBHelper = new DBHelper(AppApplication.getInstance());
            }
        }
        return mDBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_USER_INFO);
        db.execSQL(SQL_CREATE_TABLE_BASE_CONFIG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
