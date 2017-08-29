package cn.longmaster.ihuvc.core.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.longmaster.ihuvc.core.AppApplication;
import cn.longmaster.ihuvc.core.db.contract.MaterialTaskContract.MaterialTaskEntry;
import cn.longmaster.ihuvc.core.db.contract.MaterialTaskFileContract.MaterialTaskFileEntry;

/**
 * 数据库操作类
 * Created by yangyong on 2016/4/15.
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

    public static final String DB_NAME = "ihuvc.db";
    public static final int DB_VERSION = 2;

    private static DBHelper sDBHelper;

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DBHelper getInstance() {
        if (sDBHelper == null) {
            synchronized (DBHelper.class) {
                if (sDBHelper == null)
                    sDBHelper = new DBHelper(AppApplication.getInstance());
            }
        }
        return sDBHelper;
    }


    /**
     * 创建辅助资料上传信息表sql语句
     */
    private static final String SQL_CREATE_MATERIAL_TASK_TABLE =
            CREATE_TABLE + MaterialTaskEntry.TABLE_NAME + "(" +
                    MaterialTaskEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT " + COMMA_SEP +
                    MaterialTaskEntry.COLUMN_NAME_USER_ID + TEXT_TYPE + COMMA_SEP +
                    MaterialTaskEntry.COLUMN_NAME_TASK_ID + TEXT_TYPE + COMMA_SEP +
                    MaterialTaskEntry.COLUMN_NAME_STATE + INTEGER_TYPE + COMMA_SEP +
                    MaterialTaskEntry.COLUMN_NAME_APPOINTMENT_ID + TEXT_TYPE + COMMA_SEP +
                    MaterialTaskEntry.COLUMN_NAME_MATERIAL_ID + TEXT_TYPE + COMMA_SEP +
                    MaterialTaskEntry.COLUMN_NAME_MATERIAL_DATE + TEXT_TYPE + COMMA_SEP +
                    MaterialTaskEntry.COLUMN_NAME_RECUR_NUM + TEXT_TYPE + COMMA_SEP +
                    MaterialTaskEntry.COLUMN_NAME_DOCTOR_ID + TEXT_TYPE + ")";

    /**
     * 创建辅助资料上传文件上传进度信息表sql语句
     */
    private static final String SQL_CREATE_MATERIAL_TASK_FILE_TABLE =
            CREATE_TABLE + MaterialTaskFileEntry.TABLE_NAME + "(" +
                    MaterialTaskFileEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT " + COMMA_SEP +
                    MaterialTaskFileEntry.COLUMN_NAME_TASK_ID + TEXT_TYPE + COMMA_SEP +
                    MaterialTaskFileEntry.COLUMN_NAME_SESSION_ID + TEXT_TYPE + COMMA_SEP +
                    MaterialTaskFileEntry.COLUMN_NAME_STATE + INTEGER_TYPE + COMMA_SEP +
                    MaterialTaskFileEntry.COLUMN_NAME_PROGRESS + INTEGER_TYPE + COMMA_SEP +
                    MaterialTaskFileEntry.COLUMN_NAME_MATERIAL_DT + TEXT_TYPE + COMMA_SEP +
                    MaterialTaskFileEntry.COLUMN_NAME_LOCAL_FILE_PATH + TEXT_TYPE + COMMA_SEP +
                    MaterialTaskFileEntry.COLUMN_NAME_LOCAL_FILE_NAME + TEXT_TYPE + COMMA_SEP +
                    MaterialTaskFileEntry.COLUMN_NAME_LOCAL_POSTFIX + TEXT_TYPE + COMMA_SEP +
                    MaterialTaskFileEntry.COLUMN_NAME_SERVER_FILE_NAME + TEXT_TYPE + ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MATERIAL_TASK_TABLE);
        db.execSQL(SQL_CREATE_MATERIAL_TASK_FILE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void addColumn(SQLiteDatabase db, String tableName, String columnName, String dataType) {
        db.execSQL("ALTER TABLE " + tableName + " ADD " + columnName + dataType);
    }
}
