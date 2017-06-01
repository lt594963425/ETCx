package com.etcxc.android.modle.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.etcxc.android.utils.Base64Util;
import com.etcxc.android.utils.LogUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * fixme 单列模式写法优化？
 * 若多表联合查询，表字段名一样时处理起来比较麻烦，所以定义字段时都加上表名，如message1表的client_mid字段，则定义成"_message1_client_mid"，均以"-"开头，以保证所有表字段名都不一样。
 *Created by xwpeng on 2017/5/25.<br/>
 */
public class PrivateDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "PrivateDbHelper";

    private static final String DATABASE_NAME_SUFFIX = ".db";

    /**
     * 数据库的版本号<br/>
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * 数据库表操作对象
     */
    private static PrivateDbHelper sSingleton = null;

    /**
     * @param primaryKey 账户主键，如手机号
     */
    public static PrivateDbHelper getInstance(Context context, String primaryKey) {
        LogUtil.d(TAG, "new DatabaseHelper");
        String databaseName = Base64Util.encode(primaryKey).replace("/", "_");//把/替换成_以免不能作为文件名 on 2017/03/10
        sSingleton = new PrivateDbHelper(context, databaseName);
        return sSingleton;
    }

    private PrivateDbHelper(Context context, String memCode) {
        super(context, memCode + DATABASE_NAME_SUFFIX,
                null, DATABASE_VERSION);
        LogUtil.i(TAG, "init DatabaseHelper()  memCode : " + memCode);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            createTableContact(db); // 联系人表
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
        //示例
      /*  if (oldVersion < 37) {
            createTableNfUTask(db);
            createTableMimeMail4Upgrade(db);
            addColumnsIfNotExit(db, Tables.DOWNLOAD_FILE, DownLoadFileColumns.DELETE, "INTEGER", "0");
        }*/
    }

    private void addColumnsIfNotExit2(SQLiteDatabase db, String table, String columns, String columnsType, String defaultValue) {
        long t1 = System.currentTimeMillis();
        Cursor cursor = db.rawQuery("SELECT sql FROM sqlite_master where name ='" + table + "'", null);
        if (cursor != null && cursor.moveToFirst()) {
            String sql = cursor.getString(cursor.getColumnIndex("sql"));
            if (!TextUtils.isEmpty(sql)) {
                String colStr = sql.substring(sql.indexOf("(") + 1, sql.indexOf(")"));
                String[] clos = colStr.split("\\,");
                boolean exists = false;
                for (String col : clos) {
                    if (col.startsWith(columns + " ")) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    String sql1 = "ALTER TABLE " + table + " ADD COLUMN " + columns + " " + columnsType;
                    if (!TextUtils.isEmpty(defaultValue)) {
                        sql1 += (" DEFAULT " + defaultValue);
                    }
                    db.execSQL(sql1);
                }
            }
        }
        DbUtil.closeCursor(cursor);
        LogUtil.e(TAG, "addColumnsIfNotExit2 alter tabel spend time=" + (System.currentTimeMillis() - t1));
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
        /**
         * 联系人表
         */
        String CONTACT = "_contact";
    }

    public void createTableContact(SQLiteDatabase db) {
        LinkedHashMap<String, String> types = new LinkedHashMap<>();
        types.put(Contact1Columns.UID, "TEXT");
        types.put(Contact1Columns.TRUE_NAME, "TEXT");
        types.put(Contact1Columns.EMAIL, "TEXT");
        types.put(Contact1Columns.UNREAD, "INTEGER");
        types.put(Contact1Columns.LAST_MODIFIED, "INTEGER");
        types.put(Contact1Columns.DELETED, "INTEGER");
        types.put(Contact1Columns.PRESENCE, "INTEGER");
        types.put(Contact1Columns.USER_STATUS, "INTEGER");
        types.put(Contact1Columns.EXTERNAL_LIMIT, "INTEGER DEFAULT 0 ");
        db.execSQL(assemblySql(types, Tables.CONTACT));
        types.clear();
    }

    /**
     * 联系人表
     */
    public interface Contact1Columns extends BaseColumns {
        String UID = "_uid";
        String TRUE_NAME = "_true_name";
        String EMAIL = "_email";
        String UNREAD = "_unread";
        String LAST_MODIFIED = "_last_modified";
        String DELETED = "_deleted";
        String PRESENCE = "_presence";
        String USER_STATUS = "_user_status";
        String EXTERNAL_LIMIT = "_external_limit";
        String ORG_ID = "_org_id";
        String OU_ID = "_ou_id";
        String CUSTOMER_ID = "_customer_id";
        String MY_TRUST_LEVEL = "_my_trust_level";
        String OTHER_TRUST_LEVEL = "_other_trust_level";
    }

    /**
     * 组装SQL建表语句
     *
     * @param types
     * @param table
     * @return
     */
    private String assemblySql(LinkedHashMap<String, String> types, String table) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CREATE TABLE IF NOT EXISTS ")
                .append(table)
                .append("(").append(BaseColumns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT");
        for (Map.Entry<String, String> entry : types.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            buffer.append(",").append(key).append(" ").append(value);
        }
        buffer.append(")");
        String sql = buffer.toString();
        buffer.setLength(0);
        LogUtil.d(TAG, "create " + table + " sql:" + sql);
        return sql;
    }
}
