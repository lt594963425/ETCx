package com.etcxc.android.modle.db;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteTransactionListener;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.etcxc.MeManager;
import com.etcxc.android.utils.LogUtil;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xwpeng on 2017/5/25.
 * <p>
 * tips: 增加表 按照下列逻辑操作。
 * 1.1  在 xxxDbHelper（公有数据 PublicDbHelper， 私有数据用 PrivateDbHelper）中的 interface Tables  增加 你的表名，
 * 1.2  创建你的 xxColumns 列， 新增方法 createTablexxx 执行表创建
 * <p>
 * 2.1  在 xxxUriField 中，增加你的 Uri，同时增加 UriQueryPath。
 * <p>
 * 3.1  在 xxxContentProvider 中增加成员变量ID  例如 GROUP , REMIND
 * 3.2  在 UriMatcher 增加你的配置 例如  sMatcher.addURI(PrivateUriField.AUTHORITY, UriQueryPath.GROUP, GROUP);
 * 3.3  在 getTableNameByMatch 增加表匹配， 在getUriByMatch 增加Uri 匹配
 * 3.4  在 insert query delete update  都对应增加 case
 * 如果用mDb.query 能满足需求，则不需要处理 该条，因为default 中默认用 mDb.query 处理）
 */
public class PrivateContentProvider extends ContentProvider implements SQLiteTransactionListener{
    private final String TAG = PrivateContentProvider.class.getSimpleName();
    private static final UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private PrivateDbHelper mDbHelper;
    private String mUid;//当前DBHelper操作的数据库所属用户id;

    //各线程是否在批处理的标记
    private final AtomicBoolean mApplyingBatch = new AtomicBoolean(false);
    private static final int CONVERT_DB = 0x0001;

    static {
        sMatcher.addURI(PrivateUriField.AUTHORITY, PrivateUriField.UriQueryPath.CONVERT_DB, CONVERT_DB);
    }

    /**
     * 切换数据库，没有目标数据库会新建一个
     */
    private synchronized void convertDbHelper() {
        String uid = MeManager.getUid();
        if (TextUtils.isEmpty(uid)) return;
        if (uid.equals(mUid)) return;
        mDbHelper = new PrivateDbHelper(uid);
        mUid = uid;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = sMatcher.match(uri);
        if (match == CONVERT_DB) {
           convertDbHelper();
            return null;
        }
        if (mDbHelper == null) return null;
        Cursor c = mDbHelper.getReadableDatabase().query(getTableNameByMatch(match),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (mDbHelper == null) return null;
        int match = sMatcher.match(uri);
        long rowId =  mDbHelper.getWritableDatabase().insert(getTableNameByMatch(match), null, values);
        if (!mApplyingBatch.get()) getContext().getContentResolver().notifyChange(uri, null);
        if (rowId > 0) return ContentUris.withAppendedId(getUriByMatch(match), rowId);
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (mDbHelper == null) return 0;
        int match = sMatcher.match(uri);
        int count = mDbHelper.getWritableDatabase().delete(getTableNameByMatch(match),
                selection,
                selectionArgs);
        if (!mApplyingBatch.get()) getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (mDbHelper == null) return 0;
        int match = sMatcher.match(uri);
        int count = mDbHelper.getWritableDatabase().update(getTableNameByMatch(match),
                values,
                selection,
                selectionArgs);
        if (!mApplyingBatch.get()) getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        if (mDbHelper == null) return null;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransactionWithListener(this);
        Uri uri = null;
        try {
            mApplyingBatch.set(true);
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                final ContentProviderOperation operation = operations.get(i);
                results[i] = operation.apply(this, results, i);
                if (uri == null) {
                    uri = operation.getUri();
                }
            }
            db.setTransactionSuccessful();
            return results;
        } catch (Exception e) {
            LogUtil.e(TAG, "applyBatch", e);
            return null;
        } finally {
            mApplyingBatch.set(false);
            db.endTransaction();
            if (uri != null) getContext().getContentResolver().notifyChange(uri, null);
        }
    }

    private String getTableNameByMatch(int match) {
        String tableName = null;
        switch (match) {

        }
        return tableName;
    }


    private Uri getUriByMatch(int match) {
        Uri uri = null;
        switch (match) {

        }
        return uri;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public void onBegin() {
    }

    @Override
    public void onCommit() {
    }

    @Override
    public void onRollback() {
    }
}
