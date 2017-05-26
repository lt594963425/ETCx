package com.etcxc.android.modle.db;

import android.database.Cursor;
import android.provider.BaseColumns;

import com.android.etcxc.BuildConfig;
import com.etcxc.android.util.LogUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 数据库工具类
 * Created by xwpeng on 2015/7/15.
 */
public class DbUtil {

    /**
     * 组装SQL建表语句
     */
    public static String assemblySql(LinkedHashMap<String, String> types, String table) {
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
        LogUtil.d("DbUtl", "create " + table + " sql:" + sql);
        return sql;
    }

    /**
     * 关闭游标
     */
    public static void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    /**
     * Public数据库存储的AUTHORITY<br/>
     *
     * @return
     */
    public static String publicUriAuthority() {
        return BuildConfig.APPLICATION_ID + ".public";
    }

    /**
     * Private数据库存储的AUTHORITY<br/>
     *
     * @return
     */
    public static String privateUriAuthority() {
        return BuildConfig.APPLICATION_ID + ".private";
    }
}