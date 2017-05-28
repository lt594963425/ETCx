package com.etcxc.android.modle.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import com.etcxc.android.utils.LogUtil;

import java.util.LinkedHashMap;

import static com.etcxc.android.modle.db.DbUtil.assemblySql;

/**
 * fixme：单例写法
 * Created by xwpeng on 2017/5/25.
 */
public class PublicDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "PublicDbHelper";

    /**
     * 数据库名字
     */
    private static final String DATABASE_NAME = "public.db";
    /**
     * 数据库的版本号
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * 数据库表操作对象
     */
    private static PublicDbHelper sSingleton = null;

    /**
     * 获取单例
     */
    public static synchronized PublicDbHelper getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new PublicDbHelper(context);
        }
        return sSingleton;
    }

    private PublicDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            createTableInterface1(db);//接口调用表
            db.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtil.e(TAG, "create table exception: " + e.toString());
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtil.d(TAG, "Old version is " + oldVersion + ",New version is " + newVersion);
    }

    public interface Tables {
        /**
         * 接口使用表
         */
        String INTERFACE1 = "_interface1";
    }

    public interface Interface1Columns extends BaseColumns {
        String NAME = "_interface1_name";
        String URL = "_interface1_url";
        String FORMDATA = "_interface1_formdata";
        String REQUESTTTIME = "_interface1_requesttime";
        String RESPONETIME = "_interface1_reponsetime";
        String RESULT = "_interface1_result";
        String UID = "_interface1_uid";
    }

    private void createTableInterface1(SQLiteDatabase db) {
        LinkedHashMap<String, String> types = new LinkedHashMap<>();
        types.put(Interface1Columns.NAME, "TEXT");
        types.put(Interface1Columns.URL, "TEXT");
        types.put(Interface1Columns.FORMDATA, "TEXT");
        types.put(Interface1Columns.REQUESTTTIME, "TEXT");
        types.put(Interface1Columns.RESPONETIME, "INTEGER");
        types.put(Interface1Columns.RESULT, "TEXT");
        types.put(Interface1Columns.UID, "TEXT");
        db.execSQL(assemblySql(types, Tables.INTERFACE1));
        types.clear();
    }
}
