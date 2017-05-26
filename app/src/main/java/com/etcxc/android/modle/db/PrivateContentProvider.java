package com.etcxc.android.modle.db;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteTransactionListener;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.etcxc.MeManager;
import com.etcxc.android.util.LogUtil;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.icu.lang.UScript.COMMON;

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
public class PrivateContentProvider extends ContentProvider implements
        SQLiteTransactionListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private final String TAG = "PrivateContentProvider";
    // FIXME: 2017/5/25  不知道这个字段是干嘛的
    private static final int CONVERT_DB = 0x1FFF; //8191
    /**
     * 联系人
     */
    private static final int CONTACT = 0x2001;

    /**
     * URI键值匹配器
     */
    private static final UriMatcher sMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        sMatcher.addURI(PrivateUriField.AUTHORITY, PrivateUriField.UriQueryPath.CONVERT_DB, CONVERT_DB);
        sMatcher.addURI(PrivateUriField.AUTHORITY, PrivateUriField.UriQueryPath.CONTACT1, CONTACT);
    }

    PrivateDbHelper dbHelper;

    SQLiteDatabase mDb;
    //标识用户uid,使用手机号码
    private String mUid;

    /**
     * 各线程是否在批处理的标记
     */
    private final AtomicBoolean mApplyingBatch = new AtomicBoolean(false);

    @Override
    public boolean onCreate() {
        LogUtil.d(TAG, "PrivateContentProvider.onCreate");
        Context context = getContext();
        dbHelper = getDatabaseHelper(context);
        return initialize();
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = sMatcher.match(uri);
        if (match == CONVERT_DB) {
            LogUtil.d(TAG, "query: init db");
            getHelperInitial();
            return null;
        }
        if (dbHelper == null) {
            LogUtil.d(TAG, "query: dbHelper is null");
            return null;
        }
        //这里的参数不可以包含null，否则报IllegalArgumentException异常 by Yuan
        if (selectionArgs != null) {
            for (int i = selectionArgs.length; i != 0; i--) {
                if (selectionArgs[i - 1] == null) {
                    LogUtil.e(TAG, "query: IllegalArgumentException, the bind value at index " + i + " is null");
                    return null;
                }
            }
        }
        mDb = dbHelper.getReadableDatabase();
        if (mDb == null) {
            LogUtil.e(TAG, "query: mDb is null");
            return null;
        }
        Cursor c;
        switch (match) {
            case COMMON:
                c = mDb.rawQuery(selection, selectionArgs);
                break;
            default: // todo 所有默认用 mDb.query 查询的都在default中处理
                c = mDb.query(getTableNameByMatch(match),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
        }
        return c;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (dbHelper == null) {
            return null;
        }
        mDb = dbHelper.getWritableDatabase();
        if (mDb == null) {
            LogUtil.e(TAG, "insert mDb is null");
            return null;
        }
        long rowId;
        int match = sMatcher.match(uri);
        switch (match) {
            case COMMON:
                break;
            default:
                rowId = mDb.insert(getTableNameByMatch(match), null, values);
                LogUtil.v(TAG, "insert [" + uri.toString() + "], rowId=" + rowId);
                if (!mApplyingBatch.get()) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                if (rowId > 0) {
                    Uri noteUri = ContentUris.withAppendedId(getUriByMatch(match),
                            rowId);
                    return noteUri;
                }
                break;

        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (dbHelper == null) {
            return 0;
        }
        mDb = dbHelper.getWritableDatabase();
        if (mDb == null) {
            LogUtil.e(TAG, "delete mDb is null");
            return 0;
        }
        int count = 0;
        int match = sMatcher.match(uri);
        switch (match) {
            case COMMON:
                break;
            default:
                count = mDb.delete(getTableNameByMatch(match),
                        selection,
                        selectionArgs);
                break;
        }
        if (!mApplyingBatch.get()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (dbHelper == null) {
            return 0;
        }
        mDb = dbHelper.getWritableDatabase();
        if (mDb == null) {
            LogUtil.e(TAG, "update mDb is null");
            return 0;
        }
        int count = 0;
        int match = sMatcher.match(uri);
        switch (match) {
            case COMMON:
                break;
            default:
                count = mDb.update(getTableNameByMatch(match),
                        values,
                        selection,
                        selectionArgs);
                if (!mApplyingBatch.get()) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    LogUtil.d(TAG, "uri:" + uri.toString());
                }
                break;
        }
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }

    @Override
    public ContentProviderResult[] applyBatch(
            ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        if (dbHelper == null) {
            return null;
        }
        mDb = dbHelper.getWritableDatabase();
        if (mDb == null) {
            LogUtil.e(TAG, "applyBatch mDb is null");
            return null;
        }
        mDb.beginTransactionWithListener(this); // 如果是并发执行 applyBatch，
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


    /**
     *sharedPreference变动,私人数据库创建
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        LogUtil.d(TAG, "onSharedPreferenceChanged ------." + key);
        if (TextUtils.equals(key, MeManager.KEY_Uid)) {
            mUid = MeManager.getUid();
            if (TextUtils.isEmpty(mUid)) {
                LogUtil.e(TAG, "onSharedPreferenceChanged  uid is null.Ignore it.");
                return;
            } else {
                LogUtil.d(TAG, "onSharedPreferenceChanged  userSysId : " + mUid);
                getHelperInitial();
            }
        }
    }

    private synchronized void getHelperInitial() {
        String uid = MeManager.getUid();
        if (TextUtils.isEmpty(uid)) {
            LogUtil.d(TAG, " uid is null");
            return;
        }

        if (uid.equals(mUid) && dbHelper != null) { // db初始化已经在onCreate 中完成，则不需要重新初始化
            LogUtil.d(TAG, " uid eq mUid, dbHelper not null");
            return;
        }
        mUid = uid;
        PrivateDbHelper helper = PrivateDbHelper.getInstance(getContext(),
                mUid);
        LogUtil.d(TAG, " db init, PrimaryEmail:" + mUid);
        setDatabaseHelper(helper);
        initialize();
    }

    protected void setDatabaseHelper(PrivateDbHelper openHelper) {
        dbHelper = openHelper;
    }

    protected PrivateDbHelper getDatabaseHelper(Context context) {
        mUid = MeManager.getUid();
        LogUtil.d(TAG, "mUid:" + mUid);
        if (TextUtils.isEmpty(mUid)) {
            LogUtil.d(TAG, "mUid is null");
            return null;
        }
        PrivateDbHelper helper = PrivateDbHelper.getInstance(context,
                mUid);
//        setDatabaseHelper(helper);
        return helper;

//        if (null != dbHelperPool.get(mUid)) {
//            setDatabaseHelper(dbHelperPool.get(mUid));
//        } else {
//            PrivateDbHelper helper = PrivateDbHelper.getInstance(context,
//                    mUid);
//            dbHelperPool.put(mUid, helper);
//            setDatabaseHelper(helper);
//        }
//        return dbHelperPool.get(mUid);
    }

    private boolean initialize() {
        if (null != dbHelper) {
            mDb = dbHelper.getWritableDatabase();
        }
        return null != mDb;
    }


    private String getTableNameByMatch(int match) {
        String tableName = null;
        switch (match) {
            case CONTACT:
                tableName = PrivateDbHelper.Tables.CONTACT;
                break;
            default: // 没有匹配到对应的表
                throw new IllegalArgumentException("table No match type ! match:" + match);
        }
        return tableName;
    }


    private Uri getUriByMatch(int match) {
        Uri uri = null;
        switch (match) {
            case CONTACT:
                uri = PrivateUriField.CONTACT1_URI;
                break;
            default:
                throw new IllegalArgumentException("Uri No match type ! match:" + match);
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
