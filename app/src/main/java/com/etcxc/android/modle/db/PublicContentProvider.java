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

import com.etcxc.android.utils.LogUtil;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xwpeng on 2017/5/25.
 * tips: 增加表 按照下列逻辑操作。
 * 1.1  在 xxxDbHelper（公有数据 PublicDbHelper， 私有数据用 PrivateDbHelper）中的 interface Tables  增加 你的表名，
 * 1.2  创建你的 xxColumns 列， 新增方法 createTable_xxx 执行表创建 ,同时在 onCreate 中调用该方法
 * <p>
 * 2.1  在 xxxUriField 中，增加你的 Uri，同时增加 UriQueryPath。
 * <p>
 * 3.1  在 xxxContentProvider 中增加成员变量ID  例如 GROUP , REMIND
 * 3.2  在 UriMatcher 增加你的配置 例如  sMatcher.addURI(PrivateUriField.AUTHORITY, UriQueryPath.GROUP, GROUP);
 * 3.3  在 getTableNameByMatch 增加表匹配， 在getUriByMatch 增加Uri 匹配
 * 3.4  insert query delete update  都对应增加 case
 * 如果用mDb.query 能满足需求，则不需要处理 该条，因为default 中默认用 mDb.query 处理）
 */
public class PublicContentProvider extends ContentProvider implements SQLiteTransactionListener {
    private final String TAG = PublicDbHelper.class.getSimpleName();
    PublicDbHelper mDbHelper;
    SQLiteDatabase mDb;

    /**
     * 接口调用记录
     */
    private static final int API_RECORD = 0x1004;

    /**
     * URI键值匹配器
     */
    private static final UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sMatcher.addURI(PublicUriField.AUTHORITY, PublicUriField.UriQueryPath.API_RECORD, API_RECORD);
    }

    /**
     * 各线程是否在批处理的标记  , true 不通知， false 通知
     */
//    private final ThreadLocal<Boolean> mApplyingBatch = new ThreadLocal<Boolean>();
    private final AtomicBoolean mApplyingBatch = new AtomicBoolean(false);

    @Override
    public boolean onCreate() {
        mDbHelper = PublicDbHelper.getInstance();
        return initialize();
    }

    private boolean initialize() {
        if (null != mDbHelper) mDb = mDbHelper.getWritableDatabase();
        return null != mDb;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        mDb = mDbHelper.getReadableDatabase();
        Cursor c = mDb.query(getTableNameByMatch(sMatcher.match(uri)),
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
        mDb = mDbHelper.getWritableDatabase();
        long rowId;
        int match = sMatcher.match(uri);
                rowId = mDb.insert(getTableNameByMatch(match), null, values);
                LogUtil.d(TAG, "rowId:" + rowId);
                if (!mApplyingBatch.get()) getContext().getContentResolver().notifyChange(uri, null);
                if (rowId > 0) {
                    Uri noteUri = ContentUris.withAppendedId(getUriByMatch(match), rowId);
                    return noteUri;
                }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        mDb = mDbHelper.getWritableDatabase();
        int match = sMatcher.match(uri);
        int count = mDb.delete(getTableNameByMatch(match),
                        selection,
                        selectionArgs);
        if (!mApplyingBatch.get()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        mDb = mDbHelper.getWritableDatabase();
        int match = sMatcher.match(uri);
        int count = mDb.update(getTableNameByMatch(match),
                        values,
                        selection,
                        selectionArgs);
                if (!mApplyingBatch.get()) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    LogUtil.d(TAG, "uri:" + uri.toString());
                }
        return count;
    }

    private String getTableNameByMatch(int match) {
        String tableName;
        switch (match) {
            case API_RECORD:
                tableName = PublicDbHelper.Tables.ApiRecord;
                break;
            default:
                throw new IllegalArgumentException("table No match type ! match:" + match);
        }
        return tableName;
    }

    private Uri getUriByMatch(int match) {
        Uri uri;
        switch (match) {
            case API_RECORD:
                uri = PublicUriField.API_RECORD_URI;
                break;
            default:
                throw new IllegalArgumentException("Uri No match type ! match:" + match);
        }
        return uri;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        mDb = mDbHelper.getWritableDatabase();
        mDb.beginTransactionWithListener(this);
        Uri uri = null;
        try {
            mApplyingBatch.set(true);
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                LogUtil.d(TAG, "numOperations");
                final ContentProviderOperation operation = operations.get(i);
                results[i] = operation.apply(this, results, i);
                if (uri == null) {
                    uri = operation.getUri();
                }
            }
            mDb.setTransactionSuccessful();
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            mApplyingBatch.set(false);
            mDb.endTransaction();
            if (uri != null) {
                LogUtil.d(TAG, "applyBatch uri:" + uri.toString());
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
    }

    @Override
    public void onBegin() {}

    @Override
    public void onCommit() {}

    @Override
    public void onRollback() {}

    @Override
    public String getType(Uri uri) {
        return null;
    }

}