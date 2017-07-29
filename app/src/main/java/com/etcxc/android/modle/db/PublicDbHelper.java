package com.etcxc.android.modle.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.etcxc.android.base.App;
import com.etcxc.android.utils.LogUtil;

import java.util.LinkedHashMap;

import static com.etcxc.android.modle.db.DbUtil.assemblySql;

/**
 * 公有数据库帮助类
 * Created by xwpeng on 2017/5/25.
 */
public class PublicDbHelper extends SQLiteOpenHelper {
    private static final String TAG = PublicDbHelper.class.getSimpleName();
    /**
     * 数据库的版本号
     */
    private static final int DATABASE_VERSION = 1;

    private PublicDbHelper(Context context) {
        super(context, "public.db", null, DATABASE_VERSION);
    }

    private static class InnerClass {
        private static PublicDbHelper instance = new PublicDbHelper(App.get());
    }

    public static PublicDbHelper getInstance() {
        return InnerClass.instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            createTableApiRecord(db);//接口调用表
            db.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtil.e(TAG, "onCreate", e);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtil.d(TAG, "Old version is " + oldVersion + ",New version is " + newVersion);
    }

    public interface Tables {
        //接口使用记录表
        String ApiRecord = "_api_record";
    }

    public interface ApiRecordColumns extends BaseColumns {
        String NAME = "_name";
        String URL = "_url";
        String REQUEST_DATA = "_requestData";
        String REQUEST_TTIME = "_requestTime";
        String RESPONSE_DATA = "_reponseData";
        String RESPONSE_TIME = "_responseTime";
        String UID = "_uid";
    }

    private void createTableApiRecord(SQLiteDatabase db) {
        LinkedHashMap<String, String> types = new LinkedHashMap<>();
        types.put(ApiRecordColumns.NAME, "TEXT");
        types.put(ApiRecordColumns.URL, "TEXT");
        types.put(ApiRecordColumns.REQUEST_DATA, "TEXT");
        types.put(ApiRecordColumns.REQUEST_TTIME, "INTEGER");
        types.put(ApiRecordColumns.RESPONSE_DATA, "TEXT");
        types.put(ApiRecordColumns.RESPONSE_TIME, "INTEGER");
        types.put(ApiRecordColumns.UID, "TEXT");
        db.execSQL(assemblySql(types, Tables.ApiRecord));
        types.clear();
    }
}
