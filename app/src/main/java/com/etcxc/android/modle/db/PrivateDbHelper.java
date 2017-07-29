package com.etcxc.android.modle.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.etcxc.android.base.App;
import com.etcxc.android.utils.Base64Util;
import com.etcxc.android.utils.LogUtil;

/**
 * 数据库创建、升级、连接，继承自SQLiteOpenHelper
 *Created by xwpeng on 2017/5/25.<br/>
 */
public class PrivateDbHelper extends SQLiteOpenHelper {
    private static final String TAG = PrivateDbHelper.class.getSimpleName();
    //数据库版本号
    private static final int DATABASE_VERSION = 1;

    public PrivateDbHelper(String primaryKey) {
        super(App.get(), Base64Util.encode(primaryKey).replace("/", "_") + ".db", null, DATABASE_VERSION);//把/替换成_以免不能作为文件名
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
           //建表
            db.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtil.e(TAG, "onCreate", e);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //示例
      /*  if (oldVersion < 37) {
            createTableNfUTask(db);
            createTableMimeMail4Upgrade(db);
            addColumnsIfNotExit(db, Tables.DOWNLOAD_FILE, DownLoadFileColumns.DELETE, "INTEGER", "0");
        }*/
    }

    private void addColumnsIfNotExit(SQLiteDatabase db, String table, String columns, String columnsType, String defaultValue) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + table, null);
        int deleteStateColumnIndex = cursor.getColumnIndex(columns);
        if (deleteStateColumnIndex < 0) {
            String sql = "ALTER TABLE " + table + " ADD COLUMN " + columns + " " + columnsType;
            if (!TextUtils.isEmpty(defaultValue)) {
                sql += (" DEFAULT " + defaultValue);
            }
            db.execSQL(sql);
        }
        cursor.close();
    }

    public interface Tables {
       //表
    }

    public interface RechargeColumns extends BaseColumns {
       //字段
    }

    private void createTableXXX(SQLiteDatabase db) {
     //建表
    }

}
